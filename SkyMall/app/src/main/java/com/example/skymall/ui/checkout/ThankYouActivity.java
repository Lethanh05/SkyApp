package com.example.skymall.ui.checkout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.skymall.R;
import com.example.skymall.MainActivity;
import com.example.skymall.ui.order.OrderListActivity;
import com.example.skymall.utils.MoneyFmt;

public class ThankYouActivity extends AppCompatActivity {

    private TextView tvOrderId, tvTotalAmount;
    private Button btnBackToHome, btnViewOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);

        initViews();
        displayOrderInfo();
        setupClickListeners();
    }

    private void initViews() {
        tvOrderId = findViewById(R.id.tvOrderId);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnBackToHome = findViewById(R.id.btnBackToHome);
        btnViewOrders = findViewById(R.id.btnViewOrders);
    }

    private void displayOrderInfo() {
        String orderId = getIntent().getStringExtra("order_id");
        double totalAmount = getIntent().getDoubleExtra("total_amount", 0);

        tvOrderId.setText("Mã đơn hàng: " + orderId);
        tvTotalAmount.setText(MoneyFmt.format(totalAmount));
    }

    private void setupClickListeners() {
        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnViewOrders.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderListActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        // Redirect to home instead of going back to checkout
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
