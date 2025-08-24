package com.example.skymall.ui.seller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skymall.R;
import com.example.skymall.data.model.VoucherStats;
import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TopVouchersAdapter extends RecyclerView.Adapter<TopVouchersAdapter.ViewHolder> {

    private List<VoucherStats.TopVoucher> topVouchers;
    private NumberFormat currencyFormat;

    public TopVouchersAdapter(List<VoucherStats.TopVoucher> topVouchers) {
        this.topVouchers = topVouchers;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_top_voucher, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VoucherStats.TopVoucher voucher = topVouchers.get(position);
        holder.bind(voucher, position + 1);
    }

    @Override
    public int getItemCount() {
        return topVouchers.size();
    }

    public void updateData(List<VoucherStats.TopVoucher> newData) {
        this.topVouchers = newData;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvRank, tvCode, tvUsageCount, tvUniqueUsers, tvTotalDiscount, tvValue;
        private MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            tvCode = itemView.findViewById(R.id.tvCode);
            tvUsageCount = itemView.findViewById(R.id.tvUsageCount);
            tvUniqueUsers = itemView.findViewById(R.id.tvUniqueUsers);
            tvTotalDiscount = itemView.findViewById(R.id.tvTotalDiscount);
            tvValue = itemView.findViewById(R.id.tvValue);
            cardView = itemView.findViewById(R.id.cardTopVoucher);
        }

        public void bind(VoucherStats.TopVoucher voucher, int rank) {
            tvRank.setText(String.valueOf(rank));
            tvCode.setText(voucher.code);
            tvUsageCount.setText(String.valueOf(voucher.usageCount));
            tvUniqueUsers.setText(String.valueOf(voucher.uniqueUsers));
            tvTotalDiscount.setText(currencyFormat.format(voucher.totalDiscount));

            // Format value based on type
            if ("percentage".equals(voucher.type)) {
                tvValue.setText(String.format("%.0f%%", voucher.value));
            } else {
                tvValue.setText(currencyFormat.format(voucher.value));
            }

            // Highlight top 3 with different colors
            if (rank == 1) {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(R.color.rank_gold));
            } else if (rank == 2) {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(R.color.rank_silver));
            } else if (rank == 3) {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(R.color.rank_bronze));
            } else {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(R.color.white));
            }
        }
    }
}
