package com.example.skymall.ui.checkout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skymall.R;
import com.example.skymall.data.model.CartItem;     // Serializable model dùng chung
import com.example.skymall.data.model.Voucher;
import com.example.skymall.data.remote.ApiClient;
import com.example.skymall.data.remote.ApiService;
import com.example.skymall.data.remote.DTO.CreateOrderResp;
import com.example.skymall.ui.address.Address;
import com.example.skymall.ui.address.AddressRepository;
import com.example.skymall.ui.voucher.VoucherSelectDialog;
import com.example.skymall.utils.MoneyFmt;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {

    // Intent Keys
    public static final String EXTRA_CART_ITEMS = "cart_items";   // ArrayList<CartItem> (Serializable)
    public static final String EXTRA_CART_ID    = "cart_id";      // int
    public static final String EXTRA_ADDRESS_ID = "address_id";   // int (optional nếu đã có mặc định)

    private RecyclerView rvCheckoutItems;
    private LinearLayout llVoucherSection;
    private TextView tvVoucherName, tvVoucherDiscount, tvSubtotal, tvShippingFee,
            tvVoucherDiscountAmount, tvTotal, tvVoucherDescription;

    // Address UI
    private Button btnSelectAddress;
    private TextView tvAddressLine;

    // Voucher & action
    private Button btnSelectVoucher, btnPlaceOrder;

    private CheckoutItemsAdapter adapter; // adapter hiển thị item trong checkout
    private List<CartItem> cartItems = new ArrayList<>();
    private Voucher selectedVoucher;
    private ApiService api;

    // Address data
    private AddressRepository addressRepo;
    private List<Address> addressList = new ArrayList<>();
    private Integer selectedAddressId = null;

    // Cart id từ giỏ
    private int cartIdFromIntent = -1;

    // Pricing
    private double subtotal = 0;
    private double shippingFee = 30_000; // phí ship demo
    private double voucherDiscountAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        initViews();
        setupApi();
        getCartItemsAndIdsFromIntent();
        setupRecyclerView();
        setupClickListeners();

        // Load địa chỉ thật từ server -> set mặc định
        loadAddressesThenPickDefault();

        // Tính tiền sau cùng (voucher/ship)
        calculatePrices();
    }

    private void initViews() {
        rvCheckoutItems = findViewById(R.id.rvCheckoutItems);

        // Address UI
        btnSelectAddress = findViewById(R.id.btnSelectAddress);
        tvAddressLine    = findViewById(R.id.tvAddressLine);

        // Voucher UI
        llVoucherSection = findViewById(R.id.llVoucherSection);
        tvVoucherName = findViewById(R.id.tvVoucherName);
        tvVoucherDiscount = findViewById(R.id.tvVoucherDiscount);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvShippingFee = findViewById(R.id.tvShippingFee);
        tvVoucherDiscountAmount = findViewById(R.id.tvVoucherDiscountAmount);
        tvTotal = findViewById(R.id.tvTotal);
        btnSelectVoucher = findViewById(R.id.btnSelectVoucher);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        // view này có thể chưa có trong XML, nên lấy xong vẫn null-check khi dùng:
        tvVoucherDescription = findViewById(R.id.tvVoucherDescription);
    }

    private void setupApi() {
        api = ApiClient.create(this);
        addressRepo = AddressRepository.get(this);
    }

    @SuppressWarnings("unchecked")
    private void getCartItemsAndIdsFromIntent() {
        List<CartItem> incoming = (List<CartItem>) getIntent().getSerializableExtra(EXTRA_CART_ITEMS);
        if (incoming != null) cartItems = new ArrayList<>(incoming);
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ trống, không thể thanh toán", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        cartIdFromIntent = getIntent().getIntExtra(EXTRA_CART_ID, -1);

        // Nếu caller đã truyền sẵn address_id thì ưu tiên
        int addrFromIntent = getIntent().getIntExtra(EXTRA_ADDRESS_ID, -1);
        if (addrFromIntent > 0) {
            selectedAddressId = addrFromIntent;
            tvAddressLine.setText("Địa chỉ ID: " + selectedAddressId); // Sẽ được thay thế bằng mô tả thật sau khi load
        }
    }

    private void setupRecyclerView() {
        adapter = new CheckoutItemsAdapter(cartItems);
        rvCheckoutItems.setLayoutManager(new LinearLayoutManager(this));
        rvCheckoutItems.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnSelectVoucher.setOnClickListener(v -> showVoucherSelectionDialog());
        btnPlaceOrder.setOnClickListener(v -> placeOrder());
        btnSelectAddress.setOnClickListener(v -> openAddressDialog());
    }

    // ==== ĐỊA CHỈ ====
    private void loadAddressesThenPickDefault() {
        addressRepo.loadAddresses(new AddressRepository.AddressCallback() {
            @Override
            public void onSuccess(List<Address> addresses) {
                addressList = addresses != null ? addresses : new ArrayList<>();

                // Nếu đã có selectedAddressId từ Intent, cố tìm để hiển thị mô tả:
                if (selectedAddressId != null && selectedAddressId > 0) {
                    Address found = findAddressById(selectedAddressId);
                    if (found != null) {
                        tvAddressLine.setText(formatAddress(found));
                        return;
                    }
                    // Nếu không tìm thấy -> tiếp tục chọn mặc định
                    selectedAddressId = null;
                }

                // Chưa có id -> chọn default nếu có, nếu không lấy phần tử đầu
                if (!addressList.isEmpty()) {
                    Address def = null;
                    for (Address a : addressList) {
                        if (a.isDefault) { def = a; break; }
                    }
                    Address pick = def != null ? def : addressList.get(0);
                    selectedAddressId = pick.id;
                    tvAddressLine.setText(formatAddress(pick));
                } else {
                    tvAddressLine.setText("Chưa có địa chỉ. Nhấn 'Chọn địa chỉ' để thêm.");
                }
            }

            @Override
            public void onError(String error) {
                tvAddressLine.setText("Không tải được địa chỉ: " + error);
                // vẫn cho người dùng bấm chọn (để thử lại)
            }
        });
    }

    private Address findAddressById(int id) {
        for (Address a : addressList) if (a.id == id) return a;
        return null;
    }

    private String formatAddress(Address a) {
        // Tùy vào Address bạn định nghĩa field
        // Ví dụ: name/phone + addressLine, ward, district, province
        String line1 = (a.name != null ? a.name : "") +
                (a.phone != null ? " - " + a.phone : "");
        String line2 = (a.addressLine != null ? a.addressLine : "");
        if (a.ward != null && !a.ward.isEmpty()) line2 += ", " + a.ward;
        if (a.district != null && !a.district.isEmpty()) line2 += ", " + a.district;
        if (a.province != null && !a.province.isEmpty()) line2 += ", " + a.province;
        return (line1.trim().isEmpty() ? "" : line1 + "\n") + line2;
    }

    private void openAddressDialog() {
        // Đảm bảo data mới nhất
        addressRepo.loadAddresses(new AddressRepository.AddressCallback() {
            @Override
            public void onSuccess(List<Address> addresses) {
                addressList = addresses != null ? addresses : new ArrayList<>();
                if (addressList.isEmpty()) {
                    Toast.makeText(CheckoutActivity.this, "Bạn chưa có địa chỉ. Hãy thêm địa chỉ trước.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String[] labels = new String[addressList.size()];
                for (int i = 0; i < addressList.size(); i++) {
                    labels[i] = formatAddress(addressList.get(i));
                }

                new androidx.appcompat.app.AlertDialog.Builder(CheckoutActivity.this)
                        .setTitle("Chọn địa chỉ")
                        .setItems(labels, (d, which) -> {
                            Address pick = addressList.get(which);
                            selectedAddressId = pick.id;
                            tvAddressLine.setText(formatAddress(pick));
                        })
                        .setNegativeButton("Đóng", null)
                        .show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CheckoutActivity.this, "Không tải được địa chỉ: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ==== TÍNH TIỀN ====
    private void calculatePrices() {
        subtotal = 0;
        for (CartItem item : cartItems) {
            int qty = Math.max(1, item.quantity);
            subtotal += item.price * qty;
        }

        voucherDiscountAmount = 0;
        if (selectedVoucher != null) {
            if ("percent".equalsIgnoreCase(selectedVoucher.discountType)) {
                voucherDiscountAmount = subtotal * selectedVoucher.discountValue / 100.0;
                if (selectedVoucher.maxDiscountAmount > 0) {
                    voucherDiscountAmount = Math.min(voucherDiscountAmount, selectedVoucher.maxDiscountAmount);
                }
            } else {
                voucherDiscountAmount = selectedVoucher.discountValue;
            }
            voucherDiscountAmount = Math.min(voucherDiscountAmount, subtotal);
        }

        double total = Math.max(0, subtotal + shippingFee - voucherDiscountAmount);
        updatePriceDisplay(subtotal, shippingFee, voucherDiscountAmount, total);
    }

    private void updatePriceDisplay(double subtotal, double shippingFee, double discount, double total) {
        tvSubtotal.setText(MoneyFmt.format(subtotal));
        tvShippingFee.setText(MoneyFmt.format(shippingFee));
        tvVoucherDiscountAmount.setText(discount > 0 ? "-" + MoneyFmt.format(discount) : "0đ");
        tvTotal.setText(MoneyFmt.format(total));

        if (selectedVoucher != null) {
            llVoucherSection.setVisibility(View.VISIBLE);
            tvVoucherName.setText(selectedVoucher.code != null ? selectedVoucher.code : "Voucher");
            if ("percent".equalsIgnoreCase(selectedVoucher.discountType)) {
                tvVoucherDiscount.setText("Giảm " + (int) selectedVoucher.discountValue + "%");
            } else {
                tvVoucherDiscount.setText("Giảm " + MoneyFmt.format(selectedVoucher.discountValue));
            }
            if (tvVoucherDescription != null) {
                tvVoucherDescription.setText(
                        selectedVoucher.description != null ? selectedVoucher.description : "Không có mô tả"
                );
            }
            btnSelectVoucher.setText("Thay đổi voucher");
        } else {
            llVoucherSection.setVisibility(View.GONE);
            if (tvVoucherDescription != null) tvVoucherDescription.setText("");
            btnSelectVoucher.setText("Chọn voucher");
        }
    }

    // ==== VOUCHER DIALOG ====
    private void showVoucherSelectionDialog() {
        VoucherSelectDialog dialog = new VoucherSelectDialog(
                this,
                selectedVoucher,
                voucher -> {
                    selectedVoucher = voucher;
                    calculatePrices();
                });
        dialog.show();
    }

    // ==== TẠO ĐƠN ====
    private void placeOrder() {
        if (api == null) {
            Toast.makeText(this, "API chưa sẵn sàng", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cartIdFromIntent <= 0) {
            Toast.makeText(this, "Thiếu cart_id. Vui lòng quay lại giỏ và thử lại.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedAddressId == null || selectedAddressId <= 0) {
            Toast.makeText(this, "Bạn chưa chọn địa chỉ giao hàng.", Toast.LENGTH_SHORT).show();
            return;
        }

        String voucherCode = selectedVoucher != null ? selectedVoucher.code : null;

        btnPlaceOrder.setEnabled(false);
        btnPlaceOrder.setText("Đang xử lý...");

        // Field types phải khớp ApiService (FormUrlEncoded). Dùng int nếu backend mong đợi int.
        int shippingFeeValue = (int) shippingFee;

        api.createOrder(cartIdFromIntent, selectedAddressId, voucherCode, shippingFeeValue)
                .enqueue(new Callback<CreateOrderResp>() {
                    @Override
                    public void onResponse(Call<CreateOrderResp> call, Response<CreateOrderResp> response) {
                        btnPlaceOrder.setEnabled(true);
                        btnPlaceOrder.setText("Đặt hàng");

                        if (response.isSuccessful() && response.body() != null) {
                            double total = Math.max(0, subtotal + shippingFee - voucherDiscountAmount);

                            try {
                                Intent intent = new Intent(CheckoutActivity.this,
                                        Class.forName("com.example.skymall.ui.checkout.ThankYouActivity"));
                                intent.putExtra("order_id", response.body().order_id);
                                intent.putExtra("total_amount", total);
                                startActivity(intent);
                            } catch (ClassNotFoundException e) {
                                Toast.makeText(CheckoutActivity.this,
                                        "Đặt hàng thành công! Mã đơn: " + response.body().order_id,
                                        Toast.LENGTH_LONG).show();
                            }
                            finish();
                        } else {
                            Toast.makeText(CheckoutActivity.this,
                                    "Đặt hàng thất bại. Vui lòng thử lại!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CreateOrderResp> call, Throwable t) {
                        btnPlaceOrder.setEnabled(true);
                        btnPlaceOrder.setText("Đặt hàng");
                        Toast.makeText(CheckoutActivity.this,
                                "Lỗi kết nối: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
