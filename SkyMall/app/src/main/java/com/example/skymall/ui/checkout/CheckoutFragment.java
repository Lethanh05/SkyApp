package com.example.skymall.ui.checkout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.skymall.R;
import com.example.skymall.data.repository.CustomerOrderRepository;
import com.example.skymall.utils.MoneyFmt;

public class CheckoutFragment extends Fragment {
    private static final String ARG_CART_ID = "cart_id";
    private static final String ARG_ADDRESS_ID = "address_id";
    private static final String ARG_SUBTOTAL = "subtotal";

    public static CheckoutFragment newInstance(int cartId, int addressId, double subtotal) {
        Bundle args = new Bundle();
        args.putInt(ARG_CART_ID, cartId);
        args.putInt(ARG_ADDRESS_ID, addressId);
        args.putDouble(ARG_SUBTOTAL, subtotal);
        CheckoutFragment fragment = new CheckoutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private EditText etVoucherCode, etShippingFee;
    private TextView tvSubtotal, tvDiscount, tvShippingFee, tvGrandTotal;
    private Button btnPlaceOrder;
    private CustomerOrderRepository orderRepository;

    private int cartId;
    private int addressId;
    private double subtotal;
    private double discount = 0.0;
    private double shippingFee = 0.0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        getArgumentsData();
        setupClickListeners();
        updatePriceDisplay();

        orderRepository = new CustomerOrderRepository(requireContext());
    }

    private void initViews(View view) {
        etVoucherCode = view.findViewById(R.id.etVoucherCode);
        etShippingFee = view.findViewById(R.id.etShippingFee);
        tvSubtotal = view.findViewById(R.id.tvSubtotal);
        tvDiscount = view.findViewById(R.id.tvDiscount);
        tvShippingFee = view.findViewById(R.id.tvShippingFee);
        tvGrandTotal = view.findViewById(R.id.tvGrandTotal);
        btnPlaceOrder = view.findViewById(R.id.btnPlaceOrder);
    }

    private void getArgumentsData() {
        if (getArguments() != null) {
            cartId = getArguments().getInt(ARG_CART_ID);
            addressId = getArguments().getInt(ARG_ADDRESS_ID);
            subtotal = getArguments().getDouble(ARG_SUBTOTAL);
        }
    }

    private void setupClickListeners() {
        btnPlaceOrder.setOnClickListener(v -> placeOrder());

        // Update shipping fee when user changes it
        etShippingFee.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    try {
                        shippingFee = Double.parseDouble(etShippingFee.getText().toString());
                        updatePriceDisplay();
                    } catch (NumberFormatException e) {
                        shippingFee = 0.0;
                        etShippingFee.setText("0");
                        updatePriceDisplay();
                    }
                }
            }
        });
    }

    private void updatePriceDisplay() {
        tvSubtotal.setText(MoneyFmt.vnd(subtotal));
        tvDiscount.setText(MoneyFmt.vnd(discount));
        tvShippingFee.setText(MoneyFmt.vnd(shippingFee));

        double grandTotal = subtotal - discount + shippingFee;
        tvGrandTotal.setText(MoneyFmt.vnd(grandTotal));
    }

    private void placeOrder() {
        btnPlaceOrder.setEnabled(false);
        btnPlaceOrder.setText("Đang xử lý...");

        String voucherCode = etVoucherCode.getText().toString().trim();
        if (voucherCode.isEmpty()) {
            voucherCode = null;
        }

        // Get shipping fee from input
        try {
            shippingFee = Double.parseDouble(etShippingFee.getText().toString());
        } catch (NumberFormatException e) {
            shippingFee = 0.0;
        }

        orderRepository.createOrder(cartId, addressId, voucherCode, shippingFee,
            new CustomerOrderRepository.CreateOrderCallback() {
                @Override
                public void onSuccess(int orderId) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Đặt hàng thành công! Mã đơn hàng: #" + orderId,
                                Toast.LENGTH_LONG).show();

                        // Navigate to order detail or order list
                        navigateToOrderDetail(orderId);
                    }
                }

                @Override
                public void onError(String error) {
                    btnPlaceOrder.setEnabled(true);
                    btnPlaceOrder.setText("Đặt hàng");

                    if (getContext() != null) {
                        String errorMessage = getErrorMessage(error);
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            });
    }

    private String getErrorMessage(String error) {
        switch (error) {
            case "missing_fields":
                return "Thiếu thông tin giỏ hàng hoặc địa chỉ";
            case "invalid_cart":
                return "Giỏ hàng không hợp lệ";
            case "invalid_address":
                return "Địa chỉ không hợp lệ";
            case "empty_cart":
                return "Giỏ hàng trống";
            case "invalid_voucher":
                return "Mã giảm giá không hợp lệ";
            case "voucher_exhausted":
                return "Mã giảm giá đã hết lượt sử dụng";
            case "voucher_user_limit":
                return "Bạn đã sử dụng hết lượt cho mã giảm giá này";
            case "voucher_min_order":
                return "Đơn hàng chưa đạt giá trị tối thiểu để sử dụng mã giảm giá";
            default:
                if (error.startsWith("out_of_stock:")) {
                    return "Sản phẩm đã hết hàng";
                }
                return "Đặt hàng thất bại: " + error;
        }
    }

    private void navigateToOrderDetail(int orderId) {
        // Navigate to order detail fragment
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(((ViewGroup) requireView().getParent()).getId(),
                        com.example.skymall.ui.orders.OrderDetailFragment.newInstance(orderId))
                .addToBackStack(null)
                .commit();
    }
}
