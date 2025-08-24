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

            // Format value based on type - sử dụng discountType và discountValue
            if ("percentage".equals(voucher.discountType)) {
                tvValue.setText(String.format("Giảm %.0f%%", voucher.discountValue));
                chipType.setText("Giảm %");
                chipType.setChipBackgroundColorResource(android.R.color.holo_blue_light);
            } else {
                tvValue.setText("Giảm " + currencyFormat.format(voucher.discountValue));
                chipType.setText("Giảm cố định");
                chipType.setChipBackgroundColorResource(android.R.color.holo_green_light);
            }

            // Sử dụng minOrderAmount thay vì minOrderValue
            tvMinOrder.setText("Đơn tối thiểu: " + currencyFormat.format(voucher.minOrderAmount));

            // Usage info - sử dụng các field có sẵn
            StringBuilder usageInfo = new StringBuilder();
            usageInfo.append("Đã dùng: ").append(voucher.usedCount);
            if (voucher.usageLimit > 0) { // usageLimit là int, không phải Integer
                usageInfo.append("/").append(voucher.usageLimit);
            } else {
                usageInfo.append(" (Không giới hạn)");
            }
            tvUsage.setText(usageInfo.toString());

            // Validity dates - chỉ hiển thị expiryDate
            if (voucher.expiryDate != null && !voucher.expiryDate.isEmpty()) {
                tvDates.setText("Hết hạn: " + voucher.expiryDate);
            } else {
                tvDates.setText("Không thời hạn");
            }

            // Status chip - isActive là boolean, không phải Boolean
            if (voucher.isActive) {
                chipStatus.setText("Hoạt động");
                chipStatus.setChipBackgroundColorResource(android.R.color.holo_green_light);
                chipStatus.setTextColor(itemView.getContext().getColor(android.R.color.white));
            } else {
                chipStatus.setText("Tạm dừng");
                chipStatus.setChipBackgroundColorResource(android.R.color.holo_red_light);
                chipStatus.setTextColor(itemView.getContext().getColor(android.R.color.white));
            }

            // Check if expired - sử dụng method isValid() có sẵn
            if (!voucher.isValid()) {
                chipStatus.setText("Hết hạn/Hết lượt");
                chipStatus.setChipBackgroundColorResource(android.R.color.darker_gray);
                chipStatus.setTextColor(itemView.getContext().getColor(android.R.color.white));
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

            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewDetails(voucher);
                }
            });

            cardView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onToggleStatus(voucher);
                }
                return true;
            });
        }
    }
}
