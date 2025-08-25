package com.example.skymall.auth;
import android.content.Context;
import androidx.annotation.Nullable;

public final class TokenStore {
    private static final String PREF = "auth";
    private static final String KEY  = "token";

    public static void saveToken(Context c, String token) {
        c.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .edit().putString(KEY, token).apply();
    }

    @Nullable
    public static String getToken(Context c) {
        return c.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .getString(KEY, null);
    }

    public static void clear(Context c) {
        c.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .edit().remove(KEY).apply();
    }
}
