package com.example.skymall.data.remote.DTO;

import com.example.skymall.data.model.Product;
import java.util.List;

public class ProductListResp {
    public boolean success;
    public List<Product> data;
    public int page;
    public int limit;
    public String error;
}
