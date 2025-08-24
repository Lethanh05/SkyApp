package com.example.skymall.ui.orders;

import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skymall.R;
import com.example.skymall.data.remote.DTO.OrderDto;
import com.example.skymall.data.remote.DTO.OrderListResp;
import com.example.skymall.data.repository.CustomerOrderRepository;
import java.util.*;

public class OrderListFragment extends Fragment {
    private static final String ARG_STATUS = "status";
    public static OrderListFragment newInstance(String status){
        Bundle b = new Bundle(); b.putString(ARG_STATUS, status);
        OrderListFragment f = new OrderListFragment(); f.setArguments(b); return f;
    }

    private RecyclerView rv;
    private TextView tvEmpty;
    private OrderListAdapter adapter;
    private CustomerOrderRepository orderRepository;
    private int currentPage = 1;
    private boolean isLoading = false;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup parent, @Nullable Bundle s) {
        return inf.inflate(R.layout.fragment_order_list, parent, false);
    }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        rv = v.findViewById(R.id.rvOrders);
        tvEmpty = v.findViewById(R.id.tvEmpty);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        orderRepository = new CustomerOrderRepository(requireContext());

        adapter = new OrderListAdapter(o -> {
            // mở chi tiết
            getParentFragmentManager().beginTransaction()
                    .replace(((ViewGroup)requireView().getParent()).getId(), OrderDetailFragment.newInstance(o.id))
                    .addToBackStack(null).commit();
        });
        rv.setAdapter(adapter);
        loadCustomerOrders();
    }

    private void loadCustomerOrders(){
        if (isLoading) return;
        isLoading = true;

        orderRepository.getCustomerOrders(currentPage, 20, new CustomerOrderRepository.OrderListCallback() {
            @Override
            public void onSuccess(OrderListResp response) {
                isLoading = false;
                if (getContext() == null) return;

                List<OrderDto> orders = response.data != null ? response.data : Collections.emptyList();

                // Filter by status if specified
                String targetStatus = getArguments() != null ? getArguments().getString(ARG_STATUS) : null;
                if (targetStatus != null && !targetStatus.isEmpty()) {
                    orders = filterOrdersByStatus(orders, targetStatus);
                }

                adapter.submitList(orders);
                tvEmpty.setVisibility(orders.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(String error) {
                isLoading = false;
                if (getContext() != null) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    tvEmpty.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private List<OrderDto> filterOrdersByStatus(List<OrderDto> orders, String status) {
        List<OrderDto> filtered = new ArrayList<>();
        for (OrderDto order : orders) {
            if (status.equals(order.status)) {
                filtered.add(order);
            }
        }
        return filtered;
    }
}
