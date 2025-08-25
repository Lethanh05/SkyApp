package com.example.skymall.data.remote.DTO;

import com.example.skymall.data.model.Product;
import java.util.List;
import android.util.Log;
import com.google.gson.Gson;

public class ProductListResp {
    public boolean success;
    public List<Product> data;
    public int page;
    public int limit;
    public int total;
    public String error;

    public ProductListResp() {
        Log.d("ProductListResp", "Raw JSON response: " + new Gson().toJson(this));
    }
}
