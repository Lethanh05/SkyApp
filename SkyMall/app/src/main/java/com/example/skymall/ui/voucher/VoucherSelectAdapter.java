package com.example.skymall.ui.voucher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skymall.R;
import com.example.skymall.data.model.Voucher;
import com.example.skymall.utils.MoneyFmt;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VoucherSelectAdapter extends RecyclerView.Adapter<VoucherSelectAdapter.VoucherViewHolder> {

    private List<Voucher> vouchers;
    private Voucher selectedVoucher;
    private OnVoucherClickListener listener;

    public interface OnVoucherClickListener {
        void onVoucherClick(Voucher voucher);
    }

    public VoucherSelectAdapter(List<Voucher> vouchers, Voucher selectedVoucher, OnVoucherClickListener listener) {
        this.vouchers = vouchers;
        this.selectedVoucher = selectedVoucher;
        this.listener = listener;
    }

    public void updateVouchers(List<Voucher> newVouchers) {
        this.vouchers.clear();
        this.vouchers.addAll(newVouchers);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_voucher_select, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        Voucher voucher = vouchers.get(position);

        holder.tvTitle.setText(voucher.title);
        holder.tvDescription.setText(voucher.description);

        // Format discount text
        if ("percentage".equals(voucher.discountType)) {
            holder.tvDiscount.setText("Giảm " + (int)voucher.discountValue + "%");
            if (voucher.maxDiscountAmount > 0) {
                holder.tvMaxDiscount.setText("Tối đa " + MoneyFmt.format(voucher.maxDiscountAmount));
                holder.tvMaxDiscount.setVisibility(View.VISIBLE);
            } else {
                holder.tvMaxDiscount.setVisibility(View.GONE);
            }
        } else {
            holder.tvDiscount.setText("Giảm " + MoneyFmt.format(voucher.discountValue));
            holder.tvMaxDiscount.setVisibility(View.GONE);
        }

        // Format expiry date
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date expiryDate = inputFormat.parse(voucher.expiryDate);
            holder.tvExpiry.setText("HSD: " + outputFormat.format(expiryDate));
        } catch (Exception e) {
            holder.tvExpiry.setText("HSD: " + voucher.expiryDate);
        }

        // Set minimum order text
        if (voucher.minOrderAmount > 0) {
            holder.tvMinOrder.setText("Đơn tối thiểu " + MoneyFmt.format(voucher.minOrderAmount));
            holder.tvMinOrder.setVisibility(View.VISIBLE);
        } else {
            holder.tvMinOrder.setVisibility(View.GONE);
        }

        // Set selection state
        boolean isSelected = selectedVoucher != null && selectedVoucher.id == voucher.id;
        holder.radioButton.setChecked(isSelected);

        holder.itemView.setOnClickListener(v -> {
            selectedVoucher = voucher;
            notifyDataSetChanged();
            if (listener != null) {
                listener.onVoucherClick(voucher);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vouchers.size();
    }

    static class VoucherViewHolder extends RecyclerView.ViewHolder {
        RadioButton radioButton;
        TextView tvTitle, tvDescription, tvDiscount, tvMaxDiscount, tvExpiry, tvMinOrder;

        VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.radioButton);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
            tvMaxDiscount = itemView.findViewById(R.id.tvMaxDiscount);
            tvExpiry = itemView.findViewById(R.id.tvExpiry);
            tvMinOrder = itemView.findViewById(R.id.tvMinOrder);
        }
    }
}
