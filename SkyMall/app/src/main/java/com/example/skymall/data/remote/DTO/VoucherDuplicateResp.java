package com.example.skymall.data.remote.DTO;

import com.google.gson.annotations.SerializedName;

public class VoucherDuplicateResp {
    @SerializedName("success")
    public boolean success;

    @SerializedName("error")
    public String error;

    @SerializedName("data")
    public VoucherDuplicateData data;

    public static class VoucherDuplicateData {
        @SerializedName("id")
        public int id;

        @SerializedName("code")
        public String code;

        @SerializedName("source_code")
        public String sourceCode;

        @SerializedName("message")
        public String message;
    }
}
