package com.aefottt.redrockwinterworkqt.presenter;

import com.aefottt.redrockwinterworkqt.base.BasePresenter;
import com.aefottt.redrockwinterworkqt.contract.ProjectContract;
import com.aefottt.redrockwinterworkqt.data.bean.ArticleBean;
import com.aefottt.redrockwinterworkqt.data.bean.TreeBean;
import com.aefottt.redrockwinterworkqt.model.ProjectModel;
import com.aefottt.redrockwinterworkqt.model.ProjectModelCallback;

import java.util.ArrayList;

public class ProjectPresenter extends BasePresenter<ProjectContract.view> implements ProjectContract.presenter {
    private final ProjectModel model;

    public ProjectPresenter() {
        this.model = new ProjectModel();
    }

    @Override
    public void onLoadTreeData(String url) {
        if (!isViewAttached()){
            return;
        }
        model.getTreeData(url, new ProjectModelCallback.ProjectTreeCallback() {
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
        model.getArticleData(url, new ProjectModelCallback.ProjectArticleCallback() {
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
        model.getArticleData(url, new ProjectModelCallback.ProjectArticleCallback() {
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
