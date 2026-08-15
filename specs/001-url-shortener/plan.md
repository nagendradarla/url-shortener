# Plan — URL Shortener Service

## Stack
- Java 21, zero external runtime dependencies (uses `com.sun.net.httpserver`
  for the HTTP layer) so the prototype compiles/runs anywhere a JDK exists.
- JUnit 5 for tests, executed with `mvn test`.
- In-repo Java SAST scanner (`com.example.shortener.sast.SastScanner`) as the
  Checkmarx stand-in. No Python in the product or quality gates.

## Components
1. `Base62Codec` — encodes a numeric ID into a short, non-sequential-looking
   token.
2. `UrlValidator` — allow-lists `http`/`https` schemes, requires a host,
   rejects everything else (blocks `javascript:`, `file:`, `data:` etc. →
   open-redirect/SSRF defense, FR-4).
3. `UrlShortenerService` — core logic: shorten(), resolve(), idempotency,
   short-code generation using `SecureRandom` (FR-6).
4. `UrlShortenerServer` — thin HTTP layer: `POST /shorten`, `GET /{code}`.
5. `SastScanner` — constitution II security gate.

## Quality Gates (enforced every loop iteration)
1. Build/compile (`mvn test` includes compile)
2. Unit tests (JUnit 5) — must cover FR-1..FR-6
3. SAST scan — must be clean of HIGH/CRITICAL
4. HITL review — human approves/rejects before merge (GitHub PR review)

## Agentic Loop
```
implement(task) -> compile -> test -> security_scan -> {
    all_pass?  -> present HITL package, STOP for human
    fail?      -> diagnose failure -> patch -> loop (max 5 iterations)
}
```

Run locally: `./tools/agentic-loop.sh`
On GitHub: `.github/workflows/quality-gates.yml`
