package com.ybj366533.videoplayer.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.danikula.videocache.HttpProxyCacheServer;
import com.ybj366533.videoplayer.VideoManager;
import com.ybj366533.videoplayer.utils.CommonUtil;

import java.io.File;

import tv.danmaku.ijk.media.player.IjkLibLoader;



/**
 * 兼容的空View，目前用于 VideoManager的设置
 * Created by Summer on 2018/06/1.
 */

public abstract class BaseVideoPlayer extends BaseVideoView {

    public BaseVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public BaseVideoPlayer(Context context) {
        super(context);
    }

    public BaseVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseVideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置自定义so包加载类，必须在setUp之前调用
     * 不然setUp时会第一次实例化GSYVideoManager
     */
    public void setIjkLibLoader(IjkLibLoader libLoader) {
        VideoManager.setIjkLibLoader(libLoader);
    }

    /**
     * 单例管理器
     */
    @Override
    public VideoViewBridge getVideoManager() {
        return VideoManager.instance();
    }


    /**
     * 页面销毁了记得调用是否所有的video
     */
    @Override
    protected void releaseVideos() {
        VideoManager.releaseAllVideos();
    }

    /**
     * 获取全屏播放器对象
     *
     * @return VideoPlayer 如果没有则返回空。
     */
    @SuppressWarnings("ResourceType")
    public BaseVideoPlayer getFullWindowPlayer() {
        ViewGroup vp = (ViewGroup) (CommonUtil.scanForActivity(getContext())).findViewById(Window.ID_ANDROID_CONTENT);
        final View full = vp.findViewById(VideoManager.FULLSCREEN_ID);
        BaseVideoPlayer mVideoPlayer = null;
        if (full != null) {
            mVideoPlayer = (BaseVideoPlayer) full;
        }
        return mVideoPlayer;
    }

    /**
     * 获取小窗口播放器对象
     *
     * @return VideoPlayer 如果没有则返回空。
     */
    @SuppressWarnings("ResourceType")
    public BaseVideoPlayer getSmallWindowPlayer() {
        ViewGroup vp = (ViewGroup) (CommonUtil.scanForActivity(getContext())).findViewById(Window.ID_ANDROID_CONTENT);
        final View small = vp.findViewById(VideoManager.SMALL_ID);
        BaseVideoPlayer mVideoPlayer = null;
        if (small != null) {
            mVideoPlayer = (BaseVideoPlayer) small;
        }
        return mVideoPlayer;
    }

    /**
     * 获取当前长在播放的播放控件
     */
    public BaseVideoPlayer getCurrentPlayer() {
        if (getFullWindowPlayer() != null) {
            return getFullWindowPlayer();
        }
        if (getSmallWindowPlayer() != null) {
            return getSmallWindowPlayer();
        }
        return this;
    }
    @Override
    public HttpProxyCacheServer getProxy(Context context, File file) {
        return VideoManager.getProxy(context, file);
    }

}