package com.example.shortener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Covers spec.md FR-1..FR-6. Maps to tasks.md T6. */
class UrlShortenerServiceTest {

    private UrlShortenerService service;

    @BeforeEach
    void setUp() {
        service = new UrlShortenerService();
    }

    @Test // FR-1
    void shortenReturnsNonEmptyCodeForValidUrl() {
        String code = service.shorten("https://example.com/some/long/path");
        assertNotNull(code);
        assertFalse(code.isBlank());
        assertTrue(code.matches("[0-9A-Za-z]+"));
    }

    @Test // FR-2
    void resolveReturnsOriginalUrlForKnownCode() {
        String longUrl = "https://example.com/page";
        String code = service.shorten(longUrl);
        Optional<String> resolved = service.resolve(code);
        assertTrue(resolved.isPresent());
        assertEquals(longUrl, resolved.get());
    }

    @Test // FR-3
    void resolveReturnsEmptyForUnknownCode() {
        Optional<String> resolved = service.resolve("doesNotExist");
        assertTrue(resolved.isEmpty());
    }

    @Test // FR-3 boundary
    void resolveReturnsEmptyForBlankCode() {
        assertTrue(service.resolve("").isEmpty());
        assertTrue(service.resolve(null).isEmpty());
    }

    @Test // FR-4 (blank input)
    void shortenRejectsBlankInput() {
        assertThrows(IllegalArgumentException.class, () -> service.shorten("   "));
    }

    @Test // FR-4 (dangerous scheme)
    void shortenRejectsDangerousScheme() {
        assertThrows(IllegalArgumentException.class,
                () -> service.shorten("javascript:alert(1)"));
    }

    @Test // FR-4 (dangerous scheme)
    void shortenRejectsFileScheme() {
        assertThrows(IllegalArgumentException.class,
                () -> service.shorten("file:///etc/passwd"));
    }

    @Test // FR-5
    void shorteningSameUrlTwiceReturnsSameCode() {
        String longUrl = "https://example.com/idempotent";
        String code1 = service.shorten(longUrl);
        String code2 = service.shorten(longUrl);
        assertEquals(code1, code2);
    }

    @Test // FR-6
    void shortCodesAreNotSequentialAndUseSecureRandom() throws Exception {
        var randomField = UrlShortenerService.class.getDeclaredField("random");
        randomField.setAccessible(true);
        assertInstanceOf(SecureRandom.class, randomField.get(service));

        Set<String> codes = new HashSet<>();
        for (int i = 0; i < 8; i++) {
            codes.add(service.shorten("https://example.com/page-" + i));
        }
        assertEquals(8, codes.size());
        assertNotEquals("1", service.shorten("https://example.com/other"));
    }

    @Test // FR-001
    void newShortenReportsClickCountZero() {
        String code = service.shorten("https://example.com/counted");
        Optional<Long> count = service.clickCount(code);
        assertTrue(count.isPresent());
        assertEquals(0L, count.get());
    }

    @Test // FR-002
    void resolveIncrementsClickCount() {
        String code = service.shorten("https://example.com/inc");
        assertEquals(0L, service.clickCount(code).orElseThrow());
        service.resolve(code);
        assertEquals(1L, service.clickCount(code).orElseThrow());
        service.resolve(code);
        assertEquals(2L, service.clickCount(code).orElseThrow());
    }

    @Test // FR-003
    void blankOrNullResolveDoesNotIncrement() {
        String code = service.shorten("https://example.com/blank-resolve");
        service.resolve("   ");
        service.resolve(null);
        assertEquals(0L, service.clickCount(code).orElseThrow());
    }

    @Test // FR-004
    void clickCountDoesNotIncrement() {
        String code = service.shorten("https://example.com/lookup");
        assertEquals(0L, service.clickCount(code).orElseThrow());
        assertEquals(0L, service.clickCount(code).orElseThrow());
        service.resolve(code);
        assertEquals(1L, service.clickCount(code).orElseThrow());
        assertEquals(1L, service.clickCount(code).orElseThrow());
    }

    @Test // FR-003, FR-005
    void unknownOrBlankClickCountIsEmptyAndDoesNotCreateCounter() {
        assertTrue(service.clickCount("doesNotExist").isEmpty());
        assertTrue(service.clickCount("").isEmpty());
        assertTrue(service.clickCount(null).isEmpty());
        assertTrue(service.resolve("doesNotExist").isEmpty());
        assertTrue(service.clickCount("doesNotExist").isEmpty());
    }

    @Test // idempotent shorten shares count
    void repeatingShortenDoesNotResetCount() {
        String url = "https://example.com/shared-count";
        String code = service.shorten(url);
        service.resolve(code);
        assertEquals(code, service.shorten(url));
        assertEquals(1L, service.clickCount(code).orElseThrow());
    }

    @Test // concurrent increments
    void concurrentResolvesDoNotLoseIncrements() throws Exception {
        String code = service.shorten("https://example.com/concurrent");
        int threads = 20;
        int perThread = 5;
        var pool = java.util.concurrent.Executors.newFixedThreadPool(threads);
        try {
            var latch = new java.util.concurrent.CountDownLatch(threads);
            for (int i = 0; i < threads; i++) {
                pool.submit(() -> {
                    try {
                        for (int n = 0; n < perThread; n++) {
                            service.resolve(code);
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }
            assertTrue(latch.await(5, java.util.concurrent.TimeUnit.SECONDS));
        } finally {
            pool.shutdown();
            assertTrue(pool.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS));
        }
        assertEquals((long) threads * perThread, service.clickCount(code).orElseThrow());
    }
}
