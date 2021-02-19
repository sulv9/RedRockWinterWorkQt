package com.aefottt.redrockwinterworkqt.model;

import com.aefottt.redrockwinterworkqt.data.bean.ArticleBean;
import com.aefottt.redrockwinterworkqt.data.bean.CollectWebBean;

import java.util.ArrayList;

public class MyInfoCallback {
    public interface CoinAndRankModelCallback{
        void onSuccess(int coin, int level, int rank);
        void onFail(Exception e);
    }
    public interface CollectArticleModelCallback{
        void onSuccess(ArrayList<ArticleBean> articleBeans);
        void onFail(Exception e);
    }
    public interface CollectWebOrCoinModelCallback{
        void onSuccess(ArrayList<CollectWebBean> collectWebBeans);
        void onFail(Exception e);
    }
}
