package com.aefottt.redrockwinterworkqt.data.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class TreeSuperChapterBean implements Parcelable {
    private final String superChapterName; //一级分类名称
    private final ArrayList<TreeChapterBean> chapters; //一级分类对应的二类分类

    public TreeSuperChapterBean(String superChapterName, ArrayList<TreeChapterBean> chapters) {
        this.superChapterName = superChapterName;
        this.chapters = chapters;
    }

    protected TreeSuperChapterBean(Parcel in) {
        superChapterName = in.readString();
        chapters = in.createTypedArrayList(TreeChapterBean.CREATOR);
    }

    public static final Creator<TreeSuperChapterBean> CREATOR = new Creator<TreeSuperChapterBean>() {
        @Override
        public TreeSuperChapterBean createFromParcel(Parcel in) {
            return new TreeSuperChapterBean(in);
        }

        @Override
        public TreeSuperChapterBean[] newArray(int size) {
            return new TreeSuperChapterBean[size];
        }
    };

    public String getSuperChapterName() {
        return superChapterName;
    }

    public ArrayList<TreeChapterBean> getChapters() {
        return chapters;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(superChapterName);
        parcel.writeTypedList(chapters);
    }
}
