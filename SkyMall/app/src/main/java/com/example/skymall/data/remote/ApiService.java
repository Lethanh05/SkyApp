package com.example.skymall.data.remote;

import com.example.skymall.data.model.Product;
import com.example.skymall.data.remote.DTO.AddressCreateResp;
import com.example.skymall.data.remote.DTO.AddressDetailResp;
import com.example.skymall.data.remote.DTO.AddressListResp;
import com.example.skymall.data.remote.DTO.AuthResp;
import com.example.skymall.data.remote.DTO.BaseResp;
import com.example.skymall.data.remote.DTO.CategoryListResp;
import com.example.skymall.data.remote.DTO.CreateOrderResp;
import com.example.skymall.data.remote.DTO.MeResp;
import com.example.skymall.data.remote.DTO.OrderDetailResp;
import com.example.skymall.data.remote.DTO.OrderDto;
import com.example.skymall.data.remote.DTO.OrderItemDto;
import com.example.skymall.data.remote.DTO.OrderListResp;
import com.example.skymall.data.remote.DTO.OrderStatusEventDto;
import com.example.skymall.data.remote.DTO.ProductListResp;
import com.example.skymall.data.remote.DTO.UploadResp;
import com.example.skymall.data.remote.DTO.VoucherCheckResp;
import com.example.skymall.data.remote.DTO.VoucherDetailResp;
import com.example.skymall.data.remote.DTO.VoucherHistoryResp;
import com.example.skymall.data.remote.DTO.VoucherListResp;
import com.example.skymall.data.remote.DTO.VoucherUseResp;
import com.example.skymall.data.remote.DTO.VoucherDuplicateResp;
import com.example.skymall.data.remote.DTO.VoucherStatsResp;
import com.example.skymall.data.remote.DTO.CartAddResp;
import com.example.skymall.data.remote.DTO.CartListResp;

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

    @GET("api/store/categories.php")
    Call<CategoryListResp> storeCategories();
    @GET("api/product/products")
    Call<List<Product>> getProducts(
            @Query("category_id") Integer categoryId,
            @Query("q")           String q,
            @Query("page")        Integer page,
            @Query("limit")       Integer limit
    );

    @GET("api/product/flashsale")
    Call<List<Product>> getFlashSale();

    @GET("api/store/products.php")
    Call<ProductListResp> storeProducts(
            @Query("q") String q,
            @Query("page") Integer page,
            @Query("limit") Integer limit
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

    @FormUrlEncoded
    @POST("api/auth/change-password.php")
    Call<BaseResp> changePassword(
            @Field("current_password") String currentPassword,
            @Field("new_password")     String newPassword,
            @Field("confirm_password") String confirmPassword
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

    // Voucher APIs
    @GET("api/voucher/list.php")
    Call<VoucherListResp> getVouchers(
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );

    @FormUrlEncoded
    @POST("api/voucher/check.php")
    Call<VoucherCheckResp> checkVoucher(
            @Field("code") String code,
            @Field("order_value") double orderValue
    );

    @FormUrlEncoded
    @POST("api/voucher/use.php")
    Call<VoucherUseResp> useVoucher(
            @Field("voucher_id") int voucherId,
            @Field("order_id") int orderId
    );

    @GET("api/voucher/history.php")
    Call<VoucherHistoryResp> getVoucherHistory(
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );

    @Multipart
    @POST("api/store/update.php")
    Call<BaseResp> storeUpdate(
            @Part("id") RequestBody id,
            @Part("name") RequestBody name,
            @Part("price") RequestBody price,
            @Part("description") RequestBody description,
            @Part("categoryId") RequestBody categoryId,
            @Part("stock") RequestBody stock,
            @Part("badge") RequestBody badge,
            @Part("is_active") RequestBody isActive,
            @Part MultipartBody.Part img // có thể null
    );

    // New methods to support category name instead of ID
    @Multipart
    @POST("api/store/create.php")
    Call<BaseResp> storeCreateWithCategoryName(
            @Part("name") RequestBody name,
            @Part("price") RequestBody price,
            @Part("description") RequestBody description,
            @Part("categoryName") RequestBody categoryName,
            @Part("stock") RequestBody stock,
            @Part("badge") RequestBody badge,
            @Part MultipartBody.Part img
    );

    @Multipart
    @POST("api/store/update.php")
    Call<BaseResp> storeUpdateWithCategoryName(
            @Part("id") RequestBody id,
            @Part("name") RequestBody name,
            @Part("price") RequestBody price,
            @Part("description") RequestBody description,
            @Part("categoryName") RequestBody categoryName,
            @Part("stock") RequestBody stock,
            @Part("badge") RequestBody badge,
            @Part("is_active") RequestBody isActive,
            @Part MultipartBody.Part img // có thể null
    );

    @FormUrlEncoded
    @POST("api/store/delete.php")
    Call<BaseResp> storeDelete(@Field("id") int id);

    @GET("api/store/products.php")
    Call<List<Product>> getStoreProducts(
        @Query("store_id") int storeId,
        @Query("q") String q,
        @Query("page") Integer page,
        @Query("limit") Integer limit
    );

    // Seller Voucher Management APIs
    @FormUrlEncoded
    @POST("api/store/voucher/create.php")
    Call<BaseResp> createVoucher(
            @Field("code") String code,
            @Field("type") String type,  // "percent" or "amount"
            @Field("value") double value,
            @Field("min_order_value") double minOrderValue,
            @Field("start_date") String startDate,
            @Field("end_date") String endDate,
            @Field("usage_limit") Integer usageLimit,
            @Field("per_user_limit") Integer perUserLimit,
            @Field("description") String description  // Server có hỗ trợ description
    );

    @GET("api/store/voucher/list.php")
    Call<VoucherListResp> getStoreVouchers(
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );

    @FormUrlEncoded
    @POST("api/store/voucher/update.php")
    Call<BaseResp> updateVoucher(
            @Field("id") int id,
            @Field("code") String code,  // Sửa lại thành code
            @Field("type") String type,
            @Field("value") double value,  // Sửa lại thành value
            @Field("min_order_value") double minOrderValue,
            @Field("start_date") String startDate,
            @Field("end_date") String endDate,
            @Field("usage_limit") Integer usageLimit,
            @Field("per_user_limit") Integer perUserLimit,
            @Field("description") String description
    );

    @FormUrlEncoded
    @POST("api/store/voucher/delete.php")
    Call<BaseResp> deleteVoucher(@Field("id") int id);

    @GET("api/store/voucher/details.php")
    Call<VoucherDetailResp> getVoucherDetails(@Query("id") int voucherId);

    @FormUrlEncoded
    @POST("api/store/voucher/duplicate.php")
    Call<VoucherDuplicateResp> duplicateVoucher(
            @Field("source_id") int sourceId,
            @Field("new_code") String newCode
    );

    @GET("api/store/voucher/stats.php")
    Call<VoucherStatsResp> getVoucherStats(@Query("period") int periodDays);

    // Customer Order APIs
    @FormUrlEncoded
    @POST("api/order/create.php")
    Call<CreateOrderResp> createOrder(
            @Field("cart_id") int cartId,
            @Field("address_id") int addressId,
            @Field("voucher_code") String voucherCode,
            @Field("shipping_fee") double shippingFee
    );

    @GET("api/order/list.php")
    Call<OrderListResp> getCustomerOrders(
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );

    @GET("api/order/get_order_detail.php")
    Call<OrderDetailResp> getOrderDetail(
            @Query("id") int orderId
    );

    @FormUrlEncoded
    @POST("api/order/cancel_order.php")
    Call<BaseResp> cancelOrder(
            @Field("id") int orderId
    );

    // Seller Order APIs
    @FormUrlEncoded
    @POST("api/order/update_order_status.php")
    Call<BaseResp> updateOrderStatus(
            @Field("id") int orderId,
            @Field("status") String status,
            @Field("note") String note
    );

    @GET("api/order/get_order_detail.php")
    Call<OrderDetailResp> getSellerOrderDetail(
            @Query("id") int orderId
    );

    // Customer Address APIs (đã cập nhật)
    @GET("api/address/get_addresses.php")
    Call<AddressListResp> getAddresses();

    @FormUrlEncoded
    @POST("api/address/add_address.php")
    Call<AddressCreateResp> createAddress(
            @Field("receiver_name") String receiverName,
            @Field("receiver_phone") String receiverPhone,
            @Field("address_line") String addressLine,
            @Field("ward") String ward,
            @Field("district") String district,
            @Field("province") String province,
            @Field("is_default") int isDefault
    );

    @FormUrlEncoded
    @POST("api/address/update_address.php")
    Call<BaseResp> updateAddress(
            @Field("id") int id,
            @Field("receiver_name") String receiverName,
            @Field("receiver_phone") String receiverPhone,
            @Field("address_line") String addressLine,
            @Field("ward") String ward,
            @Field("district") String district,
            @Field("province") String province,
            @Field("is_default") int isDefault
    );

    @FormUrlEncoded
    @POST("api/address/delete_address.php")
    Call<BaseResp> deleteAddress(
            @Field("id") int id
    );

    @GET("api/address/get_address_detail.php")
    Call<AddressDetailResp> getAddressDetail(
            @Query("id") int id
    );

    @FormUrlEncoded
    @POST("api/address/set_default_address.php")
    Call<BaseResp> setDefaultAddress(
            @Field("id") int id
    );

    @GET("api/address/get_default_address.php")
    Call<AddressDetailResp> getDefaultAddress();

    @FormUrlEncoded
    @POST("api/address/validate_address.php")
    Call<BaseResp> validateAddress(
            @Field("receiver_name") String receiverName,
            @Field("receiver_phone") String receiverPhone,
            @Field("address_line") String addressLine,
            @Field("ward") String ward,
            @Field("district") String district,
            @Field("province") String province
    );

    // Customer Cart APIs
    @FormUrlEncoded
    @POST("api/cart/add.php")
    Call<CartAddResp> addToCart(
            @Field("product_id") int productId,
            @Field("quantity") int quantity
    );

    @GET("api/cart/list.php")
    Call<CartListResp> getCart();

    @POST("api/cart/clear.php")
    Call<BaseResp> clearCart();

    @FormUrlEncoded
    @POST("api/cart/update.php")
    Call<BaseResp> updateCartItem(
            @Field("product_id") int productId,
            @Field("quantity") int quantity
    );
}
