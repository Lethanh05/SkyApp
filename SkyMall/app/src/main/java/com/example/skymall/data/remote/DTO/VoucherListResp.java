package com.example.skymall.data.remote.DTO;

import com.example.skymall.data.model.Voucher;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VoucherListResp {
    @SerializedName("success")
    public boolean success;

    @SerializedName("vouchers")
    public List<Voucher> vouchers;

    @SerializedName("pagination")
    public Pagination pagination;

    @SerializedName("error")
    public String error;

    @SerializedName("msg")
    public String message;

    public static class Pagination {
        @SerializedName("current_page")
        public int currentPage;

        @SerializedName("total_items")
        public int totalItems;

        @SerializedName("total_pages")
        public int totalPages;

        @SerializedName("items_per_page")
        public int itemsPerPage;
    }
}
