package com.example.skymall.ui.voucher;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.example.skymall.R;
import com.example.skymall.data.remote.DTO.VoucherCheckResp;
import com.example.skymall.data.repository.VoucherRepository;

public class VoucherCheckDialog {

    public interface OnVoucherAppliedListener {
        void onVoucherApplied(VoucherCheckResp.VoucherCheckData voucher);
        void onVoucherRemoved();
    }

    private final Context context;
    private final double orderValue;
    private final OnVoucherAppliedListener listener;
    private final VoucherRepository voucherRepository;
    private Dialog dialog;

    // Views
    private EditText etVoucherCode;
    private Button btnCheck;
    private Button btnApply;
    private Button btnRemove;
    private TextView tvResult;
    private ProgressBar progressBar;

    private VoucherCheckResp.VoucherCheckData currentVoucher;

    public VoucherCheckDialog(Context context, double orderValue, OnVoucherAppliedListener listener) {
        this.context = context;
        this.orderValue = orderValue;
        this.listener = listener;
        this.voucherRepository = new VoucherRepository(context);
    }

    public void show() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_voucher_check, null);
        initViews(view);
        setupListeners();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setTitle("Áp dụng Voucher");
        builder.setNegativeButton("Đóng", null);

        dialog = builder.create();
        dialog.show();
    }

    private void initViews(View view) {
        etVoucherCode = view.findViewById(R.id.et_voucher_code);
        btnCheck = view.findViewById(R.id.btn_check);
        btnApply = view.findViewById(R.id.btn_apply);
        btnRemove = view.findViewById(R.id.btn_remove);
        tvResult = view.findViewById(R.id.tv_result);
        progressBar = view.findViewById(R.id.progress_bar);

        // Initially hide result views
        btnApply.setVisibility(View.GONE);
        btnRemove.setVisibility(View.GONE);
        tvResult.setVisibility(View.GONE);
    }

    private void setupListeners() {
        etVoucherCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                resetResult();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnCheck.setOnClickListener(v -> checkVoucher());
        btnApply.setOnClickListener(v -> applyVoucher());
        btnRemove.setOnClickListener(v -> removeVoucher());
    }

    private void checkVoucher() {
        String code = etVoucherCode.getText().toString().trim();
        if (code.isEmpty()) {
            Toast.makeText(context, "Vui lòng nhập mã voucher", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        voucherRepository.checkVoucher(code, orderValue, new VoucherRepository.VoucherCheckCallback() {
            @Override
            public void onSuccess(VoucherCheckResp response) {
                setLoading(false);
                if (response.voucher != null) {
                    currentVoucher = response.voucher;
                    showSuccessResult(response.voucher);
                }
            }

            @Override
            public void onError(String error, String[] details) {
                setLoading(false);
                showErrorResult(error, details);
            }
        });
    }

    private void showSuccessResult(VoucherCheckResp.VoucherCheckData voucher) {
        tvResult.setVisibility(View.VISIBLE);
        tvResult.setText(String.format(
            "✓ Voucher hợp lệ!\nMã: %s\nGiảm: %s\nĐơn tối thiểu: %s",
            voucher.code,
            VoucherUtils.formatDiscountAmount(voucher.discountAmount),
            VoucherUtils.formatOrderValue(voucher.minOrderValue)
        ));
        tvResult.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));

        btnApply.setVisibility(View.VISIBLE);
        btnRemove.setVisibility(View.GONE);
    }

    private void showErrorResult(String error, String[] details) {
        tvResult.setVisibility(View.VISIBLE);

        String errorMessage = "✗ " + VoucherUtils.getErrorMessage(context, error);
        if (details.length > 0) {
            errorMessage += "\n" + VoucherUtils.formatErrorMessages(context, details);
        }

        tvResult.setText(errorMessage);
        tvResult.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));

        btnApply.setVisibility(View.GONE);
        btnRemove.setVisibility(View.GONE);
        currentVoucher = null;
    }

    private void resetResult() {
        tvResult.setVisibility(View.GONE);
        btnApply.setVisibility(View.GONE);
        btnRemove.setVisibility(View.GONE);
        currentVoucher = null;
    }

    private void applyVoucher() {
        if (currentVoucher != null && listener != null) {
            listener.onVoucherApplied(currentVoucher);
            dialog.dismiss();
        }
    }

    private void removeVoucher() {
        if (listener != null) {
            listener.onVoucherRemoved();
            dialog.dismiss();
        }
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnCheck.setEnabled(!loading);
        etVoucherCode.setEnabled(!loading);
    }
}
