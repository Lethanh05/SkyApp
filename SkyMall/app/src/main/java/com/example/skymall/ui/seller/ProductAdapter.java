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
        holder.name.setText(p.getName());
        holder.price.setText("Giá: " + String.format("%,.0f", p.getPrice()) + "đ");
        holder.description.setText(p.getDescription());

        if (p.getImage() != null && !p.getImage().isEmpty()) {
            // Load image from URL using Picasso
            com.squareup.picasso.Picasso.get().load(p.getImage()).into(holder.image);
        } else {
            holder.image.setImageDrawable(null);
        }

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(p, position);
        });
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(p, position);
        });
        holder.description.setText(
                (p.description == null ? "" : p.description) +
                        (p.categoryName != null ? "\nChuyên mục: " + p.categoryName : "")
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
