package com.example.skymall.data.remote;

import com.example.skymall.data.model.Category;
import com.example.skymall.data.model.Product;
import com.example.skymall.data.remote.DTO.AuthResp;
import com.example.skymall.data.remote.DTO.MeResp;
import com.example.skymall.data.remote.DTO.OrderDto;
import com.example.skymall.data.remote.DTO.OrderItemDto;
import com.example.skymall.data.remote.DTO.OrderStatusEventDto;
import com.example.skymall.data.remote.DTO.UploadResp;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("api/product/categories")
    Call<List<Category>> getCategories();

    @GET("api/product/products")
    Call<List<Product>> getProducts(
            @Query("category_id") Integer categoryId,
            @Query("q")           String q,
            @Query("page")        Integer page,
            @Query("limit")       Integer limit
    );

    @GET("api/product/flashsale")
    Call<List<Product>> getFlashSale();

    @GET("api/store/products")
    Call<List<Product>> getStoreProducts(
            @Query("store_id") int storeId,
            @Query("q")        String q,
            @Query("page")     Integer page,
            @Query("limit")    Integer limit
    );

    @FormUrlEncoded
    @POST("api/auth/register")
    Call<AuthResp> register(
            @Field("name")     String name,
            @Field("email")    String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("api/auth/login")
    Call<AuthResp> login(
            @Field("email")    String email,
            @Field("password") String password
    );

    @GET("api/auth/me")
    Call<MeResp> me();

    @POST("api/auth/logout")
    Call<MeResp> logout();

    @FormUrlEncoded
    @POST("api/user/update_profile.php")
    Call<MeResp> updateProfile(
            @Field("name")       String name,
            @Field("phone")      String phone,
            @Field("email")      String email,
            @Field("birthDate")  String birthDate,
            @Field("gender")     String gender
    );

    @Multipart
    @POST("api/user/update_profile.php")
    Call<UploadResp> uploadAvatar(@Part MultipartBody.Part avatar);

    @GET("orders")
    Call<List<OrderDto>> getOrders(@Query("status") String status, @Query("page") Integer page);

    // GET /orders/{id}
    @GET("orders/{id}")
    Call<OrderDto> getOrder(@Path("id") int id);

    // GET /orders/{id}/items
    @GET("orders/{id}/items")
    Call<List<OrderItemDto>> getOrderItems(@Path("id") int id);

    // GET /orders/{id}/timeline
    @GET("orders/{id}/timeline")
    Call<List<OrderStatusEventDto>> getOrderTimeline(@Path("id") int id);
}
