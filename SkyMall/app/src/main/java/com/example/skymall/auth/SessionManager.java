package com.example.skymall.auth;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF = "auth_pref";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_ID = "user_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role"; // Thêm key cho role

    public static void save(Context c, String token, String id, String name, String email, String role){
        SharedPreferences sp = c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_TOKEN, token).putString(KEY_ID, id)
                .putString(KEY_NAME, name).putString(KEY_EMAIL, email)
                .putString(KEY_ROLE, role).apply(); // Lưu role
    }

    // Overload method để backward compatibility
    public static void save(Context c, String token, String id, String name, String email){
        save(c, token, id, name, email, "customer"); // Default role là customer
    }

    public static void clear(Context c){
        c.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().clear().apply();
    }

    public static String token(Context c){ return c.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY_TOKEN,null); }
    public static String userId(Context c){ return c.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY_ID,null); }
    public static String name(Context c){ return c.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY_NAME,null); }
    public static String email(Context c){ return c.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY_EMAIL,null); }
    public static String role(Context c){ return c.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY_ROLE, "customer"); } // Thêm method lấy role
    public static boolean isLoggedIn(Context c){ return token(c)!=null; }
}
