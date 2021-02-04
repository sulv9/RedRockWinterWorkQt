package com.aefottt.redrockwinterworkqt.presenter;

import com.aefottt.redrockwinterworkqt.base.BasePresenter;
import com.aefottt.redrockwinterworkqt.contract.WendaContract;
import com.aefottt.redrockwinterworkqt.data.bean.ArticleBean;
import com.aefottt.redrockwinterworkqt.model.WendaModel;
import com.aefottt.redrockwinterworkqt.model.WendaModelCallback;

import java.util.ArrayList;

public class WendaPresenter extends BasePresenter<WendaContract.View> implements WendaContract.Presenter {
    private WendaModel model;

    public WendaPresenter() {
        this.model = new WendaModel();
    }

    @Override
    public void onLoadWendaData(String url) {
        if (!isViewAttached()){
            return;
        }
        model.getWendaData(url, new WendaModelCallback.WendaDataCallback() {
            @Override
            public void onSuccess(ArrayList<ArticleBean> articleBeans) {
                mView.getWendaDataSuccess(articleBeans);
            }
            @Override
            public void onFail(Exception e) {
                mView.onError(e);
            }
        });
    }

    @Override
    public void onLoadMoreData(String url) {
        if (!isViewAttached()){
            return;
        }
        model.getWendaData(url, new WendaModelCallback.WendaDataCallback() {
            @Override
            public void onSuccess(ArrayList<ArticleBean> articleBeans) {
                mView.getMoreDataSuccess(articleBeans);
            }
            @Override
            public void onFail(Exception e) {
                mView.onError(e);
            }
        });
    }
}
