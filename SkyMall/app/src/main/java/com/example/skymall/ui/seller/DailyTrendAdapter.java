package com.example.skymall.ui.seller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skymall.R;
import com.example.skymall.data.model.VoucherStats;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DailyTrendAdapter extends RecyclerView.Adapter<DailyTrendAdapter.ViewHolder> {

    private List<VoucherStats.DailyTrend> dailyTrend;
    private NumberFormat currencyFormat;
    private SimpleDateFormat inputFormat;
    private SimpleDateFormat displayFormat;

    public DailyTrendAdapter(List<VoucherStats.DailyTrend> dailyTrend) {
        this.dailyTrend = dailyTrend;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        this.inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        this.displayFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_daily_trend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VoucherStats.DailyTrend trend = dailyTrend.get(position);
        holder.bind(trend);
    }

    @Override
    public int getItemCount() {
        return dailyTrend.size();
    }

    public void updateData(List<VoucherStats.DailyTrend> newData) {
        this.dailyTrend = newData;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate, tvUsageCount, tvDailyDiscount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvUsageCount = itemView.findViewById(R.id.tvUsageCount);
            tvDailyDiscount = itemView.findViewById(R.id.tvDailyDiscount);
        }

        public void bind(VoucherStats.DailyTrend trend) {
            // Format date
            try {
                Date date = inputFormat.parse(trend.date);
                tvDate.setText(displayFormat.format(date));
            } catch (ParseException e) {
                tvDate.setText(trend.date);
            }

            tvUsageCount.setText(String.valueOf(trend.usageCount));
            tvDailyDiscount.setText(currencyFormat.format(trend.dailyDiscount));
        }
    }
}
