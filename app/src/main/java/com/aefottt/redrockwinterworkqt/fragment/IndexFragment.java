package com.aefottt.redrockwinterworkqt.fragment;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aefottt.redrockwinterworkqt.MyApplication;
import com.aefottt.redrockwinterworkqt.R;
import com.aefottt.redrockwinterworkqt.adapter.ArticleRecyclerAdapter;
import com.aefottt.redrockwinterworkqt.bean.BannerBean;
import com.aefottt.redrockwinterworkqt.bean.IndexArticleBean;
import com.aefottt.redrockwinterworkqt.contract.IndexContract;
import com.aefottt.redrockwinterworkqt.presenter.IndexPresenter;

import java.util.ArrayList;

public class IndexFragment extends Fragment implements IndexContract.View {

    //Banner数据源
    private static final ArrayList<BannerBean> bannerList = new ArrayList<>();
    //Article数据源
    private final ArrayList<IndexArticleBean> articleList = new ArrayList<>();

    //文章RecyclerView
    public RecyclerView rvArticle;

    // 文章Recycler适配器
    private ArticleRecyclerAdapter articleAdapter;

    // Index的中间层
    private IndexPresenter mPresenter;

    // 自动播放Banner的Message
    private static final int WHAT_AUTO_PLAY_BANNER = 1;
    // 更新BannerRecycler数据的Message
    private static final int WHAT_GET_BANNER_DATA = 2;
    // 更新Article数据的Message
    private static final int WHAT_GET_ARTICLE_DATA = 3;
    // 刷新界面线程的Message
    private static final int WHAT_REFRESH_INDEX = 4;
    // 加载失败线程的Message
    private static final int WHAT_LOAD_FAIL = 5;
    // Banner自动播放时间间隔
    private static final int AUTO_PLAY_TIME = 2600;

    // 储存BannerList的BundleKey值
    private static final String BUNDLE_KEY_BANNER_LIST = "BannerList";
    // 储存ArticleList的BundleKey值
    private static final String BUNDLE_KEY_ARTICLE_LIST = "ArticleList";

    // 获取首页Banner地址
    private static final String URL_BANNER = "https://www.wanandroid.com/banner/json";

    // Article头地址
    private static final String URL_ARTICLE_HEAD = "https://www.wanandroid.com/article/list/";
    // 当前Article页数，初始为0
    private int mCurrentArticlePage = 0;
    // Article尾地址
    private static final String URL_ARTICLE_TAIL = "/json";

    // 当前Banner位置
    public static int mCurrentBannerPosition = 0;

    //是否自动播放
    public static boolean isAutoPlay;

    // 缓存Banner的Holder以便重复使用
    public Object tempMessageObj;

    // 触摸时的xy坐标
    private int lastY;

    // 拖动阻力值
    private static final float mDragF = 0.4F;

    // 头部刷新View
    private View refreshView;

    // 底部加载View
    private View footerView;

    // 刷新View中的提示文字
    private TextView tvRefresh;

    // 刷新View中的图片
    private ImageView ivRefresh;

