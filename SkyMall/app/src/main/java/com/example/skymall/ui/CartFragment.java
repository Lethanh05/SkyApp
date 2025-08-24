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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.skymall.R;
import com.example.skymall.data.remote.DTO.CartListResp;
import com.example.skymall.data.repository.CartRepository;

import java.util.*;

public class CartFragment extends Fragment {

    private CheckBox cbSelectAll;
    private TextView tvSelectedCount, tvTotalPrice;
    private Button btnCheckout;
    private RecyclerView rvCart;
    private SwipeRefreshLayout swipeRefresh;
    private CartAdapter adapter;
    private CartRepository cartRepository;

    // Updated CartItem to match API structure
    public static class CartItem {
        public int productId;
        public String name;
        public double price;
        public String img;
        public int quantity;
        public boolean selected;

        public CartItem(int productId, String name, double price, String img, int quantity) {
            this.productId = productId;
            this.name = name;
            this.price = price;
            this.img = img;
            this.quantity = quantity;
            this.selected = false; // Default not selected
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
        swipeRefresh = v.findViewById(R.id.swipeRefresh);

        cartRepository = new CartRepository(requireContext());
        
        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CartAdapter(new ArrayList<>(), this::onCartChanged, this::onItemDelete, this::onItemQuantityChanged);
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
            // TODO: Navigate to checkout with selected items
            Toast.makeText(getContext(), "Đặt " + selected.size() + " sản phẩm", Toast.LENGTH_SHORT).show();
        });

        swipeRefresh.setOnRefreshListener(this::loadCart);

        // Load cart data on start
        loadCart();
    }

    private void loadCart() {
        cartRepository.getCart(new CartRepository.CartListCallback() {
            @Override
            public void onSuccess(CartListResp response) {
                swipeRefresh.setRefreshing(false);
                if (getContext() == null) return;
                
                List<CartItem> cartItems = new ArrayList<>();
                if (response.items != null) {
                    for (CartListResp.CartItem apiItem : response.items) {
                        cartItems.add(new CartItem(
                            apiItem.productId,
                            apiItem.name,
                            apiItem.price,
                            apiItem.img,
                            apiItem.quantity
                        ));
                    }
                }
                
                adapter.updateItems(cartItems);
                recalcSummary();
            }

            @Override
            public void onError(String error) {
                swipeRefresh.setRefreshing(false);
                if (getContext() != null) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Khi có thay đổi (tick chọn, cộng trừ số lượng…)
    private void onCartChanged() {
        recalcSummary();
    }

    private void onItemDelete(CartItem item) {
        cartRepository.removeFromCart(item.productId, new CartRepository.CartActionCallback() {
            @Override
            public void onSuccess() {
                if (getContext() != null) {
                    adapter.remove(item);
                    recalcSummary();
                    Toast.makeText(getContext(), "Đã xóa " + item.name, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi khi xóa: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void onItemQuantityChanged(CartItem item) {
        cartRepository.updateCartItem(item.productId, item.quantity, new CartRepository.CartActionCallback() {
            @Override
            public void onSuccess() {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Đã cập nhật số lượng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi cập nhật: " + error, Toast.LENGTH_SHORT).show();
                    // Reload cart to revert changes
                    loadCart();
                }
            }
        });
    }

    private void recalcSummary() {
        int selectedCount = 0;
        double total = 0;
        for (CartItem it : adapter.items) {
            if (it.selected) {
                selectedCount++;
                total += it.price * it.quantity;
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
        interface QuantityListener { void onQuantityChanged(CartItem item); }

        List<CartItem> items;
        Listener listener;
        DeleteListener deleteListener;
        QuantityListener quantityListener;

        CartAdapter(List<CartItem> items, Listener l, DeleteListener d, QuantityListener q){
            this.items = items; this.listener = l; this.deleteListener = d; this.quantityListener = q;
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

        void updateItems(List<CartItem> newItems) {
            items.clear();
            items.addAll(newItems);
            notifyDataSetChanged();
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
            h.title.setText(it.name);
            h.price.setText("₫" + String.format("%,.0f", it.price));
            h.qty.setText(String.valueOf(it.quantity));
            Glide.with(h.img.getContext()).load(it.img).into(h.img);

            h.cb.setOnCheckedChangeListener((b, c) -> {
                it.selected = c;
                if (listener != null) listener.onChanged();
            });

            h.btnMinus.setOnClickListener(v -> {
                if (it.quantity > 1) {
                    it.quantity--;
                    notifyItemChanged(h.getAdapterPosition(), "qty");
                    if (listener != null) listener.onChanged();
                    if (quantityListener != null) quantityListener.onQuantityChanged(it);
                }
            });

            h.btnPlus.setOnClickListener(v -> {
                it.quantity++;
                notifyItemChanged(h.getAdapterPosition(), "qty");
                if (listener != null) listener.onChanged();
                if (quantityListener != null) quantityListener.onQuantityChanged(it);
            });

            h.btnDelete.setOnClickListener(v -> { if (deleteListener != null) deleteListener.onDelete(it); });
        }

        @Override public int getItemCount(){ return items==null?0:items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            CheckBox cb; ImageView img; TextView title, price, qty;
            ImageButton btnMinus, btnPlus;
            TextView btnDelete;
            VH(@NonNull View v){
                super(v);
                cb = v.findViewById(R.id.cbItem);
                img = v.findViewById(R.id.imgProduct);
                title = v.findViewById(R.id.tvTitle);
                price = v.findViewById(R.id.tvPrice);
                qty = v.findViewById(R.id.tvQty);
                btnMinus = v.findViewById(R.id.btnMinus);
                btnPlus = v.findViewById(R.id.btnPlus);
                btnDelete = v.findViewById(R.id.btnDelete);
            }
        }
    }
}
