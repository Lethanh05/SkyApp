package com.example.skymall.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.skymall.CartBadgeHost;
import com.example.skymall.R;
import com.example.skymall.data.model.Product;
import com.example.skymall.data.remote.ApiClient;
import com.example.skymall.data.remote.ApiService;
import com.example.skymall.data.remote.DTO.BaseResp;
import com.example.skymall.data.remote.DTO.CartResp;
import com.example.skymall.data.remote.DTO.ProductListResp;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Cửa hàng cho KH: hiển thị toàn bộ sản phẩm (grid 2 cột) + refresh + load more.
 */
public class StoreFragment extends Fragment {

    private SwipeRefreshLayout swipe;
    private RecyclerView rv;
    private ProgressBar progress;

    private final List<Product> data = new ArrayList<>();
    private ProductGridAdapter adapter;

    private ApiService api;

    // paging
    private int page = 1;
    private final int limit = 20;
    private boolean isLoading = false;
    private boolean canLoadMore = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_store, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);
        api = ApiClient.create(getContext());

        swipe = v.findViewById(R.id.swipe);
        rv = v.findViewById(R.id.rvStoreProducts);
        progress = v.findViewById(R.id.progress);

        // Adapter
        adapter = new ProductGridAdapter(data, new ProductGridAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product p) {
                // TODO: mở chi tiết sản phẩm
                Toast.makeText(getContext(), p.name, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddToCart(Product p) {
                addToCart(p.id, 1);
            }
        });

        rv.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rv.setAdapter(adapter);

        // Kéo xuống để refresh
        swipe.setOnRefreshListener(() -> {
            page = 1;
            canLoadMore = true;
            data.clear();
            adapter.notifyDataSetChanged();
            loadProducts(true);
        });

        // Cuộn để load thêm
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy <= 0 || isLoading || !canLoadMore) return;
                GridLayoutManager lm = (GridLayoutManager) recyclerView.getLayoutManager();
                if (lm == null) return;
                int visible = lm.getChildCount();
                int total = lm.getItemCount();
                int first = lm.findFirstVisibleItemPosition();
                if (first + visible >= total - 4) { // gần cuối thì tải thêm
                    loadProducts(false);
                }
            }
        });

        // load đầu tiên
        loadProducts(true);
    }

    private void addToCart(int productId, int qty) {
        ApiService apiService = ApiClient.create(getContext());
        apiService.cartAddItem(productId, qty).enqueue(new Callback<BaseResp<CartResp>>() {
            @Override
            public void onResponse(Call<BaseResp<CartResp>> call, Response<BaseResp<CartResp>> res) {
                if (!isAdded()) return;
                if (res.isSuccessful() && res.body() != null && res.body().success) {
                    int count = res.body().data != null ? res.body().data.count : 0;
                    Toast.makeText(getContext(), "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show();
                    // Cập nhật badge giỏ nếu có
                    if (getActivity() instanceof CartBadgeHost) {
                        ((CartBadgeHost) getActivity()).updateCartCount(count);
                    }
                } else if (res.code() == 401) {
                    Toast.makeText(getContext(), "Vui lòng đăng nhập lại (401)", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Thêm giỏ thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResp<CartResp>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProducts(boolean showMainLoading) {
        isLoading = true;
        if (showMainLoading) setMainLoading(true);

        api.fetchProducts(page, limit, null, null, "newest")
                .enqueue(new Callback<ProductListResp>() {
                    @Override
                    public void onResponse(@NonNull Call<ProductListResp> call,
                                           @NonNull Response<ProductListResp> res) {
                        isLoading = false;
                        setMainLoading(false);
                        swipe.setRefreshing(false);

                        if (!isAdded()) return;

                        if (res.isSuccessful() && res.body() != null && res.body().success) {
                            List<Product> list = res.body().data != null ? res.body().data : new ArrayList<>();

                            // Chuẩn hoá URL ảnh nếu backend trả path tương đối
                            for (Product p : list) {
                                p.image = getFullImageUrl(p.image != null ? p.image : p.img);
                            }

                            if (page == 1) {
                                data.clear();
                            }
                            data.addAll(list);
                            adapter.notifyDataSetChanged();

                            // xác định còn tải nữa không
                            int total = res.body().total;
                            if (data.size() >= total || list.size() < limit) {
                                canLoadMore = false;
                            } else {
                                canLoadMore = true;
                                page++;
                            }
                        } else {
                            Toast.makeText(getContext(), "Tải sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ProductListResp> call, @NonNull Throwable t) {
                        isLoading = false;
                        setMainLoading(false);
                        swipe.setRefreshing(false);
                        if (!isAdded()) return;
                        Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setMainLoading(boolean b) {
        if (progress != null) progress.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    public static StoreFragment newInstance(int storeId) {
        StoreFragment fragment = new StoreFragment();
        Bundle args = new Bundle();
        args.putInt("store_id", storeId);
        fragment.setArguments(args);
        return fragment;
    }

    private String getFullImageUrl(String imagePath) {
        if (imagePath == null) return null;
        if (imagePath.startsWith("http")) return imagePath;
        return "https://lequangthanh.click/" + (imagePath.startsWith("/") ? imagePath.substring(1) : imagePath);
    }

    // ================= Adapter nội bộ cho Store =================
    static class StoreProductAdapter extends RecyclerView.Adapter<StoreProductAdapter.VH> {

        interface Listener {
            void onClick(Product p);
            void onAddToCart(Product p);
        }

        private final List<Product> items;
        private final Listener listener;

        StoreProductAdapter(List<Product> items, Listener listener) {
            this.items = items;
            this.listener = listener;
        }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store_product, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            Product p = items.get(position);
            h.title.setText(p.name != null ? p.name : "Sản phẩm");
            h.price.setText("₫" + String.format("%,.0f", p.price));

            Glide.with(h.img.getContext())
                    .load(p.image)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .into(h.img);

            h.itemView.setOnClickListener(v -> { if (listener != null) listener.onClick(p); });
            h.btnAdd.setOnClickListener(v -> { if (listener != null) listener.onAddToCart(p); });
        }

        @Override
        public int getItemCount() { return items != null ? items.size() : 0; }

        static class VH extends RecyclerView.ViewHolder {
            ImageView img;
            android.widget.TextView title, price;
            View btnAdd;
            VH(@NonNull View v) {
                super(v);
                img   = v.findViewById(R.id.img);
                title = v.findViewById(R.id.tvTitle);
                price = v.findViewById(R.id.tvPrice);
                btnAdd= v.findViewById(R.id.btnAdd);
            }
        }
    }
}
