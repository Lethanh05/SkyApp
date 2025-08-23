package com.example.skymall.ui.payment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.skymall.R;

public class PaymentActivity extends AppCompatActivity {

    private LinearLayout paymentMethodContainer;
    private TextView totalAmountText;
    private TextView payButton;
    private String selectedPaymentMethod = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initViews();
        setupToolbar();
        setupPaymentMethods();
        setupPayButton();

        // Get data from intent
        double totalAmount = getIntent().getDoubleExtra("total_amount", 0.0);
        totalAmountText.setText(String.format("₫%,.0f", totalAmount));
    }

    private void initViews() {
        paymentMethodContainer = findViewById(R.id.payment_method_container);
        totalAmountText = findViewById(R.id.total_amount_text);
        payButton = findViewById(R.id.pay_button);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Thanh toán");

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupPaymentMethods() {
        // Setup click listeners for payment methods
        findViewById(R.id.payment_method_momo).setOnClickListener(v -> selectPaymentMethod("momo"));
        findViewById(R.id.payment_method_banking).setOnClickListener(v -> selectPaymentMethod("banking"));
        findViewById(R.id.payment_method_cod).setOnClickListener(v -> selectPaymentMethod("cod"));
        findViewById(R.id.payment_method_credit).setOnClickListener(v -> selectPaymentMethod("credit"));
    }

    private void selectPaymentMethod(String method) {
        selectedPaymentMethod = method;

        // Reset all selections
        resetPaymentMethodSelection();

        // Highlight selected method
        switch (method) {
            case "momo":
                findViewById(R.id.payment_method_momo).setSelected(true);
                findViewById(R.id.radio_momo).setVisibility(View.VISIBLE);
                break;
            case "banking":
                findViewById(R.id.payment_method_banking).setSelected(true);
                findViewById(R.id.radio_banking).setVisibility(View.VISIBLE);
                break;
            case "cod":
                findViewById(R.id.payment_method_cod).setSelected(true);
                findViewById(R.id.radio_cod).setVisibility(View.VISIBLE);
                break;
            case "credit":
                findViewById(R.id.payment_method_credit).setSelected(true);
                findViewById(R.id.radio_credit).setVisibility(View.VISIBLE);
                break;
        }
    }

    private void resetPaymentMethodSelection() {
        findViewById(R.id.payment_method_momo).setSelected(false);
        findViewById(R.id.payment_method_banking).setSelected(false);
        findViewById(R.id.payment_method_cod).setSelected(false);
        findViewById(R.id.payment_method_credit).setSelected(false);

        findViewById(R.id.radio_momo).setVisibility(View.GONE);
        findViewById(R.id.radio_banking).setVisibility(View.GONE);
        findViewById(R.id.radio_cod).setVisibility(View.GONE);
        findViewById(R.id.radio_credit).setVisibility(View.GONE);
    }

    private void setupPayButton() {
        payButton.setOnClickListener(v -> {
            if (selectedPaymentMethod.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
                return;
            }

            processPayment();
        });
    }

    private void processPayment() {
        // Implement payment processing logic here
        String message = "";
        switch (selectedPaymentMethod) {
            case "momo":
                message = "Đang chuyển đến MoMo...";
                break;
            case "banking":
                message = "Đang chuyển đến ngân hàng...";
                break;
            case "cod":
                message = "Đặt hàng thành công! Thanh toán khi nhận hàng.";
                break;
            case "credit":
                message = "Đang xử lý thanh toán thẻ tín dụng...";
                break;
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        // For COD, finish the activity
        if ("cod".equals(selectedPaymentMethod)) {
            finish();
        }
    }
}
