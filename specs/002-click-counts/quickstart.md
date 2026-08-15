# Quickstart: Click Counts on Resolve

Validate this feature after `/speckit.implement` using the running server. See [contracts/http-stats.md](./contracts/http-stats.md) and [data-model.md](./data-model.md).

## Prerequisites

- JDK 17+, Maven 3.9+
- Repository root

## Automated gates

```bash
mvn test
mvn -q exec:java -Dexec.mainClass=com.example.shortener.sast.SastScanner
```

Expect: all tests pass, including new count/resolve cases; SAST reports no HIGH/CRITICAL findings.

## Manual walkthrough

```bash
mvn exec:java
```

In another terminal:

```bash
CODE=$(curl -s -X POST --data "https://example.com/counted" http://localhost:8080/shorten)
curl -s http://localhost:8080/stats/$CODE          # expect 0
curl -s -o /dev/null -w "%{http_code}" -I http://localhost:8080/$CODE
curl -s http://localhost:8080/stats/$CODE          # expect 1
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/stats/doesNotExist
# expect 404
```

## Expected outcomes

| Check | Result |
|-------|--------|
| New code stats | `0` |
| After one successful follow | `1` |
| Unknown stats | HTTP 404, not `0` |
| Count lookup | Does not increment |
| Existing shorten / invalid URL | Unchanged |

Then produce `hitl/HITL_GATE.md` and open a PR. Do not merge without HITL.
