package com.pharmacy.util;

import java.time.LocalDate;
import java.util.Scanner;

public class ConsoleUtil {
    public static final String LINE_EQUALS = "============================================================";
    public static final String LINE_DASHES = "------------------------------------------------------------";

    public static void printHeader(String title) {
        System.out.println();
        System.out.println(LINE_EQUALS);
        System.out.println(centerText(title.toUpperCase(), 60));
        System.out.println(LINE_EQUALS);
    }

    public static void printSubHeader(String subtitle) {
        System.out.println();
        System.out.println(centerText(subtitle.toUpperCase(), 60));
        System.out.println(LINE_DASHES);
    }

    public static void printSection(String sectionName) {
        System.out.println();
        System.out.println(centerText(sectionName, 60));
        System.out.println(LINE_DASHES);
    }

    public static void printSuccess(String message) {
        System.out.println("[SUCCESS] " + message);
    }

    public static void printWarning(String message) {
        System.out.println("[WARNING] " + message);
    }

    public static void printError(String message) {
        System.out.println("[ERROR] " + message);
    }

    public static void printAlert(String message) {
        System.out.println("[ALERT] " + message);
    }

    public static void printInfo(String message) {
        System.out.println("[INFO] " + message);
    }

    public static String centerText(String text, int width) {
        if (text == null) text = "";
        if (text.length() >= width) return text;
        int leftPadding = (width - text.length()) / 2;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < leftPadding; i++) sb.append(" ");
        sb.append(text);
        return sb.toString();
    }

    public static int readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            if (!scanner.hasNextLine()) {
                return max; // Return default exit option if stream ended
            }
            String input = scanner.nextLine().trim();
            TeePrintStream.logInput(input);
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                printWarning("Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                printWarning("Invalid input. Please enter a valid numeric value.");
            }
        }
    }

    public static double readDouble(Scanner scanner, String prompt, double min) {
        while (true) {
            System.out.print(prompt);
            if (!scanner.hasNextLine()) {
                return min;
            }
            String input = scanner.nextLine().trim();
            TeePrintStream.logInput(input);
            try {
                double value = Double.parseDouble(input);
                if (value >= min) {
                    return value;
                }
                printWarning("Value must be at least " + min + ".");
            } catch (NumberFormatException e) {
                printWarning("Invalid input. Please enter a valid numeric amount.");
            }
        }
    }

    public static String readString(Scanner scanner, String prompt, boolean allowEmpty) {
        while (true) {
            System.out.print(prompt);
            if (!scanner.hasNextLine()) {
                return "";
            }
            String input = scanner.nextLine().trim();
            TeePrintStream.logInput(input);
            if (!input.isEmpty() || allowEmpty) {
                return input;
            }
            printWarning("This field cannot be blank.");
        }
    }

    public static LocalDate readDate(Scanner scanner, String prompt, boolean allowDefaultToday) {
        while (true) {
            System.out.print(prompt);
            if (!scanner.hasNextLine()) {
                return LocalDate.now();
            }
            String input = scanner.nextLine().trim();
            TeePrintStream.logInput(input);
            if (input.isEmpty() && allowDefaultToday) {
                return LocalDate.now();
            }
            LocalDate parsed = DateUtil.parse(input);
            if (parsed != null) {
                return parsed;
            }
            printWarning("Invalid date format. Please use YYYY-MM-DD (e.g. 2026-11-20).");
        }
    }

    public static void pause(Scanner scanner) {
        System.out.print("\nPress Enter to continue...");
        if (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            TeePrintStream.logInput(input);
        } else {
            System.out.println();
        }
    }
}
