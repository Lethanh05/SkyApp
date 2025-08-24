package com.example.skymall.auth;

import android.content.Intent; import android.os.Bundle; import android.widget.*;
import androidx.annotation.Nullable; import androidx.appcompat.app.AppCompatActivity;
import com.example.skymall.MainActivity; import com.example.skymall.R;
import com.example.skymall.data.remote.*; import com.example.skymall.data.remote.DTO.AuthResp;
import com.example.skymall.ui.seller.SellerHomeActivity;

import retrofit2.*;

public class LoginActivity extends AppCompatActivity {
    private EditText edtEmail, edtPass; private ApiService api;

    @Override protected void onCreate(@Nullable Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_login);
        edtEmail = findViewById(R.id.edtEmail);
        edtPass  = findViewById(R.id.edtPass);
        Button btn = findViewById(R.id.btnLogin);
        TextView tvReg = findViewById(R.id.tvGotoRegister);

        api = ApiClient.create(this);

        btn.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String pass  = edtPass.getText().toString();
            if (email.isEmpty() || pass.isEmpty()) { toast("Nhập email & mật khẩu"); return; }

            api.login(email, pass).enqueue(new Callback<AuthResp>() {
                @Override public void onResponse(Call<AuthResp> call, Response<AuthResp> res) {
                    AuthResp b = res.body();
                    if (!res.isSuccessful() || b == null || !b.success || b.user == null) {
                        toast("Sai tài khoản/mật khẩu"); return;
                    }

                    // Chuẩn hoá role
                    String role = (b.user.role == null || b.user.role.trim().isEmpty())
                            ? "customer" : b.user.role.trim().toLowerCase();

                    // Lưu session (ép id -> String)
                    SessionManager.save(
                            LoginActivity.this,
                            b.token,
                            String.valueOf(b.user.id),
                            b.user.name,
                            b.user.email,
                            role
                    );

                    // Điều hướng theo role (admin vào trang người bán nếu bạn muốn)
                    if ("store".equals(role) || "admin".equals(role)) {
                        startActivity(new Intent(LoginActivity.this, SellerHomeActivity.class));
                    } else {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                    finishAffinity(); // optional: xoá back stack cho sạch
                }

                @Override public void onFailure(Call<AuthResp> call, Throwable t) { toast("Lỗi mạng"); }
            });
        });

        tvReg.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void toast(String s){ Toast.makeText(this,s,Toast.LENGTH_SHORT).show(); }
}
