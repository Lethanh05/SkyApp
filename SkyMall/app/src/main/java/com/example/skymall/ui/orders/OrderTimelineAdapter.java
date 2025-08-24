package com.example.skymall.ui.orders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skymall.R;
import com.example.skymall.data.remote.DTO.OrderDetailResp;
import java.util.List;

public class OrderTimelineAdapter extends RecyclerView.Adapter<OrderTimelineAdapter.ViewHolder> {
    private List<OrderDetailResp.OrderHistory> historyList;

    public OrderTimelineAdapter(List<OrderDetailResp.OrderHistory> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_timeline, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderDetailResp.OrderHistory history = historyList.get(position);

        holder.tvStatus.setText(getStatusText(history.new_status));
        holder.tvTime.setText(history.changed_at);
        holder.tvNote.setText(history.note != null ? history.note : "");

        // Show/hide note if empty
        holder.tvNote.setVisibility(
            history.note != null && !history.note.isEmpty() ? View.VISIBLE : View.GONE
        );
    }

    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatus, tvTime, tvNote;

        ViewHolder(View itemView) {
            super(itemView);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvNote = itemView.findViewById(R.id.tvNote);
        }
    }
}
