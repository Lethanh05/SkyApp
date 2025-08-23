package com.example.skymall.ui;

import android.view.*; import android.widget.*;
import androidx.annotation.NonNull; import androidx.recyclerview.widget.RecyclerView;
import com.example.skymall.R; import com.example.skymall.data.model.Notification;
import java.text.SimpleDateFormat; import java.util.*;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.VH> {
    public interface OnItemClick { void onClick(Notification item, int pos); }
    private final List<Notification> data; private final OnItemClick onItemClick;

    public NotificationsAdapter(List<Notification> data, OnItemClick onItemClick) {
        this.data = data; this.onItemClick = onItemClick;
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        Notification it = data.get(pos);
        h.tvTitle.setText(it.title);
        h.tvMessage.setText(it.message);
        h.dotUnread.setVisibility(it.read ? View.INVISIBLE : View.VISIBLE);
        h.tvTime.setText(new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(new Date(it.createdAt)));
        h.itemView.setAlpha(it.read ? 0.6f : 1f);
        h.itemView.setOnClickListener(v -> onItemClick.onClick(it, pos));
    }

    @Override public int getItemCount() { return data.size(); }

    public static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvTime; View dotUnread;
        public VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.notification_title);
            tvMessage = itemView.findViewById(R.id.notification_message);
            tvTime = itemView.findViewById(R.id.notification_time);
            dotUnread = itemView.findViewById(R.id.unread_indicator);
        }
    }
}
