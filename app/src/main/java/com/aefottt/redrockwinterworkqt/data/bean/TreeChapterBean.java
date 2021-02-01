package com.aefottt.redrockwinterworkqt.data.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class TreeChapterBean implements Parcelable {
    private final String chapterName; //二级目录名称
    private final int articleId; //文章对应的id

    public TreeChapterBean(String chapterName, int id) {
        this.chapterName = chapterName;
        this.articleId = id;
    }

    protected TreeChapterBean(Parcel in) {
        chapterName = in.readString();
        articleId = in.readInt();
    }

    public static final Creator<TreeChapterBean> CREATOR = new Creator<TreeChapterBean>() {
        @Override
        public TreeChapterBean createFromParcel(Parcel in) {
            return new TreeChapterBean(in);
        }

        @Override
        public TreeChapterBean[] newArray(int size) {
            return new TreeChapterBean[size];
        }
    };

    public String getChapterName() {
        return chapterName;
    }

    public int getArticleId() {
        return articleId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(chapterName);
        parcel.writeInt(articleId);
    }
}
