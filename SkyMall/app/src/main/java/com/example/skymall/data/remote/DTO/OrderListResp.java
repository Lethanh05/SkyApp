package com.example.skymall.data.remote.DTO;

import java.util.List;

public class OrderListResp {
    public boolean success;
    public String error;
    public List<OrderDto> data;
    public Pagination pagination;

    public static class Pagination {
        public int page;
        public int limit;
        public int total;
    }
}
