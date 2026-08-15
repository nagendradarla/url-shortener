# Tasks — URL Shortener Service

| ID | Task | Maps to | Status |
|----|------|---------|--------|
| T1 | Implement `UrlValidator` (scheme allow-list + host required) | FR-4 | done |
| T2 | Implement `Base62Codec` | FR-1, FR-2 | done |
| T3 | Implement `UrlShortenerService.shorten()` incl. idempotency | FR-1, FR-5 | done |
| T4 | Implement `UrlShortenerService.resolve()` incl. not-found case | FR-2, FR-3 | done |
| T5 | Generate short codes via `SecureRandom`, not `Random` | FR-6 | done |
| T6 | Write JUnit tests for T1–T5 (happy / edge / failure) | acceptance | done |
| T7 | Implement Java SAST scanner; remediate any HIGH/CRITICAL | acceptance | done |
| T8 | Implement HTTP layer (`POST /shorten`, `GET /{code}`) + tests | FR-1, FR-2, FR-3 | done |
| T9 | Package HITL review bundle (diff + test report + SAST report) | constitution III | done |
| T10 | Await human decision (Approve / Request Changes / Reject) | constitution III | **awaiting HITL** |
