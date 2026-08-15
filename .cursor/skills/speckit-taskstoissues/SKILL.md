---
name: speckit-taskstoissues
description: Convert tasks.md into GitHub issues for tracking. Requires gh auth.
---

# /speckit.taskstoissues

Use the GitHub CLI from the repo root (never invent issue numbers):

1. `gh auth status` — stop and tell the user to run `gh auth login` if needed.
2. `gh issue list --limit 100` and skip task IDs that already have issues.
3. For each remaining task, `gh issue create --title "T<n> <task>" --body "<maps to FR> ..."`.
4. Link the issue URLs back in `tasks.md`.

Do not close issues or merge PRs. HITL stays with the human reviewer.
