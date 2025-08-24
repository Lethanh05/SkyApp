package com.example.skymall.data.repository;

import android.content.Context;
import com.example.skymall.data.remote.ApiClient;
import com.example.skymall.data.remote.ApiService;
import com.example.skymall.data.remote.DTO.BaseResp;
import com.example.skymall.data.remote.DTO.CreateOrderResp;
import com.example.skymall.data.remote.DTO.OrderDetailResp;
import com.example.skymall.data.remote.DTO.OrderListResp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerOrderRepository {
    private ApiService apiService;

    public CustomerOrderRepository(Context context) {
        this.apiService = ApiClient.create(context);
    }

    public interface CreateOrderCallback {
        void onSuccess(int orderId);
        void onError(String error);
    }

    public interface OrderListCallback {
        void onSuccess(OrderListResp response);
        void onError(String error);
    }

    public interface OrderDetailCallback {
        void onSuccess(OrderDetailResp.OrderDetailData orderDetail);
        void onError(String error);
    }

    public interface CancelOrderCallback {
        void onSuccess();
        void onError(String error);
    }

    public void createOrder(int cartId, int addressId, String voucherCode, double shippingFee, CreateOrderCallback callback) {
        Call<CreateOrderResp> call = apiService.createOrder(cartId, addressId, voucherCode, shippingFee);
        call.enqueue(new Callback<CreateOrderResp>() {
            @Override
            public void onResponse(Call<CreateOrderResp> call, Response<CreateOrderResp> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CreateOrderResp resp = response.body();
                    if (resp.success) {
                        callback.onSuccess(resp.order_id);
                    } else {
                        callback.onError(resp.error != null ? resp.error : "Tạo đơn hàng thất bại");
                    }
                } else {
                    callback.onError("Lỗi kết nối server");
                }
            }

            @Override
            public void onFailure(Call<CreateOrderResp> call, Throwable t) {
                callback.onError("Lỗi mạng: " + t.getMessage());
            }
        });
    }

    public void getCustomerOrders(Integer page, Integer limit, OrderListCallback callback) {
        Call<OrderListResp> call = apiService.getCustomerOrders(page, limit);
        call.enqueue(new Callback<OrderListResp>() {
            @Override
            public void onResponse(Call<OrderListResp> call, Response<OrderListResp> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OrderListResp resp = response.body();
                    if (resp.success) {
                        callback.onSuccess(resp);
                    } else {
                        callback.onError(resp.error != null ? resp.error : "Không thể tải danh sách đơn hàng");
                    }
                } else {
                    callback.onError("Lỗi kết nối server");
                }
            }

            @Override
            public void onFailure(Call<OrderListResp> call, Throwable t) {
                callback.onError("Lỗi mạng: " + t.getMessage());
            }
        });
    }

    public void getOrderDetail(int orderId, OrderDetailCallback callback) {
        Call<OrderDetailResp> call = apiService.getOrderDetail(orderId);
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

    public void cancelOrder(int orderId, CancelOrderCallback callback) {
        Call<BaseResp> call = apiService.cancelOrder(orderId);
        call.enqueue(new Callback<BaseResp>() {
            @Override
            public void onResponse(Call<BaseResp> call, Response<BaseResp> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BaseResp resp = response.body();
                    // BaseResp doesn't have success field, check for error message
                    if (resp.message == null || !resp.message.contains("error")) {
                        callback.onSuccess();
                    } else {
                        callback.onError(resp.message);
                    }
                } else {
                    callback.onError("Lỗi kết nối server");
                }
            }

            @Override
            public void onFailure(Call<BaseResp> call, Throwable t) {
                callback.onError("Lỗi mạng: " + t.getMessage());
            }
        });
    }
}
