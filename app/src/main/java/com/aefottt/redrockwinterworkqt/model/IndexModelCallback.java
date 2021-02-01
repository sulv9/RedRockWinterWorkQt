package com.aefottt.redrockwinterworkqt.model;

import com.aefottt.redrockwinterworkqt.data.bean.BannerBean;
import com.aefottt.redrockwinterworkqt.data.bean.ArticleBean;

import java.util.ArrayList;

public class IndexModelCallback {
    public interface BannerModelCallback{
        void onSuccess(ArrayList<BannerBean> beans);
        void onFail(Exception e);
    }
    public interface ArticleModelCallback{
        void onSuccess(ArrayList<ArticleBean> beans);
        void onFail(Exception e);
    }
}
