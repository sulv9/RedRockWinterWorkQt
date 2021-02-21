package com.aefottt.redrockwinterworkqt.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.aefottt.redrockwinterworkqt.R;
import com.aefottt.redrockwinterworkqt.data.bean.ArticleBean;
import com.aefottt.redrockwinterworkqt.data.bean.BannerBean;
import com.aefottt.redrockwinterworkqt.util.Utility;
import com.aefottt.redrockwinterworkqt.view.fragment.IndexFragment;
import com.aefottt.redrockwinterworkqt.view.my.MyApplication;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class IndexArticleRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<ArticleBean> aList;
    private final ArrayList<BannerBean> bannerList;
    private View refreshView;
    private View footerView;

    public enum VIEW_TYPE {
        REFRESH_VIEW,
        BANNER_VIEW,
        ARTICLE_VIEW,
        FOOTER_VIEW
    }

    public IndexArticleRecyclerAdapter(ArrayList<ArticleBean> aList, ArrayList<BannerBean> bannerList) {
        this.aList = aList;
        this.bannerList = bannerList;
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
        }else if (viewType == VIEW_TYPE.FOOTER_VIEW.ordinal()){
            footerView.setVisibility(View.GONE);
            return new RecyclerView.ViewHolder(footerView) {};
        }else if (viewType == VIEW_TYPE.BANNER_VIEW.ordinal()) { // Banner布局
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_rv_index_banner, parent, false);
            // 对Banner进行初始化操作
            ArticleViewHolder holder = new ArticleViewHolder(view);
            initIndicator(holder.indicatorContainer);
            return holder;
        } else { // 中间文章布局
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_rv_article, parent, false);
            return new ArticleViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE.BANNER_VIEW.ordinal()) {
            ArticleViewHolder bannerHolder = (ArticleViewHolder) holder;
            // 设置BannerRecycler的布局管理器及适配器
            LinearLayoutManager bannerManager = new LinearLayoutManager(MyApplication.getContext());
            bannerManager.setOrientation(RecyclerView.HORIZONTAL);
            bannerHolder.rvBanner.setLayoutManager(bannerManager);
            BannerRvAdapter bannerAdapter = new BannerRvAdapter(bannerList);
            // 设置Banner点击事件
            bannerAdapter.setOnClickBannerListener(url -> {
                bannerListener.onClickBanner(url);
            });
            bannerHolder.rvBanner.setAdapter(bannerAdapter);
            if (IndexFragment.mCurrentBannerPosition == 0) {
                // 第一次移动到第1000页
                IndexFragment.mCurrentBannerPosition = 1000 * bannerList.size();
            }
            bannerHolder.rvBanner.scrollToPosition(IndexFragment.mCurrentBannerPosition);
            // 更新指示器颜色
            IndexFragment.updateIndicator(bannerHolder.indicatorContainer);
            // 自动播放Banner回调
            bannerListener.sendAutoPlayHandler(bannerHolder);
            // 添加BannerRecycler滑动监听事件 -- 滑动时停止自动播放
            bannerHolder.rvBanner.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        // 滑动状态停止自动播放
                        IndexFragment.isAutoPlay = false;
                    }
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        // 停止滑动后继续播放
                        IndexFragment.isAutoPlay = true;
                    }
                }
            });
            // 设置PagerSnapHelper使BannerRecycler实现像ViewPager那样的效果
            PagerSnapHelper snapHelper = new PagerSnapHelper() {
                @Override
                public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                    // 当前滑动的项
                    int target = super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
                    // 每次滑动更新当前位置以及指示器颜色
                    IndexFragment.mCurrentBannerPosition = target;
                    IndexFragment.updateIndicator(bannerHolder.indicatorContainer);
                    return target;
                }
            };
            bannerHolder.rvBanner.setOnFlingListener(null); //TODO 这点很重要，否则会出现onFlingListener already set报错
            // 绑定snapHelper到BannerRecycler上
            snapHelper.attachToRecyclerView(bannerHolder.rvBanner);
        } else if (getItemViewType(position) == VIEW_TYPE.ARTICLE_VIEW.ordinal()) {
            ArticleViewHolder articleHolder = (ArticleViewHolder) holder;
            ArticleBean bean = aList.get(position - 2);
            // 设置值
            articleHolder.author.setText(bean.getAuthor());
            articleHolder.time.setText(bean.getTime());
            articleHolder.title.setText(bean.getTitle());
            articleHolder.desc.setText(bean.getDesc());
            articleHolder.superChapter.setText(bean.getSuperChapter());
            articleHolder.chapter.setText(bean.getChapter());
            articleHolder.collect.setBackgroundResource(R.mipmap.icon_article_dislove);
            String picUrl = bean.getPic();
            if (!"".equals(picUrl)) { // 如果url不为空则加载封面的网络图片
                Glide.with(MyApplication.getContext())
                        .load(bean.getPic()).into(articleHolder.pic);
            }
            articleHolder.itemView.setOnClickListener(v->{
                articleListener.onClickListener(bean.getLink());
            });
            articleHolder.collect.setOnClickListener(view -> {
                if ("dislove".contentEquals((String) view.getTag())){
                    view.setBackgroundResource(R.mipmap.icon_article_love);
                    view.setTag("love");
                }else if ("love".contentEquals((String) view.getTag())){
                    view.setBackgroundResource(R.mipmap.icon_article_dislove);
                    view.setTag("dislove");
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 1) {
            return VIEW_TYPE.BANNER_VIEW.ordinal();
        }else if (position == aList.size()){
            return VIEW_TYPE.FOOTER_VIEW.ordinal();
        }else if (position == 0){
            return VIEW_TYPE.REFRESH_VIEW.ordinal();
        }
        return VIEW_TYPE.ARTICLE_VIEW.ordinal();
    }

    @Override
    public int getItemCount() {
        return aList.size() + 1;
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        TextView author, time, title, desc, superChapter, chapter;
        ImageView collect, pic;
        public RecyclerView rvBanner;
        public LinearLayout indicatorContainer;

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
            rvBanner = itemView.findViewById(R.id.rv_index_banner);
            indicatorContainer = itemView.findViewById(R.id.ll_banner_dots);
        }
    }

    public void setRefreshView(View refreshView) {
        this.refreshView = refreshView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public interface BannerListener {
        void sendAutoPlayHandler(ArticleViewHolder holder);
        void onClickBanner(String url);
    }

    private BannerListener bannerListener;

    public void setBannerListener(IndexArticleRecyclerAdapter.BannerListener bannerListener) {
        this.bannerListener = bannerListener;
    }

    public interface ArticleListener{
        void onClickListener(String url);
    }
    private ArticleListener articleListener;

    public void setArticleListener(ArticleListener articleListener) {
        this.articleListener = articleListener;
    }

    /**
     * 初始化指示器
     */
    private void initIndicator(LinearLayout indicatorContainer) {
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
}
