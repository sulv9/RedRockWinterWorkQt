package com.aefottt.redrockwinterworkqt.model;

import com.aefottt.redrockwinterworkqt.bean.BannerBean;

import java.util.ArrayList;

public interface IndexModelCallback {
    void onSuccess(ArrayList<BannerBean> beans);
    void onFail(Exception e);
}
