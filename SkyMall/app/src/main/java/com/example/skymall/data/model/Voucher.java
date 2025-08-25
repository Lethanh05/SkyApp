package com.example.skymall.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Voucher implements Serializable {
    @SerializedName("id")
    public int id;
    @SerializedName("code")
    public String code;
    @SerializedName("description")
    public String description;
    @SerializedName("type")
    public String discountType; // "percent" or "amount"
    @SerializedName("value")
    public double discountValue;
    @SerializedName("min_order_value")
    public double minOrderAmount;
    @SerializedName("start_date")
    public String startDate;
    @SerializedName("end_date")
    public String expiryDate;
    @SerializedName("usage_limit")
    public int usageLimit;
    @SerializedName("per_user_limit")
    public int perUserLimit;
    @SerializedName("total_used")
    public int usedCount;
    @SerializedName("created_at")
    public String createdAt;
    public String image; // Thêm trường image để lưu link ảnh voucher
    @SerializedName("is_active")
    public boolean isActive;
    @SerializedName("max_discount_amount")
    public double maxDiscountAmount;

    public Voucher() {}

    public boolean isValid() {
        return isActive && usedCount < usageLimit;
    }

    public boolean canApplyToOrder(double orderAmount) {
        return isValid() && orderAmount >= minOrderAmount;
    }

    public double calculateDiscount(double orderAmount) {
        if (!canApplyToOrder(orderAmount)) {
            return 0;
        }

        double discount = 0;
        if ("percentage".equals(discountType)) {
            discount = orderAmount * discountValue / 100;
            if (maxDiscountAmount > 0) {
                discount = Math.min(discount, maxDiscountAmount);
            }
        } else {
            discount = discountValue;
        }

        return Math.min(discount, orderAmount);
    }
}
