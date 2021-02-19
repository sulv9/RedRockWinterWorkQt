package com.aefottt.redrockwinterworkqt.model;

import com.aefottt.redrockwinterworkqt.contract.LoginContract;
import com.aefottt.redrockwinterworkqt.util.PrefUtil;
import com.aefottt.redrockwinterworkqt.util.Utility;
import com.aefottt.redrockwinterworkqt.util.http.HttpCallbackListener;
import com.aefottt.redrockwinterworkqt.util.http.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class LoginModel implements LoginContract.Model {
    @Override
    public void onLoginOrRegister(String url, Map<String, String> account, LoginModelCallback callback) {
        HttpUtil.sendHttpPostRequest(url, account, new HttpCallbackListener() {
            @Override
            public void onResponse(String response) {
                if (isLoginRight(response)){
                    callback.isSendSuccess(true);
                    PrefUtil.getInstance().put(Utility.FILE_NAME_USER_INFO, Utility.KEY_IS_LOGIN, true);
                }else {
                    callback.isSendSuccess(false);
                }
            }

            @Override
            public void onError(Exception e) {
                callback.isSendSuccess(false);
            }
        });
    }

    /**
     * 判断errorCode是否为0
     * @param response
     * @return
     */
    private boolean isLoginRight(String response){
        int errorCode = -1;
        try {
            JSONObject jsonObject = new JSONObject(response);
            errorCode = jsonObject.getInt("errorCode");
            if (errorCode == 0){
                // 储存用户名信息
                JSONObject data = jsonObject.getJSONObject("data");
                String username = data.getString("username");
                PrefUtil.getInstance().put(Utility.FILE_NAME_USER_INFO, Utility.KEY_USERNAME, username);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return errorCode == 0;
    }
}
