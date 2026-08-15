package com.example.shortener;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Maps to T1 / FR-4. Happy, boundary, and failure cases. */
class UrlValidatorTest {

    @Test
    void acceptsHttpAndHttpsUrls() {
        assertTrue(UrlValidator.isValid("https://example.com/path"));
        assertTrue(UrlValidator.isValid("http://example.com"));
    }

    @Test
    void rejectsBlankAndNull() {
        assertFalse(UrlValidator.isValid(null));
        assertFalse(UrlValidator.isValid(""));
        assertFalse(UrlValidator.isValid("   "));
    }

    @Test
    void rejectsDangerousAndMalformedUrls() {
        assertFalse(UrlValidator.isValid("javascript:alert(1)"));
        assertFalse(UrlValidator.isValid("file:///etc/passwd"));
        assertFalse(UrlValidator.isValid("data:text/html,hi"));
        assertFalse(UrlValidator.isValid("not a url"));
        assertFalse(UrlValidator.isValid("https://"));
    }
}
