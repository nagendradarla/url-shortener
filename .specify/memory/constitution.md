# Constitution — URL Shortener Service

Non-negotiable rules the agent must satisfy before any code is proposed for merge.
These gates apply to every iteration of the agentic loop, not just the final one.

**Canonical copy:** `.specify/memory/constitution.md` (Spec Kit). Keep both files in sync.

## I. Test Coverage
- Every public behavior in the service layer MUST have a corresponding JUnit 5 test.
- Minimum: happy path, boundary/edge case, and failure/exception case per method.
- No task is "done" until its tests exist AND pass.
- Application and gate code is Java-only. Do not add Python (or other) runtime/test/SAST tooling.

## II. Security (SAST Gate)
- All code MUST pass static application security testing before merge.
- The in-repo Java scanner (`com.example.shortener.sast.SastScanner`) stands in for Checkmarx and checks for:
  - Use of cryptographically weak PRNGs for security-relevant values (CWE-338)
  - Unvalidated/open-redirect-prone URL handling (CWE-601)
  - Hardcoded secrets/credentials (CWE-798)
  - Unsafe deserialization / injection sinks (CWE-502)
- Any HIGH or CRITICAL finding blocks the loop from proceeding to HITL review.

## III. Human-in-the-Loop (HITL)
- No code reaches "merged" status without explicit human approval.
- The agent MUST stop and produce a reviewable package (diff, test report,
  security report) at the HITL gate — it MUST NOT self-approve.
- Human reviewer may: Approve, Request Changes (loop continues), or Reject.
- On GitHub, HITL is a required pull-request review. The agent may open a PR; it may not merge it.

## IV. Controlled Autonomy
- The agent may freely iterate (implement → test → scan → fix) without asking
  permission, but MAY NOT cross the HITL gate autonomously.
- Every iteration is logged: what changed, why, and the resulting gate status.
- Max autonomous iterations before forced human check-in: 5.

## V. Traceability
- Every task in tasks.md maps to spec.md requirements.
- Every code change maps to a task ID.
- Every test maps to a requirement ID.

## Governance
- Version: 1.1.0
- Amendment: change this file via a dedicated PR; HITL still required.
- Compliance: `/speckit.analyze` and the quality-gate workflow must both pass.
