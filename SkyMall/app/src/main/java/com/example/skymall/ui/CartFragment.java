package com.example.skymall.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skymall.R;

import java.util.*;

public class CartFragment extends Fragment {

    private CheckBox cbSelectAll;
    private TextView tvSelectedCount, tvTotalPrice;
    private Button btnCheckout;
    private RecyclerView rvCart;
    private CartAdapter adapter;

    // Mô hình đơn giản cho demo; khi gắn backend thì map từ API về
    public static class CartItem {
        public int id;
        public String title;
        public String variant;
        public double price; // đơn giá
        public String imageUrl;
        public int qty;
        public boolean selected;

        public CartItem(int id, String title, String variant, double price, String imageUrl, int qty, boolean selected) {
            this.id = id; this.title = title; this.variant = variant; this.price = price;
            this.imageUrl = imageUrl; this.qty = qty; this.selected = selected;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle s) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        cbSelectAll = v.findViewById(R.id.cbSelectAll);
        tvSelectedCount = v.findViewById(R.id.tvSelectedCount);
        tvTotalPrice = v.findViewById(R.id.tvTotalPrice);
        btnCheckout = v.findViewById(R.id.btnCheckout);
        rvCart = v.findViewById(R.id.rvCart);

        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CartAdapter(mockData(), this::onCartChanged, this::onItemDelete, this::onItemWishlist);
        rvCart.setAdapter(adapter);

        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            adapter.setAllSelected(isChecked);
        });

        btnCheckout.setOnClickListener(view -> {
            List<CartItem> selected = adapter.getSelectedItems();
            if (selected.isEmpty()) {
                Toast.makeText(getContext(), "Chưa chọn sản phẩm nào", Toast.LENGTH_SHORT).show();
                return;
            }
            // TODO: chuyển sang màn xác nhận/đặt hàng, gửi selected items
            Toast.makeText(getContext(), "Đặt " + selected.size() + " sản phẩm", Toast.LENGTH_SHORT).show();
        });

        // Khởi tạo tổng
        recalcSummary();
    }

    // Khi có thay đổi (tick chọn, cộng trừ số lượng…)
    private void onCartChanged() {
        recalcSummary();
    }

    private void onItemDelete(CartItem item) {
        adapter.remove(item);
        recalcSummary();
    }

    private void onItemWishlist(CartItem item) {
        // TODO: gọi API thêm vào wishlist
        Toast.makeText(getContext(), "Đã để dành: " + item.title, Toast.LENGTH_SHORT).show();
    }

    private void recalcSummary() {
        int selectedCount = 0;
        double total = 0;
        for (CartItem it : adapter.items) {
            if (it.selected) {
                selectedCount++;
                total += it.price * it.qty;
            }
        }
        tvSelectedCount.setText("(" + selectedCount + ")");
        tvTotalPrice.setText("₫" + formatMoney(total));
        cbSelectAll.setOnCheckedChangeListener(null);
        cbSelectAll.setChecked(selectedCount == adapter.getItemCount() && adapter.getItemCount() > 0);
        cbSelectAll.setOnCheckedChangeListener((b, c) -> adapter.setAllSelected(c));
        btnCheckout.setEnabled(selectedCount > 0);
    }

    private String formatMoney(double v) {
        return String.format("%,.0f", v);
    }

    // ---------- Adapter ----------
    static class CartAdapter extends RecyclerView.Adapter<CartAdapter.VH> {

        interface Listener { void onChanged(); }
        interface DeleteListener { void onDelete(CartItem item); }
        interface WishlistListener { void onWishlist(CartItem item); }

        List<CartItem> items;
        Listener listener;
        DeleteListener deleteListener;
        WishlistListener wishlistListener;

        CartAdapter(List<CartItem> items, Listener l, DeleteListener d, WishlistListener w){
            this.items = items; this.listener = l; this.deleteListener = d; this.wishlistListener = w;
        }

        void setAllSelected(boolean selected){
            for (CartItem i: items) i.selected = selected;
            notifyDataSetChanged();
            if (listener != null) listener.onChanged();
        }

        List<CartItem> getSelectedItems(){
            List<CartItem> out = new ArrayList<>();
            for (CartItem i: items) if (i.selected) out.add(i);
            return out;
        }

        void remove(CartItem item){
            int idx = items.indexOf(item);
            if (idx >= 0) { items.remove(idx); notifyItemRemoved(idx); }
            if (listener != null) listener.onChanged();
        }

        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int vType) {
            View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_cart_product, p, false);
            return new VH(v);
        }

        @Override public void onBindViewHolder(@NonNull VH h, int i) {
            CartItem it = items.get(i);
            h.cb.setOnCheckedChangeListener(null);
            h.cb.setChecked(it.selected);
            h.title.setText(it.title);
            h.variant.setText(TextUtils.isEmpty(it.variant) ? "Phân loại: Mặc định" : it.variant);
            h.price.setText("₫" + String.format("%,.0f", it.price));
            h.qty.setText(String.valueOf(it.qty));
            Glide.with(h.img.getContext()).load(it.imageUrl).into(h.img);

            h.cb.setOnCheckedChangeListener((b, c) -> {
                it.selected = c;
                if (listener != null) listener.onChanged();
            });

            h.btnMinus.setOnClickListener(v -> {
                if (it.qty > 1) {
                    it.qty--;
                    notifyItemChanged(h.getAdapterPosition(), "qty");
                    if (listener != null) listener.onChanged();
                }
            });

            h.btnPlus.setOnClickListener(v -> {
                it.qty++;
                notifyItemChanged(h.getAdapterPosition(), "qty");
                if (listener != null) listener.onChanged();
            });

            h.btnDelete.setOnClickListener(v -> { if (deleteListener != null) deleteListener.onDelete(it); });
            h.btnWishlist.setOnClickListener(v -> { if (wishlistListener != null) wishlistListener.onWishlist(it); });
        }

        @Override public int getItemCount(){ return items==null?0:items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            CheckBox cb; ImageView img; TextView title, variant, price, qty;
            ImageButton btnMinus, btnPlus;
            TextView btnDelete, btnWishlist;
            VH(@NonNull View v){
                super(v);
                cb = v.findViewById(R.id.cbItem);
                img = v.findViewById(R.id.imgProduct);
                title = v.findViewById(R.id.tvTitle);
                variant = v.findViewById(R.id.tvVariant);
                price = v.findViewById(R.id.tvPrice);
                qty = v.findViewById(R.id.tvQty);
                btnMinus = v.findViewById(R.id.btnMinus);
                btnPlus = v.findViewById(R.id.btnPlus);
                btnDelete = v.findViewById(R.id.btnDelete);
                btnWishlist = v.findViewById(R.id.btnWishlist);
            }
        }
    }

    // Dữ liệu demo; khi gắn API thì thay bằng Retrofit gọi từ server
    private List<CartItem> mockData(){
        List<CartItem> l = new ArrayList<>();
        l.add(new CartItem(1, "Áo thun nam basic", "Size M / Trắng", 99000, "https://picsum.photos/200?1", 1, true));
        l.add(new CartItem(2, "Bình giữ nhiệt 500ml", "Đen", 129000, "https://picsum.photos/200?2", 2, true));
        l.add(new CartItem(3, "Tai nghe Bluetooth", "", 159000, "https://picsum.photos/200?3", 1, false));
        return l;
    }
}
