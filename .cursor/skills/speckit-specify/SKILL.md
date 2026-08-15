---
name: speckit-specify
description: Create a feature specification (what/why, no stack). Use for initial product specs and incremental features.
---

# /speckit.specify

Write `specs/<NNN>-<short-name>/spec.md` focused on **what** and **why**. No Java, Maven, or HTTP details.

## Initial (0-to-1)
Active feature is `specs/001-url-shortener/spec.md` (FR-1..FR-6). Do not rewrite it unless the user is changing product behavior.

## Incremental
1. Choose the next number (`002-...`).
2. Create `spec.md` with FRs, out of scope, and acceptance criteria.
3. Set `.specify/feature.json` `feature_directory` to the new folder.
4. Load `.specify/memory/constitution.md` and refuse FRs that violate it.

Then hand off to `/speckit.clarify` or `/speckit.plan`.
