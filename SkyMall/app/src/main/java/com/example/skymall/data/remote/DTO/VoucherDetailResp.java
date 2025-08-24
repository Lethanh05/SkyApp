package com.example.skymall.data.remote.DTO;

import com.example.skymall.data.model.Voucher;
import com.example.skymall.data.model.VoucherUsage;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VoucherDetailResp {
    @SerializedName("success")
    public boolean success;

    @SerializedName("data")
    public VoucherDetailData data;

    public static class VoucherDetailData {
        @SerializedName("voucher")
        public Voucher voucher;

        @SerializedName("usage_history")
        public List<VoucherUsage> usageHistory;
    }
}
