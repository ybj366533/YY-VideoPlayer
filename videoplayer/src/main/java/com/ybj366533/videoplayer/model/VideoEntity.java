package com.ybj366533.videoplayer.model;

/**
 *
 */

public class VideoEntity {

    private String mUrl;
    private String mTitle;

    public VideoEntity(String url, String title) {
        mUrl = url;
        mTitle = title;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }
}
