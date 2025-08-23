package com.example.skymall.data.repository;

import android.content.Context;

import com.example.skymall.data.remote.ApiClient;
import com.example.skymall.data.remote.ApiService;
import com.example.skymall.data.remote.DTO.VoucherListResp;
import com.example.skymall.data.remote.DTO.VoucherCheckResp;
import com.example.skymall.data.remote.DTO.VoucherUseResp;
import com.example.skymall.data.remote.DTO.VoucherHistoryResp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoucherRepository {
    private final ApiService apiService;

    public VoucherRepository(Context context) {
        this.apiService = ApiClient.create(context);
    }

    public interface VoucherListCallback {
        void onSuccess(VoucherListResp response);
        void onError(String error);
    }

    public interface VoucherCheckCallback {
        void onSuccess(VoucherCheckResp response);
        void onError(String error, String[] details);
    }

    public interface VoucherUseCallback {
        void onSuccess(VoucherUseResp response);
        void onError(String error);
    }

    public interface VoucherHistoryCallback {
        void onSuccess(VoucherHistoryResp response);
        void onError(String error);
    }

    public void getVouchers(Integer page, Integer limit, VoucherListCallback callback) {
        Call<VoucherListResp> call = apiService.getVouchers(page, limit);
        call.enqueue(new Callback<VoucherListResp>() {
            @Override
            public void onResponse(Call<VoucherListResp> call, Response<VoucherListResp> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VoucherListResp body = response.body();
                    if (body.success) {
                        callback.onSuccess(body);
                    } else {
                        callback.onError(body.error != null ? body.error : "Unknown error");
                    }
                } else {
                    callback.onError("Network error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<VoucherListResp> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void checkVoucher(String code, double orderValue, VoucherCheckCallback callback) {
        Call<VoucherCheckResp> call = apiService.checkVoucher(code, orderValue);
        call.enqueue(new Callback<VoucherCheckResp>() {
            @Override
            public void onResponse(Call<VoucherCheckResp> call, Response<VoucherCheckResp> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VoucherCheckResp body = response.body();
                    if (body.success) {
                        callback.onSuccess(body);
                    } else {
                        String[] details = body.details != null ?
                            body.details.toArray(new String[0]) : new String[0];
                        callback.onError(body.error != null ? body.error : "Voucher không hợp lệ", details);
                    }
                } else {
                    callback.onError("Lỗi mạng: " + response.code(), new String[0]);
                }
            }

            @Override
            public void onFailure(Call<VoucherCheckResp> call, Throwable t) {
                callback.onError("Lỗi mạng: " + t.getMessage(), new String[0]);
            }
        });
    }

    public void useVoucher(int voucherId, int orderId, VoucherUseCallback callback) {
        Call<VoucherUseResp> call = apiService.useVoucher(voucherId, orderId);
        call.enqueue(new Callback<VoucherUseResp>() {
            @Override
            public void onResponse(Call<VoucherUseResp> call, Response<VoucherUseResp> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VoucherUseResp body = response.body();
                    if (body.success) {
                        callback.onSuccess(body);
                    } else {
                        callback.onError(body.error != null ? body.error : "Không thể sử dụng voucher");
                    }
                } else {
                    callback.onError("Lỗi mạng: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<VoucherUseResp> call, Throwable t) {
                callback.onError("Lỗi mạng: " + t.getMessage());
            }
        });
    }

    public void getVoucherHistory(Integer page, Integer limit, VoucherHistoryCallback callback) {
        Call<VoucherHistoryResp> call = apiService.getVoucherHistory(page, limit);
        call.enqueue(new Callback<VoucherHistoryResp>() {
            @Override
            public void onResponse(Call<VoucherHistoryResp> call, Response<VoucherHistoryResp> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VoucherHistoryResp body = response.body();
                    if (body.success) {
                        callback.onSuccess(body);
                    } else {
                        callback.onError(body.error != null ? body.error : "Không thể lấy lịch sử voucher");
                    }
                } else {
                    callback.onError("Lỗi mạng: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<VoucherHistoryResp> call, Throwable t) {
                callback.onError("Lỗi mạng: " + t.getMessage());
            }
        });
    }
}
