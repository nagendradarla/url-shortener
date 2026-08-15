package com.example.shortener;

import java.net.URI;

/**
 * Validates long URLs before they are shortened (T1 / FR-4).
 * Allow-lists http/https and requires a host to block open-redirect/SSRF schemes.
 */
public final class UrlValidator {

    private UrlValidator() { }

    public static boolean isValid(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        try {
            URI uri = URI.create(url.trim());
            String scheme = uri.getScheme();
            String host = uri.getHost();
            boolean allowedScheme = "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
            return allowedScheme && host != null && !host.isBlank();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
