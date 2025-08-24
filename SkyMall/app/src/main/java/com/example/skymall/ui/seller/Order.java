package com.example.skymall.ui.seller;

public class Order {
    private int id;
    private String customerName;
    private String customerPhone;
    private String createdAt;
    private String status; // PENDING / CONFIRMED
    private double totalAmount;

    public Order(int id, String customerName, String customerPhone,
                 String createdAt, String status, double totalAmount) {
        this.id = id;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.createdAt = createdAt;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    public int getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public String getCreatedAt() { return createdAt; }
    public String getStatus() { return status; }
    public double getTotalAmount() { return totalAmount; }
}
