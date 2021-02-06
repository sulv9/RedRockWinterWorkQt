package com.aefottt.redrockwinterworkqt.view.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aefottt.redrockwinterworkqt.R;
import com.aefottt.redrockwinterworkqt.contract.ProjectContract;
import com.aefottt.redrockwinterworkqt.data.adapter.ArticleRecyclerAdapter;
import com.aefottt.redrockwinterworkqt.data.bean.ArticleBean;
import com.aefottt.redrockwinterworkqt.data.bean.TreeBean;
import com.aefottt.redrockwinterworkqt.presenter.ProjectPresenter;
import com.aefottt.redrockwinterworkqt.util.Utility;
import com.aefottt.redrockwinterworkqt.view.my.MyApplication;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Objects;

public class ProjectFragment extends Fragment implements ProjectContract.view {
    private static final String URL_PROJECT_TREE = "https://www.wanandroid.com/project/tree/json";
    private static final String URL_PROJECT_ARTICLE_HEAD = "https://www.wanandroid.com/project/list/";
    private static final String URL_PROJECT_ARTICLE_TAIL = "/json?cid=";
    private int mCurrentPage = 1;
    private int mCurrentCid;

    private static final int WHAT_GET_PROJECT_TREE = 0;
    private static final int WHAT_GET_PROJECT_ARTICLE = 1;
    private static final int WHAT_GET_MORE_ARTICLE_DATA = 2;
    private static final String BUNDLE_KEY_PROJECT_TREE_DATA = "ProjectTreeData";
    private static final String BUNDLE_KEY_PROJECT_ARTICLE_DATA = "ProjectArticleData";

    private TabLayout tb;
    private RecyclerView rv;

    private final ArrayList<ArticleBean> articleList = new ArrayList<>();
    private ArticleRecyclerAdapter adapter;
    private LinearLayoutManager manager;

    private ProjectPresenter mPresenter;

    private TabLayout.Tab lastTab;

