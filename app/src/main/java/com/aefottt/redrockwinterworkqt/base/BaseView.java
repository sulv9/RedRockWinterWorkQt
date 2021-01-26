package com.aefottt.redrockwinterworkqt.base;

public interface BaseView {
    /**
     * 显示加载中ing
     */
    void showLodaing();

    /**
     * 隐藏加载
     */
    void hideLoading();

    /**
     * 加载失败
     */
    void onError(Throwable throwable);
}
