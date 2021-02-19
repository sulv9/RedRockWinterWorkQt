package com.aefottt.redrockwinterworkqt.contract;

import android.widget.TextView;

import com.aefottt.redrockwinterworkqt.base.BaseView;
import com.aefottt.redrockwinterworkqt.model.LoginModelCallback;

import java.util.Map;

public interface LoginContract {
    interface Model{
        // 请求发送并处理结果
        void onLoginOrRegister(String url, Map<String, String> account, LoginModelCallback callback);
    }

    interface View{
        // 登录或注册成功
        void loginOrRegisterSuccess();
        // 登录或注册失败
        void loginOrRegisterFail();
    }

    interface Presenter{
        // 发送登录或者注册请求
        void sendHttpRequest(String url, Map<String, String> account);
        // 检查用户填入信息是否符合要求
        boolean checkEtInfo(String username, String password, String passwordAgain, TextView tipTv);
    }
}
