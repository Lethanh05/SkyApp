package com.example.skymall.ui.orders;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.*;
import com.example.skymall.R;
import com.example.skymall.data.remote.DTO.OrderDto;
import com.example.skymall.utils.MoneyFmt;
import java.util.Objects;

public class OrderListAdapter extends ListAdapter<OrderDto, OrderListAdapter.VH> {

    public interface OnClick { void onClick(OrderDto order); }
    private final OnClick onClick;

    public OrderListAdapter(OnClick onClick) {
        super(new DiffUtil.ItemCallback<OrderDto>() {
            @Override public boolean areItemsTheSame(@NonNull OrderDto a, @NonNull OrderDto b){ return a.id==b.id; }
            @Override public boolean areContentsTheSame(@NonNull OrderDto a, @NonNull OrderDto b){
                return Objects.equals(a.status,b.status) && a.grand_total==b.grand_total && Objects.equals(a.updated_at,b.updated_at);
            }
        });
        this.onClick = onClick;
    }

    static class VH extends RecyclerView.ViewHolder{
        TextView tvOrderId,tvOrderDate,tvStatus,tvReceiver,tvGrandTotal;
        public VH(@NonNull View v){
            super(v);
            tvOrderId = v.findViewById(R.id.tvOrderId);
            tvOrderDate = v.findViewById(R.id.tvOrderDate);
            tvStatus = v.findViewById(R.id.tvStatus);
            tvReceiver = v.findViewById(R.id.tvReceiver);
            tvGrandTotal = v.findViewById(R.id.tvGrandTotal);
        }
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int vt) {
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_order, p, false));
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        OrderDto o = getItem(pos);
        h.tvOrderId.setText("#"+o.id);
        h.tvOrderDate.setText(o.date!=null? o.date.split("\\.")[0] : "");
        h.tvStatus.setText(o.status);
        h.tvReceiver.setText((o.receiver_name!=null? o.receiver_name:"") + " • " +
                (o.receiver_phone!=null? o.receiver_phone:"") + " • " +
                (o.district!=null? o.district:""));
        h.tvGrandTotal.setText(MoneyFmt.vnd(o.grand_total));

        h.itemView.setOnClickListener(v -> onClick.onClick(o));
    }
}
