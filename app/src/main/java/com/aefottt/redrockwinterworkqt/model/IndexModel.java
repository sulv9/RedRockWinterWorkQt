package com.aefottt.redrockwinterworkqt.model;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.aefottt.redrockwinterworkqt.bean.BannerBean;
import com.aefottt.redrockwinterworkqt.contract.IndexContract;
import com.aefottt.redrockwinterworkqt.http.HttpCallbackListener;
import com.aefottt.redrockwinterworkqt.http.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class IndexModel implements IndexContract.Model {
    private static final int WHAT_GET_BANNER_DATA = 1;

    /**
     * 网络请求
     */
    @Override
    public void getBannerData(String url, IndexModelCallback callback) {
        HttpUtil.sendHttpGetRequest(url, new HttpCallbackListener() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(handlerBannerJSON(response));
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
    public ArrayList<BannerBean> handlerBannerJSON(String response) {
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
}
