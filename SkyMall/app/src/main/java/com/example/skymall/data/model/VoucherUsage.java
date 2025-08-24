package com.example.skymall.data.model;

import com.google.gson.annotations.SerializedName;

public class VoucherUsage {
    @SerializedName("id")
    public int id;

    @SerializedName("voucherId")
    public int voucherId;

    @SerializedName("userId")
    public int userId;

    @SerializedName("orderId")
    public int orderId;

    @SerializedName("used_at")
    public String usedAt;

    @SerializedName("discount_amount")
    public double discountAmount;

    @SerializedName("user_name")
    public String userName;

    @SerializedName("user_email")
    public String userEmail;

    @SerializedName("order_total")
    public Double orderTotal;

    // Constructor
    public VoucherUsage() {}
}
