package com.aefottt.redrockwinterworkqt.view.my;

import android.app.Application;
import android.content.Context;

import com.aefottt.redrockwinterworkqt.util.PrefUtil;
import com.aefottt.redrockwinterworkqt.util.Utility;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    private static Context context;
    private static CookieManager cookieManager;
    private static List<HttpCookie> cookies;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        cookieManager = new CookieManager();
        cookies = new ArrayList<>();
        boolean isLogin = (boolean) PrefUtil.getInstance().get(Utility.FILE_NAME_USER_INFO, Utility.KEY_IS_LOGIN, false);
        if (isLogin){
            cookies.clear();
            cookies.addAll(PrefUtil.getInstance().getCookies());
        }
    }

    public static Context getContext(){
        return context;
    }

    public static CookieManager getCookieManager() {
        return cookieManager;
    }

    public static void setCookies(List<HttpCookie> cookies) {
        MyApplication.cookies = cookies;
    }

    public static List<HttpCookie> getCookies() {
        return cookies;
    }

    public static void clearCookies(){
        cookies.clear();
    }
}
