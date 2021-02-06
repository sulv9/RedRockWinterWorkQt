package com.aefottt.redrockwinterworkqt.data.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class TreeBean implements Parcelable {
    private final String name;
    private final int id;

    public TreeBean(String name, int id) {
        this.name = name;
        this.id = id;
    }

    protected TreeBean(Parcel in) {
        name = in.readString();
        id = in.readInt();
    }

    public static final Creator<TreeBean> CREATOR = new Creator<TreeBean>() {
        @Override
        public TreeBean createFromParcel(Parcel in) {
            return new TreeBean(in);
        }

        @Override
        public TreeBean[] newArray(int size) {
            return new TreeBean[size];
        }
    };

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(id);
    }
}
