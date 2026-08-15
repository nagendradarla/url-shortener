# HITL Review Gate — URL Shortener Service

**Status: AWAITING HUMAN APPROVAL**

Java-only quality gates replaced the previous Python sandbox harness.
Per constitution.md, the agent stops here and requires an explicit human
decision before merge.

## Summary
| Gate | Result |
|---|---|
| Requirements coverage | FR-1..FR-6 implemented (`specs/001-url-shortener/spec.md`) |
| Test gate | `mvn test` (JUnit 5: validator, codec, service, server, SAST) |
| Security gate (SAST) | `com.example.shortener.sast.SastScanner` — HIGH/CRITICAL block merge |
| Traceability | Tasks T1–T10 map to FRs; tests annotated with FR/task IDs |
| HITL | This file + GitHub PR review. Agent must not merge. |

## What changed in this increment
- Removed Python mirrors (`tools/*.py`, `security/sast_scan.py`, `SelfCheckRunner`)
- Native Java SAST + Maven/GitHub Actions gates
- Spec Kit layout (`.specify/`, `specs/001-url-shortener/`)
- Cursor rules, skills, and stop-hook loop (max 5)
- GitHub PR template / CODEOWNERS as the HITL surface

## Reviewer decision
- [ ] **Approve** — merge as-is
- [ ] Request changes
- [ ] Reject

Reviewer:          Date:          Notes:
