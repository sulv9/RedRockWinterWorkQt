package com.aefottt.redrockwinterworkqt.model;

import com.aefottt.redrockwinterworkqt.contract.MyInfoContract;
import com.aefottt.redrockwinterworkqt.data.bean.ArticleBean;
import com.aefottt.redrockwinterworkqt.data.bean.CollectWebBean;
import com.aefottt.redrockwinterworkqt.util.http.HttpCallbackListener;
import com.aefottt.redrockwinterworkqt.util.http.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.util.ArrayList;

public class MyInfoModel implements MyInfoContract.model {
    @Override
    public void getCoinCountAndRankData(String url, MyInfoCallback.CoinAndRankModelCallback callback) {
        CookieHandler.getDefault();
        HttpUtil.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onResponse(String response) {
                int[] result = handleCoinAndRankData(response);
                callback.onSuccess(result[0], result[1], result[2]);
            }
            @Override
            public void onError(Exception e) {
                callback.onFail(e);
            }
        });
    }

    @Override
    public void getCollectArticleData(String url, MyInfoCallback.CollectArticleModelCallback callback) {
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

    @Override
    public void getCollectWebData(String url, MyInfoCallback.CollectWebOrCoinModelCallback callback) {
        HttpUtil.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(handleCollectWebData(response));
            }
            @Override
            public void onError(Exception e) {
                callback.onFail(e);
            }
        });
    }

    @Override
    public void getCollectCoinData(String url, MyInfoCallback.CollectWebOrCoinModelCallback callback) {
        HttpUtil.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(handleCollectCoinData(response));
            }
            @Override
            public void onError(Exception e) {
                callback.onFail(e);
            }
        });
    }

    private int[] handleCoinAndRankData(String response){
        int[] result = new int[3];
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject data = jsonObject.getJSONObject("data");
            int coinCount = data.getInt("coinCount");
            int level = data.getInt("level");
            int rank = data.getInt("rank");
            result[0] = coinCount;
            result[1] = level;
            result[2] = rank;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 处理得到的文章JSON数据
     */
    private ArrayList<ArticleBean> handleArticleJSON(String response){
        ArrayList<ArticleBean> articleList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray datas = data.getJSONArray("datas");
            for (int i = 0;i < datas.length();i++){
                JSONObject articleObject = datas.getJSONObject(i);
                articleList.add(new ArticleBean(
                        articleObject.getString("author"), //作者
                        articleObject.getString("niceDate"), //分享时间
                        articleObject.getString("title"), //标题
                        articleObject.getString("desc"), //描述
                        "", //一级分类名称
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

    private ArrayList<CollectWebBean> handleCollectWebData(String response){
        ArrayList<CollectWebBean> webBeans = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray data = jsonObject.getJSONArray("data");
            for (int i = 0;i < data.length();i++){
                JSONObject collectWebBean = data.getJSONObject(i);
                webBeans.add(new CollectWebBean(collectWebBean.getString("name"),
                        collectWebBean.getString("link")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return webBeans;
    }

    private ArrayList<CollectWebBean> handleCollectCoinData(String response){
        ArrayList<CollectWebBean> webBeans = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray datas = data.getJSONArray("datas");
            for (int i = 0;i < datas.length();i++){
                JSONObject collectCoinBean = datas.getJSONObject(i);
                webBeans.add(new CollectWebBean(collectCoinBean.getString("reason"),
                        collectCoinBean.getString("desc")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return webBeans;
    }
}
