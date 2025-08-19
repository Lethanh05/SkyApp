// app/src/main/java/com/example/skymall/ui/NotificationsFragment.java
package com.example.skymall.ui;

import android.os.Bundle; import android.view.*; import android.widget.TextView;
import androidx.annotation.*; import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*; import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.skymall.R; import com.example.skymall.data.model.Notification;
import java.util.*; import java.util.concurrent.ThreadLocalRandom;

public class NotificationsFragment extends Fragment {
    private final ArrayList<Notification> items = new ArrayList<>();
    private NotificationsAdapter adapter; private TextView tvEmpty; private SwipeRefreshLayout swipe;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notifications, container, false);
        RecyclerView rv = v.findViewById(R.id.rvNotifications);
        tvEmpty = v.findViewById(R.id.tvEmpty);
        swipe = v.findViewById(R.id.swipe);

        adapter = new NotificationsAdapter(items, (item, pos) -> {
            // Đánh dấu đã đọc khi click
            if (!item.read) { item.read = true; adapter.notifyItemChanged(pos); }
            // TODO: mở màn hình chi tiết nếu cần
        });
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        enableSwipeToDelete(rv);
        swipe.setOnRefreshListener(this::reload);
        reload(); // tải dữ liệu lần đầu
        return v;
    }

    private void reload() {
        // TODO: thay bằng call API của bạn sau
        swipe.setRefreshing(true);
        items.clear();
        items.addAll(mockData()); // dữ liệu mẫu
        adapter.notifyDataSetChanged();
        tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        swipe.setRefreshing(false);
    }

    private List<Notification> mockData() {
        long now = System.currentTimeMillis();
        return Arrays.asList(
                new Notification("1","Đơn hàng #A123","Đặt hàng thành công!", now - 3_600_000, false),
                new Notification("2","Khuyến mãi 50%","Chỉ hôm nay, deal sốc!", now - 7_200_000, true),
                new Notification("3","Cập nhật vận chuyển","Đơn #A123 đã bàn giao cho đơn vị vận chuyển.", now - 18_000_000, false)
        );
    }

    private void enableSwipeToDelete(RecyclerView rv) {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override public boolean onMove(@NonNull RecyclerView r,@NonNull RecyclerView.ViewHolder a,@NonNull RecyclerView.ViewHolder t){ return false; }
            @Override public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int dir) {
                int pos = vh.getAdapterPosition();
                items.remove(pos);
                adapter.notifyItemRemoved(pos);
                tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
                // TODO: gọi API xoá notification nếu cần
            }
        }).attachToRecyclerView(rv);
    }
}
