package com.aefottt.redrockwinterworkqt.presenter;

import android.text.TextUtils;
import android.widget.TextView;

import com.aefottt.redrockwinterworkqt.contract.LoginContract;
import com.aefottt.redrockwinterworkqt.model.LoginModel;
import com.aefottt.redrockwinterworkqt.view.activity.LoginActivity;

import java.util.Map;

public class LoginPresenter implements LoginContract.Presenter {
    private final LoginActivity mView;
    private final LoginModel model;

    public LoginPresenter(LoginActivity view) {
        mView = view;
        model = new LoginModel();
    }

    @Override
    public void sendHttpRequest(String url, Map<String, String> account) {
        if (mView == null){
            return;
        }
        model.onLoginOrRegister(url, account, isSuccess -> {
            if (isSuccess){
                mView.loginOrRegisterSuccess();
            }else {
                mView.loginOrRegisterFail();
            }
        });
    }

    /**
     * 检查输入信息是否符合基本要求
     * @param username 输入的用户名
     * @param password 第一次输入密码
     * @param passwordAgain 第二次输入密码
     * @param tipTv 提示信息
     * @return true or false
     */
    @Override
    public boolean checkEtInfo(String username, String password, String passwordAgain, TextView tipTv) {
        if (TextUtils.isEmpty(username)){
            tipTv.setText("用户名不能为空哦");
            return false;
        }
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordAgain)){
            tipTv.setText("密码不能为空哦");
            return false;
        }
        if (!password.equals(passwordAgain)){
            tipTv.setText("两次密码输入需要相同哦");
            return false;
        }
        return true;
    }
}
