package com.example.skymall.ui.seller;

public class Product {
    public int id;
    public String name;
    public double price;        // decimal(12,2)
    public String img;          // URL ảnh (có thể null)
    public String badge;        // optional label
    public String description;
    public Integer categoryId;
    public String categoryName;// có thể null
    public int stock;
    public int is_active;       // 1/0
    public String created_at;
    public String updated_at;

    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public String getImage() { return img; }
}