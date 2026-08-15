# Research: Click Counts on Resolve

## 1. Where to store counts

**Decision**: In-process `ConcurrentHashMap<String, AtomicLong>` keyed by short code, created at `shorten()` with value 0. Not persisted.

**Rationale**: Spec FR-006 forbids persistence. Existing URL maps already use `ConcurrentHashMap`. `AtomicLong` gives lost-update-free increments under concurrent follows (spec edge case).

**Alternatives considered**:
- Database / file log — out of scope.
- Plain `Long` in the map with `compute` — workable, but `AtomicLong` is clearer for increment-only.
- Increment only in the HTTP layer — would skip counts when `resolve()` is called from tests or other callers; spec defines a click as a successful follow of a known code, which is `resolve()` succeeding.

## 2. When a click is counted

**Decision**: Increment inside `UrlShortenerService.resolve()` only when the code is known and a destination is returned. Blank/null/unknown codes return empty and do not create or increment a counter. `clickCount()` lookups do not increment.

**Rationale**: Matches FR-002, FR-003, and the assumption that a count lookup is not a click. Keeps HTTP as a thin adapter.

**Alternatives considered**:
- Count at HTTP 302 only — misses non-HTTP callers and splits behavior.
- Count unknown codes as 0 — violates FR-005 (not-found, not zero).

## 3. How users retrieve the count

**Decision**: `GET /stats/{code}` returns the decimal count as `text/plain` with 200, or 404 `"Not found"` for unknown/blank codes. Existing `GET /{code}` remains a 302 redirect and still increments.

**Rationale**: `com.sun.net.httpserver` matches the longest context prefix. A dedicated `/stats` context avoids treating `stats` as a short code and avoids overloading GET (redirect vs body). Plain text matches `POST /shorten` / `GET /{code}` style.

**Alternatives considered**:
- `GET /{code}/clicks` — awkward with a catch-all `/` context.
- Include count on the 302 response body — users following a link never see it (SC-004 wants lookup without following).
- JSON — extra format; not used elsewhere in this prototype.

## 4. Idempotent shorten vs counts

**Decision**: Second `shorten()` of the same URL returns the existing code and does not reset the counter.

**Rationale**: Spec: one code, one shared count.

**Alternatives considered**: Resetting to 0 on repeat shorten would erase real follows.

## 5. Constitution / stack

**Decision**: Stay on Java 17, JUnit 5, `SastScanner`, zero new runtime libraries.

**Rationale**: Constitution I–II and `001` plan. Click counts do not justify a new stack.

**Alternatives considered**: Metrics libraries, Redis — out of scope and would add dependencies.
