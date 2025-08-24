package com.example.skymall.data.model;

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

    public Product() {}

    public Product(int id, String name, String description, double price, String image, int quantity, int categoryId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
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
}
