package com.example.skymall.ui.seller;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skymall.R;
import com.example.skymall.data.model.VoucherStats;
import com.example.skymall.data.remote.ApiClient;
import com.example.skymall.data.remote.ApiService;
import com.example.skymall.data.remote.DTO.VoucherStatsResp;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoucherStatsActivity extends AppCompatActivity {

    private ApiService apiService;
    private NumberFormat currencyFormat;

    // Views
    private Spinner spinnerPeriod;
    private TextView tvTotalVouchers, tvActiveVouchers, tvExpiredVouchers, tvScheduledVouchers;
    private TextView tvVouchersUsed, tvTotalUsage, tvUniqueUsers, tvTotalDiscount;
    private RecyclerView rvTopVouchers, rvDailyTrend;
    private MaterialCardView cardOverall, cardUsage;

    // Adapters
    private TopVouchersAdapter topVouchersAdapter;
    private DailyTrendAdapter dailyTrendAdapter;

    private int selectedPeriod = 30; // Default 30 days

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_stats);

        initViews();
        setupToolbar();
        setupApiService();
        setupPeriodSelector();
        setupRecyclerViews();
        loadStats();
    }

    private void initViews() {
        spinnerPeriod = findViewById(R.id.spinnerPeriod);

        // Overall stats
        tvTotalVouchers = findViewById(R.id.tvTotalVouchers);
        tvActiveVouchers = findViewById(R.id.tvActiveVouchers);
        tvExpiredVouchers = findViewById(R.id.tvExpiredVouchers);
        tvScheduledVouchers = findViewById(R.id.tvScheduledVouchers);

        // Usage stats
        tvVouchersUsed = findViewById(R.id.tvVouchersUsed);
        tvTotalUsage = findViewById(R.id.tvTotalUsage);
        tvUniqueUsers = findViewById(R.id.tvUniqueUsers);
        tvTotalDiscount = findViewById(R.id.tvTotalDiscount);

        // RecyclerViews
        rvTopVouchers = findViewById(R.id.rvTopVouchers);
        rvDailyTrend = findViewById(R.id.rvDailyTrend);

        // Cards
        cardOverall = findViewById(R.id.cardOverall);
        cardUsage = findViewById(R.id.cardUsage);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Thống kê Voucher");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupApiService() {
        apiService = ApiClient.create(this);
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }

    private void setupPeriodSelector() {
        String[] periods = {"7 ngày", "30 ngày", "90 ngày", "180 ngày", "365 ngày"};
        int[] periodValues = {7, 30, 90, 180, 365};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, periods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPeriod.setAdapter(adapter);

        // Set default selection (30 days)
        spinnerPeriod.setSelection(1);

        spinnerPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPeriod = periodValues[position];
                loadStats();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupRecyclerViews() {
        // Top vouchers
        topVouchersAdapter = new TopVouchersAdapter(new ArrayList<>());
        rvTopVouchers.setLayoutManager(new LinearLayoutManager(this));
        rvTopVouchers.setAdapter(topVouchersAdapter);

        // Daily trend
        dailyTrendAdapter = new DailyTrendAdapter(new ArrayList<>());
        rvDailyTrend.setLayoutManager(new LinearLayoutManager(this));
        rvDailyTrend.setAdapter(dailyTrendAdapter);
    }

    private void loadStats() {
        apiService.getVoucherStats(selectedPeriod).enqueue(new Callback<VoucherStatsResp>() {
            @Override
            public void onResponse(@NonNull Call<VoucherStatsResp> call, @NonNull Response<VoucherStatsResp> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    updateUI(response.body().data);
                } else {
                    Toast.makeText(VoucherStatsActivity.this,
                        "Không thể tải thống kê", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<VoucherStatsResp> call, @NonNull Throwable t) {
                Toast.makeText(VoucherStatsActivity.this,
                    "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(VoucherStats stats) {
        if (stats == null) return;

        // Update overall stats
        if (stats.overall != null) {
            tvTotalVouchers.setText(String.valueOf(stats.overall.totalVouchers));
            tvActiveVouchers.setText(String.valueOf(stats.overall.activeVouchers));
            tvExpiredVouchers.setText(String.valueOf(stats.overall.expiredVouchers));
            tvScheduledVouchers.setText(String.valueOf(stats.overall.scheduledVouchers));
        }

        // Update usage stats
        if (stats.usageStats != null) {
            tvVouchersUsed.setText(String.valueOf(stats.usageStats.vouchersUsed));
            tvTotalUsage.setText(String.valueOf(stats.usageStats.totalUsage));
            tvUniqueUsers.setText(String.valueOf(stats.usageStats.uniqueUsers));
            tvTotalDiscount.setText(currencyFormat.format(stats.usageStats.totalDiscountGiven));
        }

        // Update top vouchers
        if (stats.topVouchers != null) {
            topVouchersAdapter.updateData(stats.topVouchers);
        }

        // Update daily trend
        if (stats.dailyTrend != null) {
            dailyTrendAdapter.updateData(stats.dailyTrend);
        }
    }
}
