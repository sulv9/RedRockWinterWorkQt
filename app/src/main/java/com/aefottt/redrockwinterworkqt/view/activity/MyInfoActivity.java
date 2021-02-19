package com.aefottt.redrockwinterworkqt.view.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aefottt.redrockwinterworkqt.R;
import com.aefottt.redrockwinterworkqt.base.BaseActivity;
import com.aefottt.redrockwinterworkqt.contract.MyInfoContract;
import com.aefottt.redrockwinterworkqt.data.adapter.ArticleRecyclerAdapter;
import com.aefottt.redrockwinterworkqt.data.bean.ArticleBean;
import com.aefottt.redrockwinterworkqt.data.bean.CollectWebBean;
import com.aefottt.redrockwinterworkqt.presenter.MyInfoPresenter;
import com.aefottt.redrockwinterworkqt.util.PrefUtil;
import com.aefottt.redrockwinterworkqt.util.Utility;
import com.aefottt.redrockwinterworkqt.view.my.MyApplication;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MyInfoActivity extends BaseActivity implements MyInfoContract.view {
    private static final int WHAT_GET_COIN_DATA = 0;
    private static final int WHAT_GET_ARTICLE_DATA = 1;
    private static final int WHAT_GET_MORE_ARTICLE_DATA = 2;
    private static final int WHAT_GET_COLLECT_WEB_DATA = 3;
    private static final int WHAT_GET_COIN_LIST_DATA = 4;
    private static final int WHAT_GET_MORE_COIN_LIST_DATA = 5;
    private static final String BUNDLE_KEY_ARTICLE_DATA = "ArticleData";
    private static final String BUNDLE_KEY_COLLECT_WEB_DATA = "CollectWebData";
    private static final String BUNDLE_KEY_COIN_LIST_DATA = "CoinListData";
    // 积分，列表，排名等网址
    private static final String URL_COIN_AND_RANK = "https://www.wanandroid.com/lg/coin/userinfo/json";
    // 积分获取列表网址
    private static final String URL_COIN_LIST_HEADER = "https://www.wanandroid.com//lg/coin/list/";
    private int mCurrentCoinListPage = 1;
    private static final String URL_COIN_LIST_TAIL = "/json";
    // 收藏文章网址
    private static final String URL_COLLECT_ARTICLE_LIST_HEADER = "https://www.wanandroid.com/lg/collect/list/";
    private int mCurrentArticleList = 0;
    private static final String URL_COLLECT_ARTICLE_LIST_TAIL = "/json";
    // 收藏网站网址
    private static final String URL_COLLECT_WEB = "https://www.wanandroid.com/lg/collect/usertools/json";

    private static final String[] infoTitles = new String[]{"文章", "网站", "积分"};
    private TextView tvUsername, tvRank, tvCoinCount, tvLevel, loginOut;
    private TabLayout tbInfo;
    private RecyclerView rvInfo;
    private LinearLayoutManager manager;
    private ArticleRecyclerAdapter adapter;
    private final ArrayList<ArticleBean> articleList = new ArrayList<>();
    private MyInfoPresenter mPresenter;

    private View refreshView;
    private View footerView;
    private TextView tvRefresh;
    private ImageView ivRefresh;
    private int targetTop;
    private ObjectAnimator anim;
    private int lastY;
    private final float mDragF = 0.4F;

    enum RECYCLER_TYPE{
        ARTICLE,
        WEB,
        COIN
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 开启动画特征
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setFullScreen();
        setStatusBarTextBlack();
        setContentView(R.layout.activity_my_info);
        initView();
        initData();
        addListener();
    }

    /**
     * 添加TabLayout点击监听事件和按钮点击事件
     */
    private void addListener() {
        tbInfo.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String tabTitle = (String) tab.getText();
                if (infoTitles[0].equals(tabTitle)) {
                    // TODO 加载文章
                    mPresenter.loadCollectArticleData(URL_COLLECT_ARTICLE_LIST_HEADER + mCurrentArticleList + URL_COLLECT_ARTICLE_LIST_TAIL);
                    mCurrentCoinListPage = 1;
                } else if (infoTitles[1].equals(tabTitle)) {
                    // TODO 加载网站
                    mPresenter.loadCollectWebData(URL_COLLECT_WEB);
                    mCurrentArticleList = 0;
                    mCurrentCoinListPage = 1;
                } else if ((infoTitles[2].equals(tabTitle))) {
                    // TODO 加载积分获取列表
                    mPresenter.loadCollectCoinData(URL_COIN_LIST_HEADER + mCurrentCoinListPage + URL_COIN_LIST_TAIL);
                    mCurrentArticleList = 0;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        loginOut.setOnClickListener(view -> {
            PrefUtil.getInstance().clear(Utility.FILE_NAME_USER_INFO);
            finish();
        });
    }

    @Override
    protected void initView() {
        ImageView blurBg = findViewById(R.id.iv_info_blur_bg);
        // 实现高斯模糊
        Bitmap bitmap = ((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.mipmap.photo_view, null)).getBitmap();
        blurBg.setImageBitmap(Utility.blurBitmap(bitmap, 24));
        tvUsername = findViewById(R.id.tv_info_username);
        tvCoinCount = findViewById(R.id.tv_info_coin_count);
        tvRank = findViewById(R.id.tv_info_rank);
        tvLevel = findViewById(R.id.tv_info_level);
        tbInfo = findViewById(R.id.tb_info);
        rvInfo = findViewById(R.id.rv_info);
        loginOut = findViewById(R.id.tv_login_out);
    }

    @Override
    protected void initData() {
        // 加载用户名称
        tvUsername.setText((String) PrefUtil.getInstance().get(Utility.FILE_NAME_USER_INFO, Utility.KEY_USERNAME, ""));
        for (String title : infoTitles) {
            tbInfo.addTab(tbInfo.newTab().setText(title));
        }
        // 初始化Recycler布局管理器
        manager = new LinearLayoutManager(MyApplication.getContext());
        rvInfo.setLayoutManager(manager);
        // 初始化底部加载布局
        refreshView = LayoutInflater.from(this).inflate(R.layout.item_rv_refresh, rvInfo, false);
        footerView = LayoutInflater.from(this).inflate(R.layout.item_rv_footer, rvInfo, false);
        tvRefresh = refreshView.findViewById(R.id.tv_rv_refresh);
        ivRefresh = refreshView.findViewById(R.id.iv_rv_refresh);
        targetTop = -Utility.dpToPx(80);
        // 初始化动画
        anim = ObjectAnimator.ofFloat(refreshView, "qt", 0.0f, 1.0f);
        // TODO 加载初始信息
        mPresenter = new MyInfoPresenter();
        mPresenter.attachView(this);
        mPresenter.loadInitData(URL_COIN_AND_RANK, URL_COLLECT_ARTICLE_LIST_HEADER + mCurrentArticleList + URL_COLLECT_ARTICLE_LIST_TAIL);
    }

    private final Handler handler = new Handler(new Handler.Callback() {
        @SuppressLint("SetTextI18n")
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == WHAT_GET_COIN_DATA) { //获取积分等信息
                // 加载积分，排名，等级
                int[] obj = (int[]) message.obj;
                tvCoinCount.setText("Coins: " + obj[0]);
                tvLevel.setText("Lv." + obj[1]);
                tvRank.setText("Rank: " + obj[2]);
            } else if (message.what == WHAT_GET_ARTICLE_DATA) { // 获取收藏文章数据
                // 加载收集文章数据
                articleList.clear();
                articleList.addAll(message.getData().getParcelableArrayList(BUNDLE_KEY_ARTICLE_DATA));
                adapter = new ArticleRecyclerAdapter(articleList);
                adapter.setRefreshView(refreshView);
                adapter.setFooterView(footerView);
                rvInfo.setAdapter(adapter);
                addRecyclerSwipeListener(RECYCLER_TYPE.ARTICLE.ordinal());
            } else if (message.what == WHAT_GET_MORE_ARTICLE_DATA) { //加载更多文章数据
                int startPos = articleList.size() - 1;
                ArrayList<ArticleBean> articleBeans = message.getData().getParcelableArrayList(BUNDLE_KEY_ARTICLE_DATA);
                articleList.addAll(articleBeans);
                adapter.notifyItemRangeChanged(startPos, articleBeans.size());
            } else if (message.what == WHAT_GET_COLLECT_WEB_DATA) { //获取收藏网站列表
                articleList.clear();
                articleList.addAll(message.getData().getParcelableArrayList(BUNDLE_KEY_COLLECT_WEB_DATA));
                adapter = new ArticleRecyclerAdapter(articleList);
                adapter.setRefreshView(refreshView);
                adapter.setFooterView(null);
                rvInfo.setAdapter(adapter);
                addRecyclerSwipeListener(RECYCLER_TYPE.WEB.ordinal());
            } else if (message.what == WHAT_GET_COIN_LIST_DATA) { //加载积分获取列表数据
                articleList.clear();
                articleList.addAll(message.getData().getParcelableArrayList(BUNDLE_KEY_COIN_LIST_DATA));
                adapter = new ArticleRecyclerAdapter(articleList);
                adapter.setRefreshView(refreshView);
                adapter.setFooterView(footerView);
                rvInfo.setAdapter(adapter);
                addRecyclerSwipeListener(RECYCLER_TYPE.COIN.ordinal());
            } else if (message.what == WHAT_GET_MORE_COIN_LIST_DATA) { //加载更多积分获取列表
                int startPos = articleList.size() - 1;
                ArrayList<ArticleBean> coinListBeans = message.getData().getParcelableArrayList(BUNDLE_KEY_COIN_LIST_DATA);
                articleList.addAll(coinListBeans);
                adapter.notifyItemRangeChanged(startPos, coinListBeans.size());
            }
            return false;
        }
    });

    /**
     * 添加Recycler滑动监听事件，实现上滑加载更多
     */
    @SuppressLint("ClickableViewAccessibility")
    private void addRecyclerSwipeListener(int type) {
        // 下拉刷新
        rvInfo.setOnTouchListener((v, e) -> {
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
                        refreshing(type);
                    } else {
                        // 否则回到隐藏状态
                        animRefreshView(lp.topMargin, targetTop);
                    }
                    break;
            }
            return false;
        });
        if (type == RECYCLER_TYPE.WEB.ordinal()){
            return;
        }
        rvInfo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    int lastItem = manager.findLastCompletelyVisibleItemPosition();
                    if (lastItem == manager.getItemCount() - 1) {
                        footerView.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(() -> {
                            if (type == RECYCLER_TYPE.ARTICLE.ordinal()) {
                                mCurrentArticleList++;
                                mPresenter.loadMoreArticleData(URL_COLLECT_ARTICLE_LIST_HEADER + mCurrentArticleList + URL_COLLECT_ARTICLE_LIST_TAIL);
                            } else if (type == RECYCLER_TYPE.COIN.ordinal()){
                                mCurrentCoinListPage++;
                                mPresenter.loadMoreCoinListData(URL_COIN_LIST_HEADER + mCurrentCoinListPage + URL_COIN_LIST_TAIL);
                            }
                        }, 600);
                    }
                }
            }
        });
    }

    @Override
    public void getCoinCountAndRankInfo(int coinCount, int level, int rank) {
        int[] obj = {coinCount, level, rank};
        Message message = Message.obtain(handler, WHAT_GET_COIN_DATA, obj);
        message.sendToTarget();
    }

    @Override
    public void getCollectArticleList(ArrayList<ArticleBean> articleBeans) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BUNDLE_KEY_ARTICLE_DATA, articleBeans);
        message.what = WHAT_GET_ARTICLE_DATA;
        message.setData(bundle);
        handler.sendMessage(message);
    }

    @Override
    public void getCollectWebList(ArrayList<CollectWebBean> collectWebBeans) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BUNDLE_KEY_COLLECT_WEB_DATA, collectWebBeans);
        message.what = WHAT_GET_COLLECT_WEB_DATA;
        message.setData(bundle);
        handler.sendMessage(message);
    }

    @Override
    public void getCoinList(ArrayList<CollectWebBean> collectCoinBeans) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BUNDLE_KEY_COIN_LIST_DATA, collectCoinBeans);
        message.what = WHAT_GET_COIN_LIST_DATA;
        message.setData(bundle);
        handler.sendMessage(message);
    }

    @Override
    public void getMoreArticleData(ArrayList<ArticleBean> articleBeans) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BUNDLE_KEY_ARTICLE_DATA, articleBeans);
        message.setData(bundle);
        message.what = WHAT_GET_MORE_ARTICLE_DATA;
        handler.sendMessage(message);
    }

    @Override
    public void getMoreCoinListData(ArrayList<CollectWebBean> collectCoinBeans) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BUNDLE_KEY_COIN_LIST_DATA, collectCoinBeans);
        message.what = WHAT_GET_MORE_COIN_LIST_DATA;
        message.setData(bundle);
        handler.sendMessage(message);
    }

    @Override
    public void showLodaing() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onError(Exception e) {
        Log.e("MyInfoError: ", e.toString());
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
    private void refreshing(int type) {
        tvRefresh.setText("正在刷新...");
        ivRefresh.setBackgroundResource(R.mipmap.load_refresh);
        // 开始刷新
        if (type == RECYCLER_TYPE.ARTICLE.ordinal()){
            mCurrentArticleList = 0;
            mPresenter.loadCollectArticleData(URL_COLLECT_ARTICLE_LIST_HEADER+mCurrentArticleList+URL_COLLECT_ARTICLE_LIST_TAIL);
        }else if (type == RECYCLER_TYPE.WEB.ordinal()){
            mPresenter.loadCollectWebData(URL_COLLECT_WEB);
        }else if (type == RECYCLER_TYPE.COIN.ordinal()){
            mCurrentCoinListPage = 1;
            mPresenter.loadCollectCoinData(URL_COIN_LIST_HEADER+mCurrentCoinListPage+URL_COIN_LIST_TAIL);
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}