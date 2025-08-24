package com.example.skymall;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.skymall.ui.HomeFragment;
import com.example.skymall.ui.NotificationsFragment;
import com.example.skymall.ui.ProfileFragment;
import com.example.skymall.ui.StoreFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

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
        // Kiểm tra role, nếu là store hoặc admin thì chuyển hướng SellerHomeActivity
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
                loadFragment(StoreFragment.newInstance(1), false);
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


        // Thiết lập màu cho status bar
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.skymall_primary));
        WindowInsetsControllerCompat insetsController =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setAppearanceLightStatusBars(false);
    }


    // Phương thức để tải các Fragment vào container
    private void loadFragment(Fragment fragment, boolean isInitial) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isInitial) {
            // Sử dụng add cho lần đầu tiên
            fragmentTransaction.add(R.id.fragment_container, fragment);
        } else {
            // Sử dụng replace cho các lần chuyển đổi fragment sau
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

}