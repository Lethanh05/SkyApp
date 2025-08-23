package com.example.skymall.data.remote.DTO;

import com.example.skymall.data.model.VoucherHistory;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VoucherHistoryResp {
    @SerializedName("success")
    public boolean success;

    @SerializedName("history")
    public List<VoucherHistory> history;

    @SerializedName("pagination")
    public Pagination pagination;

    @SerializedName("error")
    public String error;

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
