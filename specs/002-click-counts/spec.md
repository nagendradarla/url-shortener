# Feature Specification: Click Counts on Resolve

**Feature Branch**: `002-click-counts`

**Created**: 2026-08-14

**Status**: Draft

**Input**: User description: "Add click counts on resolve. Persist still out of scope."

## Clarifications

### Session 2026-08-15

- Q: Are click counts stored in a database? → A: No. In-memory only; no database.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Count successful follows (Priority: P1)

A person who created or shared a short link wants to know how many times that link was actually followed. Each time someone successfully uses the short code to reach the original destination, the click count for that code goes up by one.

**Why this priority**: Without recording successful follows, there is no click count to show. This is the core value of the feature.

**Independent Test**: Shorten a valid link, follow it a known number of times, then confirm the reported count matches that number.

**Acceptance Scenarios**:

1. **Given** a known short code that has never been followed, **When** someone successfully follows it, **Then** the click count for that code is 1.
2. **Given** a known short code whose count is N, **When** someone successfully follows it again, **Then** the click count is N + 1.
3. **Given** a newly shortened link, **When** nobody has followed it yet, **Then** the click count is 0.

---

### User Story 2 - View the click count (Priority: P1)

A person who knows a short code can look up how many successful follows that code has received so far.

**Why this priority**: Recording counts has no user value unless the count can be retrieved.

**Independent Test**: After a known number of successful follows, look up the count for that code and confirm it matches.

**Acceptance Scenarios**:

1. **Given** a known short code with a recorded count, **When** someone requests that code’s click count, **Then** the system returns the current count clearly.
2. **Given** a known short code that has not been followed, **When** someone requests its click count, **Then** the system returns 0.

---

### User Story 3 - Unknown codes do not invent counts (Priority: P2)

Someone who presents a short code the system does not know must get a clear not-found result. The system must not create a count, increment anything, or fail silently.

**Why this priority**: Protects existing “unknown code” behavior and keeps counts honest.

**Independent Test**: Request a follow and a count for a code that was never shortened; both must report not found, and no new count appears.

**Acceptance Scenarios**:

1. **Given** an unknown short code, **When** someone tries to follow it, **Then** they receive a clear not-found result and no click count is created.
2. **Given** an unknown short code, **When** someone requests its click count, **Then** they receive a clear not-found result.

---

### Edge Cases

- Following an unknown short code must not create or increment a count.
- Looking up a count for an unknown short code must return not found, not zero.
- Failed or rejected follow attempts (including blank codes) must not increment a count.
- Shortening the same long URL twice still yields one short code and one shared count.
- Counts are not retained after the service stops. There is no database; counts exist only in the running service’s memory.
- Concurrent follows of the same code must not lose increments (each successful follow is counted).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST record a click count for each existing short code, starting at 0 when the code is created.
- **FR-002**: The system MUST increment that code’s click count by exactly 1 for each successful follow (resolve) of a known short code.
- **FR-003**: The system MUST NOT increment a click count when a follow fails or the short code is unknown.
- **FR-004**: Users MUST be able to retrieve the current click count for a known short code.
- **FR-005**: Given an unknown short code, a count lookup MUST return a clear not-found result rather than 0 or a crash.
- **FR-006**: Click counts MUST live only in the running service’s memory. The system MUST NOT use a database (or any other durable store). Counts are lost when the service stops.
- **FR-007**: Existing shorten and resolve behavior (valid URLs, rejection of dangerous/malformed input, idempotent shortening, non-guessable codes) MUST remain unchanged.

### Key Entities

- **Short code**: Existing identifier that maps to one original destination.
- **Click count**: Non-negative whole number of successful follows for one short code, held only in memory for the current service lifetime (not in a database).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: After N successful follows of a known short code, a count lookup for that code reports N (100% match in verification).
- **SC-002**: A newly created short code reports 0 clicks before any successful follow.
- **SC-003**: Count lookup or follow for an unknown code is reported as not found in every verification case; no false zero counts.
- **SC-004**: A person can obtain the current count for a known code in a single lookup without following the link again.

## Assumptions

- Persistence remains out of scope: no database and no files. Counts live only while the service is running.
- Anyone who knows a short code may view its count; this prototype has no user accounts, so counts are not access-controlled.
- “Click” means a successful follow of a known short code, not a count-lookup request and not a failed follow.
- Idempotent shortening (same long URL → same code) shares one count for that code.
- No dashboards, date ranges, referrers, or unique-visitor analytics in this increment.

## Out of Scope

- Persistent storage of counts (no database, no files)
- User accounts / authentication
- Unique visitors, geography, or referrer analytics
- Custom aliases
- Changing how short codes are generated
