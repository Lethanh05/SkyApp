package com.example.skymall.ui.profile;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.bumptech.glide.Glide;
import com.example.skymall.R;
import com.example.skymall.data.remote.ApiService;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView btnBack, btnChangeAvatar;
    private ShapeableImageView imgAvatar;
    private TextView btnSave;
    private EditText etFullName, etPhone, etEmail, etBirthDate;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale, rbOther;

    private ApiService api;

    // NEW: Cho chức năng cập nhật avatar
    private Uri currentPhotoUri;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        api = com.example.skymall.data.remote.ApiClient.create(this);

        initViews();
        initImagePickers(); // NEW
        setupListeners();
        loadCurrentData();
    }

    // NEW: Khởi tạo các launcher cho chọn ảnh
    private void initImagePickers() {
        galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        uploadAvatar(imageUri);
                    }
                }
            }
        );

        cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (currentPhotoUri != null) {
                        uploadAvatar(currentPhotoUri);
                    }
                }
            }
        );
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);
        imgAvatar = findViewById(R.id.imgAvatar);
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar);
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etBirthDate = findViewById(R.id.etBirthDate);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        rbOther = findViewById(R.id.rbOther);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        btnSave.setOnClickListener(v -> saveProfile());

        btnChangeAvatar.setOnClickListener(v -> changeAvatar());

        etBirthDate.setOnClickListener(v -> showDatePicker());
    }

    private void loadCurrentData() {
        // Load dữ liệu hiện tại từ API me()
        api.me().enqueue(new retrofit2.Callback<com.example.skymall.data.remote.DTO.MeResp>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.skymall.data.remote.DTO.MeResp> call,
                                 retrofit2.Response<com.example.skymall.data.remote.DTO.MeResp> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success && response.body().user != null) {
                    com.example.skymall.data.remote.DTO.MeResp.User user = response.body().user;

                    // Load thông tin vào form
                    etFullName.setText(user.name != null ? user.name : "");
                    etPhone.setText(user.phone != null ? user.phone : "");
                    etEmail.setText(user.email != null ? user.email : "");

                    // Xử lý birthDate
                    if (user.birthDate != null && !user.birthDate.isEmpty()) {
                        String displayDate = convertToDisplayFormat(user.birthDate);
                        etBirthDate.setText(displayDate);
                        etBirthDate.setTag(user.birthDate);
                    } else {
                        etBirthDate.setText("");
                        etBirthDate.setTag(null);
                    }

                    // Load avatar
                    String avatarUrl = null;
                    if (user.avatar_url != null && !user.avatar_url.isEmpty()) {
                        avatarUrl = user.avatar_url;
                    } else if (user.avt != null && !user.avt.isEmpty()) {
                        avatarUrl = "https://lequangthanh.click/" + user.avt;
                    }

                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Glide.with(EditProfileActivity.this)
                            .load(avatarUrl)
                            .placeholder(R.drawable.ic_avatar)
                            .error(R.drawable.ic_avatar)
                            .into(imgAvatar);
                    } else {
                        imgAvatar.setImageResource(R.drawable.ic_avatar);
                    }

                    // Set giới tính mặc định
                    rbMale.setChecked(true);
                } else {
                    Toast.makeText(EditProfileActivity.this, "Không thể tải thông tin cá nhân", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.skymall.data.remote.DTO.MeResp> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        // Lấy birthDate - ưu tiên từ tag (đã load từ server)
        String birthDate = null;
        if (etBirthDate.getTag() != null) {
            birthDate = etBirthDate.getTag().toString();
        } else if (!etBirthDate.getText().toString().trim().isEmpty()) {
            String displayDate = etBirthDate.getText().toString().trim();
            birthDate = convertDateFormat(displayDate);
        }

        // Validate dữ liệu
        if (fullName.isEmpty()) {
            etFullName.setError("Vui lòng nhập họ và tên");
            etFullName.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            etPhone.setError("Vui lòng nhập số điện thoại");
            etPhone.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Vui lòng nhập email");
            etEmail.requestFocus();
            return;
        }

        if (!isValidEmail(email)) {
            etEmail.setError("Định dạng email không hợp lệ");
            etEmail.requestFocus();
            return;
        }

        // Validate birthDate chỉ khi có giá trị
        if (birthDate != null && !birthDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            etBirthDate.setError("Định dạng ngày sinh không hợp lệ");
            etBirthDate.requestFocus();
            return;
        }

        String gender = getSelectedGender();


        // Gọi API để cập nhật thông tin
        updateProfile(fullName, phone, email, birthDate, gender);
    }

    private String getSelectedGender() {
        int selectedId = rgGender.getCheckedRadioButtonId();
        if (selectedId == R.id.rbMale) return "Nam";
        if (selectedId == R.id.rbFemale) return "Nữ";
        if (selectedId == R.id.rbOther) return "Khác";
        return "";
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void updateProfile(String fullName, String phone, String email, String birthDate, String gender) {
        // Hiển thị loading
        btnSave.setEnabled(false);
        btnSave.setText("Đang lưu...");

        // Gọi API để cập nhật thông tin
        api.updateProfile(fullName, phone, email, birthDate, gender).enqueue(new retrofit2.Callback<com.example.skymall.data.remote.DTO.MeResp>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.skymall.data.remote.DTO.MeResp> call,
                                 retrofit2.Response<com.example.skymall.data.remote.DTO.MeResp> response) {
                btnSave.setEnabled(true);
                btnSave.setText("Lưu");

                if (response.isSuccessful() && response.body() != null) {
                    com.example.skymall.data.remote.DTO.MeResp resp = response.body();
                    if (resp.success) {

                        // Trả kết quả về cho ProfileFragment
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("fullName", fullName);
                        resultIntent.putExtra("phone", phone);
                        resultIntent.putExtra("email", email);
                        resultIntent.putExtra("birthDate", birthDate);
                        resultIntent.putExtra("gender", gender);
                        setResult(RESULT_OK, resultIntent);

                        finish();
                    } else {
                        Toast.makeText(EditProfileActivity.this, " Không thể cập nhật thông tin", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, " Lỗi kết nối với máy chủ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.skymall.data.remote.DTO.MeResp> call, Throwable t) {
                btnSave.setEnabled(true);
                btnSave.setText("Lưu");
                Toast.makeText(EditProfileActivity.this, " Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeAvatar() {
        String[] options = {"Chọn từ thư viện", "Chụp ảnh mới", "Hủy"};

        new AlertDialog.Builder(this)
            .setTitle("Cập nhật ảnh đại diện")
            .setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0: // Chọn từ thư viện
                        openGallery();
                        break;
                    case 1: // Chụp ảnh mới
                        openCamera();
                        break;
                    case 2: // Hủy
                        dialog.dismiss();
                        break;
                }
            })
            .show();
    }

    // NEW: Mở gallery để chọn ảnh
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        galleryLauncher.launch(intent);
    }

    // NEW: Mở camera để chụp ảnh
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                currentPhotoUri = FileProvider.getUriForFile(
                    this,
                    "com.example.skymall.fileprovider",
                    photoFile
                );
                intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri);
                cameraLauncher.launch(intent);
            }
        }
    }

    // NEW: Tạo file để lưu ảnh từ camera
    private File createImageFile() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir("Pictures");
            return File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException ex) {
            Toast.makeText(this, "Không thể tạo file ảnh", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    // NEW: Upload avatar lên server (sửa lại để chỉ upload avatar trước)
    private void uploadAvatar(Uri imageUri) {
        Toast.makeText(this, " Đang tải ảnh lên...", Toast.LENGTH_SHORT).show();

        try {
            if (imageUri == null) {
                Toast.makeText(this, " không đúng định dạng ảnh", Toast.LENGTH_SHORT).show();
                return;
            }

            java.io.InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                Toast.makeText(this, " lỗi vui lòng thử lại", Toast.LENGTH_SHORT).show();
                return;
            }

            // Đọc dữ liệu ảnh
            java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            inputStream.close();

            byte[] bytes = buffer.toByteArray();
            buffer.close();

            if (bytes.length <= 0) {
                Toast.makeText(this, " chưa có ảnh", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra kích thước file (tối đa 5MB)
            if (bytes.length > 5 * 1024 * 1024) {
                Toast.makeText(this, " Ảnh quá lớn (tối đa 5MB)", Toast.LENGTH_SHORT).show();
                return;
            }

            // Xác định loại file
            String mimeType = getContentResolver().getType(imageUri);
            if (mimeType == null) {
                mimeType = "image/jpeg";
            }

            // Kiểm tra định dạng ảnh được hỗ trợ
            String[] allowedTypes = {"image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"};
            boolean isValidType = false;
            for (String allowedType : allowedTypes) {
                if (mimeType.equals(allowedType)) {
                    isValidType = true;
                    break;
                }
            }

            if (!isValidType) {
                Toast.makeText(this, " Định dạng ảnh không được hỗ trợ", Toast.LENGTH_SHORT).show();
                return;
            }

            RequestBody requestFile = RequestBody.create(bytes, MediaType.parse(mimeType));

            // Tạo tên file
            String fileName = "avatar_" + System.currentTimeMillis();
            if (mimeType.contains("png")) {
                fileName += ".png";
            } else if (mimeType.contains("gif")) {
                fileName += ".gif";
            } else if (mimeType.contains("webp")) {
                fileName += ".webp";
            } else {
                fileName += ".jpg";
            }

            MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", fileName, requestFile);

            // Gọi API upload avatar
            api.uploadAvatar(body).enqueue(new Callback<com.example.skymall.data.remote.DTO.UploadResp>() {
                @Override
                public void onResponse(Call<com.example.skymall.data.remote.DTO.UploadResp> call,
                                     Response<com.example.skymall.data.remote.DTO.UploadResp> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        com.example.skymall.data.remote.DTO.UploadResp uploadResp = response.body();

                        if (uploadResp.success) {
                            Toast.makeText(EditProfileActivity.this, " Cập nhật ảnh đại diện thành công!", Toast.LENGTH_SHORT).show();

                            // Lấy avatar URL từ response
                            String avatarUrl = null;
                            if (uploadResp.user != null && uploadResp.user.avatar_url != null) {
                                avatarUrl = uploadResp.user.avatar_url;
                            } else if (uploadResp.avatar_url != null) {
                                avatarUrl = uploadResp.avatar_url;
                            }

                            // Cập nhật avatar trên UI
                            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                Glide.with(EditProfileActivity.this)
                                    .load(avatarUrl)
                                    .placeholder(R.drawable.ic_avatar)
                                    .error(R.drawable.ic_avatar)
                                    .into(imgAvatar);
                            } else {
                                Glide.with(EditProfileActivity.this)
                                    .load(imageUri)
                                    .placeholder(R.drawable.ic_avatar)
                                    .error(R.drawable.ic_avatar)
                                    .into(imgAvatar);
                            }

                            // Reload data để đồng bộ
                            loadCurrentData();
                        } else {
                            String errorMsg = " Không thể cập nhật ảnh đại diện";
                            if (uploadResp.error != null) {
                                errorMsg += ": " + uploadResp.error;
                            }
                            Toast.makeText(EditProfileActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EditProfileActivity.this, " Lỗi kết nối với máy chủ", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<com.example.skymall.data.remote.DTO.UploadResp> call, Throwable t) {
                    Toast.makeText(EditProfileActivity.this, " Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, " Lỗi khi xử lý ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, selectedYear, selectedMonth, selectedDay) -> {
                // Format cho hiển thị UI: DD/MM/YYYY (user-friendly)
                String displayDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                etBirthDate.setText(displayDate);

                // Lưu format ISO cho API: YYYY-MM-DD (backend expect)
                String isoDate = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                etBirthDate.setTag(isoDate); // Lưu vào tag để dùng khi gọi API
            },
            year, month, day
        );

        datePickerDialog.show();
    }

    private String convertDateFormat(String ddmmyyyy) {
        try {
            if (ddmmyyyy.matches("\\d{2}/\\d{2}/\\d{4}")) {
                String[] parts = ddmmyyyy.split("/");
                return parts[2] + "-" + parts[1] + "-" + parts[0]; // YYYY-MM-DD
            }
        } catch (Exception e) {
            // Ignore conversion error, return original
        }
        return ddmmyyyy;
    }

    private String convertToDisplayFormat(String yyyymmdd) {
        try {
            if (yyyymmdd.matches("\\d{4}-\\d{2}-\\d{2}")) {
                String[] parts = yyyymmdd.split("-");
                return parts[2] + "/" + parts[1] + "/" + parts[0]; // DD/MM/YYYY
            }
        } catch (Exception e) {
            // Ignore conversion error, return original
        }
        return yyyymmdd;
    }
}
