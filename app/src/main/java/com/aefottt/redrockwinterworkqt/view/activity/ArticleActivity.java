package com.aefottt.redrockwinterworkqt.view.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.aefottt.redrockwinterworkqt.R;
import com.aefottt.redrockwinterworkqt.base.BaseActivity;
import com.aefottt.redrockwinterworkqt.util.Utility;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ArticleActivity extends BaseActivity {
    private BottomAppBar bab;
    private WebView wb;
    private FloatingActionButton fab;
    private String url;
    private String articleTitle;
    private LinearLayout llComment;
    private boolean isCommenting;
    private View shadeView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTextBlack();
        // 开启动画特征
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        // 滑动退出
        getWindow().setExitTransition(new Slide());
        setContentView(R.layout.activity_article);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        bab = findViewById(R.id.bab_article);
        wb = findViewById(R.id.wv_article);
        fab = findViewById(R.id.fab_article_back);
        // 获取url
        url = getIntent().getStringExtra("url");
        llComment = findViewById(R.id.ll_article_comment);
        shadeView = findViewById(R.id.view_article_shade);
    }

    @Override
    protected void initData() {
        // 设置ActionBar
        setSupportActionBar(bab);
        // 设置menu的点击事件
        bab.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_bottom_collect_article){
                if (item.getTitle().equals("love")){
                    item.setIcon(R.mipmap.icon_article_love);
                    item.setTitle("dislove");
                }else if (item.getTitle().equals("dislove")){
                    item.setIcon(R.mipmap.icon_article_dislove);
                    item.setTitle("love");
                }
            }else if (id == R.id.menu_bottom_article_share){
                // 分享
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TITLE, articleTitle);
                sendIntent.putExtra(Intent.EXTRA_TEXT, articleTitle+": "+url);
                startActivity(Intent.createChooser(sendIntent, "WanAndroid-Share"));
            }else if (id == R.id.menu_bottom_comment){
                //TODO 弹出评论界面
                if (isCommenting){
                    hideCommentView();
                }else {
                    showCommentView();
                }
            }
            return false;
        });
        // 设置fab的点击事件
        fab.setOnClickListener(v->{
            finish();
        });
        // 设置fab的长按事件
        fab.setOnLongClickListener(v->{
            if (bab.getFabAlignmentMode() == BottomAppBar.FAB_ALIGNMENT_MODE_CENTER){
                bab.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
            }else if (bab.getFabAlignmentMode() == BottomAppBar.FAB_ALIGNMENT_MODE_END){
                bab.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
            }
            return true;
        });
        // 配置WebView，加载网页
        wb.getSettings().setJavaScriptEnabled(true);
        wb.setWebViewClient(new WebViewClient());
        wb.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                articleTitle = title;
            }
        });
        wb.loadUrl(url);
        // 设置shadeView点击事件
        shadeView.setOnClickListener(v->{
            if (isCommenting){
                // 评论界面消失
                hideCommentView();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bottom_app_bar, menu);
        return true;
    }

    /**
     * 展示评论界面
     */
    private void showCommentView(){
        // 设置背景阴影
        isCommenting = true;
        shadeView.setVisibility(View.VISIBLE);
        // 设置评论View弹起的动画
        ObjectAnimator anim = ObjectAnimator.ofFloat(llComment, "bottomSheet", 0, 1.0f);
        anim.setDuration(600);
        anim.start();
        anim.addUpdateListener(valueAnimator -> {
            float val = (float) valueAnimator.getAnimatedValue();
            CoordinatorLayout.LayoutParams llLp = (CoordinatorLayout.LayoutParams) llComment.getLayoutParams();
            CoordinatorLayout.LayoutParams babLp = (CoordinatorLayout.LayoutParams) bab.getLayoutParams();
            llLp.bottomMargin = (int) (-Utility.dpToPx(600) + Utility.dpToPx(600) * val);
            babLp.bottomMargin = (int) (Utility.dpToPx(600) * val);
            llComment.setLayoutParams(llLp);
            bab.setLayoutParams(babLp);
            llComment.invalidate();
            bab.invalidate();
        });
    }

    /**
     * 隐藏评论界面
     */
    private void hideCommentView(){
        // 设置背景阴影
        isCommenting = false;
        shadeView.setVisibility(View.GONE);
        // 设置评论View返回的动画
        ObjectAnimator anim = ObjectAnimator.ofFloat(llComment, "bottomSheet", 1.0f, 0);
        anim.setDuration(600);
        anim.start();
        anim.addUpdateListener(valueAnimator -> {
            float val = (float) valueAnimator.getAnimatedValue();
            CoordinatorLayout.LayoutParams llLp = (CoordinatorLayout.LayoutParams) llComment.getLayoutParams();
            CoordinatorLayout.LayoutParams babLp = (CoordinatorLayout.LayoutParams) bab.getLayoutParams();
            llLp.bottomMargin = (int) (-Utility.dpToPx(600) + Utility.dpToPx(600) * val);
            babLp.bottomMargin = (int) (Utility.dpToPx(600) * val);
            llComment.setLayoutParams(llLp);
            bab.setLayoutParams(babLp);
            llComment.invalidate();
            bab.invalidate();
        });
    }
}