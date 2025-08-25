package com.example.skymall.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skymall.R;
import com.example.skymall.data.model.Product;

import java.util.List;

public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.VH> {

    public interface Listener {
        void onClick(Product p);
        void onAddToCart(Product p);
    }

    private final Context ctx;
    private final List<Product> data;
    private final Listener listener;

    public RecommendAdapter(Context ctx, List<Product> data, Listener listener) {
        this.ctx = ctx;
        this.data = data;
        this.listener = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_recommend_small, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Product p = data.get(pos);
        h.name.setText(p.name != null ? p.name : "Sản phẩm");
        h.price.setText("₫" + String.format("%,.0f", p.price));
        Glide.with(h.img.getContext())
                .load(p.image != null ? p.image : p.img) // image đã xử lý tuyệt đối nếu có
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .into(h.img);

        h.itemView.setOnClickListener(v -> { if (listener != null) listener.onClick(p); });
        h.btnAdd.setOnClickListener(v -> { if (listener != null) listener.onAddToCart(p); });
    }

    @Override
    public int getItemCount() { return data != null ? data.size() : 0; }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name, price;
        ImageButton btnAdd;
        VH(@NonNull View v) {
            super(v);
            img = v.findViewById(R.id.img);
            name = v.findViewById(R.id.name);
            price = v.findViewById(R.id.price);
            btnAdd = v.findViewById(R.id.btnAdd);
        }
    }
}
