package com.example.skymall.data.remote;

import android.content.Context;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://lequangthanh.click/";
    private static Retrofit retrofitWithAuth;
    private static Retrofit retrofitWithoutAuth;

    // Method cho API calls cần authentication
    public static ApiService create(Context context) {
        if (retrofitWithAuth == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(context))
                    .addInterceptor(logging)
                    .build();

            retrofitWithAuth = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitWithAuth.create(ApiService.class);
    }

    // Method cho API calls không cần authentication (login, register)
    public static ApiService createWithoutAuth() {
        if (retrofitWithoutAuth == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            retrofitWithoutAuth = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitWithoutAuth.create(ApiService.class);
    }

    // Method tương thích với code cũ
    public static Retrofit getRetrofitInstance() {
        return getRetrofitInstance(null);
    }

    public static Retrofit getRetrofitInstance(Context context) {
        if (context != null) {
            return retrofitWithAuth != null ? retrofitWithAuth :
                   (retrofitWithAuth = createRetrofitWithAuth(context));
        } else {
            return retrofitWithoutAuth != null ? retrofitWithoutAuth :
                   (retrofitWithoutAuth = createRetrofitWithoutAuth());
        }
    }

    private static Retrofit createRetrofitWithAuth(Context context) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(context))
                .addInterceptor(logging)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static Retrofit createRetrofitWithoutAuth() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    // Reset instances khi cần (ví dụ khi logout)
    public static void reset() {
        retrofitWithAuth = null;
        retrofitWithoutAuth = null;
    }
}
