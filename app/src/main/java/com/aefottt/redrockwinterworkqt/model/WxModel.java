package com.aefottt.redrockwinterworkqt.model;

import com.aefottt.redrockwinterworkqt.contract.WxContract;
import com.aefottt.redrockwinterworkqt.data.bean.ArticleBean;
import com.aefottt.redrockwinterworkqt.data.bean.TreeBean;
import com.aefottt.redrockwinterworkqt.util.http.HttpCallbackListener;
import com.aefottt.redrockwinterworkqt.util.http.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WxModel implements WxContract.model {
    @Override
    public void getTreeData(String url, WxModelCallback.WxTreeCallback callback) {
        HttpUtil.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(handlerTreeData(response));
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    @Override
    public void getArticleData(String url, WxModelCallback.WxArticleCallback callback) {
        HttpUtil.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(handlerArticleData(response));
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    private ArrayList<TreeBean> handlerTreeData(String response) {
        ArrayList<TreeBean> treeBeans = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray data = jsonObject.getJSONArray("data");
            for (int i = 0;i < data.length();i++){
                JSONObject dataObject = data.getJSONObject(i);
                String name = dataObject.getString("name");
                int id = dataObject.getInt("id");
                treeBeans.add(new TreeBean(name, id));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return treeBeans;
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
}
