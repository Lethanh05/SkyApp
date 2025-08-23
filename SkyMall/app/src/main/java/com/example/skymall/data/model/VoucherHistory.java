package com.example.skymall.data.model;

import com.google.gson.annotations.SerializedName;

public class VoucherHistory {
    @SerializedName("id")
    public int id;

    @SerializedName("voucher_code")
    public String voucherCode;

    @SerializedName("voucher_type")
    public String voucherType;

    @SerializedName("voucher_value")
    public double voucherValue;

    @SerializedName("order_id")
    public Integer orderId; // có thể null

    @SerializedName("order_total")
    public Double orderTotal; // có thể null

    @SerializedName("used_at")
    public String usedAt;

    // Helper methods
    public String getDisplayValue() {
        if ("percentage".equals(voucherType)) {
            return (int)voucherValue + "%";
        } else {
            return String.format("%.0fđ", voucherValue);
        }
    }

    public String getFormattedOrderTotal() {
        if (orderTotal != null) {
            return String.format("%.0fđ", orderTotal);
        }
        return "N/A";
    }
}
