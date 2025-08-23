package com.example.skymall.ui.voucher;

import android.os.Bundle;
import android.view.*; import android.widget.*;
import androidx.annotation.*; import androidx.fragment.app.Fragment; import androidx.recyclerview.widget.*;
import com.example.skymall.R;
import com.example.skymall.data.model.Voucher;
import com.example.skymall.data.remote.ApiClient;
import com.example.skymall.data.remote.ApiService;
import java.util.*;
import retrofit2.*;

public class VoucherListFragment extends Fragment {
    private RecyclerView rv; private View emptyView; private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipe;
    private VoucherAdapter adapter; private ApiService api;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle s) {
        View v = inf.inflate(R.layout.fragment_voucher_list, c, false);
        swipe = v.findViewById(R.id.swipe);
        rv = v.findViewById(R.id.rvVouchers);
        emptyView = v.findViewById(R.id.emptyView);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new VoucherAdapter();
        adapter.setOnVoucherClickListener(this::applyVoucher);
        rv.setAdapter(adapter);

        api = ApiClient.create(requireContext());

        swipe.setOnRefreshListener(this::loadVouchers);
        loadVouchers();
        return v;
    }

    private void loadVouchers() {
        swipe.setRefreshing(true);
        api.getVouchers(1, 50).enqueue(new retrofit2.Callback<com.example.skymall.data.remote.DTO.VoucherListResp>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.skymall.data.remote.DTO.VoucherListResp> call, retrofit2.Response<com.example.skymall.data.remote.DTO.VoucherListResp> response) {
                swipe.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    List<Voucher> vouchers = response.body().vouchers;
                    adapter.setVouchers(vouchers != null ? vouchers : new ArrayList<>());
                    emptyView.setVisibility((vouchers == null || vouchers.isEmpty()) ? View.VISIBLE : View.GONE);
                } else {
                    adapter.setVouchers(new ArrayList<>());
                    emptyView.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "Không lấy được danh sách voucher", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(retrofit2.Call<com.example.skymall.data.remote.DTO.VoucherListResp> call, Throwable t) {
                swipe.setRefreshing(false);
                adapter.setVouchers(new ArrayList<>());
                emptyView.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyVoucher(Voucher v) {
        Toast.makeText(getContext(), "Đã chọn: " + v.code, Toast.LENGTH_SHORT).show();
        // TODO: trả về cho giỏ hàng/checkout qua ViewModel/FragmentResult
    }
}
