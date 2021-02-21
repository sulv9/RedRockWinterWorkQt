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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aefottt.redrockwinterworkqt.R;
import com.aefottt.redrockwinterworkqt.contract.WendaContract;
import com.aefottt.redrockwinterworkqt.data.adapter.ArticleRecyclerAdapter;
import com.aefottt.redrockwinterworkqt.data.bean.ArticleBean;
import com.aefottt.redrockwinterworkqt.presenter.WendaPresenter;
import com.aefottt.redrockwinterworkqt.util.Utility;
import com.aefottt.redrockwinterworkqt.view.activity.ArticleActivity;
import com.aefottt.redrockwinterworkqt.view.my.MyApplication;

import java.util.ArrayList;

public class WendaFragment extends Fragment implements WendaContract.View {
    private static final int WHAT_GET_WENDA_DATA = 0;
    private static final int WHAT_GET_MORE_DATA = 1;
    private static final String BUNDLE_KEY_WENDA_DATA = "WendaData";
    private static final String URL_WENDA_HEAD = "https://wanandroid.com/wenda/list/";
    private static final String URL_WENDA_TAIL = "/json";
    private int pageId = 1;

    private ArticleRecyclerAdapter adapter;

    private LinearLayoutManager manager;

    private WendaPresenter mPresenter;

    private final ArrayList<ArticleBean> articleBeans = new ArrayList<>();

    private View refreshView;
    private View footerView;
    private TextView tvRefresh;
    private ImageView ivRefresh;
    private int targetTop;
    private ObjectAnimator anim;
    private int lastY;
    private final float mDragF = 0.4F;

    public RecyclerView rv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wenda, container, false);
        rv = view.findViewById(R.id.rv_wenda);
        manager = new LinearLayoutManager(MyApplication.getContext());
        rv.setLayoutManager(manager);

        // 初始化View
        refreshView = LayoutInflater.from(getActivity()).inflate(R.layout.item_rv_refresh, rv, false);
        footerView = LayoutInflater.from(getActivity()).inflate(R.layout.item_rv_footer, rv, false);
        tvRefresh = refreshView.findViewById(R.id.tv_rv_refresh);
        ivRefresh = refreshView.findViewById(R.id.iv_rv_refresh);
        targetTop = -Utility.dpToPx(80);
        // 初始化动画
        anim = ObjectAnimator.ofFloat(refreshView, "qt", 0.0f, 1.0f);

        // 加载问答数据
        mPresenter = new WendaPresenter();
        mPresenter.attachView(this);
        mPresenter.onLoadWendaData(URL_WENDA_HEAD + pageId + URL_WENDA_TAIL);

        initRecycler();

        return view;
    }

    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == WHAT_GET_WENDA_DATA) {
                articleBeans.clear();
                articleBeans.addAll(message.getData().getParcelableArrayList(BUNDLE_KEY_WENDA_DATA));
                adapter = new ArticleRecyclerAdapter(articleBeans);
                adapter.setRefreshView(refreshView);
                adapter.setFooterView(footerView);
                adapter.setArticleListener(url->{
                    Intent intent = new Intent(getActivity(), ArticleActivity.class);
                    intent.putExtra("url", url);
                    startActivity(intent);
                });
                rv.setAdapter(adapter);
            }else if (message.what == WHAT_GET_MORE_DATA){
                int lastPosition = articleBeans.size() - 1;
                articleBeans.addAll(message.getData().getParcelableArrayList(BUNDLE_KEY_WENDA_DATA));
                adapter.notifyItemRangeChanged(lastPosition, articleBeans.size());
            }
            return false;
        }
    });

    @SuppressLint("ClickableViewAccessibility")
    private void initRecycler(){
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
                    if (lastItem == manager.getItemCount() - 1){
                        footerView.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(() -> {
                            pageId++;
                            mPresenter.onLoadMoreData(URL_WENDA_HEAD+pageId+URL_WENDA_TAIL);
                        }, 600);
                    }
                }
            }
        });
    }

    @Override
    public void getWendaDataSuccess(ArrayList<ArticleBean> articleBeans) {
        Message message = new Message();
        message.what = WHAT_GET_WENDA_DATA;
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BUNDLE_KEY_WENDA_DATA, articleBeans);
        message.setData(bundle);
        handler.sendMessageDelayed(message, 1000);
    }

    @Override
    public void getMoreDataSuccess(ArrayList<ArticleBean> articleBeans) {
        Message message = new Message();
        message.what = WHAT_GET_MORE_DATA;
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BUNDLE_KEY_WENDA_DATA, articleBeans);
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
        pageId = 1;
        mPresenter.onLoadWendaData(URL_WENDA_HEAD + pageId + URL_WENDA_TAIL);
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