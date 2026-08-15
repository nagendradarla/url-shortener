package com.example.shortener;

/** Encodes non-negative longs into a compact Base62 string (T2). */
public final class Base62Codec {

    private static final String ALPHABET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private Base62Codec() { }

    public static String encode(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("value must be non-negative");
        }
        if (value == 0) {
            return String.valueOf(ALPHABET.charAt(0));
        }
        StringBuilder sb = new StringBuilder();
        long v = value;
        while (v > 0) {
            sb.append(ALPHABET.charAt((int) (v % 62)));
            v /= 62;
        }
        return sb.reverse().toString();
    }
}
