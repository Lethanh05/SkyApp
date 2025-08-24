package com.example.skymall.ui.address;

import android.content.Context;
import com.example.skymall.data.remote.ApiClient;
import com.example.skymall.data.remote.ApiService;
import com.example.skymall.data.remote.DTO.AddressCreateResp;
import com.example.skymall.data.remote.DTO.AddressListResp;
import com.example.skymall.data.remote.DTO.BaseResp;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressRepository {
    private static AddressRepository INSTANCE;
    private ApiService apiService;
    private List<Address> cachedAddresses = new ArrayList<>();

    private AddressRepository(Context context) {
        this.apiService = ApiClient.create(context);
    }

    public static AddressRepository get(Context context) {
        if (INSTANCE == null) INSTANCE = new AddressRepository(context);
        return INSTANCE;
    }

    // Legacy method for compatibility - returns cached data
    public List<Address> list() {
        return cachedAddresses;
    }

    // New async methods using customer address APIs
    public interface AddressCallback {
        void onSuccess(List<Address> addresses);
        void onError(String error);
    }

    public interface AddressActionCallback {
        void onSuccess();
        void onError(String error);
    }

    public void loadAddresses(AddressCallback callback) {
        Call<AddressListResp> call = apiService.getAddresses();
        call.enqueue(new Callback<AddressListResp>() {
            @Override
            public void onResponse(Call<AddressListResp> call, Response<AddressListResp> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AddressListResp resp = response.body();
                    if (resp.success) {
                        cachedAddresses.clear();
                        if (resp.data != null) { // Changed from resp.addresses to resp.data
                            for (AddressListResp.AddressDto dto : resp.data) {
                                cachedAddresses.add(new Address(dto));
                            }
                        }
                        callback.onSuccess(cachedAddresses);
                    } else {
                        String errorMsg = "API Error: " + (resp.error != null ? resp.error : "unknown error");
                        callback.onError(errorMsg);
                    }
                } else {
                    String errorMsg = "HTTP Error: " + response.code() + " - " + response.message();
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<AddressListResp> call, Throwable t) {
                String errorMsg = "Network Error: " + t.getClass().getSimpleName() + " - " + t.getMessage();
                callback.onError(errorMsg);
            }
        });
    }

    public void add(Address addr, AddressActionCallback callback) {
        Call<AddressCreateResp> call = apiService.createAddress(
                addr.name,           // Maps to receiver_name
                addr.phone,          // Maps to receiver_phone
                addr.addressLine,    // Maps to address_line
                addr.ward,
                addr.district,
                addr.province,       // Maps to province (not city)
                addr.isDefault ? 1 : 0
        );

        call.enqueue(new Callback<AddressCreateResp>() {
            @Override
            public void onResponse(Call<AddressCreateResp> call, Response<AddressCreateResp> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AddressCreateResp resp = response.body();
                    if (resp.success && resp.data != null) {
                        addr.id = resp.data.id; // Get ID from nested data object
                        if (addr.isDefault) clearDefault();
                        cachedAddresses.add(0, addr);
                        callback.onSuccess();
                    } else {
                        callback.onError(resp.error != null ? resp.error : "Thêm địa chỉ thất bại");
                    }
                } else {
                    callback.onError("Lỗi kết nối server");
                }
            }

            @Override
            public void onFailure(Call<AddressCreateResp> call, Throwable t) {
                callback.onError("Lỗi mạng: " + t.getMessage());
            }
        });
    }

    public void update(Address addr, AddressActionCallback callback) {
        Call<BaseResp> call = apiService.updateAddress(
                addr.id,
                addr.name,           // Maps to receiver_name
                addr.phone,          // Maps to receiver_phone
                addr.addressLine,    // Maps to address_line
                addr.ward,
                addr.district,
                addr.province,       // Maps to province
                addr.isDefault ? 1 : 0
        );

        call.enqueue(new Callback<BaseResp>() {
            @Override
            public void onResponse(Call<BaseResp> call, Response<BaseResp> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Update local cache
                    for (int i = 0; i < cachedAddresses.size(); i++) {
                        if (cachedAddresses.get(i).id == addr.id) {
                            if (addr.isDefault) clearDefault();
                            cachedAddresses.set(i, addr);
                            break;
                        }
                    }
                    callback.onSuccess();
                } else {
                    callback.onError("Cập nhật địa chỉ thất bại");
                }
            }

            @Override
            public void onFailure(Call<BaseResp> call, Throwable t) {
                callback.onError("Lỗi mạng: " + t.getMessage());
            }
        });
    }

    // New method to use the dedicated set_default.php endpoint
    public void setDefault(int addressId, AddressActionCallback callback) {
        Call<BaseResp> call = apiService.setDefaultAddress(addressId);
        call.enqueue(new Callback<BaseResp>() {
            @Override
            public void onResponse(Call<BaseResp> call, Response<BaseResp> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Update local cache - clear all defaults and set the new one
                    clearDefault();
                    for (Address addr : cachedAddresses) {
                        if (addr.id == addressId) {
                            addr.isDefault = true;
                            break;
                        }
                    }
                    callback.onSuccess();
                } else {
                    callback.onError("Không thể đặt làm địa chỉ mặc định");
                }
            }

            @Override
            public void onFailure(Call<BaseResp> call, Throwable t) {
                callback.onError("Lỗi mạng: " + t.getMessage());
            }
        });
    }

    public void remove(int id, AddressActionCallback callback) {
        Call<BaseResp> call = apiService.deleteAddress(id);
        call.enqueue(new Callback<BaseResp>() {
            @Override
            public void onResponse(Call<BaseResp> call, Response<BaseResp> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Remove from local cache
                    for (int i = 0; i < cachedAddresses.size(); i++) {
                        if (cachedAddresses.get(i).id == id) {
                            cachedAddresses.remove(i);
                            break;
                        }
                    }
                    callback.onSuccess();
                } else {
                    callback.onError("Xóa địa chỉ thất bại");
                }
            }

            @Override
            public void onFailure(Call<BaseResp> call, Throwable t) {
                callback.onError("Lỗi mạng: " + t.getMessage());
            }
        });
    }

    // Legacy remove method for backward compatibility
    public void remove(String stringId) {
        try {
            int id = Integer.parseInt(stringId);
            remove(id, new AddressActionCallback() {
                @Override
                public void onSuccess() {
                    // Silent success for backward compatibility
                }

                @Override
                public void onError(String error) {
                    // Silent error for backward compatibility
                }
            });
        } catch (NumberFormatException e) {
            // Handle legacy string IDs if needed
        }
    }

    private void clearDefault() {
        for (Address addr : cachedAddresses) {
            addr.isDefault = false;
        }
    }
}
