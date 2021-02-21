package com.aefottt.redrockwinterworkqt.util.http;

import android.util.Log;

import com.aefottt.redrockwinterworkqt.util.PrefUtil;
import com.aefottt.redrockwinterworkqt.view.my.MyApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

public class HttpUtil {
    /**
     * HttpGet请求封装
     */
    public static void sendHttpGetRequest(String address, HttpCallbackListener listener) {
        new Thread(
                () -> {
                    HttpURLConnection connection = null;
                    try {
                        URL url = new URL(address);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(1800);
                        connection.setReadTimeout(1800);
                        // 开启CookieHandler线程
                        CookieHandler.setDefault(MyApplication.getCookieManager());
                        CookieStore cookieJar = MyApplication.getCookieManager().getCookieStore();
                        cookieJar.removeAll();
                        // 添加已经存储的Cookies
                        for (HttpCookie cookie : MyApplication.getCookies()){
                            cookieJar.add(url.toURI(), cookie);
                        }
                        connection.connect();
                        String response = StreamToString(connection.getInputStream());
                        if (listener != null) {
                            listener.onResponse(response);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("qt", "Response Fail!");
                        if (listener != null) {
                            listener.onError(e);
                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    } finally {
                        if (connection != null) {
                            Log.e("qt", "Connection Disconnect!");
                            connection.disconnect();
                        }
                        if (connection == null) {
                            Log.e("qt", "Connection Null!");
                        }
                    }
                }).start();
    }

    /**
     * HttpPost请求封装
     */
    public static void sendHttpPostRequest(String address, Map<String, String> params, HttpCallbackListener listener) {
        new Thread(
                () -> {
                    HttpURLConnection connection = null;
                    try {
                        URL url = new URL(address);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setConnectTimeout(8000);
                        connection.setReadTimeout(8000);
                        connection.setDoInput(true);
                        connection.setDoOutput(true);
                        // 储存Cookies值
                        CookieHandler.setDefault(MyApplication.getCookieManager());
                        CookieStore cookieJar = MyApplication.getCookieManager().getCookieStore();
                        if (MyApplication.getCookies().size() == 0){
                            MyApplication.setCookies(cookieJar.getCookies());
                            // sp存储cookies
                            PrefUtil.getInstance().storeCookies(cookieJar.getCookies());
                        }else {
                            cookieJar.removeAll();
                            // 如果已经有Cookie值了就直接取出来
                            for (HttpCookie cookie : MyApplication.getCookies()){
                                cookieJar.add(url.toURI(), cookie);
                            }
                        }
                        connection.connect();
                        // 拼接字符串
                        StringBuilder builder = new StringBuilder();
                        for (String key : params.keySet()) {
                            builder.append(key).append("=").append(params.get(key)).append("&");
                        }
                        // 开启输入流
                        OutputStream outputStream = connection.getOutputStream();
                        // 输入信息，去掉最后一个&
                        outputStream.write(builder.substring(0, builder.length() - 1).getBytes());
                        // 获取输出信息
                        String response = StreamToString(connection.getInputStream());
                        if (listener != null) {
                            listener.onResponse(response);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (listener != null) {
                            listener.onError(e);
                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                        }
                    }
                }
        ).start();
    }

    /**
     * 将输入流转化为字符串
     */
    private static String StreamToString(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();
        String lines;
        while ((lines = reader.readLine()) != null) {
            builder.append(lines);
        }
        return builder.toString();
    }
}
