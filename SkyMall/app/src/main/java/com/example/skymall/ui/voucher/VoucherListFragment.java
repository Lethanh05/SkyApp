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
        adapter = new VoucherAdapter(this::applyVoucher);
        rv.setAdapter(adapter);

        api = ApiClient.create(requireContext());

        swipe.setOnRefreshListener(this::loadVouchers);
        loadVouchers();
        return v;
    }

    private void loadVouchers() {
        swipe.setRefreshing(true);
        // TODO: gọi API thật: api.getVouchersAll()
        // Demo dữ liệu giả:
        List<Voucher> demo = new ArrayList<>();
        demo.add(mock("Giảm 30k", 199000, System.currentTimeMillis(), System.currentTimeMillis()+7L*86400000, false, false, "Áp dụng toàn shop"));
        demo.add(mock("Giảm 10%", 0, System.currentTimeMillis(), System.currentTimeMillis()+30L*86400000, false, false, "Tối đa 50k"));
        demo.add(mock("Giảm 50k", 299000, System.currentTimeMillis()-20L*86400000, System.currentTimeMillis()-1L*86400000, false, true, "Hết hạn"));

        adapter.submit(demo);
        emptyView.setVisibility(demo.isEmpty() ? View.VISIBLE : View.GONE);
        swipe.setRefreshing(false);
    }

    private Voucher mock(String title, int minSpend, long start, long end, boolean used, boolean expired, String note){
        Voucher v = new Voucher();
        v.id = UUID.randomUUID().toString();
        v.title = title; v.minSpend = minSpend; v.startAt = start; v.endAt = end; v.used = used; v.expired = expired; v.note = note;
        return v;
    }

    private void applyVoucher(Voucher v) {
        Toast.makeText(getContext(), "Đã chọn: " + v.title, Toast.LENGTH_SHORT).show();
        // TODO: trả về cho giỏ hàng/checkout qua ViewModel/FragmentResult
    }
}
