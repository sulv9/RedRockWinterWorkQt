package com.aefottt.redrockwinterworkqt.model;

import com.aefottt.redrockwinterworkqt.data.bean.ArticleBean;
import com.aefottt.redrockwinterworkqt.data.bean.TreeChapterBean;
import com.aefottt.redrockwinterworkqt.data.bean.TreeSuperChapterBean;
import com.aefottt.redrockwinterworkqt.contract.TreeContract;
import com.aefottt.redrockwinterworkqt.util.http.HttpCallbackListener;
import com.aefottt.redrockwinterworkqt.util.http.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TreeModel implements TreeContract.Model {
    @Override
    public void getChapterName(String url, TreeModelCallback.TabNameCallback callback) {
        HttpUtil.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(handleNameData(response));
            }

            @Override
            public void onError(Exception e) {
                callback.onFail(e);
            }
        });
    }

    @Override
    public void getArticles(String url, TreeModelCallback.ArticleCallback callback) {
        HttpUtil.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(handlerArticleData(response));
            }

            @Override
            public void onError(Exception e) {
                callback.onFail(e);
            }
        });
    }

    @Override
    public void refreshArticle(String url, TreeModelCallback.ArticleCallback callback) {
        HttpUtil.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(handlerArticleData(response));
            }

            @Override
            public void onError(Exception e) {
                callback.onFail(e);
            }
        });
    }

    private ArrayList<ArticleBean> handlerArticleData(String response) {
        ArrayList<ArticleBean> articleList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray datas = data.getJSONArray("datas");
            for (int i = 0;i < datas.length();i++){
                JSONObject articleObject = datas.getJSONObject(i);
                String author = articleObject.getString("author");
                articleList.add(new ArticleBean(
                        "".equals(author) ? articleObject.getString("shareUser") : author, //作者
                        articleObject.getString("niceShareDate"), //分享时间
                        articleObject.getString("title"), //标题
                        articleObject.getString("desc"), //描述
                        articleObject.getString("superChapterName"), //一级分类名称
                        articleObject.getString("chapterName"), //二级分类名称
                        articleObject.getString("envelopePic"), //封面图片地址
                        articleObject.getString("link") //文章对于的地址
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return articleList;
    }

    private ArrayList<TreeSuperChapterBean> handleNameData(String response) {
        ArrayList<TreeSuperChapterBean> superChapterBeans = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray data = jsonObject.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject superChapter = data.getJSONObject(i);
                String superName = superChapter.getString("name");
                JSONArray children = superChapter.getJSONArray("children");
                ArrayList<TreeChapterBean> chapterBeans = new ArrayList<>();
                for (int j = 0; j < children.length(); j++) {
                    JSONObject childrenObj = children.getJSONObject(j);
                    int id = childrenObj.getInt("id");
                    String chapterName = childrenObj.getString("name");
                    chapterBeans.add(new TreeChapterBean(chapterName, id));
                }
                superChapterBeans.add(new TreeSuperChapterBean(superName, chapterBeans));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return superChapterBeans;
    }
}
