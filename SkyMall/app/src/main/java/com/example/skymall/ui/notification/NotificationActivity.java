package com.example.skymall.ui.notification;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skymall.R;
import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<NotificationItem> notificationList;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadNotifications();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_notifications);
        emptyView = findViewById(R.id.empty_view);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Thông báo");

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationList, this::onNotificationClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadNotifications() {
        // Thêm dữ liệu thông báo mẫu
        notificationList.clear();

        // Thông báo đơn hàng
        notificationList.add(new NotificationItem(
            "order",
            "Đơn hàng đã được xác nhận",
            "Đơn hàng #SKY2024001 của bạn đã được xác nhận và đang chuẩn bị hàng",
            "2 phút trước",
            false,
            R.drawable.ic_shopping_bag
        ));

        notificationList.add(new NotificationItem(
            "promotion",
            "Khuyến mãi đặc biệt",
            "Giảm giá 50% cho tất cả sản phẩm điện tử. Thời gian có hạn!",
            "1 giờ trước",
            true,
            R.drawable.ic_discount
        ));

        notificationList.add(new NotificationItem(
            "shipping",
            "Đơn hàng đang giao",
            "Đơn hàng #SKY2024002 đang trên đường giao đến bạn",
            "3 giờ trước",
            false,
            R.drawable.ic_delivery_truck
        ));

        notificationList.add(new NotificationItem(
            "system",
            "Cập nhật ứng dụng",
            "Phiên bản mới của SkyMall đã có sẵn với nhiều tính năng thú vị",
            "1 ngày trước",
            true,
            R.drawable.ic_system_update
        ));

        notificationList.add(new NotificationItem(
            "order",
            "Đơn hàng đã giao thành công",
            "Đơn hàng #SKY2024003 đã được giao thành công. Cảm ơn bạn đã mua sắm!",
            "2 ngày trước",
            true,
            R.drawable.ic_check_circle
        ));

        // Hiển thị empty view nếu không có thông báo
        if (notificationList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        adapter.notifyDataSetChanged();
    }

    private void onNotificationClick(NotificationItem notification, int position) {
        // Đánh dấu đã đọc
        if (!notification.isRead()) {
            notification.setRead(true);
            adapter.notifyItemChanged(position);
        }

        // Xử lý action tương ứng với loại thông báo
        switch (notification.getType()) {
            case "order":
            case "shipping":
                // Mở chi tiết đơn hàng
                openOrderDetail(notification);
                break;
            case "promotion":
                // Mở trang khuyến mãi
                openPromotionPage(notification);
                break;
            case "system":
                // Mở cài đặt hoặc store
                openSystemSettings(notification);
                break;
        }
    }

    private void openOrderDetail(NotificationItem notification) {
        // TODO: Implement navigation to order detail
        // Intent intent = new Intent(this, OrderDetailActivity.class);
        // startActivity(intent);
    }

    private void openPromotionPage(NotificationItem notification) {
        // TODO: Implement navigation to promotion page
        // Intent intent = new Intent(this, PromotionActivity.class);
        // startActivity(intent);
    }

    private void openSystemSettings(NotificationItem notification) {
        // TODO: Implement navigation to settings
        // Intent intent = new Intent(this, SettingsActivity.class);
        // startActivity(intent);
    }
}
