package com.aefottt.redrockwinterworkqt.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aefottt.redrockwinterworkqt.R;
import com.aefottt.redrockwinterworkqt.data.bean.NaviTreeBean;
import com.aefottt.redrockwinterworkqt.view.my.MyApplication;

import java.util.ArrayList;

public class NaviTreeRecyclerAdapter extends RecyclerView.Adapter<NaviTreeRecyclerAdapter.ViewHolder> {
    private final ArrayList<NaviTreeBean> treeList;
    private int lastPosition = 0;
    private MoveRightRecyclerListener moveRightRecyclerListener;

    public NaviTreeRecyclerAdapter(ArrayList<NaviTreeBean> treeList) {
        this.treeList = treeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rv_navi_tree, parent, false));
    }

    /**
     * 这里需要采取数据源中数据的绝对位置进行选中与未选中状态的区别
     * 否则可能会因为Recycler奇特的缓存机制导致一些未被选中的Item也会变为选中的红色
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NaviTreeBean treeBean = treeList.get(position);
        holder.tv.setText(treeBean.getName());
        if (treeBean.isSelected()){
            holder.tv.setTextColor(MyApplication.getContext().getResources().getColor(R.color.theme));
            holder.tv.setBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.lead_white));
        }else {
            holder.tv.setTextColor(MyApplication.getContext().getResources().getColor(R.color.gray));
            holder.tv.setBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.white));
        }

        // 设置Item点击事件
        holder.tv.setOnClickListener(view -> {
            treeList.get(lastPosition).setSelected(false);
            notifyDataSetChanged();
            treeList.get(position).setSelected(true);
            notifyDataSetChanged();
            lastPosition = position;
            // 移动右侧的Recycler到指定位置
            if (moveRightRecyclerListener != null){
                moveRightRecyclerListener.moveRightRecycler(position);
            }
        });
    }

    public ArrayList<NaviTreeBean> getTreeList() {
        return treeList;
    }

    @Override
    public int getItemCount() {
        return treeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_navi_tree);
        }
    }

    public interface MoveRightRecyclerListener {
        void moveRightRecycler(int position);
    }

    public void setMoveRightRecyclerListener(MoveRightRecyclerListener moveRightRecyclerListener) {
        this.moveRightRecyclerListener = moveRightRecyclerListener;
    }
}
