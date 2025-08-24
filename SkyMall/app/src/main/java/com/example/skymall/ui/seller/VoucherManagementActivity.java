package com.example.skymall.ui.seller;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skymall.R;
import com.example.skymall.data.model.Voucher;
import com.example.skymall.data.remote.ApiClient;
import com.example.skymall.data.remote.ApiService;
import com.example.skymall.data.remote.DTO.BaseResp;
import com.example.skymall.data.remote.DTO.VoucherListResp;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoucherManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VoucherManagementAdapter adapter;
    private FloatingActionButton fabAdd;
    private ApiService apiService;
    private List<Voucher> voucherList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_management);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupApiService();
        loadVouchers();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewVouchers);
        fabAdd = findViewById(R.id.fabAddVoucher);

        fabAdd.setOnClickListener(v -> showCreateVoucherDialog());
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản lý Voucher");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Add stats menu item
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_stats) {
                startActivity(new Intent(this, VoucherStatsActivity.class));
                return true;
            }
            return false;
        });
        toolbar.inflateMenu(R.menu.menu_voucher_management);
    }

    private void setupRecyclerView() {
        adapter = new VoucherManagementAdapter(voucherList, new VoucherManagementAdapter.OnVoucherActionListener() {
            @Override
            public void onEdit(Voucher voucher) {
                showEditVoucherDialog(voucher);
            }

            @Override
            public void onDelete(Voucher voucher) {
                showDeleteConfirmDialog(voucher);
            }

            @Override
            public void onToggleStatus(Voucher voucher) {
                // Toggle voucher active status
                voucher.isActive = !voucher.isActive;
                // Update via API would go here
            }

            @Override
            public void onDuplicate(Voucher voucher) {
                showDuplicateVoucherDialog(voucher);
            }

            @Override
            public void onViewDetails(Voucher voucher) {
                showVoucherDetailsDialog(voucher);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupApiService() {
        apiService = ApiClient.create(this);
    }

    private void loadVouchers() {
        apiService.getVouchers(1, 50).enqueue(new Callback<VoucherListResp>() {
            @Override
            public void onResponse(@NonNull Call<VoucherListResp> call, @NonNull Response<VoucherListResp> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VoucherListResp responseBody = response.body();

                    if (responseBody.success) {
                        voucherList.clear();
                        List<Voucher> serverVouchers = responseBody.vouchers != null ? responseBody.vouchers : responseBody.data;

                        if (serverVouchers != null) {
                            voucherList.addAll(serverVouchers);
                        }
                        adapter.notifyDataSetChanged();
                        return;
                    }
                }

                tryStoreVouchersEndpoint();
            }

            @Override
            public void onFailure(@NonNull Call<VoucherListResp> call, @NonNull Throwable t) {
                tryStoreVouchersEndpoint();
            }
        });
    }

    private void tryStoreVouchersEndpoint() {
        apiService.getStoreVouchers(1, 50).enqueue(new Callback<VoucherListResp>() {
            @Override
            public void onResponse(@NonNull Call<VoucherListResp> call, @NonNull Response<VoucherListResp> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VoucherListResp responseBody = response.body();

                    if (responseBody.success) {
                        voucherList.clear();
                        if (responseBody.vouchers != null) {
                            voucherList.addAll(responseBody.vouchers);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        showEmptyState("Không có voucher nào: " + (responseBody.message != null ? responseBody.message : "Lỗi không xác định"));
                    }
                } else {
                    String errorMsg = "Lỗi server: " + response.code();
                    if (response.message() != null) {
                        errorMsg += " - " + response.message();
                    }
                    showEmptyState(errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<VoucherListResp> call, @NonNull Throwable t) {
                showEmptyState("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void showEmptyState(String message) {
        voucherList.clear();
        adapter.notifyDataSetChanged();
        Toast.makeText(VoucherManagementActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void showCreateVoucherDialog() {
        showVoucherDialog(null);
    }

    private void showEditVoucherDialog(Voucher voucher) {
        showVoucherDialog(voucher);
    }

    private void showVoucherDialog(Voucher existingVoucher) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_voucher_form, null);

        TextInputLayout tilCode = dialogView.findViewById(R.id.tilVoucherCode);
        TextInputLayout tilValue = dialogView.findViewById(R.id.tilVoucherValue);
        TextInputLayout tilMinOrder = dialogView.findViewById(R.id.tilMinOrderValue);
        TextInputLayout tilUsageLimit = dialogView.findViewById(R.id.tilUsageLimit);
        TextInputLayout tilPerUserLimit = dialogView.findViewById(R.id.tilPerUserLimit);
        TextInputLayout tilDescription = dialogView.findViewById(R.id.tilDescription);

        EditText etCode = tilCode.getEditText();
        EditText etValue = tilValue.getEditText();
        EditText etMinOrder = tilMinOrder.getEditText();
        EditText etUsageLimit = tilUsageLimit.getEditText();
        EditText etPerUserLimit = tilPerUserLimit.getEditText();
        EditText etDescription = tilDescription.getEditText();

        // Null check for EditText objects
        if (etCode == null || etValue == null || etMinOrder == null ||
            etUsageLimit == null || etPerUserLimit == null || etDescription == null) {
            Toast.makeText(this, "Lỗi tải form voucher", Toast.LENGTH_SHORT).show();
            return;
        }

        Spinner spinnerType = dialogView.findViewById(R.id.spinnerVoucherType);
        MaterialButton btnStartDate = dialogView.findViewById(R.id.btnStartDate);
        MaterialButton btnEndDate = dialogView.findViewById(R.id.btnEndDate);

        // Setup spinner
        String[] types = {"percent", "amount"};
        String[] typeLabels = {"Giảm theo %", "Giảm cố định"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeLabels);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        // Date pickers
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        btnStartDate.setOnClickListener(v -> {
            DatePickerDialog picker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    startDate.set(year, month, dayOfMonth);
                    btnStartDate.setText(dateFormat.format(startDate.getTime()));
                },
                startDate.get(Calendar.YEAR),
                startDate.get(Calendar.MONTH),
                startDate.get(Calendar.DAY_OF_MONTH)
            );
            picker.show();
        });

        btnEndDate.setOnClickListener(v -> {
            DatePickerDialog picker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    endDate.set(year, month, dayOfMonth);
                    btnEndDate.setText(dateFormat.format(endDate.getTime()));
                },
                endDate.get(Calendar.YEAR),
                endDate.get(Calendar.MONTH),
                endDate.get(Calendar.DAY_OF_MONTH)
            );
            picker.show();
        });

        // Fill existing data if editing
        if (existingVoucher != null) {
            if (existingVoucher.code != null) {
                etCode.setText(existingVoucher.code);
            }
            etValue.setText(String.valueOf(existingVoucher.discountValue)); // Sử dụng discountValue
            etMinOrder.setText(String.valueOf(existingVoucher.minOrderAmount)); // Sử dụng minOrderAmount
            if (existingVoucher.usageLimit > 0) { // usageLimit là int, không phải Integer
                etUsageLimit.setText(String.valueOf(existingVoucher.usageLimit));
            }
            // perUserLimit không tồn tại trong model - bỏ qua hoặc để trống
            etDescription.setText(existingVoucher.description != null ? existingVoucher.description : "");

            // Set spinner selection - sử dụng discountType
            if (existingVoucher.discountType != null) {
                spinnerType.setSelection("percentage".equals(existingVoucher.discountType) ? 0 : 1);
            }

            // Set dates - chỉ có expiryDate, không có startDate/endDate
            if (existingVoucher.expiryDate != null) {
                btnEndDate.setText(existingVoucher.expiryDate);
                // Parse và update Calendar object
                try {
                    java.util.Date parsedEndDate = dateFormat.parse(existingVoucher.expiryDate);
                    if (parsedEndDate != null) {
                        endDate.setTime(parsedEndDate);
                    }
                } catch (Exception e) {
                    // If parsing fails, keep default date
                }
            }
        } else {
            btnStartDate.setText(dateFormat.format(startDate.getTime()));
            btnEndDate.setText(dateFormat.format(endDate.getTime()));
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(existingVoucher == null ? "Tạo Voucher Mới" : "Chỉnh sửa Voucher")
                .setView(dialogView)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    if (validateVoucherFormWithType(tilCode, tilValue, tilMinOrder, spinnerType)) {
                        String code = etCode.getText().toString().trim();
                        String type = types[spinnerType.getSelectedItemPosition()];

                        try {
                            double value = Double.parseDouble(etValue.getText().toString().trim());
                            double minOrderValue = Double.parseDouble(etMinOrder.getText().toString().trim());
                            String startDateStr = btnStartDate.getText().toString();
                            String endDateStr = btnEndDate.getText().toString();

                            Integer usageLimit = TextUtils.isEmpty(etUsageLimit.getText().toString().trim()) ? null :
                                Integer.parseInt(etUsageLimit.getText().toString().trim());
                            Integer perUserLimit = TextUtils.isEmpty(etPerUserLimit.getText().toString().trim()) ? null :
                                Integer.parseInt(etPerUserLimit.getText().toString().trim());
                            String description = etDescription.getText().toString().trim();

                            if (existingVoucher == null) {
                                createVoucher(code, type, value, minOrderValue, startDateStr, endDateStr,
                                    usageLimit, perUserLimit, description);
                            } else {
                                updateVoucher(existingVoucher.id, code, type, value, minOrderValue,
                                    startDateStr, endDateStr, usageLimit, perUserLimit, description);
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "Vui lòng nhập số hợp lệ cho các trường số", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Hủy", null);

        builder.show();
    }

    private boolean validateVoucherForm(TextInputLayout tilCode, TextInputLayout tilValue, TextInputLayout tilMinOrder) {
        boolean isValid = true;

        // Validate code
        if (TextUtils.isEmpty(tilCode.getEditText().getText())) {
            tilCode.setError("Vui lòng nhập mã voucher");
            isValid = false;
        } else {
            tilCode.setError(null);
        }

        // Validate value
        if (TextUtils.isEmpty(tilValue.getEditText().getText())) {
            tilValue.setError("Vui lòng nhập giá trị giảm");
            isValid = false;
        } else {
            try {
                double value = Double.parseDouble(tilValue.getEditText().getText().toString().trim());
                if (value <= 0) {
                    tilValue.setError("Giá trị giảm phải lớn hơn 0");
                    isValid = false;
                } else if (value > 100) {
                    // Nếu là percentage, không được quá 100%
                    tilValue.setError("Giá trị giảm không được quá 100 (đối với %)");
                    isValid = false;
                } else {
                    tilValue.setError(null);
                }
            } catch (NumberFormatException e) {
                tilValue.setError("Vui lòng nhập số hợp lệ");
                isValid = false;
            }
        }

        // Validate min order value
        if (TextUtils.isEmpty(tilMinOrder.getEditText().getText())) {
            tilMinOrder.setError("Vui lòng nhập giá trị đơn hàng tối thiểu");
            isValid = false;
        } else {
            try {
                double minOrderValue = Double.parseDouble(tilMinOrder.getEditText().getText().toString().trim());
                if (minOrderValue < 0) {
                    tilMinOrder.setError("Giá trị đơn hàng tối thiểu không được âm");
                    isValid = false;
                } else {
                    tilMinOrder.setError(null);
                }
            } catch (NumberFormatException e) {
                tilMinOrder.setError("Vui lòng nhập số hợp lệ");
                isValid = false;
            }
        }

        return isValid;
    }

    private boolean validateVoucherFormWithType(TextInputLayout tilCode, TextInputLayout tilValue, TextInputLayout tilMinOrder, Spinner spinnerType) {
        boolean isValid = true;

        // Validate code
        if (TextUtils.isEmpty(tilCode.getEditText().getText())) {
            tilCode.setError("Vui lòng nhập mã voucher");
            isValid = false;
        } else {
            String code = tilCode.getEditText().getText().toString().trim();
            tilCode.setError(null);
        }

        // Get selected voucher type first
        int selectedPosition = spinnerType.getSelectedItemPosition();
        String[] types = {"percent", "amount"};
        String selectedType = selectedPosition >= 0 && selectedPosition < types.length ? types[selectedPosition] : null;

        // Validate value - DIFFERENT LOGIC FOR PERCENT vs AMOUNT
        if (TextUtils.isEmpty(tilValue.getEditText().getText())) {
            tilValue.setError("Vui lòng nhập giá trị giảm");
            isValid = false;
        } else {
            try {
                String valueStr = tilValue.getEditText().getText().toString().trim();
                double value = Double.parseDouble(valueStr);

                if (value <= 0) {
                    tilValue.setError("Giá trị giảm phải lớn hơn 0");
                    isValid = false;
                } else if ("percent".equals(selectedType) && value > 100) {
                    tilValue.setError("Giá trị phần trăm không được quá 100%");
                    isValid = false;
                } else if ("amount".equals(selectedType) && value > 10000000) {
                    tilValue.setError("Giá trị giảm không được quá 10,000,000");
                    isValid = false;
                } else {
                    tilValue.setError(null);
                }
            } catch (NumberFormatException e) {
                tilValue.setError("Vui lòng nhập số hợp lệ");
                isValid = false;
            }
        }

        // Validate min order value
        if (TextUtils.isEmpty(tilMinOrder.getEditText().getText())) {
            tilMinOrder.setError("Vui lòng nhập giá trị đơn hàng tối thiểu");
            isValid = false;
        } else {
            try {
                String minOrderStr = tilMinOrder.getEditText().getText().toString().trim();
                double minOrderValue = Double.parseDouble(minOrderStr);

                if (minOrderValue < 0) {
                    tilMinOrder.setError("Giá trị đơn hàng tối thiểu không được âm");
                    isValid = false;
                } else {
                    tilMinOrder.setError(null);
                }
            } catch (NumberFormatException e) {
                tilMinOrder.setError("Vui lòng nhập số hợp lệ");
                isValid = false;
            }
        }

        // Validate type selection
        if (selectedPosition == AdapterView.INVALID_POSITION || selectedPosition < 0) {
            Toast.makeText(this, "Vui lòng chọn loại voucher", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void createVoucher(String code, String type, double value, double minOrderValue,
                              String startDate, String endDate, Integer usageLimit,
                              Integer perUserLimit, String description) {

        // Handle null values - server expects per_user_limit minimum = 1
        Integer finalUsageLimit = usageLimit != null && usageLimit > 0 ? usageLimit : null;
        Integer finalPerUserLimit = perUserLimit != null && perUserLimit > 0 ? perUserLimit : 1;

        // Handle empty description - set to empty string instead of null
        String finalDescription = description != null && !description.trim().isEmpty() ? description : "";

        Call<BaseResp> call = apiService.createVoucher(code, type, value, minOrderValue, startDate, endDate,
                finalUsageLimit, finalPerUserLimit, finalDescription);

        call.enqueue(new Callback<BaseResp>() {
            @Override
            public void onResponse(@NonNull Call<BaseResp> call, @NonNull Response<BaseResp> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BaseResp responseBody = response.body();

                    if (responseBody.success) {
                        Toast.makeText(VoucherManagementActivity.this, "Tạo voucher thành công!", Toast.LENGTH_SHORT).show();
                        loadVouchers();
                    } else {
                        String errorMessage = responseBody.message != null ? responseBody.message : "Tạo voucher thất bại!";
                        Toast.makeText(VoucherManagementActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                } else {
                    String errorMsg = "Lỗi server (Code: " + response.code() + ")";
                    Toast.makeText(VoucherManagementActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResp> call, @NonNull Throwable t) {
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Toast.makeText(VoucherManagementActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateVoucher(int id, String code, String type, double value, double minOrderValue,
                              String startDate, String endDate, Integer usageLimit,
                              Integer perUserLimit, String description) {

        // Handle null values - server expects per_user_limit minimum = 1
        Integer finalUsageLimit = usageLimit != null && usageLimit > 0 ? usageLimit : null;
        Integer finalPerUserLimit = perUserLimit != null && perUserLimit > 0 ? perUserLimit : 1;

        // Handle empty description - set to empty string instead of null
        String finalDescription = description != null && !description.trim().isEmpty() ? description : "";

        // Validate required fields before API call
        if (code == null || code.trim().isEmpty()) {
            Toast.makeText(this, "Mã voucher không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (type == null || type.trim().isEmpty()) {
            Toast.makeText(this, "Loại voucher không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<BaseResp> call = apiService.updateVoucher(id, code, type, value, minOrderValue, startDate, endDate,
                finalUsageLimit, finalPerUserLimit, finalDescription);

        call.enqueue(new Callback<BaseResp>() {
            @Override
            public void onResponse(@NonNull Call<BaseResp> call, @NonNull Response<BaseResp> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BaseResp responseBody = response.body();

                    if (responseBody.success) {
                        Toast.makeText(VoucherManagementActivity.this, "Cập nhật voucher thành công!", Toast.LENGTH_SHORT).show();
                        loadVouchers();
                    } else {
                        String errorMessage = responseBody.message != null ? responseBody.message : "Cập nhật voucher thất bại!";
                        Toast.makeText(VoucherManagementActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                } else {
                    String errorMsg = "Lỗi server (Code: " + response.code() + ")";
                    Toast.makeText(VoucherManagementActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResp> call, @NonNull Throwable t) {

                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Toast.makeText(VoucherManagementActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showDeleteConfirmDialog(Voucher voucher) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa voucher \"" + voucher.code + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteVoucher(voucher))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteVoucher(Voucher voucher) {
        apiService.deleteVoucher(voucher.id).enqueue(new Callback<BaseResp>() {
            @Override
            public void onResponse(@NonNull Call<BaseResp> call, @NonNull Response<BaseResp> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    Toast.makeText(VoucherManagementActivity.this, "Xóa voucher thành công!", Toast.LENGTH_SHORT).show();
                    loadVouchers();
                } else {
                    Toast.makeText(VoucherManagementActivity.this, "Xóa voucher thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResp> call, @NonNull Throwable t) {
                Toast.makeText(VoucherManagementActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDuplicateVoucherDialog(Voucher voucher) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_voucher_form, null);

        TextInputLayout tilCode = dialogView.findViewById(R.id.tilVoucherCode);
        TextInputLayout tilValue = dialogView.findViewById(R.id.tilVoucherValue);
        TextInputLayout tilMinOrder = dialogView.findViewById(R.id.tilMinOrderValue);
        TextInputLayout tilUsageLimit = dialogView.findViewById(R.id.tilUsageLimit);
        TextInputLayout tilPerUserLimit = dialogView.findViewById(R.id.tilPerUserLimit);
        TextInputLayout tilDescription = dialogView.findViewById(R.id.tilDescription);

        EditText etCode = tilCode.getEditText();
        EditText etValue = tilValue.getEditText();
        EditText etMinOrder = tilMinOrder.getEditText();
        EditText etUsageLimit = tilUsageLimit.getEditText();
        EditText etPerUserLimit = tilPerUserLimit.getEditText();
        EditText etDescription = tilDescription.getEditText();

        Spinner spinnerType = dialogView.findViewById(R.id.spinnerVoucherType);
        MaterialButton btnStartDate = dialogView.findViewById(R.id.btnStartDate);
        MaterialButton btnEndDate = dialogView.findViewById(R.id.btnEndDate);

        // Setup spinner
        String[] types = {"percent", "amount"};  // Sửa để khớp với server
        String[] typeLabels = {"Giảm theo %", "Giảm cố định"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeLabels);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        // Date pickers
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        btnStartDate.setOnClickListener(v -> {
            DatePickerDialog picker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    startDate.set(year, month, dayOfMonth);
                    btnStartDate.setText(dateFormat.format(startDate.getTime()));
                },
                startDate.get(Calendar.YEAR),
                startDate.get(Calendar.MONTH),
                startDate.get(Calendar.DAY_OF_MONTH)
            );
            picker.show();
        });

        btnEndDate.setOnClickListener(v -> {
            DatePickerDialog picker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    endDate.set(year, month, dayOfMonth);
                    btnEndDate.setText(dateFormat.format(endDate.getTime()));
                },
                endDate.get(Calendar.YEAR),
                endDate.get(Calendar.MONTH),
                endDate.get(Calendar.DAY_OF_MONTH)
            );
            picker.show();
        });

        // Fill existing data for duplication
        if (voucher != null) {
            etCode.setText(voucher.code);
            etValue.setText(String.valueOf(voucher.discountValue)); // Sử dụng discountValue
            etMinOrder.setText(String.valueOf(voucher.minOrderAmount)); // Sử dụng minOrderAmount
            if (voucher.usageLimit > 0) { // usageLimit là int, không phải Integer
                etUsageLimit.setText(String.valueOf(voucher.usageLimit));
            }
            // perUserLimit không tồn tại trong model - bỏ qua hoặc để trống
            etDescription.setText(voucher.description != null ? voucher.description : "");

            // Set spinner selection - sử dụng discountType
            spinnerType.setSelection("percentage".equals(voucher.discountType) ? 0 : 1);

            // Chỉ có expiryDate, không có startDate/endDate
            btnStartDate.setText(dateFormat.format(startDate.getTime()));
            if (voucher.expiryDate != null) {
                btnEndDate.setText(voucher.expiryDate);
            } else {
                btnEndDate.setText(dateFormat.format(endDate.getTime()));
            }
        } else {
            btnStartDate.setText(dateFormat.format(startDate.getTime()));
            btnEndDate.setText(dateFormat.format(endDate.getTime()));
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle("Nhân bản Voucher")
                .setView(dialogView)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    if (validateVoucherForm(tilCode, tilValue, tilMinOrder)) {
                        String code = etCode.getText().toString().trim();
                        String type = types[spinnerType.getSelectedItemPosition()];
                        double value = Double.parseDouble(etValue.getText().toString().trim());
                        double minOrderValue = Double.parseDouble(etMinOrder.getText().toString().trim());
                        String startDateStr = btnStartDate.getText().toString();
                        String endDateStr = btnEndDate.getText().toString();

                        Integer usageLimit = TextUtils.isEmpty(etUsageLimit.getText()) ? null :
                            Integer.parseInt(etUsageLimit.getText().toString().trim());
                        Integer perUserLimit = TextUtils.isEmpty(etPerUserLimit.getText()) ? null :
                            Integer.parseInt(etPerUserLimit.getText().toString().trim());
                        String description = etDescription.getText().toString().trim();

                        // Handle null values - set to 0 instead of null for optional integer fields
                        Integer finalUsageLimit = usageLimit != null ? usageLimit : 0;
                        Integer finalPerUserLimit = perUserLimit != null ? perUserLimit : 0;

                        // Handle empty description - set to empty string instead of null
                        String finalDescription = description != null && !description.trim().isEmpty() ? description : "";

                        // TODO: Get actual user session data instead of hard-coded values
                        Integer createdBy = 1; // Should get from user session
                        Integer storeId = 1;   // Should get from user session

                        // Create duplicated voucher with new ID
                        apiService.createVoucher(code, type, value, minOrderValue, startDateStr, endDateStr,
                                finalUsageLimit, finalPerUserLimit, finalDescription).enqueue(new Callback<BaseResp>() {
                            @Override
                            public void onResponse(@NonNull Call<BaseResp> call, @NonNull Response<BaseResp> response) {
                                if (response.isSuccessful() && response.body() != null && response.body().success) {
                                    Toast.makeText(VoucherManagementActivity.this, "Nhân bản voucher thành công!", Toast.LENGTH_SHORT).show();
                                    loadVouchers();
                                } else {
                                    String errorMessage = response.body().message != null ? response.body().message : "Nhân bản voucher thất bại!";
                                    Toast.makeText(VoucherManagementActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<BaseResp> call, @NonNull Throwable t) {
                                Toast.makeText(VoucherManagementActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Hủy", null);

        builder.show();
    }

    private void showVoucherDetailsDialog(Voucher voucher) {
        // Sử dụng đúng field names từ Voucher model
        StringBuilder detailsText = new StringBuilder();
        detailsText.append("Mã voucher: ").append(voucher.code).append("\n")
                   .append("Loại: ").append("percentage".equals(voucher.discountType) ? "Giảm theo %" : "Giảm cố định").append("\n")
                   .append("Giá trị: ").append(voucher.discountValue).append("\n")
                   .append("Giá trị đơn hàng tối thiểu: ").append(voucher.minOrderAmount).append("\n")
                   .append("Ngày hết hạn: ").append(voucher.expiryDate != null ? voucher.expiryDate : "Không xác định").append("\n")
                   .append("Giới hạn sử dụng: ").append(voucher.usageLimit > 0 ? voucher.usageLimit : "Không giới hạn").append("\n")
                   .append("Đã sử dụng: ").append(voucher.usedCount).append("\n")
                   .append("Trạng thái: ").append(voucher.isActive ? "Đang hoạt động" : "Ngừng hoạt động").append("\n")
                   .append("Mô tả: ").append(voucher.description != null ? voucher.description : "Không có");

        new MaterialAlertDialogBuilder(this)
                .setTitle("Chi tiết Voucher")
                .setMessage(detailsText.toString())
                .setPositiveButton("Đóng", null)
                .show();
    }
}
