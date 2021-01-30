package com.aefottt.redrockwinterworkqt.presenter;

import com.aefottt.redrockwinterworkqt.base.BasePresenter;
import com.aefottt.redrockwinterworkqt.bean.BannerBean;
import com.aefottt.redrockwinterworkqt.bean.IndexArticleBean;
import com.aefottt.redrockwinterworkqt.contract.IndexContract;
import com.aefottt.redrockwinterworkqt.model.IndexModel;
import com.aefottt.redrockwinterworkqt.model.IndexModelCallback;

import java.util.ArrayList;

public class IndexPresenter extends BasePresenter<IndexContract.View> implements IndexContract.Presenter {
    private final IndexContract.Model model;

    public IndexPresenter() {
        this.model = new IndexModel();
    }

    /**
     * 加载Banner数据
     * @param bannerUrl Banner地址
     */
    @Override
    public void onLoadBannerData(String bannerUrl) {
        // 如果未绑定视图则不再执行
        if (!isViewAttached()){
            return;
        }
        // 获取Banner数据
        model.getBannerData(bannerUrl, new IndexModelCallback.BannerModelCallback() {
            @Override
            public void onSuccess(ArrayList<BannerBean> beans) {
                mView.getBannerDataSuccess(beans);
            }
            @Override
            public void onFail(Exception e) {
                mView.onError(e);
            }
        });
    }

    /**
     * 加载文章数据
     * @param articleUrl Article地址
     */
    @Override
    public void onLoadArticleData(String articleUrl) {
        // 如果未绑定视图则不再执行
        if (!isViewAttached()){
            return;
        }
        // 获取Article数据
        model.getArticleData(articleUrl, new IndexModelCallback.ArticleModelCallback() {
            @Override
            public void onSuccess(ArrayList<IndexArticleBean> beans) {
                mView.getArticleDataSuccess(beans);
            }
            @Override
            public void onFail(Exception e) {
                mView.onError(e);
            }
        });
    }
}
