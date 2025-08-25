package com.example.skymall.data.remote.DTO;

import com.example.skymall.data.model.Product;
import java.util.List;

public class CartResp {
    public List<Item> items;
    public double subtotal;
    public int count;

    public static class Item {
        public int productId;
        public String name;
        public String img;
        public double price;
        public int quantity;
        public int stock;
        public double line_total;
    }
}
