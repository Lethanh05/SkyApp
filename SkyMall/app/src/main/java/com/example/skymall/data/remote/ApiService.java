package com.example.skymall.data.remote;

import com.example.skymall.data.model.Category;
import com.example.skymall.data.model.Product;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("api/categories") Call<List<Category>> getCategories();
    @GET("api/products")   Call<List<Product>>  getProducts(@Query("category_id") Integer categoryId,
                                                            @Query("q") String q,
                                                            @Query("page") Integer page,
                                                            @Query("limit") Integer limit);
    @GET("api/products/flashsale") Call<List<Product>> getFlashSale();
    @GET("api/store/products") // ví dụ: https://yourdomain.com/api/store/products?store_id=1&q=ao&page=1&limit=20
    Call<List<Product>> getStoreProducts(@Query("store_id") int storeId,
                                         @Query("q") String q,
                                         @Query("page") Integer page,
                                         @Query("limit") Integer limit);

}
