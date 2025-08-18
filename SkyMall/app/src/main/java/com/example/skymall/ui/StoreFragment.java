package com.example.skymall.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skymall.R;
import com.example.skymall.data.model.Product;
import com.example.skymall.data.remote.ApiClient;
import com.example.skymall.data.remote.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreFragment extends Fragment {

    private static final String ARG_STORE_ID = "store_id";
    private int storeId = 1; // mặc định 1, có thể truyền vào khi mở fragment

    private ApiService api;
    private RecyclerView rv;
    private ProductGridAdapter adapter;
    private EditText etSearch;

    public static StoreFragment newInstance(int storeId){
        StoreFragment f = new StoreFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_STORE_ID, storeId);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle s) {
        return inflater.inflate(R.layout.fragment_store, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        if (getArguments() != null) storeId = getArguments().getInt(ARG_STORE_ID, 1);

        etSearch = v.findViewById(R.id.etSearchStore);
        rv = v.findViewById(R.id.rvStoreProducts);
        rv.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new ProductGridAdapter(new ArrayList<>());
        rv.setAdapter(adapter);

        api = ApiClient.get("https://yourdomain.com/").create(ApiService.class);
        
        v.<androidx.swiperefreshlayout.widget.SwipeRefreshLayout>findViewById(R.id.swipe)
                .setOnRefreshListener(() -> {
                    fetchProducts(etSearch.getText().toString().trim());
                });
        etSearch.setOnEditorActionListener((tv, actionId, event) -> {
            fetchProducts(etSearch.getText().toString().trim());
            return true;
        });

        fetchProducts(null);
    }

    private void fetchProducts(@Nullable String keyword) {
        final androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipe = getView().findViewById(R.id.swipe);
        if (swipe != null) swipe.setRefreshing(true);

        api.getStoreProducts(storeId, TextUtils.isEmpty(keyword) ? null : keyword, 1, 40)
                .enqueue(new Callback<List<Product>>() {
                    @Override public void onResponse(Call<List<Product>> call, Response<List<Product>> res) {
                        if (!isAdded()) return;
                        if (swipe != null) swipe.setRefreshing(false);
                        if (res.isSuccessful() && res.body()!=null) {
                            adapter.setData(res.body());
                        } else {
                            adapter.setData(new ArrayList<>());
                        }
                    }
                    @Override public void onFailure(Call<List<Product>> call, Throwable t) {
                        if (!isAdded()) return;
                        if (swipe != null) swipe.setRefreshing(false);
                        adapter.setData(new ArrayList<>());
                    }
                });
    }

    // ----- Adapter -----
    static class ProductGridAdapter extends RecyclerView.Adapter<ProductGVH>{
        List<Product> data;
        ProductGridAdapter(List<Product> d){ data=d; }
        void setData(List<Product> d){ data = d; notifyDataSetChanged(); }

        @NonNull @Override public ProductGVH onCreateViewHolder(@NonNull ViewGroup p, int vType) {
            View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_product_grid, p, false);
            return new ProductGVH(v);
        }
        @Override public void onBindViewHolder(@NonNull ProductGVH h, int i) {
            Product p = data.get(i);
            h.title.setText(p.name);
            h.price.setText("₫" + String.format("%,.0f", p.price));
            Glide.with(h.img.getContext()).load(p.image_url).into(h.img);
        }
        @Override public int getItemCount(){ return data==null?0:data.size(); }
    }
    static class ProductGVH extends RecyclerView.ViewHolder {
        android.widget.TextView title, price; ImageButton more; android.widget.ImageView img;
        ProductGVH(@NonNull View v){ super(v);
            img=v.findViewById(R.id.img); title=v.findViewById(R.id.tvTitle); price=v.findViewById(R.id.tvPrice);
        }
    }
}
