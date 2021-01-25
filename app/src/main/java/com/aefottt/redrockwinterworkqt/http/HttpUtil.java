package com.aefottt.redrockwinterworkqt.http;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
                        connection.setConnectTimeout(8000);
                        connection.setReadTimeout(8000);
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
                    } finally {
                        if (connection != null) {
                            Log.e("qt", "Connection Disconnect!");
                            connection.disconnect();
                        }
                        if (connection == null){
                            Log.e("qt", "Connection Null!");
                        }
                    }
                }).start();
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
