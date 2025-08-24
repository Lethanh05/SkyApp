package com.example.skymall.auth;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF = "auth_pref";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_ID    = "user_id";
    private static final String KEY_NAME  = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE  = "role";

    /* ===== Utils ===== */
    private static SharedPreferences sp(Context c) {
        return c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }
    private static String norm(String s) {
        return s == null ? null : s.trim().toLowerCase();
    }
    private static boolean isValidRole(String r) {
        if (r == null) return false;
        switch (r) {
            case "customer":
            case "store":
            case "admin":
                return true;
            default:
                return false;
        }
    }

    /* ===== Save all at once (khuyến nghị dùng khi login) ===== */
    public static void save(Context c, String token, String id, String name, String email, String role) {
        String r = norm(role);
        SharedPreferences.Editor e = sp(c).edit();
        e.putString(KEY_TOKEN, token);
        e.putString(KEY_ID, id);
        e.putString(KEY_NAME, name);
        e.putString(KEY_EMAIL, email);
        if (isValidRole(r)) {
            e.putString(KEY_ROLE, r);
        } else {
            // Không ghi role nếu không hợp lệ -> để Splash gọi /auth/me cập nhật
            e.remove(KEY_ROLE);
        }
        e.apply();
    }

    /**
     * Deprecated: đừng dùng vì sẽ che lỗi role.
     * Nếu buộc phải dùng (để tương thích code cũ), hãy gọi updateRole() sau khi /auth/me trả về.
     */
    @Deprecated
    public static void save(Context c, String token, String id, String name, String email) {
        SharedPreferences.Editor e = sp(c).edit();
        e.putString(KEY_TOKEN, token);
        e.putString(KEY_ID, id);
        e.putString(KEY_NAME, name);
        e.putString(KEY_EMAIL, email);
        // Không set role mặc định ở đây để tránh “cứ mở app lại là customer”
        e.remove(KEY_ROLE);
        e.apply();
    }

    /* ===== Update từng phần (dùng sau /auth/me) ===== */
    public static void updateRole(Context c, String role) {
        String r = norm(role);
        SharedPreferences.Editor e = sp(c).edit();
        if (isValidRole(r)) e.putString(KEY_ROLE, r);
        else e.remove(KEY_ROLE);
        e.apply();
    }
    public static void saveToken(Context c, String token) {
        sp(c).edit().putString(KEY_TOKEN, token).apply();
    }
    public static void clear(Context c) {
        sp(c).edit().clear().apply();
    }

    /* ===== Getters ===== */
    public static String token(Context c)  { return sp(c).getString(KEY_TOKEN, null); }
    public static String userId(Context c) { return sp(c).getString(KEY_ID, null); }
    public static String name(Context c)   { return sp(c).getString(KEY_NAME, null); }
    public static String email(Context c)  { return sp(c).getString(KEY_EMAIL, null); }

    /**
     * Trả về role đã lưu (đã normalize). Có thể null nếu chưa /auth/me.
     * Ở Splash: nếu role==null => gọi /auth/me để lấy role thật rồi route.
     */
    public static String role(Context c) {
        String r = norm(sp(c).getString(KEY_ROLE, null));
        return isValidRole(r) ? r : null;
    }

    public static boolean isLoggedIn(Context c) { return token(c) != null; }

    /* ===== Helpers quyền (dùng cho route UI) ===== */
    public static boolean isCustomer(Context c) {
        String r = role(c);
        return "customer".equals(r);
    }
    public static boolean isStore(Context c) {
        String r = role(c);
        return "store".equals(r);
    }
    public static boolean isAdmin(Context c) {
        String r = role(c);
        return "admin".equals(r);
    }
    public static boolean canManageStore(Context c) {
        String r = role(c);
        return "store".equals(r) || "admin".equals(r);
    }
}
