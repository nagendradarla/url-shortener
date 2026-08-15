---
name: speckit-plan
description: Create the technical plan for the active Spec Kit feature.
---

# /speckit.plan

Read `.specify/memory/constitution.md` and the active `spec.md` (see `.specify/feature.json`).

Write `plan.md` in the feature directory:
- Stack must stay Java 21, JUnit 5, zero runtime deps unless the user explicitly changes the constitution/plan.
- Name components, quality gates, and the agentic loop: implement → compile → test → SAST → HITL stop.
- No Python gates.

Hand off to `/speckit.tasks`.
