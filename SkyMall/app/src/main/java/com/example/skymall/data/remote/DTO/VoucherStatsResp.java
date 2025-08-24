package com.example.skymall.data.remote.DTO;

import com.example.skymall.data.model.VoucherStats;
import com.google.gson.annotations.SerializedName;

public class VoucherStatsResp {
    @SerializedName("success")
    public boolean success;

    @SerializedName("error")
    public String error;

    @SerializedName("data")
    public VoucherStats data;
}
