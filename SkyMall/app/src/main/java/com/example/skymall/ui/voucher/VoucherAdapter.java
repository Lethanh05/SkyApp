package com.example.skymall.ui.voucher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skymall.R;
import com.example.skymall.data.model.Voucher;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {
    private List<Voucher> vouchers = new ArrayList<>();
    private OnVoucherClickListener onVoucherClickListener;

    public interface OnVoucherClickListener {
        void onVoucherClick(Voucher voucher);
    }

    public void setOnVoucherClickListener(OnVoucherClickListener listener) {
        this.onVoucherClickListener = listener;
    }

    public void setVouchers(List<Voucher> vouchers) {
        this.vouchers = vouchers;
        notifyDataSetChanged();
    }

    public void addVouchers(List<Voucher> newVouchers) {
        int startPosition = this.vouchers.size();
        this.vouchers.addAll(newVouchers);
        notifyItemRangeInserted(startPosition, newVouchers.size());
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_voucher, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        Voucher voucher = vouchers.get(position);
        holder.bind(voucher);
    }

    @Override
    public int getItemCount() {
        return vouchers.size();
    }

    class VoucherViewHolder extends RecyclerView.ViewHolder {
        private TextView tvVoucherCode;
        private TextView tvVoucherValue;
        private TextView tvMinOrder;
        private TextView tvEndDate;
        private TextView tvUsageInfo;
        private View voucherCard;

        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVoucherCode = itemView.findViewById(R.id.tv_voucher_code);
            tvVoucherValue = itemView.findViewById(R.id.tv_voucher_value);
            tvMinOrder = itemView.findViewById(R.id.tv_min_order);
            tvEndDate = itemView.findViewById(R.id.tv_end_date);
            tvUsageInfo = itemView.findViewById(R.id.tv_usage_info);
            voucherCard = itemView.findViewById(R.id.voucher_card);
        }

        public void bind(Voucher voucher) {
            tvVoucherCode.setText(voucher.code);
            if ("percentage".equals(voucher.discountType)) {
                tvVoucherValue.setText("Giảm " + (int)voucher.discountValue + "%");
            } else {
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                tvVoucherValue.setText("Giảm " + formatter.format(voucher.discountValue));
            }
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            String minOrderText = "Đơn tối thiểu " + formatter.format(voucher.minOrderAmount);
            tvMinOrder.setText(minOrderText);

            if (voucher.expiryDate != null) {
                tvEndDate.setText("HSD: " + voucher.expiryDate);
                tvEndDate.setVisibility(View.VISIBLE);
            } else {
                tvEndDate.setVisibility(View.GONE);
            }

            // Hiển thị thông tin sử dụng
            StringBuilder usageInfo = new StringBuilder();
            if (voucher.usageLimit > 0) {
                int remaining = voucher.usageLimit - voucher.usedCount;
                usageInfo.append("Còn ").append(remaining)
                        .append("/").append(voucher.usageLimit);
            }
            if (voucher.perUserLimit > 0) {
                if (usageInfo.length() > 0) usageInfo.append(" • ");
                usageInfo.append("Bạn đã dùng ").append(voucher.userUsed)
                        .append("/").append(voucher.perUserLimit);
            }
            if (voucher.perUserLimit > 0) {
                if (usageInfo.length() > 0) usageInfo.append(" • ");
                usageInfo.append("Bạn đã dùng ").append(voucher.userUsed)
                        .append("/").append(voucher.perUserLimit);
            }
            if (voucher.perUserLimit > 0) {
                if (usageInfo.length() > 0) usageInfo.append(" • ");
                usageInfo.append("Bạn đã dùng ").append(voucher.userUsed)
                        .append("/").append(voucher.perUserLimit);
            }
            if (voucher.perUserLimit > 0) {
                if (usageInfo.length() > 0) usageInfo.append(" • ");
                usageInfo.append("Bạn đã dùng ").append(voucher.userUsed)
                        .append("/").append(voucher.perUserLimit);
            }
            if (voucher.perUserLimit > 0) {
                if (usageInfo.length() > 0) usageInfo.append(" • ");
                usageInfo.append("Bạn đã dùng ").append(voucher.userUsed)
                        .append("/").append(voucher.perUserLimit);
            }
            if (voucher.perUserLimit > 0) {
                if (usageInfo.length() > 0) usageInfo.append(" • ");
                usageInfo.append("Bạn đã dùng ").append(voucher.userUsed)
                        .append("/").append(voucher.perUserLimit);
            }
            if (voucher.perUserLimit > 0) {
                if (usageInfo.length() > 0) usageInfo.append(" • ");
                usageInfo.append("Bạn đã dùng ").append(voucher.userUsed)
                        .append("/").append(voucher.perUserLimit);
            }
            if (voucher.perUserLimit > 0) {
                if (usageInfo.length() > 0) usageInfo.append(" • ");
                usageInfo.append("Bạn đã dùng ").append(voucher.userUsed)
                        .append("/").append(voucher.perUserLimit);
            }
            if (voucher.perUserLimit > 0) {
                if (usageInfo.length() > 0) usageInfo.append(" • ");
                usageInfo.append("Bạn đã dùng ").append(voucher.userUsed)
                        .append("/").append(voucher.perUserLimit);
            }
            if (voucher.perUserLimit > 0) {
                if (usageInfo.length() > 0) usageInfo.append(" • ");
                usageInfo.append("Bạn đã dùng ").append(voucher.userUsed)
                        .append("/").append(voucher.perUserLimit);
            }
            if (voucher.perUserLimit > 0) {
                if (usageInfo.length() > 0) usageInfo.append(" • ");
                usageInfo.append("Bạn đã dùng ").append(voucher.userUsed)
                        .append("/").append(voucher.perUserLimit);
            }
            if (voucher.perUserLimit > 0) {
                if (usageInfo.length() > 0) usageInfo.append(" • ");
                usageInfo.append("Bạn đã dùng ").append(voucher.userUsed)
                        .append("/").append(voucher.perUserLimit);
            }
            if (voucher.perUserLimit > 0) {
                if (usageInfo.length() > 0) usageInfo.append(" • ");
                usageInfo.append("Bạn đã dùng ").append(voucher.userUsed)
                        .append("/").append(voucher.perUserLimit);
            }
            if (voucher.perUserLimit > 0) {
                if (usageInfo.length() > 0) usageInfo.append(" • ");
                usageInfo.append("Bạn đã dùng ").append(voucher.userUsed)
                        .append("/").append(voucher.perUserLimit);
            }
            if (voucher.perUserLimit > 0) {
                if (usageInfo.length() > 0) usageInfo.append(" • ");
                usageInfo.append("Bạn đã dùng ").append(voucher.userUsed)
                        .append("/").append(voucher.perUserLimit);
            }
            if (voucher.perUserLimit > 0) {
                if (usageInfo.length() > 0) usageInfo.append(" • ");
                usageInfo.append("Bạn đã dùng ").append(voucher.userUsed)
                        .append("/").append(voucher.perUserLimit);
            }

            if (usageInfo.length() > 0) {
                tvUsageInfo.setText(usageInfo.toString());
                tvUsageInfo.setVisibility(View.VISIBLE);
            } else {
                tvUsageInfo.setVisibility(View.GONE);
            }

            // Xử lý click - sử dụng isValid() method có sẵn
            voucherCard.setOnClickListener(v -> {
                if (onVoucherClickListener != null && voucher.isValid()) {
                    onVoucherClickListener.onVoucherClick(voucher);
                }
            });

            // Thay đổi giao diện nếu voucher không thể sử dụng
            voucherCard.setAlpha(voucher.isValid() ? 1.0f : 0.5f);
        }
    }
}
