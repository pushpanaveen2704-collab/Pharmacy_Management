package com.pharmacy.util;

import java.text.DecimalFormat;

public class CurrencyUtil {
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.00");

    public static String format(double amount) {
        return "Rs. " + CURRENCY_FORMAT.format(amount);
    }

    public static String formatPlain(double amount) {
        return String.format("%.2f", amount);
    }
}
