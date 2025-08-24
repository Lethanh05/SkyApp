package com.example.skymall.ui.checkout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skymall.R;
import com.example.skymall.data.model.CartItem;
import com.example.skymall.utils.MoneyFmt;

import java.util.List;

public class CheckoutItemsAdapter extends RecyclerView.Adapter<CheckoutItemsAdapter.CheckoutItemViewHolder> {

    private List<CartItem> items;

    public CheckoutItemsAdapter(List<CartItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public CheckoutItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checkout, parent, false);
        return new CheckoutItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutItemViewHolder holder, int position) {
        CartItem item = items.get(position);

        holder.tvName.setText(item.name);
        holder.tvPrice.setText(MoneyFmt.format(item.price));
        holder.tvQuantity.setText("x" + item.quantity);
        holder.tvTotalPrice.setText(MoneyFmt.format(item.getTotalPrice()));

        // Load product image with proper placeholder
        if (item.image != null && !item.image.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.image)
                    .placeholder(R.drawable.ic_product_placeholder)
                    .into(holder.ivProduct);
        } else {
            holder.ivProduct.setImageResource(R.drawable.ic_product_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CheckoutItemViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvPrice, tvQuantity, tvTotalPrice;

        CheckoutItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
        }
    }
}
