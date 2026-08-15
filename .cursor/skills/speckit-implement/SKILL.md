---
name: speckit-implement
description: Execute tasks.md under constitution gates. Use for initial build and incremental slices.
---

# /speckit.implement

Load:
- `.specify/memory/constitution.md`
- Active feature `spec.md`, `plan.md`, `tasks.md`

Loop (max 5 autonomous iterations):
1. Implement the next undone task (Java only).
2. `mvn test`
3. `mvn -q exec:java -Dexec.mainClass=com.example.shortener.sast.SastScanner`
4. Log the iteration in `agentic_loop_log.md`
5. On failure: diagnose, patch, repeat
6. On success: update `hitl/HITL_GATE.md` and **STOP**. Do not merge. Open a PR if GitHub is connected.

Every test maps to an FR. Every change maps to a task ID.
