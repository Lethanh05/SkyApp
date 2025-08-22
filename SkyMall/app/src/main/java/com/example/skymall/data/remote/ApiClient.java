package com.example.skymall.data.remote;

import android.content.Context;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;
    public static Retrofit get(String baseUrl){
        if (retrofit == null){
            HttpLoggingInterceptor log = new HttpLoggingInterceptor();
            log.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(log).build();
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://lequangthanh.click/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
    public static ApiService create(Context ctx){
        OkHttpClient ok = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(ctx))
                .build();
        return new Retrofit.Builder()
                .baseUrl("https://lequangthanh.click/") // domain thật của bạn
                .client(ok)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ApiService.class);
    }
}
