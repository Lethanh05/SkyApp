package com.example.skymall.ui.notification;

public class NotificationItem {
    private String type;
    private String title;
    private String message;
    private String timeAgo;
    private boolean isRead;
    private int iconRes;

    public NotificationItem(String type, String title, String message, String timeAgo, boolean isRead, int iconRes) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.timeAgo = timeAgo;
        this.isRead = isRead;
        this.iconRes = iconRes;
    }

    // Getters
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getTimeAgo() { return timeAgo; }
    public boolean isRead() { return isRead; }
    public int getIconRes() { return iconRes; }

    // Setters
    public void setType(String type) { this.type = type; }
    public void setTitle(String title) { this.title = title; }
    public void setMessage(String message) { this.message = message; }
    public void setTimeAgo(String timeAgo) { this.timeAgo = timeAgo; }
    public void setRead(boolean read) { isRead = read; }
    public void setIconRes(int iconRes) { this.iconRes = iconRes; }
}
