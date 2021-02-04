package com.aefottt.redrockwinterworkqt.data.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aefottt.redrockwinterworkqt.R;
import com.aefottt.redrockwinterworkqt.data.bean.ArticleBean;
import com.aefottt.redrockwinterworkqt.util.Utility;
import com.aefottt.redrockwinterworkqt.view.my.MyApplication;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ArticleRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final ArrayList<ArticleBean> aList;
    private View refreshView;
    private View footerView;

    public enum VIEW_TYPE {
        REFRESH_VIEW,
        ARTICLE_VIEW,
        FOOTER_VIEW
    }

    public ArticleRecyclerAdapter(ArrayList<ArticleBean> aList) {
        this.aList = aList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE.REFRESH_VIEW.ordinal()){
            RecyclerView.LayoutParams headerParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, Utility.dpToPx(80));
            headerParams.topMargin = -Utility.dpToPx(80);
            refreshView.setLayoutParams(headerParams);
            return new RecyclerView.ViewHolder(refreshView) {};
        }else if (viewType == VIEW_TYPE.FOOTER_VIEW.ordinal() && footerView != null){
            footerView.setVisibility(View.GONE);
            return new RecyclerView.ViewHolder(footerView) {};
        }else { // 中间文章布局
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_rv_article, parent, false);
            return new ArticleRecyclerAdapter.ArticleViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE.ARTICLE_VIEW.ordinal()) {
            ArticleRecyclerAdapter.ArticleViewHolder articleHolder = (ArticleRecyclerAdapter.ArticleViewHolder) holder;
            ArticleBean bean = aList.get(position - 1);
            // 设置值
            articleHolder.author.setText(bean.getAuthor());
            articleHolder.time.setText(bean.getTime());
            articleHolder.title.setText(bean.getTitle());
            articleHolder.desc.setText(bean.getDesc());
            articleHolder.superChapter.setText(bean.getSuperChapter());
            articleHolder.chapter.setText(bean.getChapter());
            String picUrl = bean.getPic();
            if (!"".equals(picUrl)) { // 如果url不为空则加载封面的网络图片
                Glide.with(MyApplication.getContext())
                        .load(bean.getPic()).into(articleHolder.pic);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return VIEW_TYPE.REFRESH_VIEW.ordinal();
        }else if (position == aList.size() && footerView != null){
            return VIEW_TYPE.FOOTER_VIEW.ordinal();
        }else {
            return VIEW_TYPE.ARTICLE_VIEW.ordinal();
        }
    }

    @Override
    public int getItemCount() {
        return aList.size() + 1;
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        TextView author, time, title, desc, superChapter, chapter;
        ImageView collect, pic;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.tv_index_article_item_author);
            time = itemView.findViewById(R.id.tv_index_article_item_time);
            title = itemView.findViewById(R.id.tv_index_article_item_title);
            desc = itemView.findViewById(R.id.tv_index_article_item_desc);
            superChapter = itemView.findViewById(R.id.tv_index_article_item_super_chapter);
            chapter = itemView.findViewById(R.id.tv_index_article_item_chapter);
            collect = itemView.findViewById(R.id.iv_index_article_collect);
            pic = itemView.findViewById(R.id.iv_index_article_pic);
        }
    }

    public void setRefreshView(View refreshView) {
        this.refreshView = refreshView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }
}
