package com.ybj366533.yy_videoplayer.model;

public class PlayerVideoModel {
    private String path;
    private String hd_path;
    private String name;
    private Float longTime;
    private int imgPath;
    public PlayerVideoModel(){

    }
    public PlayerVideoModel(String path, String hd_path, String name, int imgPath, Float longTime) {
        this.path = path;
        this.hd_path = hd_path;
        this.name = name;
        this.imgPath = imgPath;
        this.longTime = longTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHd_path() {
        return hd_path;
    }

    public void setHd_path(String hd_path) {
        this.hd_path = hd_path;
    }

    public int getImgPath() {
        return imgPath;
    }

    public void setImgPath(int imgPath) {
        this.imgPath = imgPath;
    }

    public Float getLongTime() {
        return longTime;
    }

    public void setLongTime(Float longTime) {
        this.longTime = longTime;
    }
}
