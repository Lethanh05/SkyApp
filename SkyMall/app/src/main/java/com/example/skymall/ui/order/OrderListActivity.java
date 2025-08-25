package com.example.skymall.ui.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.skymall.R;
import com.example.skymall.data.remote.ApiService;
import com.example.skymall.data.remote.DTO.OrderDto;
import com.example.skymall.data.remote.DTO.OrderListResp;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderListActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private RecyclerView rvOrders;
    private SwipeRefreshLayout swipeRefresh;
    private OrderListAdapter adapter;
    private ApiService api;

    private String currentStatus = "ALL";

    public static void start(Context context, String orderId) {
        Intent intent = new Intent(context, OrderListActivity.class);
        intent.putExtra("order_id", orderId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        setupApi();
        initViews();
        setupTabs();
        setupRecyclerView();
        loadOrders();
    }
    private void initViews() {
        tabLayout = findViewById(R.id.tabLayout);
        rvOrders = findViewById(R.id.rvOrders);
        swipeRefresh = findViewById(R.id.swipeRefresh);
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Tất cả"));
        tabLayout.addTab(tabLayout.newTab().setText("Chờ xác nhận"));
        tabLayout.addTab(tabLayout.newTab().setText("Đang chuẩn bị"));
        tabLayout.addTab(tabLayout.newTab().setText("Đang giao"));
        tabLayout.addTab(tabLayout.newTab().setText("Hoàn thành"));
        tabLayout.addTab(tabLayout.newTab().setText("Đã hủy"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: currentStatus = "ALL"; break;
                    case 1: currentStatus = "PENDING"; break;
                    case 2: currentStatus = "PREPARING"; break;
                    case 3: currentStatus = "SHIPPING"; break;
                    case 4: currentStatus = "COMPLETED"; break;
                    case 5: currentStatus = "CANCELLED"; break;
                }
                loadOrders();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerView() {
        adapter = new OrderListAdapter(new ArrayList<>(), this::onOrderClick);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::loadOrders);
    }

    private void loadOrders() {
        swipeRefresh.setRefreshing(true);

        api.getCustomerOrders(1, 50).enqueue(new Callback<OrderListResp>() {
            @Override
            public void onResponse(@NonNull Call<OrderListResp> call, @NonNull Response<OrderListResp> response) {
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<OrderDto> orders = response.body().data != null ? response.body().data : new ArrayList<>();

                    if (!"ALL".equals(currentStatus)) {
                        List<OrderDto> filteredOrders = new ArrayList<>();
                        for (OrderDto order : orders) {
                            if (currentStatus.equals(order.status)) {
                                filteredOrders.add(order);
                            }
                        }
                        orders = filteredOrders;
                    }

                    adapter.updateOrders(orders);
                } else {
                    Toast.makeText(OrderListActivity.this, "Không thể tải danh sách đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<OrderListResp> call, @NonNull Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(OrderListActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onOrderClick(OrderDto order) {
        Toast.makeText(this, "Chi tiết đơn hàng #" + order.id, Toast.LENGTH_SHORT).show();
    }

    private void setupApi() {
        api = com.example.skymall.data.remote.ApiClient.get("https://lequangthanh.click/").create(ApiService.class);
    }
}
