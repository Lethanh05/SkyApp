package com.example.skymall.data.remote.DTO;

import java.util.List;

public class CartListResp {
    public boolean success;
    public String error;
    public int cart_id;
    public List<CartItem> items;

    public static class CartItem {
        public int productId;
        public String name;
        public double price;
        public int quantity;
        public String img;
    }
}
