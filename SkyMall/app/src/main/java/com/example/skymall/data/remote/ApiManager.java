package com.example.skymall.data.remote;

import android.content.Context;

/**
 * Singleton class để quản lý ApiService một cách thống nhất
 * Giúp tránh việc tạo nhiều instance và đảm bảo tái sử dụng
 */
public class ApiManager {
    private static ApiManager instance;
    private ApiService apiService;
    private Context context;

    private ApiManager(Context context) {
        this.context = context.getApplicationContext();
        this.apiService = ApiClient.create(this.context);
    }

    public static synchronized ApiManager getInstance(Context context) {
        if (instance == null) {
            instance = new ApiManager(context);
        }
        return instance;
    }

    public ApiService getApiService() {
        // Kiểm tra nếu apiService bị null thì tạo lại
        if (apiService == null) {
            apiService = ApiClient.create(context);
        }
        return apiService;
    }

    /**
     * Tạo ApiService không cần authentication (cho login, register)
     */
    public ApiService getApiServiceWithoutAuth() {
        return ApiClient.createWithoutAuth();
    }

    /**
     * Reset ApiService khi cần (ví dụ khi logout)
     */
    public void reset() {
        apiService = null;
        ApiClient.reset();
    }

    /**
     * Kiểm tra xem ApiService đã được khởi tạo chưa
     */
    public boolean isInitialized() {
        return apiService != null;
    }
}
