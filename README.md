# URL Shortener — Spec-Driven Development

Java 17 URL shortener built with Spec Kit methodology: constitution-gated agentic
loop, JUnit 5, in-repo SAST, and mandatory human-in-the-loop (HITL) before merge.

**uv applies to Spec Kit CLI only**, not to the Java service. Setup steps:
[docs/SPEC_KIT_AND_UV.md](docs/SPEC_KIT_AND_UV.md).

## Spec Kit phase → artifact map

| Phase | Artifact | Contents |
|---|---|---|
| `/speckit.constitution` | `.specify/memory/constitution.md` | JUnit, SAST, HITL, Java-only, max 5 iterations |
| `/speckit.specify` | `specs/001-url-shortener/spec.md` | FR-1..FR-6 |
| `/speckit.plan` | `specs/001-url-shortener/plan.md` | Java 17 + quality gates + loop |
| `/speckit.tasks` | `specs/001-url-shortener/tasks.md` | T1–T10 |
| `/speckit.implement` | `src/` | Code + tests produced under the loop |
| HITL | `hitl/HITL_GATE.md` + GitHub PR review | Human must approve; agent must not merge |

## Project layout

```
url-shortener/
├── .specify/memory/constitution.md
├── specs/001-url-shortener/     # spec, plan, tasks
├── src/main/java/com/example/shortener/
│   ├── Base62Codec.java
│   ├── UrlValidator.java
│   ├── UrlShortenerService.java
│   ├── UrlShortenerServer.java  # POST /shorten, GET /{code}
│   └── sast/SastScanner.java    # constitution II gate
├── src/test/java/...            # JUnit 5
├── tools/agentic-loop.sh
├── .github/workflows/quality-gates.yml
├── .cursor/rules|hooks|skills   # agent loop + Spec Kit skills
├── hitl/HITL_GATE.md
└── pom.xml
```

## Run

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home   # if needed
mvn test
mvn -q exec:java -Dexec.mainClass=com.example.shortener.sast.SastScanner
./tools/agentic-loop.sh
mvn exec:java   # server on :8080
```

```bash
curl -X POST --data "https://example.com/some/long/path" http://localhost:8080/shorten
curl -i http://localhost:8080/<returned-code>
```

## Agentic loop

```
implement(task) -> compile -> test -> SAST -> {
    all_pass? -> HITL package, STOP
    fail?     -> patch -> loop (max 5)
}
```

Cursor `stop` hook runs the same gates (`loop_limit` 5). GitHub Actions runs them on every PR. Neither path merges.

## GitHub

CI workflow, PR template (HITL checklist), feature issue template, and CODEOWNERS are in `.github/`. Create the remote after `gh auth login` — see the Spec Kit doc above.
