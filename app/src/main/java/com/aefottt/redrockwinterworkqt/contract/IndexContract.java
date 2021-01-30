package com.aefottt.redrockwinterworkqt.contract;

import com.aefottt.redrockwinterworkqt.base.BaseView;
import com.aefottt.redrockwinterworkqt.bean.BannerBean;
import com.aefottt.redrockwinterworkqt.bean.IndexArticleBean;
import com.aefottt.redrockwinterworkqt.model.IndexModelCallback;

import java.util.ArrayList;

public interface IndexContract {
    /**
     * 业务层
     */
    interface Model{
        void getBannerData(String url, IndexModelCallback.BannerModelCallback callback);
        void getArticleData(String url, IndexModelCallback.ArticleModelCallback callback);
    }

    /**
     * 视图层
     */
    interface View extends BaseView{
        @Override
        void onError(Exception e);

        void getBannerDataSuccess(ArrayList<BannerBean> bannerList);

        void getArticleDataSuccess(ArrayList<IndexArticleBean> articleList);
    }

    /**
     * 中介层
     */
    interface Presenter{
        /**
         * 加载首页Banner数据
         */
        void onLoadBannerData(String bannerUrl);

        /**
         * 加载首页文章数据
         */
        void onLoadArticleData(String articleUrl);
    }
}
