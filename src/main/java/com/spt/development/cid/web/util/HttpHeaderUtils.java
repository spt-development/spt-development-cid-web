package com.spt.development.cid.web.util;

import java.util.regex.Pattern;

/**
 * Utility methods related to HTTP headers.
 */
public final class HttpHeaderUtils {
    private static final String EMPTY = "";

    private static final Pattern SANITIZE_PATTERN = Pattern.compile("[\\r\\n]");

    private HttpHeaderUtils() {}

    /**
     * Removes carriage return (\r) and newline (\n) characters from header (string) values.
     *
     * @param value the header value to sanitize.
     *
     * @return the sanitized value.
     */
    public static String sanitizeHeaderValue(String value) {
        return SANITIZE_PATTERN.matcher(value).replaceAll(EMPTY);
    }
}
