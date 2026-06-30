package com.oneplatform.log.utils;

public final class TokenMaskUtil {

    private TokenMaskUtil() {}

    public static String truncateToken(String token) {
        if (token == null || token.isBlank()) return token;

        String raw = token.startsWith("Bearer ") ? token.substring(7) : token;

        if (raw.length() <= 12) return "***";

        return raw.substring(0, 6) + "..." + raw.substring(raw.length() - 4)
                + " (len=" + raw.length() + ")";
    }
}
