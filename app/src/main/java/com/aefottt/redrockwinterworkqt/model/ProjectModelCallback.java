package com.aefottt.redrockwinterworkqt.model;

import com.aefottt.redrockwinterworkqt.data.bean.ArticleBean;
import com.aefottt.redrockwinterworkqt.data.bean.TreeBean;

import java.util.ArrayList;

public class ProjectModelCallback {
    public interface ProjectTreeCallback{
        void onSuccess(ArrayList<TreeBean> treeBeans);
        void onError(Exception e);
    }
    public interface ProjectArticleCallback{
        void onSuccess(ArrayList<ArticleBean> articleBeans);
        void onError(Exception e);
    }
}
