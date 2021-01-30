package com.aefottt.redrockwinterworkqt.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aefottt.redrockwinterworkqt.MyApplication;
import com.aefottt.redrockwinterworkqt.R;
import com.aefottt.redrockwinterworkqt.bean.BannerBean;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BannerRvAdapter extends RecyclerView.Adapter<BannerRvAdapter.viewHolder> {
    private final ArrayList<BannerBean> bannerList;

    public BannerRvAdapter(ArrayList<BannerBean> bannerList) {
        this.bannerList = bannerList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner_index, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Glide.with(MyApplication.getContext())
                .load(bannerList.get(position % bannerList.size()).getImagePath()).into(holder.iv);
    }

    @Override
    public int getItemCount() {
        if (bannerList.size() == 0) {
            return 0;
        } else if (bannerList.size() == 1) {
            return 1;
        }
        return Integer.MAX_VALUE;
    }


    class viewHolder extends RecyclerView.ViewHolder {
        ImageView iv;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv_banner);
            itemView.setOnClickListener((view) -> {
                //TODO 点击跳转对应的url
                BannerBean bean = bannerList.get(getAdapterPosition() % bannerList.size());
            });
        }
    }

}
