---
name: speckit-analyze
description: Read-only consistency check across constitution, spec, plan, and tasks.
---

# /speckit.analyze

Compare constitution, spec.md, plan.md, tasks.md, and the Java codebase.

Report (do not implement unless asked):
- FRs with no task or no test
- Tasks that violate Java-only / SAST / HITL rules
- Gaps vs plan components
- HIGH/CRITICAL SAST issues if you run the scanner

Fix at the source artifact, then re-analyze before `/speckit.implement`.
