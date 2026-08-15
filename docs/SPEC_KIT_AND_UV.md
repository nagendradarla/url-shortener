# Spec Kit + uv — initial and incremental setup

`uv` is **only** for the Spec Kit CLI (`specify-cli`). The URL shortener itself is Maven/Java 17. Do not use uv to run, test, or scan the service.

Official docs: [Spec Kit Quick Start](https://github.github.io/spec-kit/quickstart.html) · [Installation](https://github.github.io/spec-kit/installation.html)

## Prerequisites

- Python 3.11+ (needed by `specify-cli`, not by the app)
- [uv](https://docs.astral.sh/uv/)
- Git
- JDK 17+ and Maven 3.9+
- Cursor (this repo is wired for `--integration cursor-agent`)
- GitHub CLI (`gh`) for issues/PRs

## 1. Install uv and Specify

```bash
curl -LsSf https://astral.sh/uv/install.sh | sh
export PATH="$HOME/.local/bin:$PATH"

# PyPI (simple)
uv tool install specify-cli

# or pin a Spec Kit release (recommended for teams)
# uv tool install specify-cli --from git+https://github.com/github/spec-kit.git@v0.12.11

specify version
```



## 2. Initial development (this repo already has artifacts)

This repository already contains constitution, spec, plan, tasks, Java code, and Cursor skills. After cloning:

```bash
cd url-shortener
specify init --here --integration cursor-agent --script sh --ignore-agent-tools --force
```

`--force` merges Spec Kit templates into the existing tree. Prefer committing first so you can diff the merge.

If you were starting from an empty directory instead:

```bash
uv tool install specify-cli
specify init url-shortener --integration cursor-agent --script sh
cd url-shortener
```

Then in Cursor, run in order (full path):

1. `/speckit.constitution` — already filled; only amend on purpose
2. `/speckit.specify` — already `specs/001-url-shortener/spec.md`
3. `/speckit.clarify` — optional
4. `/speckit.plan` — already present (Java 17)
5. `/speckit.checklist` — optional (after official `specify init`)
6. `/speckit.tasks` — already T1–T10
7. `/speckit.analyze` — consistency check
8. `/speckit.implement` — agentic loop (max 5 iterations), then commit on `spec{N}`, push/PR, HITL
9. `/speckit.converge` — append leftover tasks if the code diverged

Shorter path for tiny follow-ups: specify → plan → tasks → implement → converge.

## 3. Incremental development (next feature)

Do **not** overwrite `specs/001-url-shortener/`. Create a new numbered folder.

```text
/speckit.specify Add click counts on resolve. Persist still out of scope.
```

Expected result: `specs/002-click-counts/spec.md` and `.specify/feature.json` pointing at it.

Then:

```text
/speckit.clarify
/speckit.plan Keep Java 17, in-memory ConcurrentHashMap, JUnit 5, existing SAST rules.
/speckit.tasks
/speckit.analyze
/speckit.taskstoissues    # optional, needs: gh auth login
/speckit.implement        # one phase at a time if the task list is large
/speckit.converge
```

After gates pass, commit on branch `spec{N}` (not `main`), push, and open a PR with reviewer `nagendradarla`. If a PR already exists for that branch, push the commit to it instead of opening another PR. CI runs constitution gates. A human reviews (HITL). The agent never merges.

Brownfield rule: upgrade Spec Kit (`specify self upgrade`) separately from changing `specs/` behavior.

## 4. GitHub integration

```bash
gh auth login
git init
git add .
git commit -m "Java URL shortener with Spec Kit gates and HITL."
gh repo create url-shortener --private --source=. --remote=origin --push
```

Then:

- Enable **Settings → Rules → Require a pull request before merging** and **require approvals** (this is HITL).
- Require the `Quality gates` check.
- Every spec lands on branch `spec{N}` with a PR reviewed by `nagendradarla` (constitution VI).
- `/speckit.taskstoissues` to turn `tasks.md` into issues.
- PRs use `.github/PULL_REQUEST_TEMPLATE.md`.

`gh` is not logged in on this machine until you run `gh auth login`, so the remote must be created locally.

## 5. What uv is not used for


| Tool             | Purpose                                             |
| ---------------- | --------------------------------------------------- |
| uv + specify-cli | Spec Kit commands, templates, Cursor skills refresh |
| Maven            | compile, JUnit, exec server, exec SAST              |
| GitHub Actions   | same Maven gates on every PR                        |
| Cursor hooks     | local agentic loop after the agent stops            |


