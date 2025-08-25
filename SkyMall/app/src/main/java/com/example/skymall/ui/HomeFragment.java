package com.example.skymall.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.example.skymall.data.remote.DTO.CategoryListResp;
import com.example.skymall.data.remote.DTO.ProductListResp;
import com.example.skymall.ui.seller.ProductAdapter;

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
        dots     = v.findViewById(R.id.dots);
        rvCat    = v.findViewById(R.id.rvCategories);
        rvFlash  = v.findViewById(R.id.rvFlash);
        rvRec    = v.findViewById(R.id.rvRecommend);

        View btnCart = v.findViewById(R.id.btnCart);
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
        api = ApiClient.get("https://lequangthanh.click/").create(ApiService.class);

        // Banner (tạm hardcode; nếu có API banner thì thay ở đây)
        List<String> banners = new ArrayList<>();
        banners.add("https://quynhongrouptour.com.vn/wp-content/uploads/2024/12/z6114876825527_7aed9efdfe2e4716e49aaaa5c90cd8c2.jpg");
        banners.add("https://homepage.momocdn.net/blogscontents/momo-upload-api-220606103940-637901087803924361.jpg");
        banners.add("https://fagoagency.vn/uploads/pictures/60c6c4a18837135b3b974d07/content_shopee-flash-sale-thu-4.jpg");
        vpBanner.setAdapter(new BannerAdapter(banners));
        setupDots(banners.size());
        vpBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override public void onPageSelected(int position) { highlightDot(position); }
        });
        fetchRecommended();

        // Gọi API thực
        loadCategories();
        loadFlashSale();   // <-- dùng ảnh URL tạm (không cần API)
        loadRecommend();
    }
    private void fetchRecommended() {
        int uid = 3; // TODO: lấy từ SharedPref nếu có login
        ApiClient.create(getContext()).getRecommended(uid, 10)
                .enqueue(new Callback<ProductListResp>() {
                    @Override
                    public void onResponse(Call<ProductListResp> call, Response<ProductListResp> res) {
                        if (!isAdded()) return;
                        if (res.isSuccessful() && res.body() != null && res.body().success) {
                            List<Product> data = res.body().data != null ? res.body().data : new ArrayList<>();

                            // Chuẩn hoá URL ảnh nếu backend trả path tương đối
                            for (Product p : data) {
                                if (p.image == null || p.image.isEmpty()) {
                                    if (p.img != null && !p.img.startsWith("http")) {
                                        p.image = "https://lequangthanh.click/" +
                                                (p.img.startsWith("/") ? p.img.substring(1) : p.img);
                                    } else {
                                        p.image = p.img;
                                    }
                                }
                            }

                            rvRec.setLayoutManager(
                                    new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
                            );
                            rvRec.setAdapter(new com.example.skymall.ui.home.RecommendAdapter(
                                    requireContext(),
                                    data,
                                    new com.example.skymall.ui.home.RecommendAdapter.Listener() {
                                        @Override public void onClick(Product p) {
                                            // TODO: mở chi tiết
                                            // startActivity(new Intent(getContext(), ProductDetailActivity.class).putExtra("id", p.id));
                                        }
                                        @Override public void onAddToCart(Product p) {
                                            addToCart(p.id, 1); // gọi API thêm giỏ
                                        }
                                    }
                            ));
                        }
                    }
                    @Override
                    public void onFailure(Call<ProductListResp> call, Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void loadCategories() {
        api.storeCategories().enqueue(new Callback<CategoryListResp>() {
            @Override public void onResponse(Call<CategoryListResp> call, Response<CategoryListResp> res) {
                if (!isAdded()) return;
                if (res.isSuccessful() && res.body()!=null && res.body().data != null) {
                    rvCat.setAdapter(new CategoryAdapter(res.body().data));
                } else {
                    // có thể set adapter rỗng để tránh crash
                    rvCat.setAdapter(new CategoryAdapter(new ArrayList<>()));
                }
            }
            @Override public void onFailure(Call<CategoryListResp> call, Throwable t) {
                if (!isAdded()) return;
                rvCat.setAdapter(new CategoryAdapter(new ArrayList<>()));
            }
        });
    }

    /**
     * FLASH SALE: tạm thời hiển thị danh sách ảnh URL chạy ngang.
     * Khi bạn có API thật (getFlashSale()), chỉ cần set adapter ProductHAdapter như cũ.
     */
    private void loadFlashSale() {
        List<String> flashImageUrls = new ArrayList<>();
        // Ảnh ngẫu nhiên ổn định từ picsum/unsplash
        flashImageUrls.add("https://picsum.photos/seed/flash1/600/400");
        flashImageUrls.add("https://picsum.photos/seed/flash2/600/400");
        flashImageUrls.add("https://images.unsplash.com/photo-1513708925375-45c0d2e1d5a5?q=80&w=800&auto=format");
        flashImageUrls.add("https://images.unsplash.com/photo-1542831371-29b0f74f9713?q=80&w=800&auto=format");
        flashImageUrls.add("https://picsum.photos/seed/flash5/600/400");

        rvFlash.setAdapter(new FlashImageAdapter(flashImageUrls));
    }


    // --- Nếu mai mốt có API thật, dùng lại đoạn cũ:
        // api.getFlashSale().enqueue(new Callback<List<Product>>() {
        //     @Override public void onResponse(Call<List<Product>> call, Response<List<Product>> res) {
        //         if (!isAdded()) return;
        //         if (res.isSuccessful() && res.body()!=null) {
        //             rvFlash.setAdapter(new ProductHAdapter(res.body()));
        //         }
        //     }
        //     @Override public void onFailure(Call<List<Product>> call, Throwable t) { /* TODO */ }
        // });


    // Helper: nếu backend trả đường dẫn ảnh tương đối, ghép thành URL tuyệt đối


    private String absoluteUrl(String path) {
        if (path == null || path.isEmpty()) return null;
        if (path.startsWith("http")) return path;
        return "https://lequangthanh.click/" + (path.startsWith("/") ? path.substring(1) : path);
    }

    private void loadRecommend() {
        api.storeProducts(null, 1, 20).enqueue(new Callback<ProductListResp>() {
            @Override public void onResponse(Call<ProductListResp> call, Response<ProductListResp> rsp) {
                if (!isAdded()) return;

                if (rsp.code() == 401) { // vẫn quên token → nhắc
                    Toast.makeText(getContext(),"401: thiếu token — kiểm tra đăng nhập", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Product> list = new ArrayList<>();
                if (rsp.isSuccessful() && rsp.body()!=null && rsp.body().success && rsp.body().data != null) {
                    for (Product p : rsp.body().data) {
                        p.image = (p.img != null && !p.img.trim().isEmpty() && !"null".equalsIgnoreCase(p.img))
                                ? absoluteUrl(p.img) : null;
                        list.add(p);
                    }
                }

                rvRec.setAdapter(new ProductGridAdapter(list) {
                    @Override public void onBindViewHolder(@NonNull ProductGVH h, int i) {
                        Product p = data.get(i);
                        h.title.setText(p.name != null ? p.name : "Sản phẩm");
                        h.price.setText("₫" + String.format("%,.0f", p.price));
                        Glide.with(h.img.getContext())
                                .load(p.image)
                                .placeholder(R.drawable.ic_image_placeholder)
                                .error(R.drawable.ic_image_placeholder)
                                .into(h.img);
                    }
                });
            }
            @Override public void onFailure(Call<ProductListResp> call, Throwable t) {
                if (!isAdded()) return;
                rvRec.setAdapter(new ProductGridAdapter(new ArrayList<>()));
            }
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
        ImageView img; BannerVH(@NonNull View v){ super(v); img=v.findViewById(R.id.ivBanner); }
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
            // Nếu có icon_url từ API:
            // Glide.with(h.icon.getContext()).load(c.icon_url).into(h.icon);
        }
        @Override public int getItemCount(){ return data.size(); }
    }
    static class CatVH extends RecyclerView.ViewHolder{
        ImageView icon; android.widget.TextView tv;
        CatVH(@NonNull View v){ super(v); icon=v.findViewById(R.id.imgIcon); tv=v.findViewById(R.id.tvName); }
    }

    // Adapter ảnh Flash tạm thời (chỉ hiển thị ảnh, không tên/giá)
    static class FlashImageAdapter extends RecyclerView.Adapter<FlashImageAdapter.FlashVH> {
        final List<String> urls;
        FlashImageAdapter(List<String> urls){ this.urls = urls != null ? urls : new ArrayList<>(); }

        @NonNull @Override public FlashVH onCreateViewHolder(@NonNull ViewGroup p, int v) {
            View view = LayoutInflater.from(p.getContext()).inflate(R.layout.item_flash_image, p, false);
            return new FlashVH(view);
        }
        @Override public void onBindViewHolder(@NonNull FlashVH h, int i) {
            Glide.with(h.img.getContext()).load(urls.get(i)).into(h.img);
        }
        @Override public int getItemCount() { return urls.size(); }

        static class FlashVH extends RecyclerView.ViewHolder {
            ImageView img;
            FlashVH(@NonNull View v){ super(v); img = v.findViewById(R.id.imgFlash); }
        }
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
            Glide.with(h.img.getContext()).load(p.image).into(h.img);
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
            Glide.with(h.img.getContext()).load(p.image).into(h.img);
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
        int size = (int) (6 * getResources().getDisplayMetrics().density); // 6dp
        int margin = (int) (4 * getResources().getDisplayMetrics().density);
        for (int i = 0; i < count; i++) {
            ImageView d = new ImageView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(margin, 0, margin, 0);
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
