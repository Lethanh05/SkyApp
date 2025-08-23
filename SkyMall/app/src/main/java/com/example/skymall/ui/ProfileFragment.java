package com.example.skymall.ui;

import android.app.AlertDialog;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*; import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.example.skymall.R;
import com.example.skymall.data.remote.ApiService;
import com.example.skymall.ui.profile.*;
import com.example.skymall.ui.orders.OrdersFragment;
import com.example.skymall.auth.SessionManager;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private static final int EDIT_PROFILE_REQUEST = 1001;
    private ImageView imgAvatar; private TextView tvName, tvPhone;
    private View qaOrders, qaVoucher, qaWallet;
    private RecyclerView rv; private ProfileAdapter adapter; private final ArrayList<ProfileItem> items=new ArrayList<>();

    // NEW: ApiService
    private ApiService api;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState){
        View v=inflater.inflate(R.layout.fragment_profile,container,false);

        rv=v.findViewById(R.id.rvProfile);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new ProfileAdapter(items, this::onMenuClick);
        rv.setAdapter(adapter);

        // NEW: khởi tạo API (baseUrl đã cấu hình trong ApiClient)
        api = com.example.skymall.data.remote.ApiClient.create(requireContext());

        buildMenu();  // tạo list mục như Shopee

        // NEW: tải thông tin user từ server để hiển thị header
        loadMe();

        return v;
    }

    private void buildMenu(){
        items.clear();

        // Lấy thông tin role của user hiện tại
        String userRole = SessionManager.role(getContext());
        String roleDisplayName = com.example.skymall.utils.UserRole.getRoleDisplayName(userRole);

        // Thêm header với avatar và thông tin user (cần thiết cho quick actions)
        items.add(ProfileItem.header("Người dùng", "098xxx123"));

        // Thêm quick actions (chứa Ví, Voucher, Đơn hàng)
        items.add(ProfileItem.quickActions());

        // Thêm order tracking
        items.add(ProfileItem.orderTracking());

        items.add(ProfileItem.section("Tài khoản"));
        items.add(ProfileItem.row("Địa chỉ", R.drawable.ic_location, null, "ADDRESS"));
        items.add(ProfileItem.row("Thông báo", R.drawable.ic_bell, "2", "NOTIFICATION_SETTINGS"));
        items.add(ProfileItem.row("Bảo mật & Đổi mật khẩu", R.drawable.ic_lock, null, "SECURITY"));

        // Thêm menu đặc biệt cho store owner và admin
        if (com.example.skymall.utils.UserRole.canManageStore(userRole)) {
            items.add(ProfileItem.section("Quản lý"));
            items.add(ProfileItem.row("Quản lý cửa hàng", R.drawable.ic_store, null, "MANAGE_STORE"));
//            items.add(ProfileItem.row("Sản phẩm của tôi", R.drawable.ic_product, null, "MY_PRODUCTS"));
        }

        // Thêm menu admin
        if (com.example.skymall.utils.UserRole.hasAdminPermission(userRole)) {
            items.add(ProfileItem.section("Quản trị hệ thống"));
//            items.add(ProfileItem.row("Quản lý người dùng", R.drawable.ic_users, null, "MANAGE_USERS"));
//            items.add(ProfileItem.row("Thống kê hệ thống", R.drawable.ic_analytics, null, "SYSTEM_ANALYTICS"));
        }

        items.add(ProfileItem.section("Hỗ trợ"));
        items.add(ProfileItem.row("Trung tâm trợ giúp", R.drawable.ic_help, null, "HELP_CENTER"));
        items.add(ProfileItem.row("Đăng xuất", R.drawable.ic_logout, null, "LOGOUT"));

        adapter.notifyDataSetChanged();
    }

    private void onMenuClick(ProfileItem it, int pos){
        switch (it.action){
            case "EDIT_PROFILE": openEditProfile(); break;
            case "ORDERS": openOrders(); break;
            case "VOUCHER": openVouchers(); break;
            case "WALLET": openWallet(); break;
            case "ORDER_PENDING": openOrdersTab(1); break;
            case "ORDER_PACKING": openOrdersTab(2); break;
            case "ORDER_SHIPPING": openOrdersTab(3); break;
            case "ORDER_DELIVERED": openOrdersTab(4); break;
            case "ORDER_REVIEW": openOrdersTab(4); break;  
            case "ADDRESS": openAddress(); break;
            case "NOTIFICATION_SETTINGS": openNotificationSettings(); break;
            case "SECURITY": openSecurity(); break;
            case "HELP_CENTER": openHelp(); break;
            case "LOGOUT": confirmLogout(); break;
            // Thêm handler cho store owner
            case "MANAGE_STORE": openManageStore(); break;
            case "MY_PRODUCTS": openMyProducts(); break;
            // Thêm handler cho admin
            case "MANAGE_USERS": openManageUsers(); break;
            case "SYSTEM_ANALYTICS": openSystemAnalytics(); break;
        }
    }

    // ====== Điều hướng giả lập (TODO: thay bằng Activity thật) ======
    private void openEditProfile(){
        Intent intent = new Intent(getContext(), EditProfileActivity.class);
        startActivityForResult(intent, EDIT_PROFILE_REQUEST);
    }
    private void openOrders(){
        // Navigate to OrdersFragment
        Fragment ordersFragment = new OrdersFragment();
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, ordersFragment)
                .addToBackStack(null)
                .commit();
    }
    private void openOrdersTab(int tab){
        // Navigate to OrdersFragment with specific tab
        Fragment ordersFragment = new OrdersFragment();
        Bundle args = new Bundle();
        args.putInt("tab", tab);
        ordersFragment.setArguments(args);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, ordersFragment)
                .addToBackStack(null)
                .commit();
    }
    private void openVouchers(){
        try {
            // Navigate to VoucherListFragment (file thực sự tồn tại)
            Fragment vouchersFragment = new com.example.skymall.ui.voucher.VoucherListFragment();
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, vouchersFragment)
                        .addToBackStack("vouchers")
                        .commit();
            }
        } catch (Exception e) {
            toast("Không thể mở voucher. Vui lòng thử lại!");
            e.printStackTrace();
        }
    }
    private void openWallet(){ toast("Mở Ví"); }
    private void openAddress(){
        try {
            // Navigate to AddressListFragment
            Fragment addressFragment = new com.example.skymall.ui.address.AddressListFragment();
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, addressFragment)
                        .addToBackStack("address")
                        .commit();
            }
        } catch (Exception e) {
            toast("Không thể mở sổ địa chỉ. Vui lòng thử lại!");
            e.printStackTrace();
        }
    }
    private void openNotificationSettings(){
        Intent intent = new Intent(getContext(), com.example.skymall.ui.notification.NotificationActivity.class);
        startActivity(intent);
    }
    private void openSecurity(){
        Intent intent = new Intent(getContext(), com.example.skymall.ui.security.ChangePasswordActivity.class);
        startActivity(intent);
    }
    private void openHelp(){ toast("Mở Trung tâm trợ giúp"); }

    // ====== Menu cho Store Owner ======
    private void openManageStore(){ toast("Mở Quản lý cửa hàng"); }
    private void openMyProducts(){ toast("Mở Sản phẩm của tôi"); }

    // ====== Menu cho Admin ======
    private void openManageUsers(){ toast("Mở Quản lý người dùng"); }
    private void openSystemAnalytics(){ toast("Mở Thống kê hệ thống"); }

    private void confirmLogout(){
        new AlertDialog.Builder(getContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (d,w)-> doLogout())
                .setNegativeButton("Huỷ", null)
                .show();
    }
    private void doLogout(){
        ApiService api = com.example.skymall.data.remote.ApiClient.create(requireContext());
        api.logout().enqueue(new retrofit2.Callback<com.example.skymall.data.remote.DTO.MeResp>() {
            @Override public void onResponse(retrofit2.Call<com.example.skymall.data.remote.DTO.MeResp> c,
                                             retrofit2.Response<com.example.skymall.data.remote.DTO.MeResp> r) {
                com.example.skymall.auth.SessionManager.clear(requireContext());
                startActivity(new android.content.Intent(requireContext(), com.example.skymall.auth.LoginActivity.class));
                requireActivity().finish();
            }
            @Override public void onFailure(retrofit2.Call<com.example.skymall.data.remote.DTO.MeResp> c, Throwable t) {
                com.example.skymall.auth.SessionManager.clear(requireContext());
                startActivity(new android.content.Intent(requireContext(), com.example.skymall.auth.LoginActivity.class));
                requireActivity().finish();
            }
        });
    }

    // NEW: gọi me() để đổ dữ liệu thật vào header
    private void loadMe() {
        api.me().enqueue(new Callback<com.example.skymall.data.remote.DTO.MeResp>() {
            @Override public void onResponse(Call<com.example.skymall.data.remote.DTO.MeResp> call,
                                             Response<com.example.skymall.data.remote.DTO.MeResp> res) {
                if (!isAdded()) return;
                if (res.isSuccessful() && res.body() != null && res.body().success) {
                    com.example.skymall.data.remote.DTO.MeResp body = res.body();

                    // Sử dụng name thay vì fullName
                    String name  = body.user.name != null && !body.user.name.isEmpty()
                            ? body.user.name
                            : body.user.email; // fallback sang email nếu name rỗng
                    String phone = body.user.phone != null ? body.user.phone : "";

                    // Ưu tiên avatar_url từ server (full URL), fallback sang avt (relative path)
                    String avatarUrl = null;
                    if (body.user.avatar_url != null && !body.user.avatar_url.isEmpty()) {
                        avatarUrl = body.user.avatar_url;
                    } else if (body.user.avt != null && !body.user.avt.isEmpty()) {
                        avatarUrl = "https://lequangthanh.click/" + body.user.avt;
                    }

                    // Cập nhật header với thông tin thật bao gồm avatar
                    items.set(0, ProfileItem.header(name, phone, avatarUrl));
                    adapter.notifyItemChanged(0);
                }
            }
            @Override public void onFailure(Call<com.example.skymall.data.remote.DTO.MeResp> call, Throwable t) {
                // Xử lý lỗi kết nối im lặng
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_PROFILE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            // Update UI ngay theo dữ liệu trả về
            String fullName = data.getStringExtra("fullName");
            String phone = data.getStringExtra("phone");
            updateProfileDisplay(fullName, phone);
            toast("Hồ sơ đã được cập nhật thành công!");

            // Reload toàn bộ data từ server để đồng bộ avatar và thông tin khác
            loadMe();
        } else if (requestCode == EDIT_PROFILE_REQUEST) {
            // Ngay cả khi không có data trả về, vẫn reload để đồng bộ avatar
            // (trường hợp user chỉ upload avatar mà không save profile)
            loadMe();
        }
    }

    private void updateProfileDisplay(String fullName, String phone) {
        for (int i = 0; i < items.size(); i++) {
            ProfileItem item = items.get(i);
            if (item.type == ProfileItem.HEADER) {
                // Giữ nguyên avatarUrl hiện tại khi cập nhật profile
                items.set(i, ProfileItem.header(fullName, phone, item.avatarUrl));
                adapter.notifyItemChanged(i);
                break;
            }
        }
    }

    private void toast(String s){ Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show(); }
}
