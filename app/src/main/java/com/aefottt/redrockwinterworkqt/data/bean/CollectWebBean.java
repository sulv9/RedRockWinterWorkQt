package com.aefottt.redrockwinterworkqt.data.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class CollectWebBean implements Parcelable {
    private final String name;
    private final String link;

    public CollectWebBean(String name, String link) {
        this.name = name;
        this.link = link;
    }

    protected CollectWebBean(Parcel in) {
        name = in.readString();
        link = in.readString();
    }

    public static final Creator<CollectWebBean> CREATOR = new Creator<CollectWebBean>() {
        @Override
        public CollectWebBean createFromParcel(Parcel in) {
            return new CollectWebBean(in);
        }

        @Override
        public CollectWebBean[] newArray(int size) {
            return new CollectWebBean[size];
        }
    };

    public String getName() {
        return name;
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
        parcel.writeString(name);
        parcel.writeString(link);
    }
}
