package com.aefottt.redrockwinterworkqt.model;

import com.aefottt.redrockwinterworkqt.data.bean.BannerBean;
import com.aefottt.redrockwinterworkqt.data.bean.ArticleBean;
import com.aefottt.redrockwinterworkqt.contract.IndexContract;
import com.aefottt.redrockwinterworkqt.util.http.HttpCallbackListener;
import com.aefottt.redrockwinterworkqt.util.http.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class IndexModel implements IndexContract.Model {
    /**
     * 网络请求获取Banner数据
     * @param url Banner数据
     * @param callback 获取成功后的回调
     */
    @Override
    public void getBannerData(String url, IndexModelCallback.BannerModelCallback callback) {
        HttpUtil.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(handleBannerJSON(response)); //请求成功返回处理后的数据
            }
            @Override
            public void onError(Exception e) {
                callback.onFail(e);
            }
        });
    }

    /**
     * 网络请求获取Article数据
     * @param url Article地址
     * @param callback 获取成功后的回调
     */
    @Override
    public void getArticleData(String url, IndexModelCallback.ArticleModelCallback callback) {
        HttpUtil.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(handleArticleJSON(response)); //请求成功返回处理后的数据
            }
            @Override
            public void onError(Exception e) {
                callback.onFail(e);
            }
        });
    }

    /**
     * 处理得到的首页广告轮播图Json数据
     */
    public ArrayList<BannerBean> handleBannerJSON(String response) {
        ArrayList<BannerBean> bannerList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray data = jsonObject.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject bannerObject = data.getJSONObject(i);
                bannerList.add(new BannerBean(bannerObject.getString("imagePath"),
                        bannerObject.getString("url")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bannerList;
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
