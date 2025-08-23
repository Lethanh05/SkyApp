package com.example.skymall.ui.voucher;

import android.content.Context;

import com.example.skymall.R;

import java.util.HashMap;
import java.util.Map;

public class VoucherUtils {

    private static final Map<String, Integer> ERROR_MESSAGES = new HashMap<>();

    static {
        ERROR_MESSAGES.put("voucher_not_found", R.string.error_voucher_not_found);
        ERROR_MESSAGES.put("voucher_not_started", R.string.error_voucher_not_started);
        ERROR_MESSAGES.put("voucher_expired", R.string.error_voucher_expired);
        ERROR_MESSAGES.put("min_order_value_not_met", R.string.error_min_order_value_not_met);
        ERROR_MESSAGES.put("voucher_usage_limit_exceeded", R.string.error_voucher_usage_limit_exceeded);
        ERROR_MESSAGES.put("user_usage_limit_exceeded", R.string.error_user_usage_limit_exceeded);
        ERROR_MESSAGES.put("voucher_invalid", R.string.error_voucher_invalid);

        // Voucher use errors
        ERROR_MESSAGES.put("unauthorized", R.string.error_unauthorized);
        ERROR_MESSAGES.put("missing_parameters", R.string.error_missing_parameters);
        ERROR_MESSAGES.put("order_not_found", R.string.error_order_not_found);
        ERROR_MESSAGES.put("voucher_already_used", R.string.error_voucher_already_used);
        ERROR_MESSAGES.put("prepare_error", R.string.error_server_error);
        ERROR_MESSAGES.put("insert_error", R.string.error_server_error);
    }

    public static String getErrorMessage(Context context, String errorCode) {
        Integer stringRes = ERROR_MESSAGES.get(errorCode);
        if (stringRes != null) {
            return context.getString(stringRes);
        }
        return errorCode; // Fallback to error code if no translation found
    }

    public static String formatErrorMessages(Context context, String[] errorDetails) {
        if (errorDetails == null || errorDetails.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < errorDetails.length; i++) {
            if (i > 0) sb.append("\n");
            sb.append("• ").append(getErrorMessage(context, errorDetails[i]));
        }
        return sb.toString();
    }

    public static String formatDiscountAmount(double amount) {
        return String.format("%.0fđ", amount);
    }

    public static String formatOrderValue(double amount) {
        return String.format("%.0fđ", amount);
    }
}
