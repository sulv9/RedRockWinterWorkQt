package com.aefottt.redrockwinterworkqt.model;

import com.aefottt.redrockwinterworkqt.contract.NaviContract;
import com.aefottt.redrockwinterworkqt.data.bean.NaviTitleBean;
import com.aefottt.redrockwinterworkqt.data.bean.NaviTreeBean;
import com.aefottt.redrockwinterworkqt.util.http.HttpCallbackListener;
import com.aefottt.redrockwinterworkqt.util.http.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NaviModel implements NaviContract.model {
    @Override
    public void getNaviData(String url, NaviModelCallback.NaviDataCallback callback) {
        HttpUtil.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(handleNaviData(response));
            }

            @Override
            public void onError(Exception e) {
                callback.onFail(e);
            }
        });
    }

    private ArrayList<NaviTreeBean> handleNaviData(String response) {
        ArrayList<NaviTreeBean> treeBeans = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray data = jsonObject.getJSONArray("data");
            for (int i = 0;i < data.length();i++){
                JSONObject dataObject = data.getJSONObject(i);
                String name = dataObject.getString("name");
                ArrayList<NaviTitleBean> titleBeans = new ArrayList<>();
                JSONArray articles = dataObject.getJSONArray("articles");
                for (int j = 0;j < articles.length();j++){
                    JSONObject articleObject = articles.getJSONObject(j);
                    String link = articleObject.getString("link");
                    String title = articleObject.getString("title");
                    titleBeans.add(new NaviTitleBean(title, link));
                }
                treeBeans.add(new NaviTreeBean(name, titleBeans));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return treeBeans;
    }
}
