package com.aefottt.redrockwinterworkqt.contract;

import com.aefottt.redrockwinterworkqt.base.BaseView;
import com.aefottt.redrockwinterworkqt.data.bean.ArticleBean;
import com.aefottt.redrockwinterworkqt.data.bean.TreeBean;
import com.aefottt.redrockwinterworkqt.model.ProjectModelCallback;

import java.util.ArrayList;

public interface ProjectContract {
    interface model{
        /**
         * 获取项目目录数据
         * @param url 项目目录地址
         * @param callback 网络请求回调
         */
        void getTreeData(String url, ProjectModelCallback.ProjectTreeCallback callback);

        /**
         * 获取项目文章数据
         * @param url 项目文章地址
         * @param callback 网络请求回调
         */
        void getArticleData(String url, ProjectModelCallback.ProjectArticleCallback callback);
    }

    interface view extends BaseView{
        void getTreeDataSuccess(ArrayList<TreeBean> treeBeans);

        void getArticleDataSuccess(ArrayList<ArticleBean> articleBeans);

        void getMoreArticleDataSuccess(ArrayList<ArticleBean> articleBeans);

        @Override
        void onError(Exception e);
    }

    interface presenter{
        void onLoadTreeData(String url);

        void onLoadArticleData(String url);

        void onLoadMoreArticleData(String url);
    }
}
