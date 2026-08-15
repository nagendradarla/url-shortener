# URL Shortener — agent operating rules

This is a Spec Kit + constitution-gated Java service.

1. Read `.specify/memory/constitution.md` first.
2. Active feature: see `.specify/feature.json` (default `specs/001-url-shortener/`).
3. Stack: Java 17, JUnit 5, in-repo `SastScanner`. No Python product/test/SAST code.
4. Loop: implement → `mvn test` → SAST → fix (max 5) → commit on `spec{N}` → push/PR → **HITL stop**. Never merge.
5. Slash/skills: `/speckit.specify`, `/speckit.plan`, `/speckit.tasks`, `/speckit.implement`, `/speckit.converge`, `/speckit.analyze`, `/speckit.taskstoissues`.
6. GitHub (constitution VI): after gates, commit on dedicated branch `spec{N}` (e.g. `specs/002-*` → `spec2`). Push and `gh pr create --reviewer nagendradarla`. If a PR already exists for the current branch, push the commit to that PR (`gh pr edit --add-reviewer nagendradarla` if the reviewer is missing). Never merge. Use `gh` after `gh auth login`.
