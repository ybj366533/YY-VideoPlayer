package com.ybj366533.videoplayer.video.base;

import android.content.Context;
import android.util.AttributeSet;

import com.danikula.videocache.HttpProxyCacheServer;
import com.ybj366533.videoplayer.VideoManager;

import java.io.File;

import tv.danmaku.ijk.media.player.IjkLibLoader;

/**
 * 兼容的空View，目前用于 GSYVideoManager的设置
 * Created by shuyu on 2016/11/11.
 */

public abstract class VideoPlayer extends BaseVideoPlayer {

    public VideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public VideoPlayer(Context context) {
        super(context);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置自定义so包加载类，必须在setUp之前调用
     * 不然setUp时会第一次实例化GSYVideoManager
     */
    public void setIjkLibLoader(IjkLibLoader libLoader) {
        VideoManager.setIjkLibLoader(libLoader);
    }

    @Override
    public MiGuVideoViewBridge getGSYVideoManager() {
        return VideoManager.instance();
    }

    @Override
    protected boolean backFromFull(Context context) {
        return VideoManager.backFromWindowFull(context);
    }

    @Override
    protected void releaseVideos() {
        VideoManager.releaseAllVideos();
    }

    @Override
    protected HttpProxyCacheServer getProxy(Context context, File file) {
        return VideoManager.getProxy(context, file);
    }

    @Override
    protected int getFullId() {
        return VideoManager.FULLSCREEN_ID;
    }

    @Override
    protected int getSmallId() {
        return VideoManager.SMALL_ID;
    }

}