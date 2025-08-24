package com.example.skymall.data.model;

import java.io.Serializable;

public class Voucher implements Serializable {
    public int id;
    public String code;
    public String title;
    public String description;
    public String discountType; // "percentage" or "fixed"
    public double discountValue;
    public double maxDiscountAmount;
    public double minOrderAmount;
    public String expiryDate;
    public boolean isActive;
    public int usageLimit;
    public int usedCount;
    public String createdAt;

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
