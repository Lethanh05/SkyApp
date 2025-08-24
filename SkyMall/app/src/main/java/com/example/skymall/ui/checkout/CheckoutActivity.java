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
import com.example.skymall.data.model.CartItem;
import com.example.skymall.data.model.Voucher;
import com.example.skymall.data.remote.ApiClient;
import com.example.skymall.data.remote.ApiService;
import com.example.skymall.data.remote.DTO.CreateOrderResp;
import com.example.skymall.ui.voucher.VoucherSelectDialog;
import com.example.skymall.utils.MoneyFmt;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {

    private RecyclerView rvCheckoutItems;
    private LinearLayout llVoucherSection;
    private TextView tvVoucherName, tvVoucherDiscount, tvSubtotal, tvShippingFee, tvVoucherDiscountAmount, tvTotal;
    private Button btnSelectVoucher, btnPlaceOrder;

    private CheckoutItemsAdapter adapter;
    private List<CartItem> cartItems;
    private Voucher selectedVoucher;
    private ApiService api;

    private double subtotal = 0;
    private double shippingFee = 30000; // Fixed shipping fee
    private double voucherDiscountAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        initViews();
        setupApi();
        getCartItemsFromIntent();
        setupRecyclerView();
        calculatePrices();
        setupClickListeners();
    }

    private void initViews() {
        rvCheckoutItems = findViewById(R.id.rvCheckoutItems);
        llVoucherSection = findViewById(R.id.llVoucherSection);
        tvVoucherName = findViewById(R.id.tvVoucherName);
        tvVoucherDiscount = findViewById(R.id.tvVoucherDiscount);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvShippingFee = findViewById(R.id.tvShippingFee);
        tvVoucherDiscountAmount = findViewById(R.id.tvVoucherDiscountAmount);
        tvTotal = findViewById(R.id.tvTotal);
        btnSelectVoucher = findViewById(R.id.btnSelectVoucher);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
    }

    private void setupApi() {
        // TODO: Initialize API client when ApiClient.getRetrofitInstance() is implemented
        // api = ApiClient.getRetrofitInstance().create(ApiService.class);
        
        // Temporary: Set to null until ApiClient is properly implemented
        api = null;
    }

    private void getCartItemsFromIntent() {
        cartItems = (List<CartItem>) getIntent().getSerializableExtra("cart_items");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
            finish();
            return;
        }
    }

    private void setupRecyclerView() {
        adapter = new CheckoutItemsAdapter(cartItems);
        rvCheckoutItems.setLayoutManager(new LinearLayoutManager(this));
        rvCheckoutItems.setAdapter(adapter);
    }

    private void calculatePrices() {
        subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.price * item.quantity;
        }

        // Calculate voucher discount
        voucherDiscountAmount = 0;
        if (selectedVoucher != null) {
            if (selectedVoucher.discountType.equals("percentage")) {
                voucherDiscountAmount = subtotal * selectedVoucher.discountValue / 100;
                if (selectedVoucher.maxDiscountAmount > 0) {
                    voucherDiscountAmount = Math.min(voucherDiscountAmount, selectedVoucher.maxDiscountAmount);
                }
            } else {
                voucherDiscountAmount = selectedVoucher.discountValue;
            }
        }

        double total = subtotal + shippingFee - voucherDiscountAmount;

        updatePriceDisplay(subtotal, shippingFee, voucherDiscountAmount, total);
    }

    private void updatePriceDisplay(double subtotal, double shippingFee, double discount, double total) {
        tvSubtotal.setText(MoneyFmt.format(subtotal));
        tvShippingFee.setText(MoneyFmt.format(shippingFee));
        tvVoucherDiscountAmount.setText(discount > 0 ? "-" + MoneyFmt.format(discount) : "0đ");
        tvTotal.setText(MoneyFmt.format(total));

        // Show/hide voucher section based on selection
        if (selectedVoucher != null) {
            llVoucherSection.setVisibility(View.VISIBLE);
            tvVoucherName.setText(selectedVoucher.title);
            if (selectedVoucher.discountType.equals("percentage")) {
                tvVoucherDiscount.setText("Giảm " + (int)selectedVoucher.discountValue + "%");
            } else {
                tvVoucherDiscount.setText("Giảm " + MoneyFmt.format(selectedVoucher.discountValue));
            }
            btnSelectVoucher.setText("Thay đổi voucher");
        } else {
            llVoucherSection.setVisibility(View.GONE);
            btnSelectVoucher.setText("Chọn voucher");
        }
    }

    private void setupClickListeners() {
        btnSelectVoucher.setOnClickListener(v -> showVoucherSelectionDialog());

        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void showVoucherSelectionDialog() {
        VoucherSelectDialog dialog = new VoucherSelectDialog(this, selectedVoucher, voucher -> {
            selectedVoucher = voucher;
            calculatePrices();
        });
        dialog.show();
    }

    private void placeOrder() {
        btnPlaceOrder.setEnabled(false);
        btnPlaceOrder.setText("Đang xử lý...");

        // TODO: Implement API call when ApiClient is properly set up
        // For now, simulate successful order creation

        // Simulate API delay
        btnPlaceOrder.postDelayed(() -> {
            btnPlaceOrder.setEnabled(true);
            btnPlaceOrder.setText("Đặt hàng");

            // Generate a mock order ID
            String mockOrderId = "ORD" + System.currentTimeMillis();
            double total = subtotal + shippingFee - voucherDiscountAmount;

            // Navigate to thank you screen
            Intent intent = new Intent(CheckoutActivity.this, ThankYouActivity.class);
            intent.putExtra("order_id", mockOrderId);
            intent.putExtra("total_amount", total);
            startActivity(intent);
            finish();
        }, 2000); // 2 second delay to simulate API call
    }

    // Inner class for order request
    public static class CreateOrderRequest {
        public List<CartItem> cartItems;
        public Integer voucherId;
        public double subtotal;
        public double shippingFee;
        public double discountAmount;
        public double total;
    }
}
