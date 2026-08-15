# Implementation Plan: Click Counts on Resolve

**Branch**: `002-click-counts` | **Date**: 2026-08-15 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `specs/002-click-counts/spec.md`

## Summary

Record how many times a known short code is successfully followed, and let anyone who knows the code look up that count. Counts live in memory for the process lifetime (no persistence). Implementation stays on the existing Java 17 service: increment on successful `resolve()`, expose `clickCount()`, add `GET /stats/{code}` without changing shorten/redirect behavior.

## Technical Context

**Language/Version**: Java 17

**Primary Dependencies**: JDK only (`com.sun.net.httpserver`). JUnit 5 for tests. In-repo `SastScanner`.

**Storage**: In-memory `ConcurrentHashMap` + `AtomicLong` (same process as URL maps). No disk or database.

**Testing**: JUnit 5 via `mvn test` (service unit tests + HTTP tests). SAST via `SastScanner`.

**Target Platform**: JVM on macOS/Linux (prototype HTTP server).

**Project Type**: Web service (minimal HTTP)

**Performance Goals**: Correct count under concurrent follows of the same code; lookup without following the link.

**Constraints**: Constitution: Java-only, JUnit happy/edge/failure per public method, SAST HIGH/CRITICAL block HITL, max 5 autonomous iterations, no merge without human approval. Must not change FR-1..FR-6 behavior from `001-url-shortener`.

**Scale/Scope**: Prototype: one process, in-memory maps, no auth.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Gate | How this plan satisfies it |
|------|----------------------------|
| I. Test Coverage | New public methods (`clickCount`, increment-on-resolve) get JUnit happy / edge / failure tests mapped to FR-001..FR-007. HTTP `GET /stats/{code}` covered. Existing tests must still pass (FR-007). |
| I. Java-only | No Python. Counts and SAST remain Java. |
| II. SAST | No `java.util.Random`, no secrets, no deserialization. Validator unchanged. Run `SastScanner` before HITL. |
| III. HITL | Agent stops at `hitl/HITL_GATE.md` + PR review. Must not merge. |
| IV. Autonomy | implement → test → SAST → fix, max 5, then forced check-in. |
| V. Traceability | `tasks.md` (later) maps T-IDs to FR-001..FR-007; tests annotate FRs. |

**Gate result (pre-design)**: PASS — incremental slice on existing stack; no constitution amendments.

**Gate result (post-design)**: PASS — data model and contracts add one in-memory counter and one stats route; no new runtime deps, no persistence, no auth.

## Project Structure

### Documentation (this feature)

```text
specs/002-click-counts/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── http-stats.md
├── spec.md
└── checklists/requirements.md
```

### Source Code (repository root)

```text
src/main/java/com/example/shortener/
├── UrlShortenerService.java    # clickCount(); increment on successful resolve
├── UrlShortenerServer.java     # GET /stats/{code}; GET /{code} still 302
├── UrlValidator.java           # unchanged
├── Base62Codec.java            # unchanged
└── sast/SastScanner.java       # unchanged gate

src/test/java/com/example/shortener/
├── UrlShortenerServiceTest.java
├── UrlShortenerServerTest.java
└── ... existing tests remain
```

**Structure Decision**: Keep the single Maven module. Extend service + HTTP layer in place; do not add a new module or store.

## Complexity Tracking

No constitution violations. Table omitted.

## Phase 0 / Phase 1

See [research.md](./research.md), [data-model.md](./data-model.md), [contracts/http-stats.md](./contracts/http-stats.md), [quickstart.md](./quickstart.md).

## Quality Gates (every implement iteration)

1. `mvn test`
2. `mvn -q exec:java -Dexec.mainClass=com.example.shortener.sast.SastScanner`
3. HITL package — stop; do not merge
