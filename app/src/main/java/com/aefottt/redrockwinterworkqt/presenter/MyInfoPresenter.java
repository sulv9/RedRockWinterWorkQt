package com.aefottt.redrockwinterworkqt.presenter;

import com.aefottt.redrockwinterworkqt.base.BasePresenter;
import com.aefottt.redrockwinterworkqt.contract.MyInfoContract;
import com.aefottt.redrockwinterworkqt.data.bean.ArticleBean;
import com.aefottt.redrockwinterworkqt.data.bean.CollectWebBean;
import com.aefottt.redrockwinterworkqt.model.MyInfoCallback;
import com.aefottt.redrockwinterworkqt.model.MyInfoModel;

import java.util.ArrayList;

public class MyInfoPresenter extends BasePresenter<MyInfoContract.view> implements MyInfoContract.presenter {
    private final MyInfoModel model;

    public MyInfoPresenter() {
        model = new MyInfoModel();
    }

    @Override
    public void loadInitData(String coinAndRankUrl, String collectArticleUrl) {
        if (!isViewAttached()){
            return;
        }
        // 获取积分等级排名信息
        model.getCoinCountAndRankData(coinAndRankUrl, new MyInfoCallback.CoinAndRankModelCallback() {
            @Override
            public void onSuccess(int coin, int level, int rank) {
                mView.getCoinCountAndRankInfo(coin, level, rank);
            }
            @Override
            public void onFail(Exception e) {
                mView.onError(e);
            }
        });
        // 获取收藏文章信息
        model.getCollectArticleData(collectArticleUrl, new MyInfoCallback.CollectArticleModelCallback() {
            @Override
            public void onSuccess(ArrayList<ArticleBean> articleBeans) {
                mView.getCollectArticleList(articleBeans);
            }
            @Override
            public void onFail(Exception e) {
                mView.onError(e);
            }
        });
    }

    @Override
    public void loadCollectArticleData(String url) {
        model.getCollectArticleData(url, new MyInfoCallback.CollectArticleModelCallback() {
            @Override
            public void onSuccess(ArrayList<ArticleBean> articleBeans) {
                mView.getCollectArticleList(articleBeans);
            }
            @Override
            public void onFail(Exception e) {
                mView.onError(e);
            }
        });
    }

    @Override
    public void loadCollectWebData(String url) {
        model.getCollectWebData(url, new MyInfoCallback.CollectWebOrCoinModelCallback() {
            @Override
            public void onSuccess(ArrayList<CollectWebBean> collectWebBeans) {
                mView.getCollectWebList(collectWebBeans);
            }

            @Override
            public void onFail(Exception e) {
                mView.onError(e);
            }
        });
    }

    @Override
    public void loadCollectCoinData(String url) {
        model.getCollectCoinData(url, new MyInfoCallback.CollectWebOrCoinModelCallback() {
            @Override
            public void onSuccess(ArrayList<CollectWebBean> collectWebBeans) {
                mView.getCoinList(collectWebBeans);
            }

            @Override
            public void onFail(Exception e) {
                mView.onError(e);
            }
        });
    }

    @Override
    public void loadMoreArticleData(String articleUrl) {
        model.getCollectArticleData(articleUrl, new MyInfoCallback.CollectArticleModelCallback() {
            @Override
            public void onSuccess(ArrayList<ArticleBean> articleBeans) {
                mView.getMoreArticleData(articleBeans);
            }
            @Override
            public void onFail(Exception e) {
                mView.onError(e);
            }
        });
    }

    @Override
    public void loadMoreCoinListData(String coinUrl) {
        model.getCollectCoinData(coinUrl, new MyInfoCallback.CollectWebOrCoinModelCallback() {
            @Override
            public void onSuccess(ArrayList<CollectWebBean> collectWebBeans) {
                mView.getMoreCoinListData(collectWebBeans);
            }

            @Override
            public void onFail(Exception e) {
                mView.onError(e);
            }
        });
    }
}
