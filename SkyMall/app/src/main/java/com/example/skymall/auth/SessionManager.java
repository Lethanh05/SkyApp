
package com.example.skymall.auth;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF = "auth_pref";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_ID = "user_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";

    public static void save(Context c, String token, String id, String name, String email){
        SharedPreferences sp = c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_TOKEN, token).putString(KEY_ID, id)
                .putString(KEY_NAME, name).putString(KEY_EMAIL, email).apply();
    }
    public static void clear(Context c){
        c.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().clear().apply();
    }
    public static String token(Context c){ return c.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY_TOKEN,null); }
    public static String userId(Context c){ return c.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY_ID,null); }
    public static String name(Context c){ return c.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY_NAME,null); }
    public static boolean isLoggedIn(Context c){ return token(c)!=null; }
}
