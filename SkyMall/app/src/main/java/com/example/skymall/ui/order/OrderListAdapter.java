package com.example.skymall.ui.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skymall.R;
import com.example.skymall.data.remote.DTO.OrderDto;
import com.example.skymall.utils.MoneyFmt;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderViewHolder> {

    private List<OrderDto> orders;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(OrderDto order);
    }

    public OrderListAdapter(List<OrderDto> orders, OnOrderClickListener listener) {
        this.orders = orders;
        this.listener = listener;
    }

    public void updateOrders(List<OrderDto> newOrders) {
        this.orders.clear();
        this.orders.addAll(newOrders);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderDto order = orders.get(position);

        holder.tvOrderId.setText(String.format("Đơn hàng #%d", order.id));
        holder.tvOrderDate.setText(formatDate(order.created_at)); // Sử dụng created_at thay vì createdAt
        holder.tvTotalAmount.setText(MoneyFmt.format(order.grand_total)); // Sử dụng grand_total thay vì totalAmount

        // OrderDto không có field items, sử dụng placeholder hoặc loại bỏ
        holder.tvItemCount.setText("Xem chi tiết"); // Thay vì hiển thị số items

        // Set status text and color
        setOrderStatus(holder, order.status);

        // Setup progress dots
        setupProgressDots(holder.progressContainer, order.status);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order);
            }
        });
    }

    private void setOrderStatus(OrderViewHolder holder, String status) {
        String statusText;
        int statusColor;

        switch (status) {
            case "PENDING":
                statusText = "Chờ xác nhận";
                statusColor = R.color.warning;
                break;
            case "PREPARING":
                statusText = "Đang chuẩn bị";
                statusColor = R.color.info;
                break;
            case "SHIPPING":
                statusText = "Đang giao hàng";
                statusColor = R.color.skymall_primary;
                break;
            case "COMPLETED":
                statusText = "Hoàn thành";
                statusColor = R.color.success;
                break;
            case "CANCELLED":
                statusText = "Đã hủy";
                statusColor = R.color.error;
                break;
            default:
                statusText = "Không xác định";
                statusColor = R.color.gray_500;
                break;
        }

        holder.tvStatus.setText(statusText);
        holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(statusColor));
    }

    private void setupProgressDots(LinearLayout container, String status) {
        container.removeAllViews();

        String[] statuses = {"PENDING", "PREPARING", "SHIPPING", "COMPLETED"};
        String[] statusLabels = {"Đặt hàng", "Chuẩn bị", "Giao hàng", "Hoàn thành"};

        int currentIndex = getCurrentStatusIndex(status);
        boolean isCancelled = "CANCELLED".equals(status);

        for (int i = 0; i < statuses.length; i++) {
            View progressItem = LayoutInflater.from(container.getContext())
                    .inflate(R.layout.item_progress_dot, container, false);

            View dot = progressItem.findViewById(R.id.progressDot);
            TextView label = progressItem.findViewById(R.id.progressLabel);
            View line = progressItem.findViewById(R.id.progressLine);

            label.setText(statusLabels[i]);

            if (isCancelled) {
                // All dots are gray for cancelled orders
                dot.setBackgroundResource(R.drawable.progress_dot_inactive);
                label.setTextColor(container.getContext().getResources().getColor(R.color.gray_500));
            } else if (i <= currentIndex) {
                // Active dots
                dot.setBackgroundResource(R.drawable.progress_dot_active);
                label.setTextColor(container.getContext().getResources().getColor(R.color.success));
            } else {
                // Inactive dots
                dot.setBackgroundResource(R.drawable.progress_dot_inactive);
                label.setTextColor(container.getContext().getResources().getColor(R.color.gray_500));
            }

            // Hide line for the last item
            if (i == statuses.length - 1) {
                line.setVisibility(View.GONE);
            } else {
                line.setVisibility(View.VISIBLE);
                if (isCancelled || i >= currentIndex) {
                    line.setBackgroundColor(container.getContext().getResources().getColor(R.color.gray_300));
                } else {
                    line.setBackgroundColor(container.getContext().getResources().getColor(R.color.success));
                }
            }

            container.addView(progressItem);
        }
    }

    private int getCurrentStatusIndex(String status) {
        switch (status) {
            case "PENDING": return 0;
            case "PREPARING": return 1;
            case "SHIPPING": return 2;
            case "COMPLETED": return 3;
            default: return -1;
        }
    }

    private String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return outputFormat.format(inputFormat.parse(dateString));
        } catch (Exception e) {
            return dateString;
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvStatus, tvTotalAmount, tvItemCount;
        LinearLayout progressContainer;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            tvItemCount = itemView.findViewById(R.id.tvItemCount);
            progressContainer = itemView.findViewById(R.id.progressContainer);
        }
    }
}
