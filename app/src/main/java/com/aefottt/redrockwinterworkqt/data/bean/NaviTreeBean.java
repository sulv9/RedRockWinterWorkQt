package com.aefottt.redrockwinterworkqt.data.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class NaviTreeBean implements Parcelable {
    private final String name; //导航名称
    private final ArrayList<NaviTitleBean> titleBeans; //导航对应的Titles
    private boolean isSelected; //是否被选中

    public NaviTreeBean(String name, ArrayList<NaviTitleBean> titleBeans) {
        this.name = name;
        this.titleBeans = titleBeans;
    }

    protected NaviTreeBean(Parcel in) {
        name = in.readString();
        titleBeans = in.createTypedArrayList(NaviTitleBean.CREATOR);
    }

    public static final Creator<NaviTreeBean> CREATOR = new Creator<NaviTreeBean>() {
        @Override
        public NaviTreeBean createFromParcel(Parcel in) {
            return new NaviTreeBean(in);
        }

        @Override
        public NaviTreeBean[] newArray(int size) {
            return new NaviTreeBean[size];
        }
    };

    public String getName() {
        return name;
    }

    public ArrayList<NaviTitleBean> getTitleBeans() {
        return titleBeans;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeTypedList(titleBeans);
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
