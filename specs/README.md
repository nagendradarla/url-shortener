# Specs

Canonical Spec Kit layout:

| Path | Role |
|------|------|
| `.specify/memory/constitution.md` | Non-negotiable project rules |
| `.specify/feature.json` | Active feature directory |
| `specs/001-url-shortener/` | Current feature: spec, plan, tasks |
| `specs/constitution.md` | Pointer / copy of the constitution for local browsing |

For a **new incremental feature**, do not edit `001-*` in place. Create `specs/00N-short-name/` via `/speckit.specify` (after `specify init`) and point `.specify/feature.json` at it.
