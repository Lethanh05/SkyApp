
package com.example.skymall.auth;

import android.content.Intent; import android.os.Bundle; import android.widget.*;
import androidx.annotation.Nullable; import androidx.appcompat.app.AppCompatActivity;
import com.example.skymall.MainActivity; import com.example.skymall.R;
import com.example.skymall.data.remote.*; import com.example.skymall.data.remote.DTO.AuthResp;
import retrofit2.*;

public class RegisterActivity extends AppCompatActivity {
    private EditText edtName, edtEmail, edtPass; private ApiService api;

    @Override protected void onCreate(@Nullable Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_register);
        edtName = findViewById(R.id.edtName);
        edtEmail= findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPass);
        Button btn = findViewById(R.id.btnRegister);
        api = ApiClient.create(this);

        btn.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String email= edtEmail.getText().toString().trim();
            String pass = edtPass.getText().toString();
            if (name.isEmpty() || email.isEmpty() || pass.length()<6) { toast("Điền đủ thông tin"); return; }

            api.register(name, email, pass).enqueue(new Callback<AuthResp>() {
                @Override public void onResponse(Call<AuthResp> call, Response<AuthResp> res) {
                    AuthResp b = res.body();
                    if (res.isSuccessful() && b!=null && b.success){
                        SessionManager.save(RegisterActivity.this, b.token, b.user.id, b.user.name, b.user.email);
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    } else toast("Email đã tồn tại?");
                }
                @Override public void onFailure(Call<AuthResp> call, Throwable t) { toast("Lỗi mạng"); }
            });
        });
    }
    private void toast(String s){ Toast.makeText(this,s,Toast.LENGTH_SHORT).show(); }
}
