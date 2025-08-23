package com.example.skymall.ui.voucher;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skymall.R;
import com.example.skymall.data.model.Voucher;
import java.text.SimpleDateFormat;
import java.util.*;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VH> {

    public interface OnApplyClick { void onApply(Voucher v); }

    private final List<Voucher> data = new ArrayList<>();
    private final OnApplyClick onApply;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public VoucherAdapter(OnApplyClick onApply) { this.onApply = onApply; }

    public void submit(List<Voucher> list){
        data.clear(); if (list != null) data.addAll(list); notifyDataSetChanged();
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        View item = LayoutInflater.from(p.getContext()).inflate(R.layout.item_voucher, p, false);
        return new VH(item);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int i) {
        Voucher v = data.get(i);
        h.tvTitle.setText(v.title);
        String sub = "ĐH tối thiểu " + formatVnd(v.minSpend) + " • HSD: " + sdf.format(new Date(v.endAt));
        h.tvSubtitle.setText(sub);
        h.tvNote.setText(v.note != null ? v.note : "");

        boolean disabled = v.expired || v.used;
        h.btnApply.setEnabled(!disabled);
        h.btnApply.setText(disabled ? (v.expired ? "Hết hạn" : "Đã dùng") : "Áp dụng");
        h.btnApply.setOnClickListener(x -> { if (!disabled) onApply.onApply(v); });
    }

    @Override public int getItemCount(){ return data.size(); }

    static class VH extends RecyclerView.ViewHolder{
        TextView tvTitle, tvSubtitle, tvNote; Button btnApply;
        VH(@NonNull View v){
            super(v);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvSubtitle = v.findViewById(R.id.tvSubtitle);
            tvNote = v.findViewById(R.id.tvNote);
            btnApply = v.findViewById(R.id.btnApply);
        }
    }

    private String formatVnd(int v){
        return "₫" + String.format(Locale.getDefault(), "%,d", v).replace(',', '.');
    }
}
