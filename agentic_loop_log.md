# Agentic Loop — Execution Log

Loop: `implement -> compile -> test -> security_scan -> {pass -> HITL stop | fail -> patch -> repeat}`
Max autonomous iterations per constitution.md: 5.

Historical iterations 1–2 (sandbox, Python test mirror + Java sources) are retained
below for traceability. Current gates are Java-only (`mvn test` + `SastScanner`).

---

## Iteration 1 (historical)

**Implemented:** T1 (naive), T2, T3, T4, T5 (naive), T6, T8 stub

**Test gate:** 5 passed, 2 failed (javascript:/file: schemes accepted)

**Security gate:** `[HIGH] CWE-338` `java.util.Random`

**Decision:** Blocking — patch and re-loop.

---

## Iteration 2 (historical)

**Patched:** scheme allow-list on `UrlValidator`; `SecureRandom` for short codes.

**Test gate:** 7 passed, 0 failed

**Security gate:** clean

**Decision:** HITL package produced. Later approved in the sandbox prototype.

---

## Iteration 3 (Java-only platform)

**Implemented:** Remove Python harness; Java `SastScanner`; JUnit coverage for
validator/codec/service/server/SAST; Spec Kit layout; GitHub Actions; Cursor
hooks/skills; HITL via PR review.

**Test gate:** 20/20 JUnit tests passed.
**Security gate:** 5 Java sources analyzed, 0 findings.
**Decision:** Automated gates green. STOP for HITL (`hitl/HITL_GATE.md`). Do not merge.
