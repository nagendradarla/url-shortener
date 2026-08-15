# Specification — URL Shortener Service

## Why
Users need to turn long URLs into short, shareable links and have those
links reliably redirect back to the original destination.

## What (Functional Requirements)

- **FR-1**: Given a valid long URL, the system generates a short code and
  returns a shortened URL.
- **FR-2**: Given a previously shortened code, the system resolves it back
  to the original long URL.
- **FR-3**: Given an unknown short code, the system returns a clear
  "not found" result rather than failing silently or crashing.
- **FR-4**: Given an invalid input (empty string, malformed URL, or a
  dangerous scheme such as `javascript:` or `file:`), the system rejects
  the request with a clear error — it does not shorten it.
- **FR-5**: Shortening the same long URL twice returns the same short code
  (idempotent), avoiding duplicate entries.
- **FR-6**: Short codes must not be trivially guessable/enumerable by a
  third party (non-functional, security-relevant).

## Out of Scope (for this prototype)
- Persistent storage (in-memory is acceptable)
- User accounts / auth
- Custom aliases
- Click analytics

## Acceptance Criteria
- All FR-1..FR-6 covered by passing JUnit tests
- Zero HIGH/CRITICAL findings from the SAST gate
- HITL sign-off recorded before "merge"
