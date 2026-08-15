---
name: speckit-converge
description: Check the codebase against spec/plan/tasks and append remaining work.
---

# /speckit.converge

Assess `src/` against the active feature artifacts.

If gaps exist, append new tasks to `tasks.md` (do not silently recode). Then tell the user to run `/speckit.implement`.

If converged: instruct them to open a PR and wait for HITL. Never merge.
