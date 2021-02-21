package com.aefottt.redrockwinterworkqt.view.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aefottt.redrockwinterworkqt.R;
import com.aefottt.redrockwinterworkqt.util.Utility;

@SuppressLint("ClickableViewAccessibility")
public class WebActivity extends AppCompatActivity {
    private boolean isEditing = false;
    private String getTitle;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置状态栏颜色为粉色
        getWindow().setStatusBarColor(getResources().getColor(R.color.theme));
        setContentView(R.layout.activity_web);
        String url = getIntent().getStringExtra("url");
        EditText et = findViewById(R.id.et_web);
        View shadeView = findViewById(R.id.view_shade);
        ImageView collectWeb = findViewById(R.id.iv_collect_web);
        WebView wb = findViewById(R.id.wv_web);
        wb.getSettings().setJavaScriptEnabled(true);
        wb.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http://") || url.startsWith("https://")){
                    // 直接加载http/https的协议地址
                    view.loadUrl(url);
                    return false;
                }else {
                    // 加载自定义协议地址
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(view.getUrl()));
//                    WebActivity.this.startActivity(intent);
                    return true;
                }
            }
        });
        wb.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                getTitle = title;
                et.setText(title);
            }
        });
        wb.loadUrl(url);
        et.setOnTouchListener((view, motionEvent) -> {
            if (!isEditing){
                isEditing = true;
                shadeView.setVisibility(View.VISIBLE);
                et.setText(url);
            }
            return false;
        });
        shadeView.setOnClickListener(v->{
            if ((isEditing)){
                isEditing = false;
                shadeView.setVisibility(View.GONE);
                et.setText(getTitle);
                Utility.cancelKeyWord();
            }
        });
        collectWeb.setOnClickListener(v->{
            if ("dislove".contentEquals((String)collectWeb.getTag())){
                collectWeb.setBackgroundResource(R.mipmap.icon_article_love);
                collectWeb.setTag("love");
            }else if ("love".contentEquals((String)collectWeb.getTag())){
                collectWeb.setBackgroundResource(R.mipmap.icon_article_dislove);
                collectWeb.setTag("dislove");
            }
        });
    }
}
