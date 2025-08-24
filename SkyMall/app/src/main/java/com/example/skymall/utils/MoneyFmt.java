package com.example.skymall.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class MoneyFmt {

    private static final DecimalFormat formatter = new DecimalFormat("#,###");
    private static final NumberFormat vietnameseFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    /**
     * Format double value to Vietnamese currency string
     * @param amount the amount to format
     * @return formatted string with "đ" suffix
     */
    public static String format(double amount) {
        if (amount == 0) {
            return "0đ";
        }

        // Round to nearest integer for currency display
        long roundedAmount = Math.round(amount);
        return formatter.format(roundedAmount) + "đ";
    }

    /**
     * Format long value to Vietnamese currency string
     * @param amount the amount to format
     * @return formatted string with "đ" suffix
     */
    public static String format(long amount) {
        if (amount == 0) {
            return "0đ";
        }
        return formatter.format(amount) + "đ";
    }

    /**
     * Format int value to Vietnamese currency string
     * @param amount the amount to format
     * @return formatted string with "đ" suffix
     */
    public static String format(int amount) {
        if (amount == 0) {
            return "0đ";
        }
        return formatter.format(amount) + "đ";
    }

    /**
     * Format with custom currency symbol
     * @param amount the amount to format
     * @param currency the currency symbol
     * @return formatted string with custom currency
     */
    public static String format(double amount, String currency) {
        if (amount == 0) {
            return "0" + currency;
        }

        long roundedAmount = Math.round(amount);
        return formatter.format(roundedAmount) + currency;
    }

    /**
     * Parse formatted currency string back to double
     * @param formattedAmount the formatted string (e.g., "100,000đ")
     * @return parsed double value
     */
    public static double parse(String formattedAmount) {
        if (formattedAmount == null || formattedAmount.isEmpty()) {
            return 0.0;
        }

        // Remove currency symbol and commas
        String cleaned = formattedAmount.replace("đ", "").replace(",", "").trim();

        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Format double value to Vietnamese currency string (alias for format)
     * @param amount the amount to format
     * @return formatted string with "đ" suffix
     */
    public static String vnd(double amount) {
        return format(amount);
    }

    /**
     * Format long value to Vietnamese currency string (alias for format)
     * @param amount the amount to format
     * @return formatted string with "đ" suffix
     */
    public static String vnd(long amount) {
        return format(amount);
    }

    /**
     * Format int value to Vietnamese currency string (alias for format)
     * @param amount the amount to format
     * @return formatted string with "đ" suffix
     */
    public static String vnd(int amount) {
        return format(amount);
    }
}
