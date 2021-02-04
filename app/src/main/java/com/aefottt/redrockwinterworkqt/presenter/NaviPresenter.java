package com.aefottt.redrockwinterworkqt.presenter;

import com.aefottt.redrockwinterworkqt.base.BasePresenter;
import com.aefottt.redrockwinterworkqt.contract.NaviContract;
import com.aefottt.redrockwinterworkqt.data.bean.NaviTreeBean;
import com.aefottt.redrockwinterworkqt.model.NaviModel;
import com.aefottt.redrockwinterworkqt.model.NaviModelCallback;

import java.util.ArrayList;

public class NaviPresenter extends BasePresenter<NaviContract.view> implements NaviContract.presenter {
    private final NaviModel model;

    public NaviPresenter() {
        this.model = new NaviModel();
    }

    @Override
    public void onLoadNaviData(String url) {
        if (!isViewAttached()){
            return;
        }
        model.getNaviData(url, new NaviModelCallback.NaviDataCallback() {
            @Override
            public void onSuccess(ArrayList<NaviTreeBean> treeBeans) {
                mView.getNaviDataSuccess(treeBeans);
            }

            @Override
            public void onFail(Exception e) {
                mView.onError(e);
            }
        });
    }
}
