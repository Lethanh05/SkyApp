package com.example.skymall.data.model;

import com.google.gson.annotations.SerializedName;

public class Voucher {
    @SerializedName("id")
    public int id;

    @SerializedName("code")
    public String code;

    @SerializedName("type")
    public String type; // "percentage" hoặc "fixed"

    @SerializedName("value")
    public double value;

    @SerializedName("min_order_value")
    public double minOrderValue;

    @SerializedName("start_date")
    public String startDate;

    @SerializedName("end_date")
    public String endDate;

    @SerializedName("usage_limit")
    public Integer usageLimit; // có thể null

    @SerializedName("per_user_limit")
    public Integer perUserLimit; // có thể null

    @SerializedName("total_used")
    public int totalUsed;

    @SerializedName("user_used")
    public int userUsed;

    @SerializedName("remaining")
    public Integer remaining; // có thể null

    // Constructor
    public Voucher() {}

    // Helper methods
    public boolean isExpired() {
        if (endDate == null) return false;
        // Implement date comparison logic if needed
        return false;
    }

    public boolean canUse() {
        return !isExpired() &&
               (usageLimit == null || totalUsed < usageLimit) &&
               (perUserLimit == null || userUsed < perUserLimit);
    }

    public String getDisplayValue() {
        if ("percentage".equals(type)) {
            return (int)value + "%";
        } else {
            return String.format("%.0fđ", value);
        }
    }
}
