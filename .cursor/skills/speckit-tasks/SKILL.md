---
name: speckit-tasks
description: Break the active plan into ordered, traceable tasks.
---

# /speckit.tasks

Read constitution, spec.md, and plan.md for the active feature.

Write `tasks.md` with IDs `T1..Tn` (or `T001` if using Spec Kit defaults). Each task maps to an FR. Order: validator/codec → service → tests → SAST → HTTP → HITL package.

Mark independent tasks so they can run in parallel. Do not implement yet.

Optional next: `/speckit.analyze`, `/speckit.taskstoissues`, then `/speckit.implement`.
