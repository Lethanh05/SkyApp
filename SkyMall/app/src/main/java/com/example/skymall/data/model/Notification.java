package com.example.skymall.data.model;

public class Notification {
    public String id;
    public String title;
    public String message;
    public long createdAt;
    public boolean read;

    public Notification(String id, String title, String message, long createdAt, boolean read) {
        this.id = id; this.title = title; this.message = message; this.createdAt = createdAt; this.read = read;
    }
}
