package com.ybj366533.yy_videoplayer.model;

/**
 */

public class VideoModel {
    private int id;
    private String name;
    private String imgPath;
    private String videoPath;
    private Boolean isCache;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public void setCache(Boolean cache) {
        isCache = cache;
    }

    public Boolean getCache() {
        return isCache;
    }
}
