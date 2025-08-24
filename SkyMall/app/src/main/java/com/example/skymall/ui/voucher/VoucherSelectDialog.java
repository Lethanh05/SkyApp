package com.example.skymall.ui.voucher;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skymall.R;
import com.example.skymall.data.model.Voucher;
import com.example.skymall.data.remote.ApiManager;
import com.example.skymall.data.remote.ApiService;
import com.example.skymall.data.remote.DTO.VoucherListResp;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoucherSelectDialog extends Dialog {

    private RecyclerView rvVouchers;
    private TextView tvNoVouchers;
    private VoucherSelectAdapter adapter;
    private ApiService api;
    private final Voucher selectedVoucher;
    private final OnVoucherSelectedListener listener;

    public interface OnVoucherSelectedListener {
        void onVoucherSelected(Voucher voucher);
    }

    public VoucherSelectDialog(@NonNull Context context, Voucher currentVoucher, OnVoucherSelectedListener listener) {
        super(context);
        this.selectedVoucher = currentVoucher;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_voucher_select);

        initViews();
        setupApi();
        setupRecyclerView();
        loadVouchers();
    }

    private void initViews() {
        rvVouchers = findViewById(R.id.rvVouchers);
        tvNoVouchers = findViewById(R.id.tvNoVouchers);

        findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());
        findViewById(R.id.btnClearVoucher).setOnClickListener(v -> {
            if (listener != null) {
                listener.onVoucherSelected(null);
            }
            dismiss();
        });
    }

    private void setupApi() {
        // Sử dụng ApiManager thống nhất
        api = ApiManager.getInstance(getContext()).getApiService();
    }

    private void setupRecyclerView() {
        adapter = new VoucherSelectAdapter(new ArrayList<>(), selectedVoucher, voucher -> {
            if (listener != null) {
                listener.onVoucherSelected(voucher);
            }
            dismiss();
        });
        rvVouchers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvVouchers.setAdapter(adapter);
    }

    private void loadVouchers() {
        api.getVouchers(1, 50).enqueue(new Callback<VoucherListResp>() {
            @Override
            public void onResponse(@NonNull Call<VoucherListResp> call, @NonNull Response<VoucherListResp> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Voucher> vouchers = response.body().vouchers;
                    if (vouchers.isEmpty()) {
                        tvNoVouchers.setVisibility(View.VISIBLE);
                        rvVouchers.setVisibility(View.GONE);
                    } else {
                        tvNoVouchers.setVisibility(View.GONE);
                        rvVouchers.setVisibility(View.VISIBLE);
                        adapter.updateVouchers(vouchers);
                    }
                } else {
                    Toast.makeText(getContext(), "Không thể tải danh sách voucher", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<VoucherListResp> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
