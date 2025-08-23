package com.example.skymall.ui.voucher;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.skymall.R;
import com.example.skymall.data.model.Voucher;
import com.example.skymall.data.remote.DTO.VoucherListResp;
import com.example.skymall.data.repository.VoucherRepository;

public class VoucherActivity extends AppCompatActivity implements VoucherAdapter.OnVoucherClickListener {

    private RecyclerView recyclerView;
    private VoucherAdapter adapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    private VoucherRepository voucherRepository;
    private int currentPage = 1;
    private final int limit = 20;
    private boolean isLoading = false;
    private boolean hasMoreData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);

        initViews();
        setupRecyclerView();
        setupSwipeRefresh();

        voucherRepository = new VoucherRepository(this);
        loadVouchers(false);
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_vouchers);
        progressBar = findViewById(R.id.progress_bar);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        // Setup toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Voucher của tôi");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        adapter = new VoucherAdapter();
        adapter.setOnVoucherClickListener(this);

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
                        loadVouchers(true);
                    }
                }
            }
        });
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            currentPage = 1;
            hasMoreData = true;
            loadVouchers(false);
        });
    }

    private void loadVouchers(boolean loadMore) {
        if (isLoading) return;

        isLoading = true;

        if (!loadMore) {
            progressBar.setVisibility(View.VISIBLE);
        }

        voucherRepository.getVouchers(currentPage, limit, new VoucherRepository.VoucherListCallback() {
            @Override
            public void onSuccess(VoucherListResp response) {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                if (response.vouchers != null) {
                    if (loadMore) {
                        adapter.addVouchers(response.vouchers);
                    } else {
                        adapter.setVouchers(response.vouchers);
                    }

                    // Check if there's more data
                    if (response.pagination != null) {
                        hasMoreData = currentPage < response.pagination.totalPages;
                        if (hasMoreData) {
                            currentPage++;
                        }
                    }
                } else {
                    hasMoreData = false;
                }
            }

            @Override
            public void onError(String error) {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(VoucherActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onVoucherClick(Voucher voucher) {
        // Handle voucher selection - có thể trả về result cho activity gọi
        Toast.makeText(this, "Đã chọn voucher: " + voucher.code, Toast.LENGTH_SHORT).show();

        // Nếu được gọi từ activity khác để chọn voucher
        if (getIntent().getBooleanExtra("select_mode", false)) {
            // Return selected voucher
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
