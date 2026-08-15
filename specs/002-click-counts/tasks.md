# Tasks: Click Counts on Resolve

**Input**: Design documents from `specs/002-click-counts/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/http-stats.md

**Tests**: Included — constitution I (JUnit happy/edge/failure) and spec independent tests. Write tests first and confirm they fail before implementation.

**Organization**: Tasks are grouped by user story so each story can be implemented and tested independently.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: US1 / US2 / US3 from spec.md
- Include exact file paths in descriptions

## Path Conventions

Single Maven module: `src/main/java/com/example/shortener/`, `src/test/java/com/example/shortener/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Brownfield baseline — project already exists; do not add modules or Python.

- [x] T001 Run existing `mvn test` as a baseline and confirm FR-1..FR-6 still pass in `src/test/java/com/example/shortener/`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: In-memory ClickCount storage and initialize to 0 on new shorten (FR-001, FR-006). Blocks all stories.

**⚠️ CRITICAL**: No user story work until this phase is complete

- [x] T002 Write failing JUnit tests that a new shorten reports click count 0 (FR-001 happy) and `clickCount` exists in `src/test/java/com/example/shortener/UrlShortenerServiceTest.java`
- [x] T003 Add `ConcurrentHashMap<String, AtomicLong>` counters, set 0 on first `shorten()`, and `clickCount(String)` for known codes in `src/main/java/com/example/shortener/UrlShortenerService.java`

**Checkpoint**: Foundation ready — a newly shortened code has count 0; user stories can start

---

## Phase 3: User Story 1 - Count successful follows (Priority: P1) 🎯 MVP

**Goal**: Each successful `resolve()` of a known short code increments that code’s count by 1 (FR-002).

**Independent Test**: Shorten a valid URL, `resolve` it N times, then `clickCount` equals N.

### Tests for User Story 1

> Write these tests FIRST; they MUST fail before T005.

- [x] T004 [US1] Write failing JUnit tests for resolve 0→1, N→N+1, and blank/null resolve does not increment (FR-002, FR-003) in `src/test/java/com/example/shortener/UrlShortenerServiceTest.java`

### Implementation for User Story 1

- [x] T005 [US1] Increment the existing `AtomicLong` only when `resolve()` finds a known code in `src/main/java/com/example/shortener/UrlShortenerService.java`

**Checkpoint**: User Story 1 is testable at the service layer without HTTP

---

## Phase 4: User Story 2 - View the click count (Priority: P1)

**Goal**: Users retrieve the current count without following the link (FR-004, SC-004). HTTP: `GET /stats/{code}`.

**Independent Test**: After N successful follows, `GET /stats/{code}` returns N; a second stats request still returns N.

### Tests for User Story 2

- [x] T006 [P] [US2] Write failing JUnit tests that `clickCount` does not increment and works without resolve (FR-004) in `src/test/java/com/example/shortener/UrlShortenerServiceTest.java`
- [x] T007 [P] [US2] Write failing HTTP tests for `GET /stats/{code}` → 200 with `0` then `1` after one redirect, and stats does not increment, in `src/test/java/com/example/shortener/UrlShortenerServerTest.java`

### Implementation for User Story 2

- [x] T008 [US2] Keep `clickCount()` read-only (no mutate) in `src/main/java/com/example/shortener/UrlShortenerService.java`
- [x] T009 [US2] Register `GET /stats/{code}` (`200` decimal body) before the catch-all `/` in `src/main/java/com/example/shortener/UrlShortenerServer.java`

**Checkpoint**: User Stories 1 and 2 work: increment on follow, lookup via service and HTTP

---

## Phase 5: User Story 3 - Unknown codes do not invent counts (Priority: P2)

**Goal**: Unknown/blank codes return not-found; no counter is created (FR-003, FR-005).

**Independent Test**: Follow and stats-lookup for a never-shortened code both not-found; no count of 0 appears.

### Tests for User Story 3

