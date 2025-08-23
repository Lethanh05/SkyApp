package com.example.skymall.data.remote.DTO;

public class OrderDto {
    public int id;
    public String date;                 // datetime
    public String status;               // pending/paid/processing/shipped/completed/cancelled
    public String receiver_name;        // snapshot địa chỉ trên Order
    public String receiver_phone;
    public String address_line;
    public String ward;
    public String district;
    public String province;
    public double subtotal;             // NOT NULL DEFAULT 0
    public double discount;             // NOT NULL DEFAULT 0
    public double shipping_fee;         // NOT NULL DEFAULT 0
    public double grand_total;          // generated: subtotal - discount + shipping_fee
    public String voucher_code;         // nullable
    public String created_at;
    public String updated_at;
}
