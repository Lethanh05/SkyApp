package com.example.skymall.data.model;

public class Voucher {
    public String id;
    public String title;          // ví dụ "Giảm 30k"
    public String note;           // "Áp dụng toàn shop"
    public long   startAt;        // millis
    public long   endAt;          // millis
    public int    minSpend;       // VND
    public boolean used;          // đã dùng?
    public boolean expired;       // hết hạn?
}
