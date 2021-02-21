package com.aefottt.redrockwinterworkqt.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aefottt.redrockwinterworkqt.R;
import com.aefottt.redrockwinterworkqt.data.bean.CollectWebBean;
import com.aefottt.redrockwinterworkqt.util.Utility;

import java.util.ArrayList;

public class CollectWebRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final ArrayList<CollectWebBean> webList;
    private View refreshView;
    private View footerView;

    public CollectWebRecyclerAdapter(ArrayList<CollectWebBean> webList) {
        this.webList = webList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ArticleRecyclerAdapter.VIEW_TYPE.REFRESH_VIEW.ordinal()){
            RecyclerView.LayoutParams headerParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, Utility.dpToPx(80));
            headerParams.topMargin = -Utility.dpToPx(80);
            refreshView.setLayoutParams(headerParams);
            return new RecyclerView.ViewHolder(refreshView) {};
        }else if (viewType == ArticleRecyclerAdapter.VIEW_TYPE.FOOTER_VIEW.ordinal() && footerView != null){
            footerView.setVisibility(View.GONE);
            return new RecyclerView.ViewHolder(footerView) {};
        }else { // 中间文章布局
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_collect_web, parent, false);
            return new viewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == ArticleRecyclerAdapter.VIEW_TYPE.ARTICLE_VIEW.ordinal()){
            CollectWebRecyclerAdapter.viewHolder viewHolder = (CollectWebRecyclerAdapter.viewHolder) holder;
            CollectWebBean webBean = webList.get(position);
            viewHolder.name.setText(webBean.getName());
            viewHolder.link.setText(webBean.getLink());
            viewHolder.itemView.setOnClickListener(view -> {
                articleListener.onClickListener(webBean.getLink());
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return ArticleRecyclerAdapter.VIEW_TYPE.REFRESH_VIEW.ordinal();
        }else if (position == webList.size() && footerView != null){
            return ArticleRecyclerAdapter.VIEW_TYPE.FOOTER_VIEW.ordinal();
        }else {
            return ArticleRecyclerAdapter.VIEW_TYPE.ARTICLE_VIEW.ordinal();
        }
    }

    @Override
    public int getItemCount() {
        return webList.size()+1;
    }

    static class viewHolder extends RecyclerView.ViewHolder{
        TextView name,link;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_collect_web_name);
            link = itemView.findViewById(R.id.tv_collect_web_link);
        }
    }

    public void setRefreshView(View refreshView) {
        this.refreshView = refreshView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public interface ArticleListener{
        void onClickListener(String url);
    }
    private CollectWebRecyclerAdapter.ArticleListener articleListener;

    public void setArticleListener(CollectWebRecyclerAdapter.ArticleListener articleListener) {
        this.articleListener = articleListener;
    }
}
