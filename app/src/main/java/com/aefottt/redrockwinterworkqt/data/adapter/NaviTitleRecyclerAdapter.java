package com.aefottt.redrockwinterworkqt.data.adapter;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aefottt.redrockwinterworkqt.R;
import com.aefottt.redrockwinterworkqt.data.bean.NaviTitleBean;
import com.aefottt.redrockwinterworkqt.data.bean.NaviTreeBean;
import com.aefottt.redrockwinterworkqt.util.Utility;
import com.aefottt.redrockwinterworkqt.view.my.FlowLayout;
import com.aefottt.redrockwinterworkqt.view.my.MyApplication;

import java.util.ArrayList;

public class NaviTitleRecyclerAdapter extends RecyclerView.Adapter<NaviTitleRecyclerAdapter.ViewHolder> {
    private final ArrayList<NaviTreeBean> treeList;

    public NaviTitleRecyclerAdapter(ArrayList<NaviTreeBean> treeList) {
        this.treeList = treeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NaviTitleRecyclerAdapter.ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_navi_title, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NaviTreeBean treeBean = treeList.get(position);
        holder.title.setText(treeBean.getName());
        ArrayList<NaviTitleBean> titleBeans = treeBean.getTitleBeans();
        //TODO 注意一定要在这里先移除所有的View，否则在Recycler滑回来的时候会添加很多重复的标签
        holder.flowLayout.removeAllViews();
        for (int i = 0; i < titleBeans.size(); i++) {
            TextView tv = (TextView) LayoutInflater.from(MyApplication.getContext())
                    .inflate(R.layout.item_flow_layout, holder.flowLayout, false);
            tv.setText(titleBeans.get(i).getTitle());
            holder.flowLayout.addView(tv);
        }
    }

    @Override
    public int getItemCount() {
        return treeList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        FlowLayout flowLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_navi_title);
            flowLayout = itemView.findViewById(R.id.flow_layout_navi);
        }
    }
}
