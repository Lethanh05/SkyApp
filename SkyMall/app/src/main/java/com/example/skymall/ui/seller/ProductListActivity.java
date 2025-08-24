package com.example.skymall.ui.seller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skymall.R;
import com.example.skymall.data.model.Product;
import com.example.skymall.data.model.Category;
import com.example.skymall.data.remote.ApiClient;
import com.example.skymall.data.remote.ApiService;
import com.example.skymall.data.remote.DTO.BaseResp;
import com.example.skymall.data.remote.DTO.CategoryListResp;
import com.example.skymall.data.remote.DTO.ProductListResp;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends AppCompatActivity {

    private RecyclerView rcvProducts;
    private FloatingActionButton fabAdd;
    private SellerProductAdapter adapter; // adapter dùng URL ảnh (ở dưới)
    private final List<Product> productList = new ArrayList<>();
    private ApiService api;

    // Chứa bytes ảnh đang chọn trong dialog
    private byte[] pickedImageBytes = null;
    private ImageView currentPreview = null;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null && currentPreview != null) {
                    try {
                        pickedImageBytes = readBytesFromUri(uri);
                        Bitmap bm = BitmapFactory.decodeByteArray(pickedImageBytes, 0, pickedImageBytes.length);
                        currentPreview.setImageBitmap(bm);
                    } catch (Exception e) {
                        Toast.makeText(this, "Không thể đọc ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private List<Category> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        api = ApiClient.create(this);

        rcvProducts = findViewById(R.id.rcvProducts);
        fabAdd = findViewById(R.id.fabAdd);

        rcvProducts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SellerProductAdapter(productList, new SellerProductAdapter.OnProductActionListener() {
            @Override public void onEdit(Product product, int position) {
                showProductDialog(product, position);
            }

            @Override public void onDelete(Product product, int position) {
                confirmDelete(product, position);
            }
        });
        rcvProducts.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> showProductDialog(null, -1));

        loadProducts(null, 1, 50);
    }

    private void loadProducts(@Nullable String q, int page, int limit) {
        api.storeProducts(q, page, limit).enqueue(new Callback<ProductListResp>() {
            @Override public void onResponse(Call<ProductListResp> call, Response<ProductListResp> rsp) {
                // Debug: Log API response
                Log.d("ProductAPI", "=== API Response Debug ===");
                Log.d("ProductAPI", "Response successful: " + rsp.isSuccessful());
                Log.d("ProductAPI", "Response code: " + rsp.code());

                if (rsp.isSuccessful() && rsp.body()!=null && rsp.body().success) {
                    Log.d("ProductAPI", "Response body success: " + rsp.body().success);
                    Log.d("ProductAPI", "Data count: " + (rsp.body().data != null ? rsp.body().data.size() : "null"));

                    // Debug: Log first product data if available
                    if (rsp.body().data != null && !rsp.body().data.isEmpty()) {
                        Product firstProduct = rsp.body().data.get(0);
                        Log.d("ProductAPI", "First product ID: " + firstProduct.id);
                        Log.d("ProductAPI", "First product name: " + firstProduct.name);
                        Log.d("ProductAPI", "First product image: '" + firstProduct.image + "'");
                        Log.d("ProductAPI", "First product price: " + firstProduct.price);
                        Log.d("ProductAPI", "First product categoryId: " + firstProduct.categoryId);
                    }

                    productList.clear();
                    if (rsp.body().data != null) productList.addAll(rsp.body().data);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("ProductAPI", "API call failed or unsuccessful");
                    if (rsp.body() != null) {
                        Log.e("ProductAPI", "Response success flag: " + rsp.body().success);
                    }
                    Toast.makeText(ProductListActivity.this, "Tải sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<ProductListResp> call, Throwable t) {
                Log.e("ProductAPI", "API call failed with error: " + t.getMessage());
                Toast.makeText(ProductListActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDelete(Product p, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Xoá sản phẩm")
                .setMessage("Bạn chắc chắn muốn xoá \"" + p.name + "\"?")
                .setPositiveButton("Xoá", (d, w) -> {
                    api.storeDelete(p.id).enqueue(new Callback<BaseResp>() {
                        @Override public void onResponse(Call<BaseResp> call, Response<BaseResp> response) {
                            if (response.isSuccessful() && response.body()!=null && response.body().success) {
                                productList.remove(position);
                                adapter.notifyItemRemoved(position);
                                Toast.makeText(ProductListActivity.this, "Đã xoá", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProductListActivity.this, "Xoá thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override public void onFailure(Call<BaseResp> call, Throwable t) {
                            Toast.makeText(ProductListActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void showProductDialog(@Nullable Product product, int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_product, null);

        // Update to use TextInputEditText from Material Design components
        com.google.android.material.textfield.TextInputEditText edtName = view.findViewById(R.id.edtName);
        com.google.android.material.textfield.TextInputEditText edtPrice = view.findViewById(R.id.edtPrice);
        com.google.android.material.textfield.TextInputEditText edtDescription = view.findViewById(R.id.edtDescription);
        com.google.android.material.textfield.TextInputEditText edtStock = view.findViewById(R.id.edtStock);
        com.google.android.material.textfield.TextInputEditText edtBadge = view.findViewById(R.id.edtBadge);

        // Change from Spinner to EditText for category
        com.google.android.material.textfield.TextInputEditText edtCategory = view.findViewById(R.id.edtCategory);

        // Update to use SwitchMaterial
        com.google.android.material.switchmaterial.SwitchMaterial switchActive = view.findViewById(R.id.switchActive);

        ImageView preview = view.findViewById(R.id.previewImage);
        com.google.android.material.button.MaterialButton btnPickImage = view.findViewById(R.id.btnPickImage);

        // reset state chọn ảnh
        currentPreview = preview;
        pickedImageBytes = null;

        if (product != null) {
            // Editing existing product
            edtName.setText(product.name != null ? product.name : "");
            edtPrice.setText(String.valueOf(product.price));
            edtDescription.setText(""); // No description field in Product model
            edtStock.setText(""); // No stock field in Product model
            edtBadge.setText(""); // No badge field in Product model
            edtCategory.setText(""); // Will be filled when we have category name from API
            switchActive.setChecked(true); // Default to active for existing products

            if (!TextUtils.isEmpty(product.image)) {
                String url = product.image.startsWith("http") ? product.image : product.image;
                Picasso.get().load(url).placeholder(R.drawable.ic_image_placeholder).into(preview);
            } else {
                preview.setImageResource(R.drawable.ic_image_placeholder);
            }
        } else {
            // Adding new product - set default values
            switchActive.setChecked(true); // Default to active for new products
            edtStock.setText("0"); // Default stock
            preview.setImageResource(R.drawable.ic_image_placeholder);
        }

        btnPickImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        new AlertDialog.Builder(this)
                .setTitle(product == null ? "Thêm sản phẩm" : "Sửa sản phẩm")
                .setView(view)
                .setPositiveButton(product == null ? "Thêm" : "Lưu", (dialog, which) -> {
                    // lấy dữ liệu
                    String name = edtName.getText().toString().trim();
                    String priceStr = edtPrice.getText().toString().trim();
                    String desc = edtDescription.getText().toString().trim();
                    String stockStr = edtStock.getText().toString().trim();
                    String badge = edtBadge.getText().toString().trim();
                    String categoryName = edtCategory.getText().toString().trim();
                    boolean isActive = switchActive.isChecked();

                    // DEBUG: Log validation values
                    Log.d("ProductAdd", "name: '" + name + "'");
                    Log.d("ProductAdd", "priceStr: '" + priceStr + "'");
                    Log.d("ProductAdd", "categoryName: '" + categoryName + "'");

                    // Basic validation
                    if (name.isEmpty()) {
                        Toast.makeText(this, "Vui lòng nhập tên sản phẩm", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (priceStr.isEmpty()) {
                        Toast.makeText(this, "Vui lòng nhập giá sản phẩm", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (categoryName.isEmpty()) {
                        Toast.makeText(this, "Vui lòng nhập danh mục sản phẩm", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // build multipart
                    RequestBody rbName = toText(name);
                    RequestBody rbPrice = toText(priceStr);
                    RequestBody rbDesc = toText(desc);
                    RequestBody rbStock = toText(stockStr.isEmpty()? "0" : stockStr);
                    RequestBody rbBadge = toText(badge);
                    RequestBody rbCategoryName = toText(categoryName); // Send category name instead of ID
                    MultipartBody.Part partImg = pickedImageBytes != null ? toImagePart("img", "product.jpg", pickedImageBytes) : null;

                    if (product == null) {
                        // CREATE - Optional image validation (remove if image not required)
                        if (pickedImageBytes == null) {
                            Toast.makeText(this, "Vui lòng chọn hình ảnh sản phẩm", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Log.d("ProductAdd", "Calling API storeCreate...");
                        // API call with category name instead of ID
                        api.storeCreateWithCategoryName(rbName, rbPrice, rbDesc, rbCategoryName, rbStock, rbBadge, partImg)
                                .enqueue(new Callback<BaseResp>() {
                                    @Override public void onResponse(Call<BaseResp> call, Response<BaseResp> response) {
                                        if (response.isSuccessful() && response.body() != null && response.body().success) {
                                            Toast.makeText(ProductListActivity.this, "Đã thêm sản phẩm", Toast.LENGTH_SHORT).show();
                                            loadProducts(null, 1, 50);
                                        } else {
                                            String errorMsg = "Thêm thất bại";
                                            try {
                                                if (response.errorBody() != null) {
                                                    String serverError = response.errorBody().string();
                                                    errorMsg += ": " + serverError;
                                                    Log.e("ProductAdd", "API errorBody: " + serverError);
                                                } else if (response.body() != null && response.body().message != null) {
                                                    errorMsg += ": " + response.body().message;
                                                    Log.e("ProductAdd", "API message: " + response.body().message);
                                                }
                                            } catch (Exception e) {
                                                Log.e("ProductAdd", "Error parsing response", e);
                                            }
                                            Toast.makeText(ProductListActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    @Override public void onFailure(Call<BaseResp> call, Throwable t) {
                                        Log.e("ProductAdd", "Network/API failure", t);
                                        Toast.makeText(ProductListActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    } else {
                        // UPDATE
                        Log.d("ProductUpdate", "Updating product ID: " + product.id);
                        Log.d("ProductUpdate", "New name: " + name);
                        Log.d("ProductUpdate", "New price: " + priceStr);
                        Log.d("ProductUpdate", "New category: " + categoryName);

                        RequestBody rbId = toText(String.valueOf(product.id));
                        RequestBody rbActive = toText(isActive ? "1" : "0");

                        // Use original storeUpdate method instead of the new one
                        // Convert category name to ID first (simplified approach)
                        api.storeUpdate(rbId, rbName, rbPrice, rbDesc, toText(""), rbStock, rbBadge, rbActive, partImg)
                                .enqueue(new Callback<BaseResp>() {
                                    @Override public void onResponse(Call<BaseResp> call, Response<BaseResp> response) {
                                        if (response.isSuccessful() && response.body() != null && response.body().success) {
                                            Toast.makeText(ProductListActivity.this, "Đã lưu thay đổi", Toast.LENGTH_SHORT).show();
                                            loadProducts(null, 1, 50);
                                        } else {
                                            String errorMsg = "Lưu thất bại";
                                            try {
                                                if (response.errorBody() != null) {
                                                    String serverError = response.errorBody().string();
                                                    errorMsg += ": " + serverError;
                                                    Log.e("ProductUpdate", "API errorBody: " + serverError);
                                                } else if (response.body() != null && response.body().message != null) {
                                                    errorMsg += ": " + response.body().message;
                                                    Log.e("ProductUpdate", "API message: " + response.body().message);
                                                }
                                            } catch (Exception e) {
                                                Log.e("ProductUpdate", "Error parsing response", e);
                                            }
                                            Toast.makeText(ProductListActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    @Override public void onFailure(Call<BaseResp> call, Throwable t) {
                                        Log.e("ProductUpdate", "Network/API failure", t);
                                        Toast.makeText(ProductListActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    /* ================== Helpers =================== */

    private byte[] readBytesFromUri(Uri uri) {
        try (InputStream is = getContentResolver().openInputStream(uri);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[16 * 1024];
            int n;
            while ((n = is.read(buf)) > 0) bos.write(buf, 0, n);
            return bos.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }

    private RequestBody toText(String s) {
        if (s == null) s = "";
        return RequestBody.create(s, MediaType.parse("text/plain; charset=utf-8"));
    }

    private MultipartBody.Part toImagePart(String name, String filename, byte[] data) {
        RequestBody body = RequestBody.create(data, MediaType.parse("image/*"));
        return MultipartBody.Part.createFormData(name, filename, body);
    }

    private List<String> getCategoryNames() {
        List<String> names = new ArrayList<>();
        for (Category c : categoryList) names.add(c.name);
        return names;
    }

    private int getCategoryIndexById(Integer id) {
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).id == id) return i;
        }
        return -1;
    }

    /* ============== Adapter nội bộ (dùng Picasso) ============== */
    public static class SellerProductAdapter extends RecyclerView.Adapter<SellerProductAdapter.VH> {
        public interface OnProductActionListener {
            void onEdit(Product product, int position);
            void onDelete(Product product, int position);
        }

        private final List<Product> items;
        private final OnProductActionListener listener;

        public SellerProductAdapter(List<Product> items, OnProductActionListener l) {
            this.items = items;
            this.listener = l;
        }

        @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
            return new VH(v);
        }

        @Override public void onBindViewHolder(VH h, int pos) {
            Product p = items.get(pos);
            h.name.setText(p.name);
            h.price.setText("Giá: " + String.format("%,.0f", p.price) + "đ");

            // Display product description instead of leaving it empty
            if (p.description != null && !p.description.trim().isEmpty()) {
                h.desc.setText(p.description);
                h.desc.setVisibility(View.VISIBLE);
            } else {
                h.desc.setText("Chưa có mô tả");
                h.desc.setVisibility(View.VISIBLE);
            }

            // Debug: Log product data
            Log.d("ProductAdapter", "=== Product Debug ===");
            Log.d("ProductAdapter", "Product ID: " + p.id);
            Log.d("ProductAdapter", "Product name: " + p.name);
            Log.d("ProductAdapter", "Product description: '" + p.description + "'");
            Log.d("ProductAdapter", "Product image_url: '" + p.image + "'");
            Log.d("ProductAdapter", "Image_url is null: " + (p.image == null));
            Log.d("ProductAdapter", "Image_url is empty: " + (p.image != null && p.image.isEmpty()));

            // Fix image URL handling
            if (p.image != null && !p.image.isEmpty() && !p.image.equals("0")) {
                String imageUrl;
                if (p.image.startsWith("http")) {
                    // Already full URL
                    imageUrl = p.image;
                    Log.d("ProductAdapter", "Case: Full URL");
                } else if (p.image.startsWith("/uploads/")) {
                    // Relative path starting with /uploads/ - convert to full URL
                    String baseUrl = "http://lequangthanh.click"; // Corrected domain
                    imageUrl = baseUrl + p.image;
                    Log.d("ProductAdapter", "Case: Relative with /uploads/");
                } else if (p.image.startsWith("uploads/")) {
                    // Relative path starting with uploads/ (no leading slash)
                    String baseUrl = "http://lequangthanh.click"; // Corrected domain
                    imageUrl = baseUrl + "/" + p.image;
                    Log.d("ProductAdapter", "Case: Relative uploads/ without slash");
                } else {
                    // Invalid or unrecognized path - use placeholder
                    Log.w("ProductAdapter", "Invalid image path: '" + p.image + "' - using placeholder");
                    h.img.setImageResource(R.drawable.ic_image_placeholder);
                    return;
                }

                Log.d("ProductAdapter", "Final image URL: " + imageUrl);

                // Test URL accessibility
                Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder) // Show placeholder on error
                    .into(h.img, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d("ProductAdapter", "✅ Image loaded successfully: " + imageUrl);
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("ProductAdapter", "❌ Image load failed: " + imageUrl);
                            Log.e("ProductAdapter", "Error: " + e.getMessage());
                        }
                    });
            } else {
                Log.d("ProductAdapter", "No valid image URL for product: " + p.name + " (image_url='" + p.image + "') - using placeholder");
                h.img.setImageResource(R.drawable.ic_image_placeholder);
            }

            h.btnEdit.setOnClickListener(v -> { if (listener != null) listener.onEdit(p, h.getAdapterPosition()); });
            h.btnDelete.setOnClickListener(v -> { if (listener != null) listener.onDelete(p, h.getAdapterPosition()); });
        }

        @Override public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView name, price, desc;
            ImageView img;
            Button btnEdit, btnDelete;
            VH(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.txtName);
                price = itemView.findViewById(R.id.txtPrice);
                desc = itemView.findViewById(R.id.txtDescription);
                img = itemView.findViewById(R.id.imgProduct);
                btnEdit = itemView.findViewById(R.id.btnEdit);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }
        }
    }
}
