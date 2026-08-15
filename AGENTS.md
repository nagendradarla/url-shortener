# URL Shortener — agent operating rules

This is a Spec Kit + constitution-gated Java service.

1. Read `.specify/memory/constitution.md` first.
2. Active feature: see `.specify/feature.json` (default `specs/001-url-shortener/`).
3. Stack: Java 21, JUnit 5, in-repo `SastScanner`. No Python product/test/SAST code.
4. Loop: implement → `mvn test` → SAST → fix (max 5) → **HITL stop**. Never merge.
5. Slash/skills: `/speckit.specify`, `/speckit.plan`, `/speckit.tasks`, `/speckit.implement`, `/speckit.converge`, `/speckit.analyze`, `/speckit.taskstoissues`.
6. GitHub: open a PR; required review is HITL. Use `gh` after `gh auth login`.
