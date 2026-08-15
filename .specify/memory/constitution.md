<!--
Sync Impact Report
- Version change: 1.1.0 → 1.2.0 (MINOR: new principle VI)
- Modified principles: none renamed
- Added sections: VI. Spec Branch and Pull Request
- Removed sections: none
- Follow-up TODOs: none
-->

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

## VI. Spec Branch and Pull Request
- After implementation for a spec is finished AND constitution gates pass
  (`mvn test` and Java SAST with zero HIGH/CRITICAL findings), the agent MUST
  commit the code. Do not commit to `main`.
- Every spec MUST use a dedicated Git branch named `spec{N}`, where `{N}` is
  the numeric prefix of the active feature directory with leading zeros
  stripped (`specs/002-click-counts` → `spec2`). Create and switch to that
  branch if it does not already exist.
- The agent MUST push the branch and open a GitHub PR targeting `main`.
- If an open PR already exists for the current branch, the agent MUST NOT
  open a second PR. Push the new commit to that branch so it appears on the
  existing PR.
- Every PR MUST request review from GitHub user `nagendradarla`
  (`gh pr create --reviewer nagendradarla`, or
  `gh pr edit --add-reviewer nagendradarla` when updating an existing PR).
  If GitHub rejects the request because that user is the PR author, record
  that in the PR body and `hitl/HITL_GATE.md`; CODEOWNERS still names
  `nagendradarla` and HITL approval is still required.
- The agent MUST NOT merge the PR.

## Governance
- Version: 1.2.0
- Last amended: 2026-08-15
- Amendment: change this file via a dedicated PR; HITL still required.
- Compliance: `/speckit.analyze` and the quality-gate workflow must both pass.
