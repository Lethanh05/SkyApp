package com.example.skymall.ui.profile;

public class ProfileItem {
    public static final int SECTION = 0;
    public static final int ROW = 1;
    public static final int HEADER = 2;
    public static final int QUICK_ACTIONS = 3;
    public static final int ORDER_TRACKING = 4;

    public int type;
    public String title;
    public String subtitle; // for header phone number
    public String avatarUrl; // for header avatar
    public int iconRes;
    public String badge;
    public String action;

    public static ProfileItem section(String title) {
        ProfileItem i = new ProfileItem(); i.type=SECTION; i.title=title; return i;
    }
    public static ProfileItem row(String title, int iconRes, String badge, String action) {
        ProfileItem i = new ProfileItem(); i.type=ROW; i.title=title; i.iconRes=iconRes; i.badge=badge; i.action=action; return i;
    }
    public static ProfileItem header(String name, String phone) {
        ProfileItem i = new ProfileItem(); i.type=HEADER; i.title=name; i.subtitle=phone; return i;
    }
    public static ProfileItem header(String name, String phone, String avatarUrl) {
        ProfileItem i = new ProfileItem(); i.type=HEADER; i.title=name; i.subtitle=phone; i.avatarUrl=avatarUrl; return i;
    }
    public static ProfileItem quickActions() {
        ProfileItem i = new ProfileItem(); i.type=QUICK_ACTIONS; return i;
    }
    public static ProfileItem orderTracking() {
        ProfileItem i = new ProfileItem(); i.type=ORDER_TRACKING; return i;
    }
}
