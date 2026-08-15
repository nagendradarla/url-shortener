package com.example.shortener;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Core service: shorten() / resolve() (T3, T4). */
public class UrlShortenerService {

    private final ConcurrentHashMap<String, String> codeToUrl = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> urlToCode = new ConcurrentHashMap<>();

    // Iteration 2: SecureRandom used for short-code generation (fixes CWE-338, FR-6).
    private final SecureRandom random = new SecureRandom();

    public String shorten(String longUrl) {
        if (!UrlValidator.isValid(longUrl)) {
            throw new IllegalArgumentException("Invalid URL: " + longUrl);
        }

        String existing = urlToCode.get(longUrl);
        if (existing != null) {
            return existing; // FR-5 idempotency
        }

        String code;
        do {
            long n = random.nextLong() & Long.MAX_VALUE;
            code = Base62Codec.encode(n % 1_000_000_000L);
        } while (codeToUrl.containsKey(code));

        codeToUrl.put(code, longUrl);
        urlToCode.put(longUrl, code);
        return code;
    }

    public Optional<String> resolve(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(codeToUrl.get(code));
    }
}
