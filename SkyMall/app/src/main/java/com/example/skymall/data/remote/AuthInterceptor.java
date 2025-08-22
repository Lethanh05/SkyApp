
package com.example.skymall.data.remote;

import android.content.Context;
import com.example.skymall.auth.SessionManager;
import java.io.IOException;
import okhttp3.Interceptor; import okhttp3.Request; import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final Context ctx;
    public AuthInterceptor(Context ctx){ this.ctx = ctx.getApplicationContext(); }
    @Override public Response intercept(Chain chain) throws IOException {
        Request req = chain.request();
        String t = SessionManager.token(ctx);
        if (t != null) {
            req = req.newBuilder().addHeader("Authorization", "Bearer " + t).build();
        }
        return chain.proceed(req);
    }
}
