package com.aefottt.redrockwinterworkqt.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.aefottt.redrockwinterworkqt.view.my.MyApplication;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

/**
 * SharedPreferences工具类封装
 */
public class PrefUtil {
    private static PrefUtil mInstance;

    private PrefUtil() {
    }

    public static PrefUtil getInstance(){
        if (mInstance == null){
            synchronized (PrefUtil.class){
                if (mInstance == null){
                    mInstance = new PrefUtil();
                }
            }
        }
        return mInstance;
    }

    /**
     * 存入数据
     * @param fileName 文件名
     * @param key key值
     * @param value value值
     */
    public void put(String fileName, String key, Object value){
        // 获取类型
        String type = value.getClass().getSimpleName();
        // 实例化Sp
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        switch (type) {
            case "Integer":
                editor.putInt(key, (Integer) value);
                break;
            case "String":
                editor.putString(key, (String) value);
                break;
            case "Boolean":
                editor.putBoolean(key, (Boolean) value);
                break;
            case "Float":
                editor.putFloat(key, (Float) value);
                break;
            case "Long":
                editor.putLong(key, (Long) value);
                break;
        }
        // 异步提交
        editor.apply();
    }

    /**
     * 获取数据
     * @param fileName 文件名
     * @param key key值
     * @param defValue 默认值
     * @return 对应数据
     */
    public Object get(String fileName, String key, Object defValue){
        // 获取类型
        String type = defValue.getClass().getSimpleName();
        // 实例化Sp
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        switch (type){
            case "Integer":
                return sp.getInt(key, (Integer) defValue);
            case "String":
                return sp.getString(key, (String) defValue);
            case "Boolean":
                return sp.getBoolean(key, (Boolean) defValue);
            case "Float":
                return sp.getFloat(key, (Float) defValue);
            case "Long":
                return sp.getLong(key, (Long) defValue);
        }
        return null;
    }

    /**
     * 清空所有数据
     * @param fileName 要清空数据的文件名
     */
    public void clear(String fileName){
        // 实例化Sp
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * 存储HttpCookie
     * @param cookies 登录成功返回的Cookies
     */
    public void storeCookies(List<HttpCookie> cookies){
        int cookieNum = 0;
        for (HttpCookie cookie : cookies){
            if (cookie != null){
                this.put(Utility.FILE_NAME_USER_INFO, Utility.KEY_COOKIE+cookieNum, cookie.toString()+
                        "="+cookie.getDomain()+"="+cookie.getPath());
                cookieNum++;
            }
        }
        this.put(Utility.FILE_NAME_USER_INFO, Utility.KEY_COOKIE_NUM, cookieNum);
    }

    /**
     * 获取Cookies
     * @return 储存的cookies
     */
    public List<HttpCookie> getCookies(){
        int cookieNum = (int) this.get(Utility.FILE_NAME_USER_INFO, Utility.KEY_COOKIE_NUM, 0);
        List<HttpCookie> cookies = new ArrayList<>();
        for (int i = 0;i < cookieNum;i++){
            String JSESSIONID = (String) this.get(Utility.FILE_NAME_USER_INFO, Utility.KEY_COOKIE+i, "");
            String[] spiltSession = JSESSIONID.split("=");
            HttpCookie cookie = new HttpCookie(spiltSession[0], spiltSession[1]);
            cookie.setDomain(spiltSession[2]);
            cookie.setPath(spiltSession[3]);
            cookies.add(cookie);
        }
        return cookies;
    }
}
