package com.aefottt.redrockwinterworkqt.view.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.aefottt.redrockwinterworkqt.R;
import com.aefottt.redrockwinterworkqt.base.BaseActivity;

/**
 * 启动页
 * 加载三个动画
 */

public class SplashActivity extends BaseActivity {
    ImageView iv;
    TextView tv;

    ObjectAnimator ivAnimator, tvAnimator;
    AnimatorSet animatorSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
        initData();
        animatorSet.start();
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 1200);
    }

    @Override
    protected void initView() {
        iv = findViewById(R.id.iv_splash);
        tv = findViewById(R.id.tv_splash);
    }

    @Override
    protected void initData() {

        Path ivPath = new Path();
        ivPath.moveTo(0, 0);
        ivPath.lineTo((float)1.5, (float)1.5);
        ivPath.lineTo(1, 1);
        ivAnimator = ObjectAnimator.ofFloat(iv, "scaleX", "scaleY", ivPath);

        tvAnimator = ObjectAnimator.ofFloat(tv, "alpha", 0, 1)
                .setDuration(1000);

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(ivAnimator, tvAnimator);
        animatorSet.setDuration(1000);
    }
}