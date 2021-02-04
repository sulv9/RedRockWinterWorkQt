package com.aefottt.redrockwinterworkqt.model;

import com.aefottt.redrockwinterworkqt.data.bean.ArticleBean;

import java.util.ArrayList;

public interface WendaModelCallback {
    interface WendaDataCallback{
        void onSuccess(ArrayList<ArticleBean> articleBeans);
        void onFail(Exception e);
    }
}
