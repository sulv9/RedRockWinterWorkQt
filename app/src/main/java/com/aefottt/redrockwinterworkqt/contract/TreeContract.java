package com.aefottt.redrockwinterworkqt.contract;

import com.aefottt.redrockwinterworkqt.base.BaseView;
import com.aefottt.redrockwinterworkqt.data.bean.ArticleBean;
import com.aefottt.redrockwinterworkqt.data.bean.TreeSuperChapterBean;
import com.aefottt.redrockwinterworkqt.model.TreeModelCallback;

import java.util.ArrayList;

public interface TreeContract {
    interface Model{
        /**
         * 获取一级和二级目录的全部内容
         * @param url 获取目录地址
         */
        void getChapterName(String url, TreeModelCallback.TabNameCallback callback);

        /**
         * 获取文章
         * @param url 文章地址，包含chapterName的id
         */
        void getArticles(String url, TreeModelCallback.ArticleCallback callback);

        /**
         * 刷新文章数据
         */
        void refreshArticle(String url, TreeModelCallback.ArticleCallback callback);
    }

    interface View extends BaseView {
        @Override
        void onError(Exception e);

        void getChapterNameSuccess(ArrayList<TreeSuperChapterBean> names);

        void getArticleSuccess(ArrayList<ArticleBean> articleBeans);

        void onFinishRefresh(ArrayList<ArticleBean> articleBeans);
    }

    interface Presenter{
        void onLoadChapter(String url);

        void onLoadArticle(String url);

        void onRefreshArticle(String url);
    }
}
