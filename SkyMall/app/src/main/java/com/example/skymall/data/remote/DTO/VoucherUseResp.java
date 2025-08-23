package com.example.skymall.data.remote.DTO;

import com.google.gson.annotations.SerializedName;

public class VoucherUseResp {
    @SerializedName("success")
    public boolean success;

    @SerializedName("message")
    public String message;

    @SerializedName("error")
    public String error;

    @SerializedName("msg")
    public String msg; // Thông điệp chi tiết từ server
}
