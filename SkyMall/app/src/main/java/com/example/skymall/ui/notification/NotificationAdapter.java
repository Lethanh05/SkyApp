package com.example.skymall.ui.notification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skymall.R;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<NotificationItem> notifications;
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationItem notification, int position);
    }

    public NotificationAdapter(List<NotificationItem> notifications, OnNotificationClickListener listener) {
        this.notifications = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem notification = notifications.get(position);
        holder.bind(notification, position);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        private ImageView iconView;
        private TextView titleView;
        private TextView messageView;
        private TextView timeView;
        private View unreadIndicator;
        private View itemContainer;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.notification_icon);
            titleView = itemView.findViewById(R.id.notification_title);
            messageView = itemView.findViewById(R.id.notification_message);
            timeView = itemView.findViewById(R.id.notification_time);
            unreadIndicator = itemView.findViewById(R.id.unread_indicator);
            itemContainer = itemView.findViewById(R.id.notification_container);
        }

        public void bind(NotificationItem notification, int position) {
            iconView.setImageResource(notification.getIconRes());
            titleView.setText(notification.getTitle());
            messageView.setText(notification.getMessage());
            timeView.setText(notification.getTimeAgo());

            // Hiển thị indicator chưa đọc
            unreadIndicator.setVisibility(notification.isRead() ? View.GONE : View.VISIBLE);

            // Thay đổi background cho thông báo chưa đọc
            if (notification.isRead()) {
                itemContainer.setBackgroundResource(R.drawable.notification_read_background);
            } else {
                itemContainer.setBackgroundResource(R.drawable.notification_unread_background);
            }

            // Set click listener
            itemContainer.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNotificationClick(notification, position);
                }
            });
        }
    }
}