    // 是否正在刷新
    private boolean isRefreshing;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_index, container, false);
        // 绑定视图到Presenter上
        mPresenter = new IndexPresenter();
        mPresenter.attachView(this);

        // 先设置ArticleRecycler的布局管理器
        rvArticle = view.findViewById(R.id.rv_index_article);
        LinearLayoutManager articleManager = new LinearLayoutManager(MyApplication.getContext());
        rvArticle.setLayoutManager(articleManager);

        // 初始化View及其子控件
        refreshView = LayoutInflater.from(getActivity()).inflate(R.layout.item_rv_refresh, rvArticle, false);
        footerView = LayoutInflater.from(getActivity()).inflate(R.layout.item_rv_footer, rvArticle, false);
        tvRefresh = refreshView.findViewById(R.id.tv_rv_refresh);
        ivRefresh = refreshView.findViewById(R.id.iv_rv_refresh);
        ivRefresh.setBackgroundResource(R.mipmap.pull_down);

        // 先加载Banner数据发送给整体的Recycler
        mPresenter.onLoadBannerData(URL_BANNER);
        // 通过Presenter获取Article初始数据
        mPresenter.onLoadArticleData(URL_ARTICLE_HEAD + mCurrentArticlePage + URL_ARTICLE_TAIL);

        // 设置ArticleRecycler触摸事件
        rvArticle.setOnTouchListener((view1, motionEvent) -> {
            int y = (int) motionEvent.getRawY(); // y要在最外面获取
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastY = y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    // Y轴偏移量
                    int offsetY = y - lastY;
                    // 如果在recycler的顶部则可以拉出刷新View
                    if (articleManager.findFirstCompletelyVisibleItemPosition() == 0 && lastY != 0) {
                        // 获取当前Layout的topMargin
                        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) refreshView.getLayoutParams();
                        float currentTopMargin = lp.topMargin;
                        // 计算应当偏移的topMargin值
                        int offsetTopMargin = (int) (currentTopMargin + offsetY * mDragF);
                        // 开始滑动，逐渐显示出刷新View
                        if (offsetTopMargin >= -dpToPx(80)) {
                            lp.topMargin = offsetTopMargin;
                            refreshView.setLayoutParams(lp);
                            refreshView.invalidate();
                        }
                        if (lp.topMargin > 0) { // 下拉超过刷新View的高度，释放后即可刷新
                            tvRefresh.setText("释放刷新...");
                            ivRefresh.setBackgroundResource(R.mipmap.pull_up);
                        } else { //下拉距离不够
                            tvRefresh.setText("下拉刷新...");
                            ivRefresh.setBackgroundResource(R.mipmap.pull_down);
                        }
                    }
                    lastY = y;
                    break;
                case MotionEvent.ACTION_UP:
                    // 如果释放后偏移量大于RefreshView高度则开始刷新
                    RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) refreshView.getLayoutParams();
                    if (lp.topMargin > 0) { //拉到了可以刷新的事件
                        isRefreshing = true;
                        footerView.setVisibility(View.GONE);
                        tvRefresh.setText("正在刷新...");
                        animRefreshView(lp.topMargin, 0, 1000);
                        ivRefresh.setBackgroundResource(R.mipmap.load);
                        new Handler().postDelayed(() -> {
                            tvRefresh.setText("刷新成功！");
                            ivRefresh.setBackgroundResource(R.mipmap.refresh_success);
                            animRefreshView(0, -dpToPx(80), 1200);
                            // 重新加载Banner
                            mPresenter.onLoadBannerData(URL_BANNER);
                            // 重新加载文章
                            mCurrentArticlePage = 0;
                            mPresenter.onLoadArticleData(URL_ARTICLE_HEAD + mCurrentArticlePage + URL_ARTICLE_TAIL);
                        }, 1000);
                    } else { //未拉到刷新View，收回
                        tvRefresh.setText("下拉刷新...");
                        ivRefresh.setBackgroundResource(R.mipmap.pull_down);
                        animRefreshView(lp.topMargin, -dpToPx(80), 1000);
                    }
                    break;
            }
            return false;
        });
        // 添加ArticleRecycler滑动监听事件 -- 滑动到最后一条Item加载新的数据
        rvArticle.addOnScrollListener(new RecyclerView.OnScrollListener() {
            // 滑动状态改变
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //总Item数量
                    int countItem = articleManager.getItemCount();
                    //当前可以看到的最后一条Item的位置
                    int lastItem = articleManager.findLastCompletelyVisibleItemPosition();
                    if (lastItem == countItem - 1 && bannerList.size() > 1) {
                        footerView.setVisibility(View.VISIBLE);
                        // 页数加1
                        mCurrentArticlePage++;
                        // 更新数据
                        mPresenter.onLoadArticleData(URL_ARTICLE_HEAD + mCurrentArticlePage + URL_ARTICLE_TAIL);
                    }
                }
            }
        });

        return view;
    }

    /**
     * 开启线程更新UI
     */
    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == WHAT_GET_BANNER_DATA) {
                //更新Banner数据
                bannerList.clear();
                bannerList.addAll(message.getData().getParcelableArrayList(BUNDLE_KEY_BANNER_LIST));
                isAutoPlay = true;
                /*
                 * 如果一个adapter绑定到了多个recyclerview上，会报错
                 * ViewHolder views must not be attached when created. Ensure that you are not passing 'true' to the attachToRoot parameter of LayoutInflater.inflate(..., boolean attachToRoot)
                 * 所以这里不用判断adapter是否为空，onResume中也不需要重新绑定adapter
                 */
//                if (articleAdapter == null)
                //将加载好的Banner数据传给Adapter
                articleAdapter = new ArticleRecyclerAdapter(articleList, bannerList);
                // 为adapter添加更新Banner回调监听
                articleAdapter.setBannerListener((holder) -> {
                    // 如果已经存在消息队列（旧消息队列）就将其移除
                    if (handler.hasMessages(WHAT_AUTO_PLAY_BANNER)) {
                        handler.removeMessages(WHAT_AUTO_PLAY_BANNER, tempMessageObj);
                        tempMessageObj = null;
                    }
                    // 发送具有新绑定的holder的消息队列
                    //TODO 注意：由于ViewHolder的缓存机制，每次Holder有一个属性（例如c7522de，不懂这是啥）都会改变，需要删除旧队列再新加新队列重新绑定Holder
                    Message message2 = new Message();
                    message2.what = WHAT_AUTO_PLAY_BANNER;
                    message2.obj = holder;
                    handler.sendMessageDelayed(message2, AUTO_PLAY_TIME);
                });
                // 设置Refresh布局
                articleAdapter.setRefreshView(refreshView);
                // 设置Footer布局
                articleAdapter.setFooterView(footerView);
                rvArticle.setAdapter(articleAdapter);
            } else if (message.what == WHAT_GET_ARTICLE_DATA) { //更新Article数据
                footerView.setVisibility(View.GONE);
                ArrayList<IndexArticleBean> articleBeans = message.getData().getParcelableArrayList(BUNDLE_KEY_ARTICLE_LIST);
                if (isRefreshing) {
                    // 设置刷新成功
                    isRefreshing = false;
                    articleList.clear();
                    articleList.addAll(articleBeans);
                    // 更新Article数据
                    articleAdapter.notifyDataSetChanged();
                } else {
                    int initSize = articleList.size();
                    articleList.addAll(articleBeans);
                    // 更新Article数据
                    articleAdapter.notifyItemRangeChanged(initSize, articleList.size() - initSize);
                }
            } else if (message.what == WHAT_AUTO_PLAY_BANNER) { //自动播放Banner
                // 更新Banner及指示器位置
                if (bannerList.size() < 2) {
                    return false;
                }
                ArticleRecyclerAdapter.ArticleViewHolder bannerHolder = (ArticleRecyclerAdapter.ArticleViewHolder) message.obj;
                if (isAutoPlay) {
                    // Recycler移动
                    bannerHolder.rvBanner.smoothScrollToPosition(++mCurrentBannerPosition);
                    // 指示器移动
                    updateIndicator(bannerHolder.indicatorContainer);
                }
                if (tempMessageObj == null) {
                    tempMessageObj = message.obj;
                }
                // 重复播放
                Message message1 = new Message();
                message1.what = WHAT_AUTO_PLAY_BANNER;
                message1.obj = tempMessageObj;
                handler.sendMessageDelayed(message1, AUTO_PLAY_TIME);
            } else if (message.what == WHAT_LOAD_FAIL) {
                Toast.makeText(MyApplication.getContext(), "哦豁，加载失败了", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    /**
     * 这里接收Presenter层传递过来的数据，并开启线程更新BannerRecycler
     *
     * @param bannerList 获取到的Banner数据
     */
    @Override
    public void getBannerDataSuccess(ArrayList<BannerBean> bannerList) {
        // 向Handler发送更新的Banner数据
        Message message = new Message();
        message.what = WHAT_GET_BANNER_DATA;
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BUNDLE_KEY_BANNER_LIST, bannerList);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    /**
     * 这里接收Presenter层传递过来的数据，并开启线程更新ArticleRecycler
     *
     * @param articleList 获取的第一页Article数据
     */
    @Override
    public void getArticleDataSuccess(ArrayList<IndexArticleBean> articleList) {
        // 向Handler发送更新的Article数据
        Message message = new Message();
        message.what = WHAT_GET_ARTICLE_DATA;
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BUNDLE_KEY_ARTICLE_LIST, articleList);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    /**
     * 更新指示器颜色
     */
    public static void updateIndicator(LinearLayout indicatorContainer) {
        // 先将所有指示器设置为灰色
        for (int i = 0; i < indicatorContainer.getChildCount(); i++) {
            if (i == 0) {
                indicatorContainer.getChildAt(i).setBackgroundResource(R.drawable.bg_indicator_left);
            } else if (i == bannerList.size() - 1) {
                indicatorContainer.getChildAt(i).setBackgroundResource(R.drawable.bg_indicator_right);
            } else {
                indicatorContainer.getChildAt(i).setBackgroundResource(R.drawable.bg_indicator_normal);
            }
        }
        // 然后将当前位置的指示器设置为红色
        int bannerPos = mCurrentBannerPosition % bannerList.size();
        if (bannerPos == 0) {
            indicatorContainer.getChildAt(bannerPos).setBackgroundResource(R.drawable.bg_indicator_left_selected);
        } else if (bannerPos == bannerList.size() - 1) {
            indicatorContainer.getChildAt(bannerPos).setBackgroundResource(R.drawable.bg_indicator_right_selected);
        } else {
            indicatorContainer.getChildAt(bannerPos).setBackgroundResource(R.drawable.bg_indicator_normal_selected);
        }
    }

    /**
     * dp转换为px
     */
    public static int dpToPx(int dpValue) {
        final float scale = MyApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 从开始位置滑动到结束位置
     */
    public void animRefreshView(final int startHeight, final int endHeight, int duration) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(refreshView, "qt", 0.0f, 1.0f);
        anim.start();
        anim.setDuration(duration);
        anim.addUpdateListener(valueAnimator -> {
            float cVal = (float) valueAnimator.getAnimatedValue();
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) refreshView.getLayoutParams();
            lp.topMargin = (int) (startHeight + cVal * (endHeight - startHeight));
            refreshView.setLayoutParams(lp);
            refreshView.invalidate();
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("qt", "onPause");
        // 页面销毁时Banner停止自动播放
        isAutoPlay = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("qt", "onResume");
        isAutoPlay = true;
        // 如果已经存在消息队列（旧消息队列）就将其移除
        if (handler.hasMessages(WHAT_AUTO_PLAY_BANNER)) {
            handler.removeMessages(WHAT_AUTO_PLAY_BANNER, tempMessageObj);
            tempMessageObj = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void showLodaing() {
        //TODO 显示加载
    }

    @Override
    public void hideLoading() {
        //TODO 隐藏加载
    }

    @Override
    public void onError(Exception e) {
        Log.e("WanAndroid", "加载失败：" + e.toString());
        Message failMessage = new Message();
        failMessage.what = WHAT_LOAD_FAIL;
        handler.sendMessage(failMessage);
    }

}