    private View refreshView;
    private View footerView;
    private TextView tvRefresh;
    private ImageView ivRefresh;
    private int targetTop;
    private ObjectAnimator anim;
    private int lastY;
    private final float mDragF = 0.4F;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project, container, false);
        tb = view.findViewById(R.id.tb_project);
        rv = view.findViewById(R.id.rv_project);
        manager = new LinearLayoutManager(MyApplication.getContext());
        rv.setLayoutManager(manager);

        // 初始化View
        refreshView = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.item_rv_refresh, rv, false);
        footerView = LayoutInflater.from(getActivity()).inflate(R.layout.item_rv_footer, rv, false);
        tvRefresh = refreshView.findViewById(R.id.tv_rv_refresh);
        ivRefresh = refreshView.findViewById(R.id.iv_rv_refresh);
        targetTop = -Utility.dpToPx(80);
        // 初始化动画
        anim = ObjectAnimator.ofFloat(refreshView, "qt", 0.0f, 1.0f);

        // 请求网络数据
        mPresenter = new ProjectPresenter();
        mPresenter.attachView(this);
        mPresenter.onLoadTreeData(URL_PROJECT_TREE);

        // 设置Recycler下滑刷新和上拉加载
        setRecyclerSwipe();

        // 设置Tab点击事件
        setTabClickEvent();

        return view;
    }

    private void setTabClickEvent() {
        tb.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 改变Tab颜色
                TextView lastTv = Objects.requireNonNull(lastTab.getCustomView()).findViewById(R.id.tv_tab_tree);
                lastTv.setTextColor(Color.parseColor("#8A000000"));
                TextView clickTv = Objects.requireNonNull(tab.getCustomView()).findViewById(R.id.tv_tab_tree);
                clickTv.setTextColor(getResources().getColor(R.color.white));
                lastTab = tab;

                // 加载相应的文章数据
                if (tab.getTag() != null){
                    mCurrentCid = (int) tab.getTag();
                }
                mCurrentPage = 1;
                mPresenter.onLoadArticleData(URL_PROJECT_ARTICLE_HEAD + mCurrentPage + URL_PROJECT_ARTICLE_TAIL + mCurrentCid);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == WHAT_GET_PROJECT_TREE) { // 加载Tab目录数据
                loadTabTreeData(message.getData().getParcelableArrayList(BUNDLE_KEY_PROJECT_TREE_DATA));
            }else if (message.what == WHAT_GET_PROJECT_ARTICLE){ //加载文章数据
                loadArticleData(message.getData().getParcelableArrayList(BUNDLE_KEY_PROJECT_ARTICLE_DATA));
            }else if (message.what == WHAT_GET_MORE_ARTICLE_DATA){ //加载更多
                loadMoreArticleData(message.getData().getParcelableArrayList(BUNDLE_KEY_PROJECT_ARTICLE_DATA));
            }
            return false;
        }
    });

    /**
     * 加载更多文章数据
     * @param articleBeans 新网络请求的文章数据
     */
    private void loadMoreArticleData(ArrayList<ArticleBean> articleBeans) {
        int startPosition = 0;
        if (articleList.size() > 0){
            startPosition = articleList.size() - 1;
        }
        articleList.addAll(articleBeans);
        adapter.notifyItemRangeChanged(startPosition, articleBeans.size());
    }

    /**
     * 加载网络请求的文章数据到Recycler上
     * @param articleBeans 文章数据源
     */
    private void loadArticleData(ArrayList<ArticleBean> articleBeans) {
        articleList.clear();
        articleList.addAll(articleBeans);
        adapter = new ArticleRecyclerAdapter(articleList);
        adapter.setRefreshView(refreshView);
        adapter.setFooterView(footerView);
        rv.setAdapter(adapter);
    }

    /**
     * 加载网络请求的目录数据到TabLayout上
     * @param treeList 目录数据源
     */
    private void loadTabTreeData(ArrayList<TreeBean> treeList) {
        for (int i = 0; i < treeList.size(); i++) {
            TreeBean treeBean = treeList.get(i);
            TabLayout.Tab tab = tb.newTab();
            View tabView = LayoutInflater.from(getActivity()).inflate(R.layout.item_tab_tree, null);
            TextView tv = tabView.findViewById(R.id.tv_tab_tree);
            tv.setText(treeBean.getName());
            if (i == 0) {
                tv.setSelected(true);
                tv.setTextColor(this.getResources().getColor(R.color.white));
                lastTab = tab;
                // 默认加载第一页目录数据
                mCurrentCid = treeBean.getId();
                mPresenter.onLoadArticleData(URL_PROJECT_ARTICLE_HEAD + mCurrentPage + URL_PROJECT_ARTICLE_TAIL + mCurrentCid);
            }
            tab.setCustomView(tabView);
            tab.setTag(treeBean.getId()); //记录当前目录对应的cid，以便加载文章
            tb.addTab(tab);
        }
    }

    @Override
    public void getTreeDataSuccess(ArrayList<TreeBean> treeBeans) {
        Message message = new Message();
        message.what = WHAT_GET_PROJECT_TREE;
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BUNDLE_KEY_PROJECT_TREE_DATA, treeBeans);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    @Override
    public void getArticleDataSuccess(ArrayList<ArticleBean> articleBeans) {
        Message message = new Message();
        message.what = WHAT_GET_PROJECT_ARTICLE;
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BUNDLE_KEY_PROJECT_ARTICLE_DATA, articleBeans);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    @Override
    public void getMoreArticleDataSuccess(ArrayList<ArticleBean> articleBeans) {
        Message message = new Message();
        message.what = WHAT_GET_MORE_ARTICLE_DATA;
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BUNDLE_KEY_PROJECT_ARTICLE_DATA, articleBeans);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setRecyclerSwipe() {
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
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_DRAGGING){
                    int lastItem = manager.findLastCompletelyVisibleItemPosition();
                    if (lastItem == manager.getItemCount() - 1){
                        footerView.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(() -> {
                            mCurrentPage++;
                            mPresenter.onLoadMoreArticleData(URL_PROJECT_ARTICLE_HEAD + mCurrentPage + URL_PROJECT_ARTICLE_TAIL + mCurrentCid);
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
        ivRefresh.setBackgroundResource(R.mipmap.load);
        // 开始刷新
        mCurrentPage = 1;
        mPresenter.onLoadArticleData(URL_PROJECT_ARTICLE_HEAD + mCurrentPage + URL_PROJECT_ARTICLE_TAIL + mCurrentCid);
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

    @Override
    public void showLodaing() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onError(Exception e) {

    }

}