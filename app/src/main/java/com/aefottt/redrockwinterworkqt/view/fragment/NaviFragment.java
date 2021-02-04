package com.aefottt.redrockwinterworkqt.view.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aefottt.redrockwinterworkqt.R;
import com.aefottt.redrockwinterworkqt.contract.NaviContract;
import com.aefottt.redrockwinterworkqt.data.adapter.NaviTitleRecyclerAdapter;
import com.aefottt.redrockwinterworkqt.data.adapter.NaviTreeRecyclerAdapter;
import com.aefottt.redrockwinterworkqt.data.bean.NaviTreeBean;
import com.aefottt.redrockwinterworkqt.presenter.NaviPresenter;
import com.aefottt.redrockwinterworkqt.view.my.MyApplication;

import java.util.ArrayList;

public class NaviFragment extends Fragment implements NaviContract.view {
    // 获取导航数据对应的Message
    private static final int WHAT_GET_NAVI_DATA = 0;
    // 储存导航数据的BundleKey值
    private static final String BUNDLE_KEY_NAVI_DATA = "NaviData";
    // 导航数据地址
    private static final String URL_NAVI = "https://www.wanandroid.com/navi/json";

    private final ArrayList<NaviTreeBean> treeList = new ArrayList<>();

    private RecyclerView rvLeft, rvRight;

    private NaviTreeRecyclerAdapter treeAdapter;

    private LinearLayoutManager titleManager;

    // 上一个被选中的Item位置
    private int lastPosition;
    // 是否正处于点击移动过程
    private boolean clickMoving;
    // 记录点击的Item位置
    private int clickPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navi, container, false);
        rvLeft = view.findViewById(R.id.rv_navi_left);
        rvRight = view.findViewById(R.id.rv_navi_right);

        // 设置Recycler布局管理器
        LinearLayoutManager treeManager = new LinearLayoutManager(MyApplication.getContext());
        titleManager = new LinearLayoutManager(MyApplication.getContext());
        rvLeft.setLayoutManager(treeManager);
        rvRight.setLayoutManager(titleManager);

        // 网络请求数据
        NaviPresenter mPresenter = new NaviPresenter();
        mPresenter.attachView(this);
        mPresenter.onLoadNaviData(URL_NAVI);

        return view;
    }

    private final Handler handler = new Handler(message -> {
        if (message.what == WHAT_GET_NAVI_DATA){ //获取导航数据
            ArrayList<NaviTreeBean> treeBeans = message.getData().getParcelableArrayList(BUNDLE_KEY_NAVI_DATA);
            // 加载左栏和右栏Recycler数据
            loadRecyclerData(treeBeans);
            // 绑定左右Recycler，点击左侧Recycler时右侧会滑到对应位置，滑动右侧Recycler时左侧会前往对应位置
            onBindRecyclerEvent();
        }
        return false;
    });

    /**
     * 绑定左右Recycler联动事件
     * 点击左侧Recycler时右侧会滑到对应位置，滑动右侧Recycler时左侧会前往对应位置
     */
    private void onBindRecyclerEvent() {
        // 点击左边Item滑倒对应位置，并设置颜色
        treeAdapter.setMoveRightRecyclerListener(position -> {
            rvRight.smoothScrollToPosition(position);
            clickMoving = true;
            clickPosition = position;
        });

        // 设置右边Recycler滑动监听
        rvRight.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (clickMoving && newState == RecyclerView.SCROLL_STATE_IDLE){
                    ArrayList<NaviTreeBean> getTreeList = treeAdapter.getTreeList();
                    clickMoving = false;
                    getTreeList.get(lastPosition).setSelected(false);
                    treeAdapter.notifyDataSetChanged();
                    lastPosition = clickPosition;
                }
            }
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (clickMoving){
                    return;
                }
                // 内容向下走dy>0，内容向上走dy<0
//                if (dy > 0){
//                    int visibleItem = treeManager.findLastVisibleItemPosition() - treeManager.findFirstVisibleItemPosition();
//                    rvLeft.smoothScrollToPosition(visibleItem + firstItem - 3);
//                }else {
//                    rvLeft.smoothScrollToPosition(treeManager.getChildCount() - firstItem + 1);
//                }
                ArrayList<NaviTreeBean> getTreeList = treeAdapter.getTreeList();
                int firstItem = titleManager.findFirstVisibleItemPosition();

                moveToMiddle(rvLeft, firstItem);

                getTreeList.get(lastPosition).setSelected(false);
                treeAdapter.notifyDataSetChanged();
                getTreeList.get(firstItem).setSelected(true);
                treeAdapter.notifyDataSetChanged();
                lastPosition = firstItem;
            }
        });
    }

    /**
     * 这段代码来源于github，一位dl写的左边Recycler滑动方式，计算出当前位置与中间位置的差值然后移动左边Recycler，
     * 效果不是太好，但起码能实现左边Recycler的精准滑动了。
     * 但说实话这段代码没有看太懂啊
     * 这段代码保证了左侧Recycler被选中位置一直处于中间
     * @param recyclerView 要滑动的RecyclerView
     * @param position 移动到的位置
     */
    public static void moveToMiddle(RecyclerView recyclerView, int position) {
        //先从RecyclerView的LayoutManager中获取当前第一项和最后一项的Position
        int firstItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        int lastItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
        //中间位置
        int middle = (firstItem + lastItem) / 2;
        // 取绝对值，index下标是当前的位置和中间位置的差，下标为index的view的top就是需要滑动的距离
        int index = (position - middle) >= 0 ? position - middle : -(position - middle);
        //左侧列表一共有getChildCount个Item，如果>这个值会返回null，程序崩溃，如果>getChildCount直接滑到指定位置,或者,都一样啦
        if (index >= recyclerView.getChildCount()) {
            recyclerView.scrollToPosition(position);  /* 这里没看太懂*/
        } else {
            //如果当前位置在中间位置上面，往下移动，这里为了防止越界
            if(position < middle) {
                recyclerView.scrollBy(0, -recyclerView.getChildAt(index).getTop());
                // 在中间位置的下面，往上移动
            } else {
                recyclerView.scrollBy(0, recyclerView.getChildAt(index).getTop());
            }
        }
    }

    /**
     * 加载左栏和右栏Recycler数据
     * @param treeBeans 对应的TreeName数据
     */
    private void loadRecyclerData(ArrayList<NaviTreeBean> treeBeans) {
        // 加载数据源
        treeList.clear();
        treeList.addAll(treeBeans);
        // 加载左栏数据
        treeAdapter = new NaviTreeRecyclerAdapter(treeList);
        rvLeft.setAdapter(treeAdapter);
        // 加载右栏数据
        NaviTitleRecyclerAdapter titleAdapter = new NaviTitleRecyclerAdapter(treeList);
        rvRight.setAdapter(titleAdapter);
    }

    @Override
    public void getNaviDataSuccess(ArrayList<NaviTreeBean> treeBeans) {
        Message message = new Message();
        message.what = WHAT_GET_NAVI_DATA;
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BUNDLE_KEY_NAVI_DATA, treeBeans);
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
}