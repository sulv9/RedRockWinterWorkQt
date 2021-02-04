package com.aefottt.redrockwinterworkqt.model;

import com.aefottt.redrockwinterworkqt.contract.WendaContract;
import com.aefottt.redrockwinterworkqt.data.bean.ArticleBean;
import com.aefottt.redrockwinterworkqt.util.http.HttpCallbackListener;
import com.aefottt.redrockwinterworkqt.util.http.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WendaModel implements WendaContract.Model {
    @Override
    public void getWendaData(String url, WendaModelCallback.WendaDataCallback callback) {
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

    private ArrayList<ArticleBean> handleArticleJSON(String response) {
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
