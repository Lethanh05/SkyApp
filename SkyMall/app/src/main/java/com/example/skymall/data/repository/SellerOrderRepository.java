package com.example.skymall.data.repository;

import android.content.Context;
import com.example.skymall.data.remote.ApiClient;
import com.example.skymall.data.remote.ApiService;
import com.example.skymall.data.remote.DTO.BaseResp;
import com.example.skymall.data.remote.DTO.OrderDetailResp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerOrderRepository {
    private ApiService apiService;

    public SellerOrderRepository(Context context) {
        this.apiService = ApiClient.create(context);
    }

    public interface OrderDetailCallback {
        void onSuccess(OrderDetailResp.OrderDetailData orderDetail);
        void onError(String error);
    }

    public interface UpdateStatusCallback {
        void onSuccess();
        void onError(String error);
    }

    public void getSellerOrderDetail(int orderId, OrderDetailCallback callback) {
        Call<OrderDetailResp> call = apiService.getSellerOrderDetail(orderId);
        call.enqueue(new Callback<OrderDetailResp>() {
            @Override
            public void onResponse(Call<OrderDetailResp> call, Response<OrderDetailResp> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OrderDetailResp resp = response.body();
                    if (resp.success) {
                        callback.onSuccess(resp.data);
                    } else {
                        callback.onError(resp.error != null ? resp.error : "Không thể tải chi tiết đơn hàng");
                    }
                } else {
                    callback.onError("Lỗi kết nối server: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<OrderDetailResp> call, Throwable t) {
                callback.onError("Lỗi mạng: " + t.getMessage());
            }
        });
    }

    public void updateOrderStatus(int orderId, String status, String note, UpdateStatusCallback callback) {
        Call<BaseResp> call = apiService.updateOrderStatus(orderId, status, note);
        call.enqueue(new Callback<BaseResp>() {
            @Override
            public void onResponse(Call<BaseResp> call, Response<BaseResp> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess();
                } else {
                    callback.onError("Cập nhật trạng thái thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BaseResp> call, Throwable t) {
                callback.onError("Lỗi mạng: " + t.getMessage());
            }
        });
    }
}
