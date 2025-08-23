package com.example.skymall.ui.cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.skymall.R;
import com.example.skymall.ui.payment.PaymentActivity;

public class CartActivity extends AppCompatActivity {

    private TextView checkoutButton;
    private double totalAmount = 299000; // Ví dụ tổng tiền

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Giả sử layout cart đã có sẵn
        // setContentView(R.layout.activity_cart);

        setupCheckoutButton();
    }

    private void setupCheckoutButton() {
        // checkoutButton = findViewById(R.id.checkout_button);
        // checkoutButton.setOnClickListener(v -> proceedToPayment());
    }

    private void proceedToPayment() {
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("total_amount", totalAmount);
        startActivity(intent);
    }
}
