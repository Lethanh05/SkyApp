package com.example.skymall.data.repository;

import android.content.Context;
import com.example.skymall.data.remote.ApiClient;
import com.example.skymall.data.remote.ApiService;
import com.example.skymall.data.remote.DTO.BaseResp;
import com.example.skymall.data.remote.DTO.CartAddResp;
import com.example.skymall.data.remote.DTO.CartListResp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartRepository {
    private ApiService apiService;

    public CartRepository(Context context) {
        this.apiService = ApiClient.create(context);
    }

    public interface AddToCartCallback {
        void onSuccess(int cartId);
        void onError(String error);
    }

    public interface CartListCallback {
        void onSuccess(CartListResp response);
        void onError(String error);
    }

    public interface CartActionCallback {
        void onSuccess();
        void onError(String error);
    }

    public void addToCart(int productId, int quantity, AddToCartCallback callback) {
        Call<CartAddResp> call = apiService.addToCart(productId, quantity);
        call.enqueue(new Callback<CartAddResp>() {
            @Override
            public void onResponse(Call<CartAddResp> call, Response<CartAddResp> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CartAddResp resp = response.body();
                    if (resp.success) {
                        callback.onSuccess(resp.cart_id);
                    } else {
                        callback.onError(getErrorMessage(resp.error));
                    }
                } else {
                    callback.onError("Lỗi kết nối server: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CartAddResp> call, Throwable t) {
                callback.onError("Lỗi mạng: " + t.getMessage());
            }
        });
    }

    public void getCart(CartListCallback callback) {
        Call<CartListResp> call = apiService.getCart();
        call.enqueue(new Callback<CartListResp>() {
            @Override
            public void onResponse(Call<CartListResp> call, Response<CartListResp> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CartListResp resp = response.body();
                    if (resp.success) {
                        callback.onSuccess(resp);
                    } else {
                        callback.onError(resp.error != null ? resp.error : "Không thể tải giỏ hàng");
                    }
                } else {
                    callback.onError("Lỗi kết nối server: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CartListResp> call, Throwable t) {
                callback.onError("Lỗi mạng: " + t.getMessage());
            }
        });
    }

    public void updateCartItem(int productId, int quantity, CartActionCallback callback) {
        Call<BaseResp> call = apiService.updateCartItem(productId, quantity);
        call.enqueue(new Callback<BaseResp>() {
            @Override
            public void onResponse(Call<BaseResp> call, Response<BaseResp> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess();
                } else {
                    callback.onError("Cập nhật giỏ hàng thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BaseResp> call, Throwable t) {
                callback.onError("Lỗi mạng: " + t.getMessage());
            }
        });
    }

    public void clearCart(CartActionCallback callback) {
        Call<BaseResp> call = apiService.clearCart();
        call.enqueue(new Callback<BaseResp>() {
            @Override
            public void onResponse(Call<BaseResp> call, Response<BaseResp> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess();
                } else {
                    callback.onError("Xóa giỏ hàng thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BaseResp> call, Throwable t) {
                callback.onError("Lỗi mạng: " + t.getMessage());
            }
        });
    }

    // Remove item by setting quantity to 0
    public void removeFromCart(int productId, CartActionCallback callback) {
        updateCartItem(productId, 0, callback);
    }

    private String getErrorMessage(String error) {
        if (error == null) return "Lỗi không xác định";
        switch (error) {
            case "invalid_input": return "Dữ liệu không hợp lệ";
            case "product_not_found": return "Sản phẩm không tồn tại";
            case "out_of_stock": return "Sản phẩm đã hết hàng";
            case "missing_product": return "Thiếu thông tin sản phẩm";
            case "empty_cart": return "Giỏ hàng trống";
            default: return "Lỗi: " + error;
        }
    }
}
