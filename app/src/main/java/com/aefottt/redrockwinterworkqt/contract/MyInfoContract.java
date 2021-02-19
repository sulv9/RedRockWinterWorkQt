package com.aefottt.redrockwinterworkqt.contract;

import com.aefottt.redrockwinterworkqt.base.BaseView;
import com.aefottt.redrockwinterworkqt.data.bean.ArticleBean;
import com.aefottt.redrockwinterworkqt.data.bean.CollectWebBean;
import com.aefottt.redrockwinterworkqt.model.MyInfoCallback;

import java.util.ArrayList;

public interface MyInfoContract {

    interface model{
        void getCoinCountAndRankData(String url, MyInfoCallback.CoinAndRankModelCallback callback);
        void getCollectArticleData(String url, MyInfoCallback.CollectArticleModelCallback callback);
        void getCollectWebData(String url, MyInfoCallback.CollectWebOrCoinModelCallback callback);
        void getCollectCoinData(String url, MyInfoCallback.CollectWebOrCoinModelCallback callback);
    }

    interface view extends BaseView {
        // 获取总积分和排名信息
        void getCoinCountAndRankInfo(int coinCount, int level, int rank);
        // 获取收藏的文章列表
        void getCollectArticleList(ArrayList<ArticleBean> articleBeans);
        // 获取收藏网站列表
        void getCollectWebList(ArrayList<CollectWebBean> collectWebBeans);
        // 获取积分获取列表
        void getCoinList(ArrayList<CollectWebBean> collectCoinBeans);
        // 加载更多文章数据
        void getMoreArticleData(ArrayList<ArticleBean> articleBeans);
        // 加载更多积分记录数据
        void getMoreCoinListData(ArrayList<CollectWebBean> collectCoinBeans);

        @Override
        void onError(Exception e);
    }

    interface presenter{
        // 加载初始数据---积分，排名以及收集的文章列表
        void loadInitData(String coinAndRankUrl, String collectArticleUrl);
        // 加载收集文章列表
        void loadCollectArticleData(String articleUrl);
        // 加载收藏网站列表
        void loadCollectWebData(String webUrl);
        // 加载积分获取列表
        void loadCollectCoinData(String coinUrl);
        // 加载更多文章数据
        void loadMoreArticleData(String articleUrl);
        // 加载更多积分记录数据
        void loadMoreCoinListData(String coinUrl);
    }
}
