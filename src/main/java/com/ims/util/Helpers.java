package com.ims.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.servlet.http.HttpSession;

public final class Helpers {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private Helpers() {
    }

    public static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static String safe(String value) {
        return value == null ? "" : value.trim();
    }

    public static String nvl(String value, String fallback) {
        return hasText(value) ? value.trim() : fallback;
    }

    public static boolean isAuthenticated(HttpSession session) {
        Object authenticated = session.getAttribute("authenticated");
        return authenticated instanceof Boolean && (Boolean) authenticated;
    }

    public static String formatDateTime(LocalDateTime value) {
        return value == null ? "" : value.format(DATE_TIME_FORMATTER);
    }
}
