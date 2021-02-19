package com.aefottt.redrockwinterworkqt.view.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.aefottt.redrockwinterworkqt.R;
import com.aefottt.redrockwinterworkqt.base.BaseActivity;
import com.aefottt.redrockwinterworkqt.contract.LoginContract;
import com.aefottt.redrockwinterworkqt.presenter.LoginPresenter;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity implements View.OnClickListener, LoginContract.View {
    private static final int WHAT_LOGIN_FAIL = 0;
    private static final int WHAT_LOGIN_SUCCESS = 1;
    private static final String URL_LOGIN = "https://www.wanandroid.com/user/login";
    private static final String URL_REGISTER = "https://www.wanandroid.com/user/register";

    private TextView loginInfo, loginMotto, toRegister;
    private EditText username, password, passwordAgain;
    private Button loginBtn;
    private View loadingBg;
    private ImageView loadSuccess, loadFail;
    private ProgressBar loading;
    private boolean isLogin = true; //true为登陆状态，false为注册状态
    private LoginPresenter loginPresenter;
    private ObjectAnimator showAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 开启动画特征
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        // 分解进入
        getWindow().setEnterTransition(new Explode());
        // 分解退出
        getWindow().setExitTransition(new Explode());
        // 设置状态栏颜色为粉色
        getWindow().setStatusBarColor(Color.parseColor("#ee9ca7"));
        setContentView(R.layout.activity_login);
        loginPresenter = new LoginPresenter(this);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        loginInfo = findViewById(R.id.tv_login_info);
        loginMotto = findViewById(R.id.tv_login_motto);
        toRegister = findViewById(R.id.tv_to_register);
        username = findViewById(R.id.et_login_username);
        password = findViewById(R.id.et_login_password);
        passwordAgain = findViewById(R.id.et_login_password_again);
        loginBtn = findViewById(R.id.btn_login);
        loading = findViewById(R.id.iv_login_loading);
        loadSuccess = findViewById(R.id.iv_login_load_success);
        loadFail = findViewById(R.id.iv_login_load_fail);
        loadingBg = findViewById(R.id.btn_loading_bg);
        toRegister.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        // 设置EditText焦点监听事件，改变Motto
        username.setOnFocusChangeListener((view, b) -> {
            if (b){
                loginMotto.setTextColor(Color.parseColor("#FFFFFFFF"));
                if (isLogin) {
                    loginMotto.setText(R.string.text_login_motto1);
                } else {
                    loginMotto.setText(R.string.text_register_motto1);
                }
            }
        });
        password.setOnFocusChangeListener((view, b) -> {
            if (b){
                loginMotto.setTextColor(Color.parseColor("#FFFFFFFF"));
                if (isLogin) {
                    loginMotto.setText(R.string.text_login_motto2);
                } else {
                    loginMotto.setText(R.string.text_register_motto2);
                }
            }
        });
        passwordAgain.setOnFocusChangeListener((view, b) -> {
            if (b){
                loginMotto.setTextColor(Color.parseColor("#FFFFFFFF"));
                if (isLogin) {
                    loginMotto.setText(R.string.text_login_motto2);
                } else {
                    loginMotto.setText(R.string.text_register_motto3);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_to_register) {
            changeCurrentState();
        } else if (id == R.id.btn_login) {
            String userName = username.getText().toString();
            String passwordInfo = password.getText().toString();
            String passwordAgainInfo = passwordAgain.getText().toString();
            if (loginPresenter.checkEtInfo(userName, passwordInfo, passwordAgainInfo, loginMotto)) {
                Map<String, String> accountMap = new HashMap<>();
                accountMap.put("username", userName);
                accountMap.put("password", passwordInfo);
                showLoading();
                if (isLogin) {
                    //TODO 登录网络请求
                    loginPresenter.sendHttpRequest(URL_LOGIN, accountMap);
                } else {
                    //TODO 注册网络请求
                    loginPresenter.sendHttpRequest(URL_REGISTER, accountMap);
                }
            } else {
                // 输入信息不符合要求，Login按钮变红一秒
                loginMotto.setText("输入信息不符合要求，请重新输入！");
                showError();
                new Handler().postDelayed(this::recoverButtonBg, 1000);
            }
        }
    }

    /**
     * 展示加载中UI
     */
    private void showLoading() {
        // 加载动画，缩小loginBtn
        showAnim = ObjectAnimator.ofFloat(loginBtn, "scaleX", 1.0f, 0.2f);
        showAnim.setDuration(300);
        showAnim.start();
        showAnim.addUpdateListener(valueAnimator -> {
            float val = (float) valueAnimator.getAnimatedValue();
            loginBtn.setScaleX(val);
        });
        new Handler().postDelayed(()->{
            // 动画结束时让loginBtn消失，loadingBg显示，loading显示
            loginBtn.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
            loadingBg.setVisibility(View.VISIBLE);
        }, 300);
    }

    /**
     * 消失加载UI
     */
    private void closeLoading(){
        // 加载动画，恢复loginBtn
        loginBtn.setVisibility(View.VISIBLE);
        // 加载动画，缩小loginBtn
        showAnim = ObjectAnimator.ofFloat(loginBtn, "scaleX", 0.2f, 1.0f);
        showAnim.setDuration(800);
        showAnim.start();
        showAnim.addUpdateListener(valueAnimator -> {
            float val = (float) valueAnimator.getAnimatedValue();
            loginBtn.setScaleX(val);
        });
        // 消失loadingBg和loadSuccess，loadFail
        loadingBg.setVisibility(View.GONE);
        loadSuccess.setVisibility(View.GONE);
        loadFail.setVisibility(View.GONE);
    }

    /**
     * 展示加载错误UI
     */
    private void showError(){
        loginBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.bg_btn_login_error, null));
        loginBtn.setTextColor(Color.parseColor("#FF0000"));
        loginMotto.setTextColor(Color.parseColor("#FF0000"));
    }

    /**
     * 恢复正常Button背景
     */
    private void recoverButtonBg(){
        loginBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.bg_btn_login, null));
        loginBtn.setTextColor(getResources().getColor(R.color.white));
    }

    private final Handler handler = new Handler(message -> {
        if (message.what == WHAT_LOGIN_SUCCESS) { // 展示加载成功
            // loading消失，loadSuccess显示
            loading.setVisibility(View.GONE);
            loadSuccess.setVisibility(View.VISIBLE);
            // 提示登陆或注册成功消息
            loginMotto.setTextColor(Color.parseColor("#FF0000"));
            if (isLogin) {
                loginMotto.setText("登录成功！");
            } else {
                loginMotto.setText("注册成功！");
            }
            // 一秒后进行判断，如果是登录，则跳转信息界面，否则转换为登录界面
            new Handler().postDelayed(() -> {
                if (isLogin){
                    startActivity(new Intent(LoginActivity.this, MyInfoActivity.class));
                    finish();
                }else {
                    changeCurrentState();
                    closeLoading();
                }
            }, 1000);
        } else if (message.what == WHAT_LOGIN_FAIL) { // 展示加载失败
            // loading消失，loadFail显示
            loading.setVisibility(View.GONE);
            loadFail.setVisibility(View.VISIBLE);
            // 提示登录或注册失败
            loginMotto.setTextColor(Color.parseColor("#FF0000"));
            if (isLogin) {
                loginMotto.setText("登录失败，请重试！");
            } else {
                loginMotto.setText("注册失败，请重试！");
            }
            // 一秒后恢复Button
            new Handler().postDelayed(this::closeLoading, 1000);
        }
        return false;
    });

    /**
     * 改变当前状态，如果为登录，则切换为注册，反之亦然
     */
    private void changeCurrentState() {
        username.setText("");
        password.setText("");
        passwordAgain.setText("");
        loginMotto.setTextColor(Color.parseColor("#FFFFFFFF"));
        if (isLogin) {
            // 如果当前为登录状态，则转换为注册状态
            isLogin = false;
            loginInfo.setText(R.string.text_register);
            loginMotto.setText(R.string.text_register_motto1);
            toRegister.setText(R.string.text_to_login);
            loginBtn.setText(R.string.text_register);
        } else {
            // 如果当前为注册状态，则转换为登录状态
            isLogin = true;
            loginInfo.setText(R.string.text_login);
            loginMotto.setText(R.string.text_login_motto1);
            toRegister.setText(R.string.text_to_register);
            loginBtn.setText(R.string.text_login);
        }
    }

    @Override
    public void loginOrRegisterSuccess() {
        Message successMsg = new Message();
        successMsg.what = WHAT_LOGIN_SUCCESS;
        handler.sendMessageDelayed(successMsg, 1500);
    }

    @Override
    public void loginOrRegisterFail() {
        Message failMsg = new Message();
        failMsg.what = WHAT_LOGIN_FAIL;
        handler.sendMessageDelayed(failMsg, 1500);
    }
}