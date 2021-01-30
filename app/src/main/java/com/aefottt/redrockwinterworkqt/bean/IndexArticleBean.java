package com.aefottt.redrockwinterworkqt.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class IndexArticleBean implements Parcelable {
    private final String author;
    private final String time;
    private final String title;
    private final String desc;
    private final String superChapter;
    private final String chapter;
    private final String pic;
    private final String link;
    private boolean isCollected; //判断该文章是否已被收藏

    public IndexArticleBean(String author, String time, String title, String desc, String superChapter, String chapter, String pic, String link) {
        this.author = author;
        this.time = time;
        this.title = title;
        this.desc = desc;
        this.superChapter = superChapter;
        this.chapter = chapter;
        this.pic = pic;
        this.link = link;
    }

    protected IndexArticleBean(Parcel in) {
        author = in.readString();
        time = in.readString();
        title = in.readString();
        desc = in.readString();
        superChapter = in.readString();
        chapter = in.readString();
        pic = in.readString();
        link = in.readString();
        isCollected = in.readByte() != 0;
    }

    public static final Creator<IndexArticleBean> CREATOR = new Creator<IndexArticleBean>() {
        @Override
        public IndexArticleBean createFromParcel(Parcel in) {
            return new IndexArticleBean(in);
        }

        @Override
        public IndexArticleBean[] newArray(int size) {
            return new IndexArticleBean[size];
        }
    };

    public String getAuthor() {
        return author;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getSuperChapter() {
        return superChapter;
    }

    public String getChapter() {
        return chapter;
    }

    public String getPic() {
        return pic;
    }

    public String getLink() {
        return link;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(author);
        parcel.writeString(time);
        parcel.writeString(title);
        parcel.writeString(desc);
        parcel.writeString(superChapter);
        parcel.writeString(chapter);
        parcel.writeString(pic);
        parcel.writeString(link);
        parcel.writeByte((byte) (isCollected ? 1 : 0));
    }
}
