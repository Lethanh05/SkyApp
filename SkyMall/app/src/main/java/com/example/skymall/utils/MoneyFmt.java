package com.example.skymall.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class MoneyFmt {
    private static final NumberFormat VN = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    public static String vnd(double x) { return VN.format(x); }
}
