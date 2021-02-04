package com.aefottt.redrockwinterworkqt.data.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class NaviTitleBean implements Parcelable {
    private final String title; //Title名称
    private final String link; //Title对应的网址

    public NaviTitleBean(String title, String link) {
        this.title = title;
        this.link = link;
    }

    protected NaviTitleBean(Parcel in) {
        title = in.readString();
        link = in.readString();
    }

    public static final Creator<NaviTitleBean> CREATOR = new Creator<NaviTitleBean>() {
        @Override
        public NaviTitleBean createFromParcel(Parcel in) {
            return new NaviTitleBean(in);
        }

        @Override
        public NaviTitleBean[] newArray(int size) {
            return new NaviTitleBean[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(link);
    }
}
