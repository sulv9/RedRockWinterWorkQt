package com.aefottt.redrockwinterworkqt.model;

import com.aefottt.redrockwinterworkqt.data.bean.NaviTreeBean;

import java.util.ArrayList;

public class NaviModelCallback {
    public interface NaviDataCallback{
        void onSuccess(ArrayList<NaviTreeBean> treeBeans);
        void onFail(Exception e);
    }
}
