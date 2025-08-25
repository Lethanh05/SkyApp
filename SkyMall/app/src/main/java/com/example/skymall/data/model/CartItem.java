package com.example.skymall.data.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    public int productId;
    public String name;
    public double price;
    public String image;
    public int quantity;
    public String img;
    public boolean selected;

    public CartItem() {}

    public CartItem(int productId, String name, double price, String image, int quantity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.image = image;
        this.quantity = quantity;
        this.selected = false;
    }

    public double getTotalPrice() {
        return price * quantity;
    }
}
