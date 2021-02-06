package com.aefottt.redrockwinterworkqt.model;

import com.aefottt.redrockwinterworkqt.data.bean.ArticleBean;
import com.aefottt.redrockwinterworkqt.data.bean.TreeBean;

import java.util.ArrayList;

public class WxModelCallback {
    public interface WxTreeCallback{
        void onSuccess(ArrayList<TreeBean> treeBeans);
        void onError(Exception e);
    }
    public interface WxArticleCallback{
        void onSuccess(ArrayList<ArticleBean> articleBeans);
        void onError(Exception e);
    }
}
