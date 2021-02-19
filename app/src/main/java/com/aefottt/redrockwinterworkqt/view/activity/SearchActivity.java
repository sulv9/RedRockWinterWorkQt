package com.aefottt.redrockwinterworkqt.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aefottt.redrockwinterworkqt.R;
import com.aefottt.redrockwinterworkqt.base.BaseActivity;
import com.aefottt.redrockwinterworkqt.data.adapter.ArticleRecyclerAdapter;
import com.aefottt.redrockwinterworkqt.data.bean.ArticleBean;
import com.aefottt.redrockwinterworkqt.model.SearchModel;
import com.aefottt.redrockwinterworkqt.util.Utility;
import com.aefottt.redrockwinterworkqt.view.my.FlowLayout;
import com.aefottt.redrockwinterworkqt.view.my.MyApplication;

import java.util.ArrayList;

public class SearchActivity extends BaseActivity implements View.OnClickListener {
    private static final int WHAT_GET_HOT_NAMES = 0;
    private static final int WHAT_GET_SEARCH_PAGES = 1;
    private static final int WHAT_LOAD_MORE = 2;
    private static final int WHAT_REFRESH_DATA = 3;
    private static final String BUNDLE_KEY_HOT_NAMES = "HotNames";
    private static final String BUNDLE_KEY_SEARCH_DATA = "SearchData";

    private static final String URL_HOT = "https://www.wanandroid.com//hotkey/json";
    private static final String URL_SEARCH_HEAD = "https://www.wanandroid.com/article/query/";
    private int mCurrentPage = 0;
    private static final String URL_SEARCH_TAIL = "/json";
    private String searchContent;

    private EditText etSearch;
    private LinearLayout llHot, llResult, llSearching;
    private FlowLayout flHot;
    private RecyclerView rv;
    private SearchModel searchModel;
    private TextView tvSearchNon;

    private LinearLayoutManager manager;
    private ArticleRecyclerAdapter adapter;
    private final ArrayList<ArticleBean> articleList = new ArrayList<>();

