package com.example.skymall;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.skymall.data.remote.ApiClient;
import com.example.skymall.data.remote.ApiService;
import com.example.skymall.data.remote.DTO.BaseResp;
import com.example.skymall.data.remote.DTO.CartResp;
import com.example.skymall.ui.HomeFragment;
import com.example.skymall.ui.NotificationsFragment;
import com.example.skymall.ui.ProfileFragment;
import com.example.skymall.ui.StoreFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements CartBadgeHost {

    private TextView tvBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        if (!com.example.skymall.auth.SessionManager.isLoggedIn(this)) {
            startActivity(new Intent(this, com.example.skymall.auth.LoginActivity.class));
            finish();
            return;
        }
        // Kiểm tra role
        String role = com.example.skymall.auth.SessionManager.role(this);
        if ("store".equals(role) || "admin".equals(role)) {
            startActivity(new Intent(this, com.example.skymall.ui.seller.SellerHomeActivity.class));
            finish();
            return;
        }

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment(), true);
        }

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                loadFragment(new HomeFragment(), false);
                return true;
            } else if (id == R.id.nav_store) {
                loadFragment(StoreFragment.newInstance(0), false);
                return true;
            } else if (id == R.id.nav_notifications) {
                loadFragment(new NotificationsFragment(), false);
                return true;
            } else if (id == R.id.nav_profile) {
                loadFragment(new ProfileFragment(), false);
                return true;
            }
            return false;
        });

        // Status bar
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.skymall_primary));
        WindowInsetsControllerCompat insetsController =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setAppearanceLightStatusBars(false);
    }

    // Inflate menu có icon giỏ hàng
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.layout.fragment_home, menu);
        MenuItem cartItem = menu.findItem(R.id.btnCart);
        View actionView = cartItem.getActionView();
        if (actionView != null) {
            tvBadge = actionView.findViewById(R.id.tvBadge);
            actionView.setOnClickListener(v -> {
                Toast.makeText(this, "Mở giỏ hàng", Toast.LENGTH_SHORT).show();
                // TODO: mở CartFragment hoặc CartActivity
            });
        }
        fetchCartAndSetBadge();
        return true;
    }

    private void fetchCartAndSetBadge() {
        ApiClient.api().cartGetSummary().enqueue(new Callback<BaseResp<CartResp>>() {
            @Override
            public void onResponse(Call<BaseResp<CartResp>> call, Response<BaseResp<CartResp>> res) {
                if (res.isSuccessful() && res.body() != null && res.body().success) {
                    int c = res.body().data != null ? res.body().data.count : 0;
                    updateCartCount(c);
                }
            }
            @Override public void onFailure(Call<BaseResp<CartResp>> call, Throwable t) { }
        });
    }

    @Override
    public void updateCartCount(int count) {
        if (tvBadge == null) return;
        if (count <= 0) {
            tvBadge.setVisibility(View.GONE);
        } else {
            tvBadge.setVisibility(View.VISIBLE);
            tvBadge.setText(String.valueOf(Math.min(count, 99)));
        }
    }

    // Load fragment
    private void loadFragment(Fragment fragment, boolean isInitial) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (isInitial) {
            fragmentTransaction.add(R.id.fragment_container, fragment);
        } else {
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }
}
