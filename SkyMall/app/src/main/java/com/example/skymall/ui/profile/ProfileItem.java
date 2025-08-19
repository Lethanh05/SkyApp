package com.example.skymall.ui.profile;

public class ProfileItem {
    public static final int SECTION = 0;
    public static final int ROW = 1;

    public int type;
    public String title;
    public int iconRes;   // chỉ với ROW
    public String badge;  // null = không hiển thị
    public String action; // key điều hướng

    public static ProfileItem section(String title) {
        ProfileItem i = new ProfileItem(); i.type=SECTION; i.title=title; return i;
    }
    public static ProfileItem row(String title, int iconRes, String badge, String action) {
        ProfileItem i = new ProfileItem(); i.type=ROW; i.title=title; i.iconRes=iconRes; i.badge=badge; i.action=action; return i;
    }
}
