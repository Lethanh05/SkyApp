package com.example.skymall.ui;

import android.animation.ObjectAnimator;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skymall.R;
import com.example.skymall.data.model.Product;
import com.example.skymall.utils.MoneyFmt;

import java.util.List;

public class FlashSaleAdapter extends RecyclerView.Adapter<FlashSaleAdapter.FlashSaleViewHolder> {

    private List<Product> products;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public FlashSaleAdapter(List<Product> products, OnProductClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FlashSaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flash_sale, parent, false);
        return new FlashSaleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashSaleViewHolder holder, int position) {
        Product product = products.get(position);

        holder.tvName.setText(product.name);
        holder.tvOriginalPrice.setText(MoneyFmt.format(product.price));

        // Apply strikethrough effect to original price
        holder.tvOriginalPrice.setPaintFlags(holder.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        // Calculate discounted price
        double discountedPrice = product.price * (100 - product.discountPercentage) / 100;
        holder.tvDiscountedPrice.setText(MoneyFmt.format(discountedPrice));
        holder.tvDiscount.setText("-" + product.discountPercentage + "%");

        // Load product image
        if (product.image != null && !product.image.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(product.image)
                    .placeholder(R.drawable.ic_product_placeholder)
                    .into(holder.ivProduct);
        }

        // Add animation effect
        addPulseAnimation(holder.tvDiscount);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });
    }

    private void addPulseAnimation(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.1f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.1f, 1f);
        scaleX.setDuration(1000);
        scaleY.setDuration(1000);
        scaleX.setRepeatCount(ObjectAnimator.INFINITE);
        scaleY.setRepeatCount(ObjectAnimator.INFINITE);
        scaleX.start();
        scaleY.start();
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class FlashSaleViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvOriginalPrice, tvDiscountedPrice, tvDiscount;

        FlashSaleViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvOriginalPrice = itemView.findViewById(R.id.tvOriginalPrice);
            tvDiscountedPrice = itemView.findViewById(R.id.tvDiscountedPrice);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
        }
    }
}
