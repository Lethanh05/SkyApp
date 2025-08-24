package com.example.skymall.ui;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

import com.example.skymall.R;
import com.example.skymall.data.model.Category;
import com.example.skymall.data.model.Product;
import com.example.skymall.data.remote.ApiManager;
import com.example.skymall.data.remote.ApiService;
import com.example.skymall.data.remote.DTO.CategoryListResp;
import com.example.skymall.data.remote.DTO.ProductListResp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvCat, rvFlash, rvRecommended;
    private ViewPager2 vpBanner;
    private LinearLayout dots;
    private EditText etSearch;
    private ApiService api;

    private BannerAdapter bannerAdapter;
    private CategoryAdapter categoryAdapter;
    private FlashSaleAdapter flashSaleAdapter;
    private ProductGridAdapter recommendedAdapter;

    private final Handler autoScrollHandler = new Handler();
    private Runnable autoScrollRunnable;
    private int currentBannerPage = 0;

    private final List<String> bannerImages = new ArrayList<>();
    private final List<Product> flashSaleProducts = new ArrayList<>();
    private final List<Product> recommendedProducts = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupApi();
        setupAdapters();
        setupSearch();
        setupBannerAutoScroll();
        loadData();
    }

    private void initViews(View view) {
        vpBanner = view.findViewById(R.id.vpBanner);
        dots = view.findViewById(R.id.dots);
        rvCat = view.findViewById(R.id.rvCategories);
        rvFlash = view.findViewById(R.id.rvFlashSale);
        rvRecommended = view.findViewById(R.id.rvRecommended);
        etSearch = view.findViewById(R.id.etSearch);
    }

    private void setupApi() {
        // Sử dụng ApiManager thay vì tạo ApiService trực tiếp
        api = ApiManager.getInstance(getContext()).getApiService();
    }

    private void setupAdapters() {
        // Banner adapter
        setupBannerImages();
        bannerAdapter = new BannerAdapter(bannerImages);
        vpBanner.setAdapter(bannerAdapter);
        setupBannerDots();

        // Category adapter
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), this::onCategoryClick);
        rvCat.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rvCat.setAdapter(categoryAdapter);

        // Flash sale adapter (horizontal scroll with animation)
        flashSaleAdapter = new FlashSaleAdapter(flashSaleProducts, this::onProductClick);
        rvFlash.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvFlash.setAdapter(flashSaleAdapter);

        // Recommended products adapter
        recommendedAdapter = new ProductGridAdapter(recommendedProducts, this::onProductClick);
        rvRecommended.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvRecommended.setAdapter(recommendedAdapter);
    }

    private void setupBannerImages() {
        // Random banner images
        bannerImages.add("https://via.placeholder.com/400x200/FF6B6B/FFFFFF?text=Flash+Sale+50%25");
        bannerImages.add("https://via.placeholder.com/400x200/4ECDC4/FFFFFF?text=New+Arrivals");
        bannerImages.add("https://via.placeholder.com/400x200/45B7D1/FFFFFF?text=Free+Shipping");
        bannerImages.add("https://via.placeholder.com/400x200/96CEB4/FFFFFF?text=Special+Offer");
        bannerImages.add("https://via.placeholder.com/400x200/FFEAA7/FFFFFF?text=Weekend+Deal");
    }

    private void setupBannerDots() {
        dots.removeAllViews();
        for (int i = 0; i < bannerImages.size(); i++) {
            ImageView dot = new ImageView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(24, 24);
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);
            dot.setImageResource(i == 0 ? R.drawable.dot_active : R.drawable.dot_inactive);
            dots.addView(dot);
        }

        vpBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentBannerPage = position;
                updateBannerDots(position);
            }
        });
    }

    private void updateBannerDots(int position) {
        for (int i = 0; i < dots.getChildCount(); i++) {
            ImageView dot = (ImageView) dots.getChildAt(i);
            dot.setImageResource(i == position ? R.drawable.dot_active : R.drawable.dot_inactive);
        }
    }

    private void setupBannerAutoScroll() {
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (bannerImages.size() > 1) {
                    currentBannerPage = (currentBannerPage + 1) % bannerImages.size();
                    vpBanner.setCurrentItem(currentBannerPage, true);
                    autoScrollHandler.postDelayed(this, 3000); // Auto scroll every 3 seconds
                }
            }
        };
        autoScrollHandler.postDelayed(autoScrollRunnable, 3000);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (query.length() > 2) {
                    searchProducts(query);
                } else if (query.isEmpty()) {
                    // Reset to recommended products
                    loadRecommendedProducts();
                }
            }
        });
    }

    private void loadData() {
        loadCategories();
        loadFlashSaleProducts();
        loadRecommendedProducts();
    }

    private void loadCategories() {
        if (api == null) {
            return;
        }

        // Sử dụng storeCategories thay vì getCategories (không tồn tại)
        api.storeCategories().enqueue(new Callback<CategoryListResp>() {
            @Override
            public void onResponse(@NonNull Call<CategoryListResp> call, @NonNull Response<CategoryListResp> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    categoryAdapter.updateCategories(response.body().data);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CategoryListResp> call, @NonNull Throwable t) {
                // Handle error silently or show a subtle error message
            }
        });
    }

    private void loadFlashSaleProducts() {
        if (api == null) {
            // Create mock flash sale products
            flashSaleProducts.clear();
            flashSaleProducts.add(new Product(1, "iPhone 15 Pro Max", "Điện thoại cao cấp", 29990000, "https://via.placeholder.com/300x300", 50, 1));
            flashSaleProducts.add(new Product(2, "Samsung Galaxy S24", "Điện thoại Samsung", 24990000, "https://via.placeholder.com/300x300", 30, 1));
            flashSaleProducts.add(new Product(3, "MacBook Air M2", "Laptop Apple", 32990000, "https://via.placeholder.com/300x300", 20, 1));
            flashSaleProducts.add(new Product(4, "iPad Pro 12.9", "Máy tính bảng", 27990000, "https://via.placeholder.com/300x300", 25, 1));
            flashSaleProducts.add(new Product(5, "AirPods Pro 2", "Tai nghe không dây", 6990000, "https://via.placeholder.com/300x300", 100, 1));

            // Add random flash sale discount (10-50%)
            for (Product product : flashSaleProducts) {
                product.discountPercentage = 10 + (int)(Math.random() * 40);
            }

            flashSaleAdapter.notifyItemRangeInserted(0, flashSaleProducts.size());
            return;
        }

        // Sử dụng getFlashSale thay vì storeProducts
        api.getFlashSale().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    flashSaleProducts.clear();
                    flashSaleProducts.addAll(response.body());

                    // Add flash sale discount for products that don't have one
                    for (Product product : flashSaleProducts) {
                        if (product.discountPercentage == 0) {
                            product.discountPercentage = 10 + (int)(Math.random() * 40);
                        }
                    }
                    flashSaleAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {
                // Handle error - có thể log hoặc hiển thị thông báo lỗi
            }
        });
    }

    private void loadRecommendedProducts() {
        if (api == null) {
            return;
        }

        // Sử dụng storeProducts thay vì getAllProducts
        api.storeProducts(null, 1, 20).enqueue(new Callback<ProductListResp>() {
            @Override
            public void onResponse(@NonNull Call<ProductListResp> call, @NonNull Response<ProductListResp> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    List<Product> allProducts = new ArrayList<>(response.body().data);

                    // Sort by popularity (random for now, can be based on sales data)
                    Collections.shuffle(allProducts);

                    recommendedProducts.clear();
                    recommendedProducts.addAll(allProducts);
                    recommendedAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductListResp> call, @NonNull Throwable t) {
                // Handle error
            }
        });
    }

    private void searchProducts(String query) {
        // TODO: Implement search API call
        // For now, filter from existing products
        List<Product> filteredProducts = new ArrayList<>();
        for (Product product : recommendedProducts) {
            if (product.name.toLowerCase().contains(query.toLowerCase())) {
                filteredProducts.add(product);
            }
        }
        recommendedAdapter.updateProducts(filteredProducts);
    }

    private void onCategoryClick(Category category) {
        // Navigate to store with category filter
        // Intent intent = new Intent(getContext(), StoreActivity.class);
        // intent.putExtra("category_id", category.id);
        // startActivity(intent);

        // Temporary: Show toast until StoreActivity is created
        Toast.makeText(getContext(), "Chọn danh mục: " + category.name, Toast.LENGTH_SHORT).show();
    }

    private void onProductClick(Product product) {
        // Navigate to product detail
        // Intent intent = new Intent(getContext(), ProductDetailActivity.class);
        // intent.putExtra("product_id", product.id);
        // startActivity(intent);

        // Temporary: Show toast until ProductDetailActivity is created
        Toast.makeText(getContext(), "Chọn sản phẩm: " + product.name, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (autoScrollHandler != null && autoScrollRunnable != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
        }
    }
}
