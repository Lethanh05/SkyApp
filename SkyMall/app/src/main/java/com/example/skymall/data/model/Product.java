package com.example.skymall.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Product implements Serializable {
    public int id;
    public String name;
    public String description;
    public double price;
    public String image;
    public int quantity;
    public int categoryId;
    public int discountPercentage; // For flash sale
    public int soldCount; // For popularity sorting
    public String createdAt;
    public String updatedAt;
    @SerializedName("img")
    public String img; // Trường nhận dữ liệu từ API

    public Product() {}

    public Product(int id, String name, String description, double price, String image, String img, int quantity, int categoryId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.img = img;
        this.quantity = quantity;
        this.categoryId = categoryId;
        this.discountPercentage = 0;
        this.soldCount = 0;
    }

    public double getDiscountedPrice() {
        if (discountPercentage > 0) {
            return price * (100 - discountPercentage) / 100.0;
        }
        return price;
    }

    public boolean isInStock() {
        return quantity > 0;
    }

    public boolean hasDiscount() {
        return discountPercentage > 0;
    }

    private static final String BASE = "https://lequangthanh.click";

    public static String normalizeImage(String img) {
        if (img == null || img.trim().isEmpty() || "null".equalsIgnoreCase(img.trim())) {
            return null;
        }
        img = img.trim();
        if (img.startsWith("http://") || img.startsWith("https://")) {
            return img;
        }
        if (img.startsWith("/")) {
            return BASE + img;
        }
        return BASE + "/" + img;
    }
}
