package com.example.skymall.ui.security;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.skymall.R;
import com.example.skymall.data.remote.ApiClient;
import com.example.skymall.data.remote.ApiService;
import com.example.skymall.data.remote.DTO.BaseResp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private Button btnChangePassword;
    private ProgressBar progressBar;
    private ImageView btnBack;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initViews();
        initApi();
        setupListeners();
    }

    private void initViews() {
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnBack);
    }

    private void initApi() {
        apiService = ApiClient.create(this);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnChangePassword.setOnClickListener(v -> handleChangePassword());
    }

    private void handleChangePassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate input
        if (!validateInput(currentPassword, newPassword, confirmPassword)) {
            return;
        }

        // Show loading
        setLoading(true);

        // Call API
        apiService.changePassword(currentPassword, newPassword, confirmPassword)
                .enqueue(new Callback<BaseResp>() {
                    @Override
                    public void onResponse(Call<BaseResp> call, Response<BaseResp> response) {
                        setLoading(false);

                        if (response.isSuccessful() && response.body() != null) {
                            BaseResp result = response.body();
                            if (result.success) {
                                showToast("Đổi mật khẩu thành công!");
                                finish(); // Quay về màn hình trước
                            } else {
                                showToast(result.message != null ? result.message : "Đổi mật khẩu thất bại");
                            }
                        } else {
                            // Handle error response
                            if (response.code() == 400) {
                                showToast("Mật khẩu hiện tại không đúng");
                            } else if (response.code() == 422) {
                                showToast("Dữ liệu không hợp lệ. Vui lòng kiểm tra lại");
                            } else {
                                showToast("Có lỗi xảy ra. Vui lòng thử lại sau");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResp> call, Throwable t) {
                        setLoading(false);
                        showToast("Lỗi kết nối. Vui lòng kiểm tra internet và thử lại");
                    }
                });
    }

    private boolean validateInput(String currentPassword, String newPassword, String confirmPassword) {
        // Check empty fields
        if (TextUtils.isEmpty(currentPassword)) {
            etCurrentPassword.setError("Vui lòng nhập mật khẩu hiện tại");
            etCurrentPassword.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError("Vui lòng nhập mật khẩu mới");
            etNewPassword.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Vui lòng xác nhận mật khẩu mới");
            etConfirmPassword.requestFocus();
            return false;
        }

        // Check password length
        if (newPassword.length() < 8) {
            etNewPassword.setError("Mật khẩu mới phải có ít nhất 8 ký tự");
            etNewPassword.requestFocus();
            return false;
        }

        // Check password match
        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            etConfirmPassword.requestFocus();
            return false;
        }

        // Check if new password is different from current
        if (currentPassword.equals(newPassword)) {
            etNewPassword.setError("Mật khẩu mới phải khác mật khẩu hiện tại");
            etNewPassword.requestFocus();
            return false;
        }

        // Check password strength (optional)
        if (!isPasswordStrong(newPassword)) {
            showToast("Mật khẩu nên bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt");
            // Don't return false, just warn user
        }

        return true;
    }

    private boolean isPasswordStrong(String password) {
        // Check if password contains uppercase, lowercase, digit and special character
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    private void setLoading(boolean loading) {
        if (loading) {
            btnChangePassword.setEnabled(false);
            btnChangePassword.setText("Đang xử lý...");
            progressBar.setVisibility(View.VISIBLE);
        } else {
            btnChangePassword.setEnabled(true);
            btnChangePassword.setText("Đổi mật khẩu");
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
