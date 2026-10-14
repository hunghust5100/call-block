package com.example.callblock.utils;

public class PhoneNumberUtils {

    /**
     * Standardizes phone number string to 0x format:
     * e.g., "+84 24 7300 8899" -> "02473008899"
     * e.g., "1900 66xx" -> "190066"
     */
    public static String normalize(String raw) {
        if (raw == null) return "";

        // Remove spaces, hyphens, dots, parentheses
        String cleaned = raw.replaceAll("[\\s\\-().]", "");

        // Remove trailing x / X / * for prefix patterns
        cleaned = cleaned.replaceAll("[xX*]+$", "");

        // Handle Vietnam country code +84 or 84
        if (cleaned.startsWith("+84")) {
            cleaned = "0" + cleaned.substring(3);
        } else if (cleaned.startsWith("84") && cleaned.length() >= 10) {
            cleaned = "0" + cleaned.substring(2);
        }

        return cleaned;
    }

    /**
     * Formats phone number for display e.g. "024 7300 8899"
     */
    public static String formatDisplay(String number) {
        if (number == null || number.isEmpty()) return "";
        String normalized = normalize(number);
        if (normalized.length() == 11 && normalized.startsWith("02")) {
            // Landline e.g. 024 7300 8899
            return normalized.substring(0, 3) + " " + normalized.substring(3, 7) + " " + normalized.substring(7);
        } else if (normalized.length() == 10 && normalized.startsWith("0")) {
            // Mobile e.g. 090 123 4567
            return normalized.substring(0, 3) + " " + normalized.substring(3, 6) + " " + normalized.substring(6);
        }
        return number;
    }

    /**
     * Checks if input raw pattern is a prefix pattern (e.g. contains 'x', 'X', '*', or is a short toll prefix like '1900').
     */
    public static boolean isPrefixPattern(String raw) {
        if (raw == null) return false;
        String lower = raw.toLowerCase();
        return lower.contains("x") || lower.contains("*") || (raw.trim().length() <= 6 && !raw.startsWith("0"));
    }
}
