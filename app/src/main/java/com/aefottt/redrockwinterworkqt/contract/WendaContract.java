package com.aefottt.redrockwinterworkqt.contract;

import com.aefottt.redrockwinterworkqt.base.BaseView;
import com.aefottt.redrockwinterworkqt.data.bean.ArticleBean;
import com.aefottt.redrockwinterworkqt.model.WendaModelCallback;

import java.util.ArrayList;

public interface WendaContract {
    interface Model{
        void getWendaData(String url, WendaModelCallback.WendaDataCallback callback);
    }
    interface View extends BaseView{
        @Override
        void onError(Exception e);

        void getWendaDataSuccess(ArrayList<ArticleBean> articleBeans);

        void getMoreDataSuccess(ArrayList<ArticleBean> articleBeans);
    }
    interface Presenter{
        void onLoadWendaData(String url);
        void onLoadMoreData(String url);
    }
}
