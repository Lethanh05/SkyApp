package com.example.skymall.ui.orders;

import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skymall.R;
import com.example.skymall.data.remote.ApiService;
import com.example.skymall.data.remote.DTO.OrderDto;
import com.example.skymall.utils.MoneyFmt;
import java.util.*;
import retrofit2.*;

public class OrderListFragment extends Fragment {
    private static final String ARG_STATUS = "status";
    public static OrderListFragment newInstance(String status){
        Bundle b = new Bundle(); b.putString(ARG_STATUS, status);
        OrderListFragment f = new OrderListFragment(); f.setArguments(b); return f;
    }

    private RecyclerView rv; private TextView tvEmpty; private OrderListAdapter adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup parent, @Nullable Bundle s) {
        return inf.inflate(R.layout.fragment_order_list, parent, false);
    }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        rv = v.findViewById(R.id.rvOrders); tvEmpty = v.findViewById(R.id.tvEmpty);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrderListAdapter(o -> {
            // mở chi tiết
            getParentFragmentManager().beginTransaction()
                    .replace(((ViewGroup)requireView().getParent()).getId(), OrderDetailFragment.newInstance(o.id))
                    .addToBackStack(null).commit();
        });
        rv.setAdapter(adapter);
        loadPage(1);
    }

    private void loadPage(int page){
        String status = getArguments()!=null? getArguments().getString(ARG_STATUS): null;
        ApiService api = com.example.skymall.data.remote.ApiClient.create(requireContext());
        api.getOrders(status, page).enqueue(new Callback<List<OrderDto>>() {
            @Override public void onResponse(Call<List<OrderDto>> c, Response<List<OrderDto>> r) {
                List<OrderDto> data = r.body()!=null? r.body(): Collections.emptyList();
                adapter.submitList(data);
                tvEmpty.setVisibility(data.isEmpty()? View.VISIBLE: View.GONE);
            }
            @Override public void onFailure(Call<List<OrderDto>> c, Throwable t) {
                adapter.submitList(Collections.emptyList());
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText("Không tải được đơn hàng 😢");
            }
        });
    }
}
