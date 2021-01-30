package com.aefottt.redrockwinterworkqt.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class BannerBean implements Parcelable {
    private final String imagePath;
    private final String url;

    public BannerBean(String imagePath, String url) {
        this.imagePath = imagePath;
        this.url = url;
    }

    protected BannerBean(Parcel in) {
        imagePath = in.readString();
        url = in.readString();
    }

    public static final Creator<BannerBean> CREATOR = new Creator<BannerBean>() {
        @Override
        public BannerBean createFromParcel(Parcel in) {
            return new BannerBean(in);
        }

        @Override
        public BannerBean[] newArray(int size) {
            return new BannerBean[size];
        }
    };

    public String getImagePath() {
        return imagePath;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(imagePath);
        parcel.writeString(url);
    }
}
