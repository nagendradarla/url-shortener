package com.example.shortener;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/** Core service: shorten() / resolve() / clickCount() (T3, T4, 002-click-counts). */
public class UrlShortenerService {

    private static final Set<String> RESERVED_CODES = Set.of("shorten", "stats");

    private final ConcurrentHashMap<String, String> codeToUrl = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> urlToCode = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> clickCounts = new ConcurrentHashMap<>();

    // Iteration 2: SecureRandom used for short-code generation (fixes CWE-338, FR-6).
    private final SecureRandom random = new SecureRandom();

    public String shorten(String longUrl) {
        if (!UrlValidator.isValid(longUrl)) {
            throw new IllegalArgumentException("Invalid URL: " + longUrl);
        }

        String existing = urlToCode.get(longUrl);
        if (existing != null) {
            return existing; // FR-5 idempotency — do not reset count
        }

        String code;
        do {
            long n = random.nextLong() & Long.MAX_VALUE;
            code = Base62Codec.encode(n % 1_000_000_000L);
        } while (codeToUrl.containsKey(code) || RESERVED_CODES.contains(code));

        codeToUrl.put(code, longUrl);
        urlToCode.put(longUrl, code);
        clickCounts.put(code, new AtomicLong(0));
        return code;
    }

    public Optional<String> resolve(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        String destination = codeToUrl.get(code);
        if (destination == null) {
            return Optional.empty();
        }
        AtomicLong count = clickCounts.get(code);
        if (count != null) {
            count.incrementAndGet();
        }
        return Optional.of(destination);
    }

    /** Current click count for a known code. Does not increment. Empty if unknown/blank. */
    public Optional<Long> clickCount(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        AtomicLong count = clickCounts.get(code);
        if (count == null) {
            return Optional.empty();
        }
        return Optional.of(count.get());
    }
}
