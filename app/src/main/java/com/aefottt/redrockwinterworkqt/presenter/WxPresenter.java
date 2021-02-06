package com.aefottt.redrockwinterworkqt.presenter;

import com.aefottt.redrockwinterworkqt.base.BasePresenter;
import com.aefottt.redrockwinterworkqt.contract.WxContract;
import com.aefottt.redrockwinterworkqt.data.bean.ArticleBean;
import com.aefottt.redrockwinterworkqt.data.bean.TreeBean;
import com.aefottt.redrockwinterworkqt.model.WxModel;
import com.aefottt.redrockwinterworkqt.model.WxModelCallback;

import java.util.ArrayList;

public class WxPresenter extends BasePresenter<WxContract.view> implements WxContract.presenter {
    private final WxModel model;

    public WxPresenter() {
        this.model = new WxModel();
    }

    @Override
    public void onLoadTreeData(String url) {
        if (!isViewAttached()){
            return;
        }
        model.getTreeData(url, new WxModelCallback.WxTreeCallback() {
            @Override
            public void onSuccess(ArrayList<TreeBean> treeBeans) {
                mView.getTreeDataSuccess(treeBeans);
            }
            @Override
            public void onError(Exception e) {
                mView.onError(e);
            }
        });
    }

    @Override
    public void onLoadArticleData(String url) {
        if (!isViewAttached()){
            return;
        }
        model.getArticleData(url, new WxModelCallback.WxArticleCallback() {
            @Override
            public void onSuccess(ArrayList<ArticleBean> articleBeans) {
                mView.getArticleDataSuccess(articleBeans);
            }
            @Override
            public void onError(Exception e) {
                mView.onError(e);
            }
        });
    }

    @Override
    public void onLoadMoreArticleData(String url) {
        if (!isViewAttached()){
            return;
        }
        model.getArticleData(url, new WxModelCallback.WxArticleCallback() {
            @Override
            public void onSuccess(ArrayList<ArticleBean> articleBeans) {
                mView.getMoreArticleDataSuccess(articleBeans);
            }
            @Override
            public void onError(Exception e) {
                mView.onError(e);
            }
        });
    }
}
