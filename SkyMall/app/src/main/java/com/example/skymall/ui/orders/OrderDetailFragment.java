package com.example.skymall.ui.orders;

import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skymall.R;
import com.example.skymall.data.remote.ApiService;
import com.example.skymall.data.remote.DTO.*;
import com.example.skymall.utils.MoneyFmt;
import java.util.*;
import retrofit2.*;

public class OrderDetailFragment extends Fragment {
    private static final String ARG_ID = "id";
    public static OrderDetailFragment newInstance(int id){
        Bundle b = new Bundle(); b.putInt(ARG_ID,id);
        OrderDetailFragment f = new OrderDetailFragment(); f.setArguments(b); return f;
    }

    private TextView tvOrderCode,tvStatus,tvCreatedAt,tvReceiver,tvAddressFull,tvSubtotal,tvDiscount,tvShip,tvVoucher,tvGrandTotal;
    private RecyclerView rvItems, rvTimeline;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup parent, @Nullable Bundle s) {
        return inf.inflate(R.layout.fragment_order_detail, parent, false);
    }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        tvOrderCode=v.findViewById(R.id.tvOrderCode);
        tvStatus=v.findViewById(R.id.tvStatus);
        tvCreatedAt=v.findViewById(R.id.tvCreatedAt);
        tvReceiver=v.findViewById(R.id.tvReceiver);
        tvAddressFull=v.findViewById(R.id.tvAddressFull);
        tvSubtotal=v.findViewById(R.id.tvSubtotal);
        tvDiscount=v.findViewById(R.id.tvDiscount);
        tvShip=v.findViewById(R.id.tvShip);
        tvVoucher=v.findViewById(R.id.tvVoucher);
        tvGrandTotal=v.findViewById(R.id.tvGrandTotal);

        rvItems=v.findViewById(R.id.rvItems); rvItems.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTimeline=v.findViewById(R.id.rvTimeline); rvTimeline.setLayoutManager(new LinearLayoutManager(getContext()));

        int id = getArguments()!=null? getArguments().getInt(ARG_ID):0;
        ApiService api = com.example.skymall.data.remote.ApiClient.create(requireContext());

        api.getOrder(id).enqueue(new Callback<OrderDto>() {
            @Override public void onResponse(Call<OrderDto> c, Response<OrderDto> r) {
                OrderDto o = r.body();
                if (o==null) return;
                tvOrderCode.setText("#"+o.id);
                tvStatus.setText(o.status);
                tvCreatedAt.setText("Tạo: "+(o.created_at!=null?o.created_at:""));
                tvReceiver.setText((o.receiver_name!=null?o.receiver_name:"")+" - "+(o.receiver_phone!=null?o.receiver_phone:""));
                String addr = (o.address_line!=null?o.address_line:"");
                if (o.ward!=null) addr += ", "+o.ward;
                if (o.district!=null) addr += ", "+o.district;
                if (o.province!=null) addr += ", "+o.province;
                tvAddressFull.setText(addr);

                tvSubtotal.setText("Tạm tính: "+MoneyFmt.vnd(o.subtotal));
                tvDiscount.setText("Giảm giá: -"+MoneyFmt.vnd(o.discount));
                tvShip.setText("Phí vận chuyển: "+MoneyFmt.vnd(o.shipping_fee));
                tvVoucher.setText("Mã giảm: "+(o.voucher_code!=null?o.voucher_code:"—"));
                tvGrandTotal.setText("Thành tiền: "+MoneyFmt.vnd(o.grand_total));
            }
            @Override public void onFailure(Call<OrderDto> c, Throwable t) {}
        });

        api.getOrderItems(id).enqueue(new Callback<List<OrderItemDto>>() {
            @Override public void onResponse(Call<List<OrderItemDto>> c, Response<List<OrderItemDto>> r) {
                rvItems.setAdapter(new RecyclerView.Adapter<ItemVH>(){
                    final List<OrderItemDto> items = r.body()!=null? r.body(): Collections.emptyList();
                    @NonNull @Override public ItemVH onCreateViewHolder(@NonNull ViewGroup p, int vt){
                        View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_order_product, p, false);
                        return new ItemVH(v);
                    }
                    @Override public void onBindViewHolder(@NonNull ItemVH h, int pos){
                        OrderItemDto it = items.get(pos);
                        h.tvName.setText(it.product_name);
                        h.tvPrice.setText(MoneyFmt.vnd(it.price));
                        h.tvQty.setText(" x"+it.quantity);
                    }
                    @Override public int getItemCount(){ return items.size(); }
                });
            }
            @Override public void onFailure(Call<List<OrderItemDto>> c, Throwable t) {}
        });

        api.getOrderTimeline(id).enqueue(new Callback<List<OrderStatusEventDto>>() {
            @Override public void onResponse(Call<List<OrderStatusEventDto>> c, Response<List<OrderStatusEventDto>> r) {
                rvTimeline.setAdapter(new RecyclerView.Adapter<TimelineVH>(){
                    final List<OrderStatusEventDto> evs = r.body()!=null? r.body(): Collections.emptyList();
                    @NonNull @Override public TimelineVH onCreateViewHolder(@NonNull ViewGroup p, int vt){
                        View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_timeline, p, false);
                        return new TimelineVH(v);
                    }
                    @Override public void onBindViewHolder(@NonNull TimelineVH h, int pos){
                        OrderStatusEventDto e = evs.get(pos);
                        h.tvEvent.setText((e.old_status!=null?e.old_status:"") + " → " + e.new_status);
                        h.tvTime.setText(e.changed_at!=null?e.changed_at:"");
                        if (e.note!=null && !e.note.isEmpty()) { h.tvNote.setVisibility(View.VISIBLE); h.tvNote.setText(e.note); }
                        else h.tvNote.setVisibility(View.GONE);
                    }
                    @Override public int getItemCount(){ return evs.size(); }
                });
            }
            @Override public void onFailure(Call<List<OrderStatusEventDto>> c, Throwable t) {}
        });
    }

    static class ItemVH extends RecyclerView.ViewHolder{
        TextView tvName,tvPrice,tvQty;
        ItemVH(@NonNull View v){ super(v);
            tvName=v.findViewById(R.id.tvName);
            tvPrice=v.findViewById(R.id.tvPrice);
            tvQty=v.findViewById(R.id.tvQty);
        }
    }
    static class TimelineVH extends RecyclerView.ViewHolder{
        TextView tvEvent,tvTime,tvNote;
        TimelineVH(@NonNull View v){ super(v);
            tvEvent=v.findViewById(R.id.tvEvent);
            tvTime=v.findViewById(R.id.tvTime);
            tvNote=v.findViewById(R.id.tvNote);
        }
    }
}
