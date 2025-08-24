package com.example.skymall.ui.seller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.skymall.R;
import com.example.skymall.auth.LoginActivity;
import com.example.skymall.data.remote.ApiClient;
import com.example.skymall.data.remote.ApiService;
import com.example.skymall.data.remote.DTO.MeResp;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerHomeActivity extends AppCompatActivity {
    Button btnProducts, btnOrders, btnVouchers, btnLogout;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);

        initViews();
        setupApiService();
        setupClickListeners();
    }

    private void initViews() {
        btnProducts = findViewById(R.id.btnProducts);
        btnOrders = findViewById(R.id.btnOrders);
        btnVouchers = findViewById(R.id.btnVouchers);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupApiService() {
        apiService = ApiClient.create(this);
    }

    private void setupClickListeners() {
        btnProducts.setOnClickListener(v ->
                startActivity(new Intent(this, ProductListActivity.class)));

        btnVouchers.setOnClickListener(v ->
                startActivity(new Intent(this, VoucherManagementActivity.class)));

        btnLogout.setOnClickListener(v -> showLogoutConfirmDialog());

//        btnOrders.setOnClickListener(v ->
//                startActivity(new Intent(this, OrderListActivity.class)));
    }

    private void showLogoutConfirmDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi tài khoản người bán?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> performLogout())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void performLogout() {
        // Call logout API
        apiService.logout().enqueue(new Callback<MeResp>() {
            @Override
            public void onResponse(@NonNull Call<MeResp> call, @NonNull Response<MeResp> response) {
                // Clear local session regardless of API response
                clearSession();
                navigateToLogin();
            }

            @Override
            public void onFailure(@NonNull Call<MeResp> call, @NonNull Throwable t) {
                // Clear local session even if API call fails
                clearSession();
                navigateToLogin();
            }
        });
    }

    private void clearSession() {
        // Clear SharedPreferences
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        prefs.edit().clear().apply();

        Toast.makeText(this, "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
