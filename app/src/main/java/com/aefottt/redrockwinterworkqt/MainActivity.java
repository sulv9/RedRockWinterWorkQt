package com.aefottt.redrockwinterworkqt;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.aefottt.redrockwinterworkqt.adapter.NormalVpAdapter;
import com.aefottt.redrockwinterworkqt.base.BaseActivity;
import com.aefottt.redrockwinterworkqt.fragment.IndexFragment;
import com.aefottt.redrockwinterworkqt.fragment.NaviFragment;
import com.aefottt.redrockwinterworkqt.fragment.ProjectFragment;
import com.aefottt.redrockwinterworkqt.fragment.TreeFragment;
import com.aefottt.redrockwinterworkqt.fragment.WendaFragment;
import com.aefottt.redrockwinterworkqt.fragment.WxarticleFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private EditText etMain;
    private CircleImageView accountMain;
    private TabLayout tbMain;
    private ViewPager vpMain;

    private static final String[] titles = { "导航", "体系", "首页", "问答", "项目", "公众号" };
    private NormalVpAdapter vpAdapterMain;

    private IndexFragment indexFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 5.0以上修改状态栏字体颜色
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        initView();
        initData();

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
                indexFragment.rvArticle.smoothScrollToPosition(0);
            }
        });
    }
    
    @Override
    protected void initData() {
        //添加Tab
        for (String title : titles) {
            tbMain.addTab(tbMain.newTab().setText(title));
        }
        //添加Fragment
        indexFragment = new IndexFragment();
        ArrayList<Fragment> fList = new ArrayList<>();
        fList.add(new NaviFragment());
        fList.add(new TreeFragment());
        fList.add(indexFragment);
        fList.add(new WendaFragment());
        fList.add(new ProjectFragment());
        fList.add(new WxarticleFragment());
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

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_main_account){
            //TODO 点击Account按钮前往用户界面（希望有MaterialAnimation）
        }else if (id == R.id.et_main_search){
            //TODO 点击搜索框前往搜索界面
        }
    }
}