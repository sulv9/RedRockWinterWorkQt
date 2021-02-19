package com.aefottt.redrockwinterworkqt.view.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.aefottt.redrockwinterworkqt.R;
import com.aefottt.redrockwinterworkqt.base.BaseActivity;
import com.aefottt.redrockwinterworkqt.data.adapter.NormalVpAdapter;
import com.aefottt.redrockwinterworkqt.util.PrefUtil;
import com.aefottt.redrockwinterworkqt.util.Utility;
import com.aefottt.redrockwinterworkqt.view.fragment.IndexFragment;
import com.aefottt.redrockwinterworkqt.view.fragment.NaviFragment;
import com.aefottt.redrockwinterworkqt.view.fragment.ProjectFragment;
import com.aefottt.redrockwinterworkqt.view.fragment.TreeFragment;
import com.aefottt.redrockwinterworkqt.view.fragment.WendaFragment;
import com.aefottt.redrockwinterworkqt.view.fragment.WxarticleFragment;
import com.aefottt.redrockwinterworkqt.view.my.CircleImageView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private TextView etMain;
    private CircleImageView accountMain;
    private TabLayout tbMain;
    private ViewPager vpMain;

    private static final String[] titles = {"导航", "体系", "首页", "问答", "项目", "公众号"};
    private NormalVpAdapter vpAdapterMain;

    private NaviFragment naviFragment;
    private TreeFragment treeFragment;
    private IndexFragment indexFragment;
    private WendaFragment wendaFragment;
    private ProjectFragment projectFragment;
    private WxarticleFragment wxarticleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setStatusBarTextBlack();
        // 初始化布局
        initView();
        // 初始化数据
        initData();
        // 设置控件相关事件
        setView();
    }

    /**
     * 设置ViewPager与TabLayout联动，并且点击Tab返回Recycler第一项
     */
    private void setView() {
        vpMain.setAdapter(vpAdapterMain);
        vpMain.setCurrentItem(2);
        tbMain.setupWithViewPager(vpMain);
        tbMain.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                String tabName = (String) tab.getText();
                if (titles[0].equals(tabName)) {
                    naviFragment.rvRight.smoothScrollToPosition(0);
                } else if (titles[1].equals(tabName)) {
                    treeFragment.rvTree.smoothScrollToPosition(0);
                } else if (titles[2].equals(tabName)) {
                    indexFragment.rvArticle.smoothScrollToPosition(0);
                } else if (titles[3].equals(tabName)) {
                    wendaFragment.rv.smoothScrollToPosition(0);
                } else if (titles[4].equals(tabName)) {
                    projectFragment.rv.smoothScrollToPosition(0);
                } else if (titles[5].equals(tabName)) {
                    wxarticleFragment.rv.smoothScrollToPosition(0);
                }
            }
        });
    }

    /**
     * 初始化数据，实例化MainActivity的每一个Fragment
     */
    @Override
    protected void initData() {
        //添加Tab
        for (String title : titles) {
            tbMain.addTab(tbMain.newTab().setText(title));
        }
        //添加Fragment
        naviFragment = new NaviFragment();
        treeFragment = new TreeFragment();
        indexFragment = new IndexFragment();
        wendaFragment = new WendaFragment();
        projectFragment = new ProjectFragment();
        wxarticleFragment = new WxarticleFragment();
        ArrayList<Fragment> fList = new ArrayList<>();
        fList.add(naviFragment);
        fList.add(treeFragment);
        fList.add(indexFragment);
        fList.add(wendaFragment);
        fList.add(projectFragment);
        fList.add(wxarticleFragment);
        //初始化VpAdapter
        vpAdapterMain = new NormalVpAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                fList, titles);
    }

    @Override
    protected void initView() {
        etMain = findViewById(R.id.et_main_search);
        etMain.setOnClickListener(this);
        accountMain = findViewById(R.id.iv_main_account);
        accountMain.setOnClickListener(this);
        tbMain = findViewById(R.id.tb_main);
        vpMain = findViewById(R.id.vp_main);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_main_account) {
            // 获取是否登录过的数据
            boolean isLogin = (boolean) PrefUtil.getInstance().get(Utility.FILE_NAME_USER_INFO, Utility.KEY_IS_LOGIN, false);
            if (isLogin) {
                //TODO 已经登录，跳转到个人信息界面
                startActivity(new Intent(MainActivity.this, MyInfoActivity.class),
                        ActivityOptions.makeSceneTransitionAnimation(this, accountMain, "shared_photo_view").toBundle());
            } else {
                //未登录，跳转到登陆界面
                startActivity(new Intent(MainActivity.this, LoginActivity.class),
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            }
        } else if (id == R.id.et_main_search) {
            //TODO 点击搜索框前往搜索界面
            startActivity(new Intent(MainActivity.this, SearchActivity.class),
                    ActivityOptions.makeSceneTransitionAnimation(this, etMain, "shared_search_et").toBundle());
        }
    }
}