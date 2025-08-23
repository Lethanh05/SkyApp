package com.example.skymall.ui.voucher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skymall.R;
import com.example.skymall.data.model.VoucherHistory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VoucherHistoryAdapter extends RecyclerView.Adapter<VoucherHistoryAdapter.HistoryViewHolder> {
    private List<VoucherHistory> historyList = new ArrayList<>();
    private OnHistoryItemClickListener onItemClickListener;

    public interface OnHistoryItemClickListener {
        void onHistoryItemClick(VoucherHistory history);
    }

    public void setOnItemClickListener(OnHistoryItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setHistoryList(List<VoucherHistory> historyList) {
        this.historyList = historyList;
        notifyDataSetChanged();
    }

    public void addHistoryList(List<VoucherHistory> newHistory) {
        int startPosition = this.historyList.size();
        this.historyList.addAll(newHistory);
        notifyItemRangeInserted(startPosition, newHistory.size());
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_voucher_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        VoucherHistory history = historyList.get(position);
        holder.bind(history);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvVoucherCode;
        private TextView tvVoucherValue;
        private TextView tvOrderId;
        private TextView tvOrderTotal;
        private TextView tvUsedDate;
        private View historyCard;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVoucherCode = itemView.findViewById(R.id.tv_voucher_code);
            tvVoucherValue = itemView.findViewById(R.id.tv_voucher_value);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderTotal = itemView.findViewById(R.id.tv_order_total);
            tvUsedDate = itemView.findViewById(R.id.tv_used_date);
            historyCard = itemView.findViewById(R.id.history_card);
        }

        public void bind(VoucherHistory history) {
            tvVoucherCode.setText(history.voucherCode);
            tvVoucherValue.setText("Giảm " + history.getDisplayValue());

            if (history.orderId != null) {
                tvOrderId.setText("Đơn hàng #" + history.orderId);
                tvOrderId.setVisibility(View.VISIBLE);
            } else {
                tvOrderId.setVisibility(View.GONE);
            }

            if (history.orderTotal != null) {
                tvOrderTotal.setText("Tổng tiền: " + history.getFormattedOrderTotal());
                tvOrderTotal.setVisibility(View.VISIBLE);
            } else {
                tvOrderTotal.setVisibility(View.GONE);
            }

            // Format date
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                Date date = inputFormat.parse(history.usedAt);
                if (date != null) {
                    tvUsedDate.setText("Đã dùng: " + outputFormat.format(date));
                } else {
                    tvUsedDate.setText("Đã dùng: " + history.usedAt);
                }
            } catch (Exception e) {
                tvUsedDate.setText("Đã dùng: " + history.usedAt);
            }

            // Click listener
            historyCard.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onHistoryItemClick(history);
                }
            });
        }
    }
}
