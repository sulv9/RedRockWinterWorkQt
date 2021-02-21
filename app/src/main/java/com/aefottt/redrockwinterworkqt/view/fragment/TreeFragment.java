package com.aefottt.redrockwinterworkqt.view.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aefottt.redrockwinterworkqt.R;
import com.aefottt.redrockwinterworkqt.contract.TreeContract;
import com.aefottt.redrockwinterworkqt.data.adapter.ArticleRecyclerAdapter;
import com.aefottt.redrockwinterworkqt.data.bean.ArticleBean;
import com.aefottt.redrockwinterworkqt.data.bean.TreeChapterBean;
import com.aefottt.redrockwinterworkqt.data.bean.TreeSuperChapterBean;
import com.aefottt.redrockwinterworkqt.presenter.TreePresenter;
import com.aefottt.redrockwinterworkqt.util.Utility;
import com.aefottt.redrockwinterworkqt.view.activity.ArticleActivity;
import com.aefottt.redrockwinterworkqt.view.my.MyApplication;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class TreeFragment extends Fragment implements TreeContract.View {

    // 发送获取目录名称线程的Message
    private static final int WHAT_GET_CHAPTER_NAME = 0;
    // 发送获取文章线程的Message
    private static final int WHAT_GET_ARTICLE = 1;
    // 发送更新文章线程的Message
    private static final int WHAT_REFRESH_ARTICLE = 2;
    // 存储目录数据的Bundle
    private static final String BUNDLE_KEY_CHAPTER_DATA = "ChapterData";
    // 存储文章数据的Bundle
    private static final String BUNDLE_KEY_ARTICLE = "ArticleData";
    // 获取目录的网址
    private static final String URL_TREE = "https://www.wanandroid.com/tree/json";
    // 获取知识体系文章的网址
    private static final String URL_ARTICLE = "https://www.wanandroid.com/article/list/0/json?cid=";

    private TabLayout tbSuperChapter;
    private TabLayout tbChapter;
    public RecyclerView rvTree;

    private final ArrayList<ArticleBean> articleList = new ArrayList<>();
    private ArticleRecyclerAdapter adapter;
    private LinearLayoutManager manager;

    private TabLayout.Tab lastSuperChapterTab, lastChapterTab;

    private TreePresenter mPresenter;

    private View refreshView;
    private TextView tvRefresh;
    private ImageView ivRefresh;
    private int targetTop;
    private ObjectAnimator anim;
    private int lastY;
    private final float mDragF = 0.4F;

    private int currentId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tree, container, false);
        tbSuperChapter = view.findViewById(R.id.tl_tree_super_chapter);
        tbChapter = view.findViewById(R.id.tl_tree_chapter);
        rvTree = view.findViewById(R.id.rv_tree);
        manager = new LinearLayoutManager(MyApplication.getContext());
        rvTree.setLayoutManager(manager);

        // 初始化View
        refreshView = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.item_rv_refresh, null);
        tvRefresh = refreshView.findViewById(R.id.tv_rv_refresh);
        ivRefresh = refreshView.findViewById(R.id.iv_rv_refresh);
        targetTop = -Utility.dpToPx(80);
        // 初始化动画
        anim = ObjectAnimator.ofFloat(refreshView, "qt", 0.0f, 1.0f);

        // 加载目录数据
        mPresenter = new TreePresenter();
        mPresenter.attachView(this);
        mPresenter.onLoadChapter(URL_TREE);

        // 设置tab点击事件
        tbSuperChapter.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 设置上一个Tab文字颜色
                View lastView = lastSuperChapterTab.getCustomView();
                TextView lastTv = lastView.findViewById(R.id.tv_tab_tree);
                lastTv.setTextColor(getResources().getColor(R.color.gray));
                // 设置选择的Tab文字颜色
                View selectedView = tab.getCustomView();
                TextView selectedTv = selectedView.findViewById(R.id.tv_tab_tree);
                selectedTv.setTextColor(getResources().getColor(R.color.white));
                lastSuperChapterTab = tab;
                // 设置对应的二级目录名称
                tbChapter.removeAllTabs();
                ArrayList<TreeChapterBean> chapterBeans = ((TreeSuperChapterBean) tab.getTag()).getChapters();
                for (int i = 0; i < chapterBeans.size(); i++) {
                    TreeChapterBean chapterBean = chapterBeans.get(i);
                    TabLayout.Tab chapterTab = tbChapter.newTab();
                    View chapterTabView = LayoutInflater.from(getActivity()).inflate(R.layout.item_tab_tree, null);
                    TextView chapterTv = chapterTabView.findViewById(R.id.tv_tab_tree);
                    chapterTv.setText(chapterBean.getChapterName());
                    if (i == 0) {
                        chapterTv.setSelected(true);
                        chapterTv.setTextColor(getResources().getColor(R.color.white));
                        lastChapterTab = chapterTab;
                    }
                    chapterTab.setCustomView(chapterTabView);
                    chapterTab.setTag(chapterBean);
                    tbChapter.addTab(chapterTab);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tbChapter.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 设置上一个Tab文字颜色
                View lastView = lastChapterTab.getCustomView();
                TextView lastTv = lastView.findViewById(R.id.tv_tab_tree);
                lastTv.setTextColor(getResources().getColor(R.color.gray));
                // 设置选择的Tab文字颜色
                View selectedView = tab.getCustomView();
                TextView selectedTv = selectedView.findViewById(R.id.tv_tab_tree);
                selectedTv.setTextColor(getResources().getColor(R.color.white));
                lastChapterTab = tab;
                // 加载对应的文章数据到Recycler上
                TreeChapterBean chapterBean = (TreeChapterBean) tab.getTag();
                currentId = chapterBean.getArticleId();
                mPresenter.onLoadArticle(URL_ARTICLE + currentId);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;
    }

    private final Handler handler = new Handler(message -> {
        if (message.what == WHAT_GET_CHAPTER_NAME) { // 获取目录数据
            initTabData(message.getData().getParcelableArrayList(BUNDLE_KEY_CHAPTER_DATA));
        } else if (message.what == WHAT_GET_ARTICLE) { // 获取文章数据
            articleList.clear();
            articleList.addAll(message.getData().getParcelableArrayList(BUNDLE_KEY_ARTICLE));
            initRecycler();
        }else if (message.what == WHAT_REFRESH_ARTICLE){ // 更新文章数据
            articleList.clear();
            articleList.addAll(message.getData().getParcelableArrayList(BUNDLE_KEY_ARTICLE));
            adapter.notifyDataSetChanged();
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) refreshView.getLayoutParams();
            tvRefresh.setText("刷新成功！");
            ivRefresh.setBackgroundResource(R.mipmap.refresh_success);
            new Handler().postDelayed(() -> animRefreshView(lp.topMargin, targetTop), 1000);
        }
        return false;
    });

    /**
     * 加载两个Tab数据
     *
     * @param superChapterBeans 一级和二级目录的数据
     */
    private void initTabData(ArrayList<TreeSuperChapterBean> superChapterBeans) {
        // 加载一级目录数据
        for (int i = 0; i < superChapterBeans.size(); i++) {
            TreeSuperChapterBean superChapterBean = superChapterBeans.get(i);
            TabLayout.Tab superTab = tbSuperChapter.newTab();
            View superTabView = LayoutInflater.from(getActivity()).inflate(R.layout.item_tab_tree, null);
            TextView superTv = superTabView.findViewById(R.id.tv_tab_tree);
            superTv.setText(superChapterBean.getSuperChapterName());
            if (i == 0) {
                superTv.setSelected(true);
                superTv.setTextColor(this.getResources().getColor(R.color.white));
                lastSuperChapterTab = superTab;
                // 加载第一个二级目录数据
                ArrayList<TreeChapterBean> treeChapterBeans = superChapterBean.getChapters();
                for (int j = 0; j < treeChapterBeans.size(); j++) {
                    TreeChapterBean chapterBean = treeChapterBeans.get(j);
                    TabLayout.Tab chapterTab = tbChapter.newTab();
                    View chapterTabView = LayoutInflater.from(getActivity()).inflate(R.layout.item_tab_tree, null);
                    TextView chapterTv = chapterTabView.findViewById(R.id.tv_tab_tree);
                    chapterTv.setText(chapterBean.getChapterName());
                    if (j == 0) {
                        chapterTv.setSelected(true);
                        chapterTv.setTextColor(this.getResources().getColor(R.color.white));
                        lastChapterTab = chapterTab;
                        currentId = chapterBean.getArticleId();
                        mPresenter.onLoadArticle(URL_ARTICLE + chapterBean.getArticleId());
                    }
                    chapterTab.setCustomView(chapterTabView);
                    chapterTab.setTag(chapterBean);
                    tbChapter.addTab(chapterTab);
                }
            }
            superTab.setCustomView(superTabView);
            superTab.setTag(superChapterBean);
            tbSuperChapter.addTab(superTab);
        }
    }

    /**
     * 加载Recycler文章数据
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initRecycler() {
        adapter = new ArticleRecyclerAdapter(articleList);
        adapter.setRefreshView(refreshView);
        adapter.setFooterView(null);
        adapter.setArticleListener(url->{
            Intent intent = new Intent(getActivity(), ArticleActivity.class);
            intent.putExtra("url", url);
            startActivity(intent);
        });
        rvTree.setAdapter(adapter);
        rvTree.setOnTouchListener((v, e) -> {
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
                    this.lastY = y;
                    break;
                case MotionEvent.ACTION_UP:
                    RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) refreshView.getLayoutParams();
                    // 如果RefreshView距离顶部的距离大于0，则松手后可以刷新
                    if (lp.topMargin > 0) {
                        animRefreshView(lp.topMargin, 0);
                        refreshing();
                    } else {
                        // 否则回到隐藏状态
                        animRefreshView(lp.topMargin, targetTop);
                    }
                    break;
            }
            return false;
        });
    }

    @Override
    public void getChapterNameSuccess(ArrayList<TreeSuperChapterBean> names) {
        Message message = new Message();
        message.what = WHAT_GET_CHAPTER_NAME;
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BUNDLE_KEY_CHAPTER_DATA, names);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    @Override
    public void getArticleSuccess(ArrayList<ArticleBean> articleBeans) {
        Message message = new Message();
        message.what = WHAT_GET_ARTICLE;
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BUNDLE_KEY_ARTICLE, articleBeans);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    @Override
    public void onFinishRefresh(ArrayList<ArticleBean> articleBeans) {
        Message message = new Message();
        message.what = WHAT_REFRESH_ARTICLE;
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BUNDLE_KEY_ARTICLE, articleBeans);
        message.setData(bundle);
        // 开始刷新线程
        handler.sendMessageDelayed(message, 800);
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
    /**
     * 用动画效果从起始位置回到终止位置
     * @param startHeight 起始高度
     * @param endHeight 终止高度
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
        mPresenter.onRefreshArticle(URL_ARTICLE+currentId);
    }

    /**
     * 释放刷新的相关操作
     */
    private void releaseToRefresh(){
        tvRefresh.setText("释放刷新...");
        ivRefresh.setBackgroundResource(R.mipmap.pull_up);
    }

    /**
     * 下拉刷新的相关操作
     */
    private void pullToRefresh(){
        tvRefresh.setText("下拉刷新...");
        ivRefresh.setBackgroundResource(R.mipmap.pull_down);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}