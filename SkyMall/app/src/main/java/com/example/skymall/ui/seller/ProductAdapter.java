package com.example.skymall.ui.seller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skymall.R;
import com.example.skymall.data.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    public interface OnProductActionListener {
        void onEdit(Product product, int position);
        void onDelete(Product product, int position);
    }

    private List<Product> productList;
    private Context context;
    private OnProductActionListener listener;

    public ProductAdapter(Context context, List<Product> productList, OnProductActionListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Product p = productList.get(position);
        // Ensure the image URL is valid
        if (p.img == null || p.img.trim().isEmpty() || "null".equalsIgnoreCase(p.img.trim())) {
            p.image = null; // Set to null if invalid
        } else {
            p.image = Product.normalizeImage(p.img); // Normalize valid image URL
        }

        holder.name.setText(p.name);
        holder.price.setText("Giá: " + String.format("%,.0f", p.price) + "đ");
        holder.description.setText(p.description);

        // Load image using Picasso
        if (p.image != null) {
            com.squareup.picasso.Picasso.get()
                .load(p.image)
                .placeholder(R.drawable.ic_product_placeholder)
                .error(R.drawable.ic_product_placeholder)
                .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.ic_product_placeholder);
        }

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(p, position);
        });
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(p, position);
        });
        holder.description.setText(
                (p.description == null ? "" : p.description)
        );

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, description;
        ImageView image;
        Button btnEdit, btnDelete;

        public ProductViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtName);
            price = itemView.findViewById(R.id.txtPrice);
            description = itemView.findViewById(R.id.txtDescription);
            image = itemView.findViewById(R.id.imgProduct);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
