package com.aefottt.redrockwinterworkqt.contract;

import com.aefottt.redrockwinterworkqt.base.BaseView;
import com.aefottt.redrockwinterworkqt.bean.BannerBean;
import com.aefottt.redrockwinterworkqt.model.IndexModelCallback;

import java.util.ArrayList;

public interface IndexContract {
    /**
     * 业务层
     */
    interface Model{
        void getBannerData(String url, IndexModelCallback callback);
    }

    /**
     * 视图层
     */
    interface View extends BaseView{
        @Override
        void showLodaing();

        @Override
        void hideLoading();

        @Override
        void onError(Throwable throwable);

        void getBannerDataSuccess(ArrayList<BannerBean> bannerList);
    }

    /**
     * 中介层
     */
    interface Presenter{
        /**
         * 获取Banner数据
         */
        void getBannerData(String url);
    }
}
