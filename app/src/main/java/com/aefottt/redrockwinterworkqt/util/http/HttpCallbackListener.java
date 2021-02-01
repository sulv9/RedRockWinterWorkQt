package com.aefottt.redrockwinterworkqt.util.http;

public interface HttpCallbackListener {
    void onResponse(String response);
    void onError(Exception e);
}