    private View refreshView;
    private View footerView;
    private TextView tvRefresh;
    private ImageView ivRefresh;
    private int targetTop;
    private ObjectAnimator anim;
    private int lastY;
    private final float mDragF = 0.4F;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 开启动画特征
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setStatusBarTextBlack();
        setContentView(R.layout.activity_search);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        etSearch = findViewById(R.id.et_search);
        ImageView ivSearch = findViewById(R.id.iv_search_go);
        llHot = findViewById(R.id.ll_search_hot);
        llResult = findViewById(R.id.ll_search_result);
        llSearching = findViewById(R.id.ll_searching);
        flHot = findViewById(R.id.fl_hot_search);
        rv = findViewById(R.id.rv_search);
        ivSearch.setOnClickListener(this);
        searchModel = new SearchModel();
        tvSearchNon = findViewById(R.id.tv_search_non);
    }

    @Override
    protected void initData() {
        // 添加热搜关键词
        searchModel.getHotFl(URL_HOT, new SearchModel.getHotNamesCallback() {
            @Override
            public void onSuccess(ArrayList<String> hotNames) {
                Message message = new Message();
                message.what = WHAT_GET_HOT_NAMES;
                Bundle bundle = new Bundle();
                bundle.putStringArrayList(BUNDLE_KEY_HOT_NAMES, hotNames);
                message.setData(bundle);
                handler.sendMessage(message);
            }

            @Override
            public void onError(Exception e) {
                Log.e("getHotNamesError", e.toString());
            }
        });
    }

    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == WHAT_GET_HOT_NAMES){
                ArrayList<String> hotNames = message.getData().getStringArrayList(BUNDLE_KEY_HOT_NAMES);
                for (String name : hotNames){
                    TextView tv = (TextView) LayoutInflater.from(MyApplication.getContext())
                            .inflate(R.layout.item_flow_layout, flHot, false);
                    tv.setText(name);
                    flHot.addView(tv);
                }
            }else if (message.what == WHAT_GET_SEARCH_PAGES){
                manager = new LinearLayoutManager(SearchActivity.this);
                llSearching.setVisibility(View.GONE);
                llResult.setVisibility(View.VISIBLE);
                rv.setLayoutManager(manager);
                ArrayList<ArticleBean> articleBeans = message.getData().getParcelableArrayList(BUNDLE_KEY_SEARCH_DATA);
                if (articleBeans.size() == 0){
                    rv.setVisibility(View.GONE);
                    tvSearchNon.setVisibility(View.VISIBLE);
                }else {
                    rv.setVisibility(View.VISIBLE);
                    tvSearchNon.setVisibility(View.GONE);
                    // 初始化View
                    refreshView = LayoutInflater.from(SearchActivity.this).inflate(R.layout.item_rv_refresh, rv, false);
                    footerView = LayoutInflater.from(SearchActivity.this).inflate(R.layout.item_rv_footer, rv, false);
                    tvRefresh = refreshView.findViewById(R.id.tv_rv_refresh);
                    ivRefresh = refreshView.findViewById(R.id.iv_rv_refresh);
                    targetTop = -Utility.dpToPx(80);
                    // 初始化动画
                    anim = ObjectAnimator.ofFloat(refreshView, "qt", 0.0f, 1.0f);
                    // 添加数据
                    articleList.clear();
                    articleList.addAll(articleBeans);
                    adapter = new ArticleRecyclerAdapter(articleList);
                    adapter.setRefreshView(refreshView);
                    adapter.setFooterView(footerView);
                    rv.setAdapter(adapter);
                    setRecycler();
                }
            }else if (message.what == WHAT_LOAD_MORE){
                ArrayList<ArticleBean> articleBeans = message.getData().getParcelableArrayList(BUNDLE_KEY_SEARCH_DATA);
                if (articleBeans.size() == 0){
                    footerView.setVisibility(View.GONE);
                }else {
                    int startPos = articleList.size() - 1;
                    articleList.addAll(articleBeans);
                    adapter.notifyItemRangeChanged(startPos, articleList.size() - startPos + 1);
                }
            }else if (message.what == WHAT_REFRESH_DATA){
                ArrayList<ArticleBean> articleBeans = message.getData().getParcelableArrayList(BUNDLE_KEY_SEARCH_DATA);
                articleList.clear();
                mCurrentPage = 0;
                articleList.addAll(articleBeans);
                adapter.notifyDataSetChanged();
                tvRefresh.setText("刷新成功！");
                ivRefresh.setBackgroundResource(R.mipmap.refresh_success);
                new Handler().postDelayed(()-> animRefreshView(0, targetTop), 800);
            }
            return false;
        }
    });

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_search_go){
            searchContent = etSearch.getText().toString();
            if (TextUtils.isEmpty(searchContent)){
                Toast.makeText(this, "搜索内容不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            llHot.setVisibility(View.GONE);
            llResult.setVisibility(View.GONE);
            llSearching.setVisibility(View.VISIBLE);
            new Handler().postDelayed(()->{
                mCurrentPage = 0;
                searchModel.getSearchPage(URL_SEARCH_HEAD + mCurrentPage + URL_SEARCH_TAIL, searchContent, new SearchModel.getSearchPageCallback() {
                    @Override
                    public void onSuccess(ArrayList<ArticleBean> articleBeans) {
                        Message message = new Message();
                        message.what = WHAT_GET_SEARCH_PAGES;
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList(BUNDLE_KEY_SEARCH_DATA, articleBeans);
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("getHotNamesError", e.toString());
                    }
                });
            }, 1000);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setRecycler() {
        // 下拉刷新
        rv.setOnTouchListener((v, e) -> {
            int y = (int) e.getRawY();
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastY = y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    // 计算坐标偏移量
                    int offset = y - lastY;
                    if (manager != null && lastY != 0) {
                        // 如果第一条数据在屏幕中完全可见的话
                        if (manager.findFirstCompletelyVisibleItemPosition() == 0) {
                            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) refreshView.getLayoutParams();
                            // 计算超出的滑动距离
                            int distance = (int) (lp.topMargin + offset * mDragF);
                            // 如果向上滑动了，就开始渐渐的显示RefreshView
                            if (distance >= targetTop) {
                                lp.topMargin = distance;
                                refreshView.setLayoutParams(lp);
                                refreshView.invalidate();
                            }
                            // 如果RefreshView已经完全显示出来了
                            if (distance > 0) {
                                // 开始释放刷新相关的操作
                                releaseToRefresh();
                            }
                        } else {
                            // 如果还没有完全显示出来，开始下拉刷新的相关操作
                            pullToRefresh();
                        }
                    }
                    // 记录下此时的y坐标
                    lastY = y;
                    break;
                case MotionEvent.ACTION_UP:
                    RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) refreshView.getLayoutParams();
                    // 如果RefreshView距离顶部的距离大于0，则松手后可以刷新
                    if (lp.topMargin > 0) {
                        animRefreshView(lp.topMargin, 0);
                        footerView.setVisibility(View.GONE);
                        refreshing();
                    } else {
                        // 否则回到隐藏状态
                        animRefreshView(lp.topMargin, targetTop);
                    }
                    break;
            }
            return false;
        });
        // 上滑加载更多
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_DRAGGING){
                    int lastItem = manager.findLastCompletelyVisibleItemPosition();
                    if (lastItem == manager.getItemCount() - 1 && articleList.size() > 5 && !TextUtils.isEmpty(searchContent)){
                        footerView.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(() -> {
                            mCurrentPage++;
                            searchModel.getSearchPage(URL_SEARCH_HEAD + mCurrentPage + URL_SEARCH_TAIL, searchContent, new SearchModel.getSearchPageCallback() {
                                @Override
                                public void onSuccess(ArrayList<ArticleBean> articleBeans) {
                                    Message message = new Message();
                                    message.what = WHAT_LOAD_MORE;
                                    Bundle bundle = new Bundle();
                                    bundle.putParcelableArrayList(BUNDLE_KEY_SEARCH_DATA, articleBeans);
                                    message.setData(bundle);
                                    handler.sendMessage(message);
                                }

                                @Override
                                public void onError(Exception e) {
                                    Log.e("getMoreSearchPagesError", e.toString());
                                }
                            });
                        }, 600);
                    }
                }
            }
        });
    }

    /**
     * 用动画效果从起始位置回到终止位置
     *
     * @param startHeight 起始高度
     * @param endHeight   终止高度
     */
    public void animRefreshView(int startHeight, int endHeight) {
        anim.setDuration(1000);
        anim.start();
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float val = (float) valueAnimator.getAnimatedValue();
                RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) refreshView.getLayoutParams();
                lp.topMargin = (int) (startHeight + val * (endHeight - startHeight));
                refreshView.setLayoutParams(lp);
                refreshView.invalidate();
            }
        });
    }

    /**
     * 开始刷新的相关操作
     */
    private void refreshing() {
        tvRefresh.setText("正在刷新...");
        ivRefresh.setBackgroundResource(R.mipmap.load_refresh);
        // 开始刷新
        if (TextUtils.isEmpty(searchContent)){
            return;
        }
        searchModel.getSearchPage(URL_SEARCH_HEAD + mCurrentPage + URL_SEARCH_TAIL, searchContent, new SearchModel.getSearchPageCallback() {
            @Override
            public void onSuccess(ArrayList<ArticleBean> articleBeans) {
                Message message = new Message();
                message.what = WHAT_REFRESH_DATA;
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(BUNDLE_KEY_SEARCH_DATA, articleBeans);
                message.setData(bundle);
                handler.sendMessage(message);
            }

            @Override
            public void onError(Exception e) {
                Log.e("refreshSearchPageError", e.toString());
            }
        });
    }

    /**
     * 释放刷新的相关操作
     */
    private void releaseToRefresh() {
        tvRefresh.setText("释放刷新...");
        ivRefresh.setBackgroundResource(R.mipmap.pull_up);
    }

    /**
     * 下拉刷新的相关操作
     */
    private void pullToRefresh() {
        tvRefresh.setText("下拉刷新...");
        ivRefresh.setBackgroundResource(R.mipmap.pull_down);
    }
}