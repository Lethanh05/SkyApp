package com.example.skymall.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.*; import android.widget.*;
import androidx.annotation.*; import androidx.fragment.app.Fragment; import androidx.recyclerview.widget.*;
import com.example.skymall.R;
import com.example.skymall.data.remote.ApiService;
import com.example.skymall.ui.profile.*;

import java.util.*;

public class ProfileFragment extends Fragment {
    private ImageView imgAvatar; private TextView tvName, tvPhone;
    private View qaOrders, qaVoucher, qaWallet;
    private RecyclerView rv; private ProfileAdapter adapter; private final ArrayList<ProfileItem> items=new ArrayList<>();

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState){
        View v=inflater.inflate(R.layout.fragment_profile,container,false);

        imgAvatar=v.findViewById(R.id.imgAvatar);
        tvName=v.findViewById(R.id.tvName);
        tvPhone=v.findViewById(R.id.tvPhone);
        v.findViewById(R.id.btnEditProfile).setOnClickListener(b->openEditProfile());

        qaOrders=v.findViewById(R.id.qaOrders);
        qaVoucher=v.findViewById(R.id.qaVoucher);
        qaWallet=v.findViewById(R.id.qaWallet);

        qaOrders.setOnClickListener(v1 -> openOrders());
        qaVoucher.setOnClickListener(v1 -> openVouchers());
        qaWallet.setOnClickListener(v1 -> openWallet());

        rv=v.findViewById(R.id.rvProfile);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new ProfileAdapter(items, this::onMenuClick);
        rv.setAdapter(adapter);

        bindUser();   // load tên/sđt/avatar
        buildMenu();  // tạo list mục như Shopee
        return v;
    }

    private void bindUser(){
        // TODO: lấy từ session/local db/API
        tvName.setText("Lê Quang Thạnh");
        tvPhone.setText("098xxx123");

    }

    private void buildMenu(){
        items.clear();
        items.add(ProfileItem.section("Tài khoản"));
        items.add(ProfileItem.row("Địa chỉ", R.drawable.ic_location, null, "ADDRESS"));
        items.add(ProfileItem.row("Phương thức thanh toán", R.drawable.ic_payment, null, "PAYMENT"));
        items.add(ProfileItem.row("Thông báo", R.drawable.ic_bell, "2", "NOTIFICATION_SETTINGS"));
        items.add(ProfileItem.row("Bảo mật & Đổi mật khẩu", R.drawable.ic_lock, null, "SECURITY"));

        items.add(ProfileItem.section("Hỗ trợ"));
        items.add(ProfileItem.row("Trung tâm trợ giúp", R.drawable.ic_help, null, "HELP_CENTER"));
        items.add(ProfileItem.row("Đăng xuất", R.drawable.ic_logout, null, "LOGOUT"));

        adapter.notifyDataSetChanged();
    }

    private void onMenuClick(ProfileItem it, int pos){
        switch (it.action){
            case "ORDER_PENDING": openOrdersTab(0); break;
            case "ORDER_SHIPPING": openOrdersTab(1); break;
            case "ORDER_REVIEW": openOrdersTab(2); break;
            case "ADDRESS": openAddress(); break;
            case "PAYMENT": openPayment(); break;
            case "NOTIFICATION_SETTINGS": openNotificationSettings(); break;
            case "SECURITY": openSecurity(); break;
            case "HELP_CENTER": openHelp(); break;
            case "LOGOUT": confirmLogout(); break;
        }
    }

    // ====== Điều hướng giả lập (TODO: thay bằng Activity thật) ======
    private void openEditProfile(){ toast("Mở trang Sửa hồ sơ"); }
    private void openOrders(){ toast("Mở Đơn mua"); }
    private void openOrdersTab(int tab){ toast("Mở Đơn mua - tab "+tab); }
    private void openVouchers(){ toast("Mở Voucher"); }
    private void openWallet(){ toast("Mở Ví"); }
    private void openAddress(){ toast("Mở Sổ địa chỉ"); }
    private void openPayment(){ toast("Mở Phương thức thanh toán"); }
    private void openNotificationSettings(){ toast("Mở Cài đặt thông báo"); }
    private void openSecurity(){ toast("Mở Bảo mật & Đổi mật khẩu"); }
    private void openHelp(){ toast("Mở Trung tâm trợ giúp"); }

    private void confirmLogout(){
        new AlertDialog.Builder(getContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (d,w)-> {
                    doLogout();
                })
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


    private void toast(String s){ Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show(); }
}
