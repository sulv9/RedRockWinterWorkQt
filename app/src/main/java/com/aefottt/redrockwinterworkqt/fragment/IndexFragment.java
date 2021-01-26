package com.aefottt.redrockwinterworkqt.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.aefottt.redrockwinterworkqt.MyApplication;
import com.aefottt.redrockwinterworkqt.R;
import com.aefottt.redrockwinterworkqt.adapter.BannerRvAdapter;
import com.aefottt.redrockwinterworkqt.bean.BannerBean;
import com.aefottt.redrockwinterworkqt.contract.IndexContract;
import com.aefottt.redrockwinterworkqt.http.HttpCallbackListener;
import com.aefottt.redrockwinterworkqt.http.HttpUtil;
import com.aefottt.redrockwinterworkqt.presenter.IndexPresenter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class IndexFragment extends Fragment implements IndexContract.View {

    // 轮播图的载体-RecyclerView
    private RecyclerView rvBanner;
    // 指示器的载体-LinearLayout
    private LinearLayout indicatorContainer;

    // Index的中间层
    private IndexPresenter mPresenter;

    //Banner数据源
    private final ArrayList<BannerBean> bannerList = new ArrayList<>();

    // 轮播图适配器
    private BannerRvAdapter adapter;

    //获取Banner数据源的Message
    private static final int WHAT_GET_BANNER = 1;
    //储存BannerList的对象
    private static final String BUNDLE_KEY_BANNER_LIST = "BannerList";
    //自动播放Banner的Message
    private static final int WHAT_AUTO_PLAY_BANNER = 2;

    //获取首页Banner地址
    private static final String URL_BANNER = "https://www.wanandroid.com/banner/json";

    //当前Banner位置
    private int mCurrentBannerPosition = 0;

    //是否自动播放
    private boolean isAutoPlay;

    //加载框
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_index, container, false);
        rvBanner = view.findViewById(R.id.rv_index_banner);
        indicatorContainer = view.findViewById(R.id.ll_banner_dots);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyApplication.getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rvBanner.setLayoutManager(linearLayoutManager);
        adapter = new BannerRvAdapter(bannerList);
        // 获取Banner数据，要放在适配器初始化之后
        // getBannerData();

        // 绑定视图到Presenter上
        mPresenter = new IndexPresenter();
        mPresenter.attachView(this);
        // 通过Presenter获取数据
        mPresenter.getBannerData(URL_BANNER);

        rvBanner.setAdapter(adapter);
        // 添加RecyclerView滑动监听事件
        rvBanner.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    // 滑动状态停止自动播放
                    isAutoPlay = false;
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 停止滑动后继续播放
                    isAutoPlay = true;
                }
            }
        });

        // 设置PagerSnapHelper使RecyclerView实现像ViewPager那样的效果
        PagerSnapHelper snapHelper = new PagerSnapHelper() {
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                // 当前滑动的项
                int target = super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
                Log.e("qt", "滑动到" + target + "位置");
                // 每次滑动更新当前位置以及指示器颜色
                mCurrentBannerPosition = target;
                updateIndicator();
                return target;
            }
        };
        snapHelper.attachToRecyclerView(rvBanner);

        return view;
    }

    /**
     * 这里接收Presenter层传递过来的数据，并开启线程更新UI
     * @param bannerList 获取到的Banner数据
     */
    @Override
    public void getBannerDataSuccess(ArrayList<BannerBean> bannerList) {
        Message message = new Message();
        message.what = WHAT_GET_BANNER;
        // 通过Bundle传递Parcelable序列化对象
        Bundle data = new Bundle();
        data.putParcelableArrayList(BUNDLE_KEY_BANNER_LIST, bannerList);
        message.setData(data);
        handler.sendMessage(message);
    }

    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == WHAT_GET_BANNER) {
                // 获取Parcelable序列化对象
                ArrayList<? extends BannerBean> list = message.getData().getParcelableArrayList(BUNDLE_KEY_BANNER_LIST);
                bannerList.clear();
                bannerList.addAll(list);
                // 更新Banner数据
                adapter.notifyDataSetChanged();
                // 移动到第1000页
                mCurrentBannerPosition = 1000 * bannerList.size();
                rvBanner.scrollToPosition(mCurrentBannerPosition);
                // 初始化指示器
                initIndicator();
                // 设置指示器颜色
                updateIndicator();
            } else if (message.what == WHAT_AUTO_PLAY_BANNER) {
                // 更新Banner及指示器位置
                if (bannerList.size() < 2) {
                    return false;
                }
                if (isAutoPlay) {
                    // Recycler移动
                    rvBanner.smoothScrollToPosition(++mCurrentBannerPosition);
                    // 指示器移动
                    updateIndicator();
                }
                // 重复播放
                handler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY_BANNER, 3000);
            }
            return false;
        }
    });

    /**
     * 初始化指示器
     */
    private void initIndicator() {
        View view;
        for (int i = 0; i < bannerList.size(); i++) {
            view = new View(MyApplication.getContext());
            if (i == 0) {
                view.setBackgroundResource(R.drawable.bg_indicator_left);
            } else if (i == bannerList.size() - 1) {
                view.setBackgroundResource(R.drawable.bg_indicator_right);
            } else {
                view.setBackgroundResource(R.drawable.bg_indicator_normal);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            params.weight = 1; //宽度均分
            indicatorContainer.addView(view, params); //动态添加View
        }
    }

    /**
     * 更新指示器颜色
     */
    private void updateIndicator() {
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
        // 页面重新开始后再次开始自动播放
        isAutoPlay = true;
        // 自动播放
        if (!handler.hasMessages(WHAT_AUTO_PLAY_BANNER)) {
            handler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY_BANNER, 3000);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void showLodaing() {
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("加载中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    @Override
    public void hideLoading() {
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onError(Throwable throwable) {
        Log.e("WanAndroid", "加载失败："+throwable.toString());
    }

}