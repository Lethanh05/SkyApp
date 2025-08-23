package com.example.skymall.data.remote.DTO;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VoucherCheckResp {
    @SerializedName("success")
    public boolean success;

    @SerializedName("voucher")
    public VoucherCheckData voucher;

    @SerializedName("error")
    public String error;

    @SerializedName("details")
    public List<String> details; // Chi tiết lỗi validation

    public static class VoucherCheckData {
        @SerializedName("id")
        public int id;

        @SerializedName("code")
        public String code;

        @SerializedName("type")
        public String type;

        @SerializedName("value")
        public double value;

        @SerializedName("min_order_value")
        public double minOrderValue;

        @SerializedName("discount_amount")
        public double discountAmount;
    }
}
