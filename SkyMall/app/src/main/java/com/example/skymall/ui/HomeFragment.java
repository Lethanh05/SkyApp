package com.example.skymall.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.skymall.R;
import com.example.skymall.data.model.Category;
import com.example.skymall.data.model.Product;
import com.example.skymall.data.remote.ApiClient;
import com.example.skymall.data.remote.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvCat, rvFlash, rvRec;
    private ViewPager2 vpBanner;
    private LinearLayout dots;
    private ApiService api;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle s) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        // init views
        vpBanner = v.findViewById(R.id.vpBanner);
        dots = v.findViewById(R.id.dots);
        rvCat = v.findViewById(R.id.rvCategories);
        rvFlash = v.findViewById(R.id.rvFlash);
        rvRec = v.findViewById(R.id.rvRecommend);

        android.view.View btnCart = v.findViewById(R.id.btnCart);
        if (btnCart != null) {
            btnCart.setOnClickListener(view -> {
                android.widget.Toast.makeText(requireContext(), "Opening cart...", android.widget.Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new CartFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }

        // adapters + layout
        rvCat.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rvFlash.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvRec.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Retrofit
        api = ApiClient.get("https://yourdomain.com/").create(ApiService.class);

        // Banner (tạm hardcode hoặc lấy từ API riêng nếu có)
        List<String> banners = new ArrayList<>();
        banners.add("https://quynhongrouptour.com.vn/wp-content/uploads/2024/12/z6114876825527_7aed9efdfe2e4716e49aaaa5c90cd8c2.jpg");
        banners.add("https://homepage.momocdn.net/blogscontents/momo-upload-api-220606103940-637901087803924361.jpg");
        banners.add("https://fagoagency.vn/uploads/pictures/60c6c4a18837135b3b974d07/content_shopee-flash-sale-thu-4.jpg");
        vpBanner.setAdapter(new BannerAdapter(banners));
        setupDots(banners.size());
        vpBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override public void onPageSelected(int position) { highlightDot(position); }
        });

        // Gọi API thực
        loadCategories();
        loadFlashSale();
        loadRecommend();
    }

    private void loadCategories() {
        api.getCategories().enqueue(new Callback<List<Category>>() {
            @Override public void onResponse(Call<List<Category>> call, Response<List<Category>> res) {
                if (!isAdded()) return;
                if (res.isSuccessful() && res.body()!=null) {
                    rvCat.setAdapter(new CategoryAdapter(res.body()));
                }
            }
            @Override public void onFailure(Call<List<Category>> call, Throwable t) { /* TODO: show lỗi */ }
        });
    }

    private void loadFlashSale() {
        api.getFlashSale().enqueue(new Callback<List<Product>>() {
            @Override public void onResponse(Call<List<Product>> call, Response<List<Product>> res) {
                if (!isAdded()) return;
                if (res.isSuccessful() && res.body()!=null) {
                    rvFlash.setAdapter(new ProductHAdapter(res.body()));
                }
            }
            @Override public void onFailure(Call<List<Product>> call, Throwable t) { /* TODO */ }
        });
    }

    private void loadRecommend() {
        api.getProducts(null, null, 1, 20).enqueue(new Callback<List<Product>>() {
            @Override public void onResponse(Call<List<Product>> call, Response<List<Product>> res) {
                if (!isAdded()) return;
                if (res.isSuccessful() && res.body()!=null) {
                    rvRec.setAdapter(new ProductGridAdapter(res.body()));
                }
            }
            @Override public void onFailure(Call<List<Product>> call, Throwable t) { /* TODO */ }
        });
    }

    // ---------- Simple Adapters ----------
    static class BannerAdapter extends RecyclerView.Adapter<BannerVH> {
        List<String> data; BannerAdapter(List<String> d){ data=d; }
        @NonNull @Override public BannerVH onCreateViewHolder(@NonNull ViewGroup p, int v) {
            View vItem = LayoutInflater.from(p.getContext()).inflate(R.layout.item_banner, p, false);
            return new BannerVH(vItem);
        }
        @Override public void onBindViewHolder(@NonNull BannerVH h, int i) {
            Glide.with(h.img.getContext()).load(data.get(i)).into(h.img);
        }
        @Override public int getItemCount(){ return data.size(); }
    }
    static class BannerVH extends RecyclerView.ViewHolder {
        ImageView img; BannerVH(@NonNull View v){ super(v); img=v.findViewById(R.id.imgBanner); }
    }

    static class CategoryAdapter extends RecyclerView.Adapter<CatVH>{
        List<Category> data; CategoryAdapter(List<Category> d){ data=d; }
        @NonNull @Override public CatVH onCreateViewHolder(@NonNull ViewGroup p, int v) {
            View view = LayoutInflater.from(p.getContext()).inflate(R.layout.item_category, p, false);
            return new CatVH(view);
        }
        @Override public void onBindViewHolder(@NonNull CatVH h, int i) {
            Category c = data.get(i);
            h.tv.setText(c.name);
            // nếu có icon_url:
            // Glide.with(h.icon.getContext()).load(c.icon_url).into(h.icon);
        }
        @Override public int getItemCount(){ return data.size(); }
    }
    static class CatVH extends RecyclerView.ViewHolder{
        ImageView icon; android.widget.TextView tv;
        CatVH(@NonNull View v){ super(v); icon=v.findViewById(R.id.imgIcon); tv=v.findViewById(R.id.tvName); }
    }

    static class ProductHAdapter extends RecyclerView.Adapter<ProductHVH>{
        List<Product> data; ProductHAdapter(List<Product> d){ data=d; }
        @NonNull @Override public ProductHVH onCreateViewHolder(@NonNull ViewGroup p, int v) {
            View view = LayoutInflater.from(p.getContext()).inflate(R.layout.item_product_horizontal, p, false);
            return new ProductHVH(view);
        }
        @Override public void onBindViewHolder(@NonNull ProductHVH h, int i) {
            Product p = data.get(i);
            h.title.setText(p.name);
            h.price.setText("₫" + String.format("%,.0f", p.price));
            Glide.with(h.img.getContext()).load(p.image_url).into(h.img);
        }
        @Override public int getItemCount(){ return data.size(); }
    }
    static class ProductHVH extends RecyclerView.ViewHolder{
        ImageView img; android.widget.TextView title, price;
        ProductHVH(@NonNull View v){ super(v); img=v.findViewById(R.id.img); title=v.findViewById(R.id.tvTitle); price=v.findViewById(R.id.tvPrice); }
    }

    static class ProductGridAdapter extends RecyclerView.Adapter<ProductGVH>{
        List<Product> data; ProductGridAdapter(List<Product> d){ data=d; }
        @NonNull @Override public ProductGVH onCreateViewHolder(@NonNull ViewGroup p, int v) {
            View view = LayoutInflater.from(p.getContext()).inflate(R.layout.item_product_grid, p, false);
            return new ProductGVH(view);
        }
        @Override public void onBindViewHolder(@NonNull ProductGVH h, int i) {
            Product p = data.get(i);
            h.title.setText(p.name);
            h.price.setText("₫" + String.format("%,.0f", p.price));
            Glide.with(h.img.getContext()).load(p.image_url).into(h.img);
        }
        @Override public int getItemCount(){ return data.size(); }
    }
    static class ProductGVH extends RecyclerView.ViewHolder{
        ImageView img; android.widget.TextView title, price;
        ProductGVH(@NonNull View v){ super(v); img=v.findViewById(R.id.img); title=v.findViewById(R.id.tvTitle); price=v.findViewById(R.id.tvPrice); }
    }

    // ---------- Dots for banner ----------
    private void setupDots(int count) {
        dots.removeAllViews();
        for (int i = 0; i < count; i++) {
            ImageView d = new ImageView(getContext());
            int size = (int) (6 * getResources().getDisplayMetrics().density); // 6dp
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(4, 0, 4, 0); // khoảng cách giữa dots
            d.setLayoutParams(params);
            d.setImageResource(R.drawable.dot_inactive);
            dots.addView(d);
        }
        highlightDot(0);
    }

    private void highlightDot(int index) {
        for (int i = 0; i < dots.getChildCount(); i++) {
            ImageView d = (ImageView) dots.getChildAt(i);
            d.setImageResource(i == index ? R.drawable.dot_active : R.drawable.dot_inactive);
        }
    }

}
