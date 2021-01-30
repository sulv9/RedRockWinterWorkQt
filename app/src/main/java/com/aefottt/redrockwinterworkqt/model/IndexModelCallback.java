package com.aefottt.redrockwinterworkqt.model;

import com.aefottt.redrockwinterworkqt.bean.BannerBean;
import com.aefottt.redrockwinterworkqt.bean.IndexArticleBean;

import java.util.ArrayList;

public class IndexModelCallback {
    public interface BannerModelCallback{
        void onSuccess(ArrayList<BannerBean> beans);
        void onFail(Exception e);
    }
    public interface ArticleModelCallback{
        void onSuccess(ArrayList<IndexArticleBean> beans);
        void onFail(Exception e);
    }
}
