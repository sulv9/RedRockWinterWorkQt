package com.aefottt.redrockwinterworkqt.model;

import com.aefottt.redrockwinterworkqt.data.bean.ArticleBean;
import com.aefottt.redrockwinterworkqt.util.http.HttpCallbackListener;
import com.aefottt.redrockwinterworkqt.util.http.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchModel {
    public SearchModel() {
    }

    public interface getHotNamesCallback{
        void onSuccess(ArrayList<String> hotNames);
        void onError(Exception e);
    }

    public interface getSearchPageCallback{
        void onSuccess(ArrayList<ArticleBean> articleBeans);
        void onError(Exception e);
    }

    /**
     * 获取热搜关键词
     */
    public void getHotFl(String url, getHotNamesCallback callback){
        ArrayList<String> hotFl = new ArrayList<>();
        HttpUtil.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray data = jsonObject.getJSONArray("data");
                    for (int i = 0;i < data.length();i++){
                        JSONObject dataObject = data.getJSONObject(i);
                        hotFl.add(dataObject.getString("name"));
                    }
                    callback.onSuccess(hotFl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    public void getSearchPage(String url, String k, getSearchPageCallback callback){
        ArrayList<ArticleBean> articleBeans = new ArrayList<>();
        Map<String, String> searchMap = new HashMap<>();
        searchMap.put("k", k);
        HttpUtil.sendHttpPostRequest(url, searchMap, new HttpCallbackListener() {
            @Override
            public void onResponse(String response) {
                articleBeans.clear();
                articleBeans.addAll(handleArticleJSON(response));
                callback.onSuccess(articleBeans);
            }
            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    /**
     * 处理得到的文章JSON数据
     */
    public ArrayList<ArticleBean> handleArticleJSON(String response){
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
