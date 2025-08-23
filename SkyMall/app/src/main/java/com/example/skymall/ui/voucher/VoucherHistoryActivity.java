package com.example.skymall.ui.voucher;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.skymall.R;
import com.example.skymall.data.model.VoucherHistory;
import com.example.skymall.data.remote.DTO.VoucherHistoryResp;
import com.example.skymall.data.repository.VoucherRepository;

public class VoucherHistoryActivity extends AppCompatActivity implements VoucherHistoryAdapter.OnHistoryItemClickListener {

    private RecyclerView recyclerView;
    private VoucherHistoryAdapter adapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvEmptyState;

    private VoucherRepository voucherRepository;
    private int currentPage = 1;
    private final int limit = 20;
    private boolean isLoading = false;
    private boolean hasMoreData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_history);

        initViews();
        setupRecyclerView();
        setupSwipeRefresh();

        voucherRepository = new VoucherRepository(this);
        loadVoucherHistory(false);
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_history);
        progressBar = findViewById(R.id.progress_bar);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        tvEmptyState = findViewById(R.id.tv_empty_state);

        // Setup toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Lịch sử Voucher");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        adapter = new VoucherHistoryAdapter();
        adapter.setOnItemClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Implement pagination
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading && hasMoreData) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5) {
                        loadVoucherHistory(true);
                    }
                }
            }
        });
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            currentPage = 1;
            hasMoreData = true;
            loadVoucherHistory(false);
        });
    }

    private void loadVoucherHistory(boolean loadMore) {
        if (isLoading) return;

        isLoading = true;

        if (!loadMore) {
            progressBar.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);
        }

        voucherRepository.getVoucherHistory(currentPage, limit, new VoucherRepository.VoucherHistoryCallback() {
            @Override
            public void onSuccess(VoucherHistoryResp response) {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                if (response.history != null && !response.history.isEmpty()) {
                    if (loadMore) {
                        adapter.addHistoryList(response.history);
                    } else {
                        adapter.setHistoryList(response.history);
                    }

                    // Check if there's more data
                    if (response.pagination != null) {
                        hasMoreData = currentPage < response.pagination.totalPages;
                        if (hasMoreData) {
                            currentPage++;
                        }
                    }

                    tvEmptyState.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    hasMoreData = false;
                    if (!loadMore) {
                        // Show empty state only for first load
                        showEmptyState();
                    }
                }
            }

            @Override
            public void onError(String error) {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(VoucherHistoryActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();

                if (!loadMore && adapter.getItemCount() == 0) {
                    showEmptyState();
                }
            }
        });
    }

    private void showEmptyState() {
        recyclerView.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHistoryItemClick(VoucherHistory history) {
        // Handle history item click - có thể mở detail của đơn hàng
        if (history.orderId != null) {
            Toast.makeText(this, "Xem chi tiết đơn hàng #" + history.orderId, Toast.LENGTH_SHORT).show();
            // TODO: Navigate to order detail
        } else {
            Toast.makeText(this, "Voucher: " + history.voucherCode, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
