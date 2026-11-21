package com.pharmacy.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class DateUtil {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String format(LocalDate date) {
        if (date == null) return "N/A";
        return date.format(DATE_FORMATTER);
    }

    public static LocalDate parse(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static long daysUntil(LocalDate targetDate) {
        if (targetDate == null) return 0;
        return ChronoUnit.DAYS.between(LocalDate.now(), targetDate);
    }

    public static boolean isExpired(LocalDate targetDate) {
        if (targetDate == null) return false;
        return targetDate.isBefore(LocalDate.now());
    }

    public static boolean isNearExpiry(LocalDate targetDate, int daysThreshold) {
        if (targetDate == null) return false;
        long days = daysUntil(targetDate);
        return days >= 0 && days <= daysThreshold;
    }
}
