---
name: speckit-constitution
description: Create or update project constitution at .specify/memory/constitution.md. Use when establishing or amending non-negotiable rules.
---

# /speckit.constitution

Update `.specify/memory/constitution.md` and keep `specs/constitution.md` in sync.

This project's constitution already exists. Amendments require:
1. Explicit human intent to change a principle
2. A dedicated PR
3. HITL approval (do not self-merge)

Current principles: JUnit 5 coverage, Java SAST gate, mandatory HITL, max 5 autonomous iterations, Java-only tooling, requirement traceability.

If the user is starting a *new* principle, add a numbered section, bump Governance version, and explain why later phases must honor it.