- [x] T010 [P] [US3] Write failing JUnit tests that unknown/blank `resolve` and `clickCount` are empty and do not create counters (FR-003, FR-005) in `src/test/java/com/example/shortener/UrlShortenerServiceTest.java`
- [x] T011 [P] [US3] Write failing HTTP tests `GET /missing` and `GET /stats/missing` → 404 `Not found` in `src/test/java/com/example/shortener/UrlShortenerServerTest.java`

### Implementation for User Story 3

- [x] T012 [US3] Return `Optional.empty()` for unknown/blank `clickCount`; never insert a counter on failed resolve in `src/main/java/com/example/shortener/UrlShortenerService.java`
- [x] T013 [US3] Return 404 for unknown/blank `GET /stats/{code}` in `src/main/java/com/example/shortener/UrlShortenerServer.java`

**Checkpoint**: All three stories independently functional

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Edges, constitution gates, HITL. Maps to FR-006, FR-007.

- [x] T014 Add JUnit that repeating `shorten` of the same URL does not reset the shared count in `src/test/java/com/example/shortener/UrlShortenerServiceTest.java`
- [x] T015 Add JUnit that concurrent `resolve` calls do not lose increments in `src/test/java/com/example/shortener/UrlShortenerServiceTest.java`
- [x] T016 Confirm existing FR-1..FR-6 tests still pass (FR-007) in `src/test/java/com/example/shortener/`
- [x] T017 Run `mvn test` and `mvn -q exec:java -Dexec.mainClass=com.example.shortener.sast.SastScanner` (constitution I–II)
- [x] T018 [P] Record iteration in `agentic_loop_log.md` and fill `hitl/HITL_GATE.md` (constitution III)
- [x] T019 [P] Verify walkthrough in `specs/002-click-counts/quickstart.md` against the running server

**Checkpoint**: Gates green → STOP for HITL. Do not merge.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies
- **Foundational (Phase 2)**: Depends on T001 — BLOCKS all user stories
- **US1 (Phase 3)**: Depends on Phase 2
- **US2 (Phase 4)**: Depends on Phase 2; uses US1 increment for HTTP “0 then 1” but stats of 0 is independently testable after T003
- **US3 (Phase 5)**: Depends on `clickCount` + `/stats` existing (Phase 2 + T009)
- **Polish (Phase 6)**: Depends on US1–US3

### User Story Dependencies

- **User Story 1 (P1)**: After Foundational — service increment + readable count
- **User Story 2 (P1)**: After Foundational — HTTP stats; sequential after US1 recommended (same `UrlShortenerServer.java` as redirect)
- **User Story 3 (P2)**: After US2 HTTP route exists — unknown 404 behavior

### Within Each User Story

- Tests MUST fail before implementation
- Service before HTTP
- Story complete before next priority when sharing the same files

### Parallel Opportunities

- T006 and T007 (different test files)
- T010 and T011 (different test files)
- T018 and T019 (different docs)
- Do **not** parallelize tasks that edit the same `UrlShortenerService.java` or `UrlShortenerServer.java`

---

## Parallel Example: User Story 2

```text
Task: "Write failing JUnit tests that clickCount does not increment in UrlShortenerServiceTest.java"
Task: "Write failing HTTP tests for GET /stats/{code} in UrlShortenerServerTest.java"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. T001 baseline
2. T002–T003 foundation (count 0 on shorten)
3. T004–T005 increment on resolve
4. **STOP and VALIDATE** at the service layer
5. Then US2 HTTP if delivering the full spec

### Incremental Delivery

1. Setup + Foundational → count 0 exists
2. US1 → counts follow — MVP
3. US2 → `GET /stats/{code}`
4. US3 → unknown codes stay not-found
5. Polish → SAST + HITL stop

### Parallel Team Strategy

Limited: service and server files are shared. One implementer sequential US1 → US2 → US3; a second person can only take [P] test files before those implementations.

---

## Notes

- Task IDs T001–T019; every task has checkbox, ID, file path
- Story labels only on US phases
- [P] only when different files
- FR-001..FR-007 mapped in task text (constitution V)
- Agent: implement → test → SAST → max 5 → HITL. Never merge
