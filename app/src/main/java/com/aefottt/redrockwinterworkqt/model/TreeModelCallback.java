package com.aefottt.redrockwinterworkqt.model;

import com.aefottt.redrockwinterworkqt.data.bean.ArticleBean;
import com.aefottt.redrockwinterworkqt.data.bean.TreeSuperChapterBean;

import java.util.ArrayList;

public class TreeModelCallback {
    public interface TabNameCallback{
        void onSuccess(ArrayList<TreeSuperChapterBean> names);
        void onFail(Exception e);
    }
    public interface ArticleCallback{
        void onSuccess(ArrayList<ArticleBean> articleBeans);
        void onFail(Exception e);
    }
}
