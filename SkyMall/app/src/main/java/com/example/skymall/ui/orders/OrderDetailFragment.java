package com.example.skymall.ui.orders;

import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skymall.R;
import com.example.skymall.data.remote.DTO.OrderDetailResp;
import com.example.skymall.data.repository.CustomerOrderRepository;
import com.example.skymall.utils.MoneyFmt;

public class OrderDetailFragment extends Fragment {
    private static final String ARG_ID = "id";
    public static OrderDetailFragment newInstance(int id){
        Bundle b = new Bundle(); b.putInt(ARG_ID,id);
        OrderDetailFragment f = new OrderDetailFragment(); f.setArguments(b); return f;
    }

    private TextView tvOrderCode,tvStatus,tvCreatedAt,tvReceiver,tvAddressFull,tvSubtotal,tvDiscount,tvShip,tvVoucher,tvGrandTotal;
    private RecyclerView rvItems, rvTimeline;
    private Button btnCancel;
    private CustomerOrderRepository orderRepository;
    private int orderId;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup parent, @Nullable Bundle s) {
        return inf.inflate(R.layout.fragment_order_detail, parent, false);
    }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        tvOrderCode=v.findViewById(R.id.tvOrderCode);
        tvStatus=v.findViewById(R.id.tvStatus);
        tvCreatedAt=v.findViewById(R.id.tvCreatedAt);
        tvReceiver=v.findViewById(R.id.tvReceiver);
        tvAddressFull=v.findViewById(R.id.tvAddressFull);
        tvSubtotal=v.findViewById(R.id.tvSubtotal);
        tvDiscount=v.findViewById(R.id.tvDiscount);
        tvShip=v.findViewById(R.id.tvShip);
        tvVoucher=v.findViewById(R.id.tvVoucher);
        tvGrandTotal=v.findViewById(R.id.tvGrandTotal);
        btnCancel=v.findViewById(R.id.btnCancel);

        rvItems=v.findViewById(R.id.rvItems);
        rvItems.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTimeline=v.findViewById(R.id.rvTimeline);
        rvTimeline.setLayoutManager(new LinearLayoutManager(getContext()));

        orderRepository = new CustomerOrderRepository(requireContext());
        orderId = getArguments()!=null? getArguments().getInt(ARG_ID):0;

        // Load order details using new customer API
        loadOrderDetail();

        // Setup cancel button
        btnCancel.setOnClickListener(view -> showCancelConfirmation());
    }

    private void loadOrderDetail() {
        orderRepository.getOrderDetail(orderId, new CustomerOrderRepository.OrderDetailCallback() {
            @Override
            public void onSuccess(OrderDetailResp.OrderDetailData orderDetail) {
                if (getContext() == null) return;
                populateOrderDetails(orderDetail);
            }

            @Override
            public void onError(String error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void populateOrderDetails(OrderDetailResp.OrderDetailData orderDetailData) {
        OrderDetailResp.OrderInfo order = orderDetailData.order;

        tvOrderCode.setText("#" + order.id);
        tvStatus.setText(getStatusText(order.status));
        tvCreatedAt.setText(order.date);
        tvReceiver.setText(order.receiver_name + " - " + order.receiver_phone);

        String fullAddress = order.address_line + ", " + order.ward + ", " + order.district + ", " + order.province;
        tvAddressFull.setText(fullAddress);

        tvSubtotal.setText(MoneyFmt.vnd(order.subtotal));
        tvDiscount.setText(MoneyFmt.vnd(order.discount));
        tvShip.setText(MoneyFmt.vnd(order.shipping_fee));
        tvVoucher.setText(order.voucher_code != null ? order.voucher_code : "Không sử dụng");
        tvGrandTotal.setText(MoneyFmt.vnd(order.grand_total));

        // Show/hide cancel button based on order status
        btnCancel.setVisibility("pending".equals(order.status) ? View.VISIBLE : View.GONE);

        // Setup items RecyclerView
        if (orderDetailData.items != null) {
            OrderItemsNewAdapter itemsAdapter = new OrderItemsNewAdapter(orderDetailData.items);
            rvItems.setAdapter(itemsAdapter);
        }

        // Setup timeline RecyclerView
        if (orderDetailData.status_history != null) {
            OrderTimelineNewAdapter timelineAdapter = new OrderTimelineNewAdapter(orderDetailData.status_history);
            rvTimeline.setAdapter(timelineAdapter);
        }
    }

    private void showCancelConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Hủy đơn hàng")
                .setMessage("Bạn có chắc chắn muốn hủy đơn hàng này?")
                .setPositiveButton("Hủy đơn hàng", (dialog, which) -> cancelOrder())
                .setNegativeButton("Không", null)
                .show();
    }

    private void cancelOrder() {
        orderRepository.cancelOrder(orderId, new CustomerOrderRepository.CancelOrderCallback() {
            @Override
            public void onSuccess() {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Đơn hàng đã được hủy thành công", Toast.LENGTH_SHORT).show();
                    // Reload order details to show updated status
                    loadOrderDetail();
                }
            }

            @Override
            public void onError(String error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Không thể hủy đơn hàng: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getStatusText(String status) {
        switch (status) {
            case "pending": return "Chờ xác nhận";
            case "paid": return "Đã thanh toán";
            case "processing": return "Đang xử lý";
            case "shipped": return "Đang vận chuyển";
            case "completed": return "Hoàn tất";
            case "cancelled": return "Đã hủy";
            default: return status;
        }
    }
}
