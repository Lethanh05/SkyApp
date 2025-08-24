package com.example.skymall.data.remote.DTO;

import java.util.List;

public class OrderDetailResp {
    public boolean success;
    public String error;
    public OrderDetailData data;

    public static class OrderDetailData {
        public OrderInfo order;
        public List<OrderItem> items;
        public List<OrderHistory> status_history;
    }

    public static class OrderInfo {
        public int id;
        public int userId;
        public String date;
        public String status;
        public String receiver_name;
        public String receiver_phone;
        public String address_line;
        public String ward;
        public String district;
        public String province;
        public double subtotal;
        public double discount;
        public double shipping_fee;
        public double grand_total;
        public String voucher_code;
        public String created_at;
        public String updated_at;
        public String user_name;
        public String user_email;
        public String user_phone;
    }

    public static class OrderItem {
        public int productId;
        public String product_name;
        public double price;
        public int quantity;
        public String img;
        public String description;
        public int categoryId;
    }

    public static class OrderHistory {
        public int orderId;
        public String old_status;
        public String new_status;
        public String changed_at;
        public String note;
    }
}
