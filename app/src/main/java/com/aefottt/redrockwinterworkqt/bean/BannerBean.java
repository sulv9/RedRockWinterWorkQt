package com.aefottt.redrockwinterworkqt.bean;

public class BannerBean {
    private String imagePath;
    private String url;

    public BannerBean(String imagePath, String url) {
        this.imagePath = imagePath;
        this.url = url;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getUrl() {
        return url;
    }
}
