package com.example.skymall.ui.seller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skymall.R;
import com.example.skymall.data.model.Voucher;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class VoucherManagementAdapter extends RecyclerView.Adapter<VoucherManagementAdapter.VoucherViewHolder> {

    private List<Voucher> voucherList;
    private OnVoucherActionListener listener;
    private NumberFormat currencyFormat;

    public interface OnVoucherActionListener {
        void onEdit(Voucher voucher);
        void onDelete(Voucher voucher);
        void onToggleStatus(Voucher voucher);
        void onDuplicate(Voucher voucher);
        void onViewDetails(Voucher voucher);
    }

    public VoucherManagementAdapter(List<Voucher> voucherList, OnVoucherActionListener listener) {
        this.voucherList = voucherList;
        this.listener = listener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_voucher_management, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        Voucher voucher = voucherList.get(position);
        holder.bind(voucher);
    }

    @Override
    public int getItemCount() {
        return voucherList.size();
    }

    class VoucherViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardView;
        private TextView tvCode, tvDescription, tvValue, tvMinOrder, tvUsage, tvDates;
        private Chip chipType, chipStatus;
        private ImageButton btnEdit, btnDelete, btnDuplicate;

        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardVoucher);
            tvCode = itemView.findViewById(R.id.tvVoucherCode);
            tvDescription = itemView.findViewById(R.id.tvVoucherDescription);
            tvValue = itemView.findViewById(R.id.tvVoucherValue);
            tvMinOrder = itemView.findViewById(R.id.tvMinOrderValue);
            tvUsage = itemView.findViewById(R.id.tvUsageInfo);
            tvDates = itemView.findViewById(R.id.tvValidityDates);
            chipType = itemView.findViewById(R.id.chipVoucherType);
            chipStatus = itemView.findViewById(R.id.chipStatus);
            btnDuplicate = itemView.findViewById(R.id.btnDuplicateVoucher);
            btnEdit = itemView.findViewById(R.id.btnEditVoucher);
            btnDelete = itemView.findViewById(R.id.btnDeleteVoucher);
        }

        public void bind(Voucher voucher) {
            tvCode.setText(voucher.code);
            tvDescription.setText(voucher.description != null ? voucher.description : "Không có mô tả");

            // Format value based on discountType
            if (voucher.discountType != null && voucher.discountType.equals("percentage")) {
                tvValue.setText((int)voucher.discountValue + "%");
            } else {
                tvValue.setText(currencyFormat.format(voucher.discountValue));
            }

            tvMinOrder.setText("Đơn tối thiểu: " + currencyFormat.format(voucher.minOrderAmount));

            // Usage info
            StringBuilder usageInfo = new StringBuilder();
            usageInfo.append("Đã dùng: " + voucher.usedCount);
            if (voucher.usageLimit > 0) {
                usageInfo.append("/").append(voucher.usageLimit);
                chipStatus.setChipBackgroundColorResource(R.color.chip_active_bg);
            }
            tvUsage.setText(usageInfo.toString());

            // Dates
            tvDates.setText("HSD: " + voucher.expiryDate);

            // Status
            if (voucher.isActive) {
                chipStatus.setText("Hoạt động");
                chipStatus.setChipBackgroundColorResource(R.color.chip_active_bg);
                chipStatus.setTextColor(itemView.getContext().getColor(R.color.chip_active_text));
            } else {
                chipStatus.setText("Không hoạt động");
                chipStatus.setChipBackgroundColorResource(R.color.chip_inactive_bg);
                chipStatus.setTextColor(itemView.getContext().getColor(R.color.chip_inactive_text));
            }

            // Button listeners
            btnDuplicate.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDuplicate(voucher);
                }
            });

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEdit(voucher);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(voucher);
                }
            });
        }
    }
}
