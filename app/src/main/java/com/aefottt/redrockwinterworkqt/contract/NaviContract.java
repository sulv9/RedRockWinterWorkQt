package com.aefottt.redrockwinterworkqt.contract;

import com.aefottt.redrockwinterworkqt.base.BaseView;
import com.aefottt.redrockwinterworkqt.data.bean.NaviTreeBean;
import com.aefottt.redrockwinterworkqt.model.NaviModelCallback;

import java.util.ArrayList;

public interface NaviContract {

    interface model{
        /**
         * 获取导航页面的全部数据
         * @param url 导航地址
         * @param callback 获取数据的回调接口
         */
        void getNaviData(String url, NaviModelCallback.NaviDataCallback callback);
    }

    interface view extends BaseView {
        void getNaviDataSuccess(ArrayList<NaviTreeBean> treeBeans);

        @Override
        void onError(Exception e);
    }

    interface presenter{
        void onLoadNaviData(String url);
    }
}
