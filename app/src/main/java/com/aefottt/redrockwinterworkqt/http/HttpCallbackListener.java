package com.aefottt.redrockwinterworkqt.http;

public interface HttpCallbackListener {
    void onResponse(String response);
    void onError(Exception e);
}
