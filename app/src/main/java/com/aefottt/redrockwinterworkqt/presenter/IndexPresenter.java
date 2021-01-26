package com.aefottt.redrockwinterworkqt.presenter;

import com.aefottt.redrockwinterworkqt.base.BasePresenter;
import com.aefottt.redrockwinterworkqt.bean.BannerBean;
import com.aefottt.redrockwinterworkqt.contract.IndexContract;
import com.aefottt.redrockwinterworkqt.model.IndexModel;
import com.aefottt.redrockwinterworkqt.model.IndexModelCallback;

import java.util.ArrayList;

public class IndexPresenter extends BasePresenter<IndexContract.View> implements IndexContract.Presenter {
    private final IndexContract.Model model;

    public IndexPresenter() {
        this.model = new IndexModel();
    }

    @Override
    public void getBannerData(String url) {
        if (!isViewAttached()){
            return;
        }
        mView.showLodaing();
        model.getBannerData(url, new IndexModelCallback() {
            @Override
            public void onSuccess(ArrayList<BannerBean> beans) {
                mView.getBannerDataSuccess(beans);
                mView.hideLoading();
            }

            @Override
            public void onFail(Exception e) {
                mView.onError(e);
            }
        });
    }
}
