package com.aefottt.redrockwinterworkqt.presenter;

import com.aefottt.redrockwinterworkqt.base.BasePresenter;
import com.aefottt.redrockwinterworkqt.data.bean.ArticleBean;
import com.aefottt.redrockwinterworkqt.data.bean.TreeSuperChapterBean;
import com.aefottt.redrockwinterworkqt.contract.TreeContract;
import com.aefottt.redrockwinterworkqt.model.TreeModel;
import com.aefottt.redrockwinterworkqt.model.TreeModelCallback;

import java.util.ArrayList;

public class TreePresenter extends BasePresenter<TreeContract.View> implements TreeContract.Presenter {
    private final TreeContract.Model model;

    public TreePresenter() {
        this.model = new TreeModel();
    }

    @Override
    public void onLoadChapter(String url) {
        if (!isViewAttached()){
            return;
        }
        model.getChapterName(url, new TreeModelCallback.TabNameCallback() {
            @Override
            public void onSuccess(ArrayList<TreeSuperChapterBean> names) {
                mView.getChapterNameSuccess(names);
            }
            @Override
            public void onFail(Exception e) {
                mView.onError(e);
            }
        });
    }

    @Override
    public void onLoadArticle(String url) {
        if (!isViewAttached()){
            return;
        }
        model.getArticles(url, new TreeModelCallback.ArticleCallback() {
            @Override
            public void onSuccess(ArrayList<ArticleBean> articleBeans) {
                mView.getArticleSuccess(articleBeans);
            }

            @Override
            public void onFail(Exception e) {
                mView.onError(e);
            }
        });
    }

    @Override
    public void onRefreshArticle(String url) {
        if (!isViewAttached()){
            return;
        }
        model.refreshArticle(url, new TreeModelCallback.ArticleCallback() {
            @Override
            public void onSuccess(ArrayList<ArticleBean> articleBeans) {
                mView.onFinishRefresh(articleBeans);
            }

            @Override
            public void onFail(Exception e) {
                mView.onError(e);
            }
        });
    }
}
