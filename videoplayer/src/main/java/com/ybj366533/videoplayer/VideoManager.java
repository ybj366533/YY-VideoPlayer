package com.ybj366533.videoplayer;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

//import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.file.Md5FileNameGenerator;
import com.ybj366533.videoplayer.listener.MediaPlayerListener;
import com.ybj366533.videoplayer.utils.CommonUtil;
import com.ybj366533.videoplayer.utils.FileUtils;
import com.ybj366533.videoplayer.utils.StorageUtils;
import com.ybj366533.videoplayer.video.base.BaseVideoPlayer;

import java.io.File;

import tv.danmaku.ijk.media.player.IjkLibLoader;


/**
 * 视频管理，单例
 */

public class VideoManager extends VideoBaseManager {

    public static final int SMALL_ID = R.id.small_id;

    public static final int FULLSCREEN_ID = R.id.full_id;

    public static String TAG = "VideoManager";

    @SuppressLint("StaticFieldLeak")
    private static VideoManager videoManager;

    /***
     * @param libLoader 是否使用外部动态加载so
     * */
    private VideoManager(IjkLibLoader libLoader) {
        init(libLoader);
    }

    /**
     * 单例管理器
     */
    public static synchronized VideoManager instance() {
        if (videoManager == null) {
            videoManager = new VideoManager(ijkLibLoader);
        }
        return videoManager;
    }

    /**
     * 同步创建一个临时管理器
     */
    public static synchronized VideoManager tmpInstance(MediaPlayerListener listener) {
        VideoManager mVideoManager = new VideoManager(ijkLibLoader);
        mVideoManager.buffterPoint = videoManager.buffterPoint;
        mVideoManager.optionModelList = videoManager.optionModelList;
        mVideoManager.cacheFile = videoManager.cacheFile;
        mVideoManager.playTag = videoManager.playTag;
        mVideoManager.mMapHeadData = videoManager.mMapHeadData;
        mVideoManager.currentVideoWidth = videoManager.currentVideoWidth;
        mVideoManager.currentVideoHeight = videoManager.currentVideoHeight;
        mVideoManager.context = videoManager.context;
        mVideoManager.lastState = videoManager.lastState;
        mVideoManager.playPosition = videoManager.playPosition;
        mVideoManager.timeOut = videoManager.timeOut;
        mVideoManager.videoType = videoManager.videoType;
        mVideoManager.needMute = videoManager.needMute;
        mVideoManager.needTimeOutOther = videoManager.needTimeOutOther;
        mVideoManager.setListener(listener);
        return mVideoManager;
    }


    /**
     * 替换管理器
     */
    public static synchronized void changeManager(VideoManager mVideoManager) {
        videoManager = mVideoManager;
    }


    /**
     * 获取缓存代理服务
     */
    protected static HttpProxyCacheServer getProxy(Context context) {
        HttpProxyCacheServer proxy = VideoManager.instance().proxy;
        return proxy == null ? (VideoManager.instance().proxy =
                VideoManager.instance().newProxy(context)) : proxy;
    }

    /**
     * 删除默认所有缓存文件
     */
    public static void clearAllDefaultCache(Context context) {

        File dataDir = context.getApplicationContext().getExternalFilesDir(null);
        String path = dataDir.getAbsolutePath() + File.separator + "gtvijk/cache";
        FileUtils.deleteFiles(new File(path));
    }

    /**
     * 删除url对应默认缓存文件
     */
    public static void clearDefaultCache(Context context, String url) {
        Md5FileNameGenerator md5FileNameGenerator = new Md5FileNameGenerator();
        String name = md5FileNameGenerator.generate(url);
        String pathTmp = StorageUtils.getIndividualCacheDirectory
                (context.getApplicationContext()).getAbsolutePath()
                + File.separator + name + ".download";
        String path = StorageUtils.getIndividualCacheDirectory
                (context.getApplicationContext()).getAbsolutePath()
                + File.separator + name;
        CommonUtil.deleteFile(pathTmp);
        CommonUtil.deleteFile(path);

    }

    /**
     * 获取缓存代理服务,带文件目录的
     */
    public static HttpProxyCacheServer getProxy(Context context, File file) {

        //如果为空，返回默认的
        if (file == null) {
            return getProxy(context);
        }

        //如果已经有缓存文件路径，那么判断缓存文件路径是否一致
        if (VideoManager.instance().cacheFile != null
                && !VideoManager.instance().cacheFile.getAbsolutePath().equals(file.getAbsolutePath())) {
            //不一致先关了旧的
            HttpProxyCacheServer proxy = VideoManager.instance().proxy;

            if (proxy != null) {
                proxy.shutdown();
            }
            //开启新的
            return (VideoManager.instance().proxy =
                    VideoManager.instance().newProxy(context, file));
        } else {
            //还没有缓存文件的或者一致的，返回原来
            HttpProxyCacheServer proxy = VideoManager.instance().proxy;
            return proxy == null ? (VideoManager.instance().proxy =
                    VideoManager.instance().newProxy(context, file)) : proxy;
        }
    }

    /**
     * 退出全屏，主要用于返回键
     *
     * @return 返回是否全屏
     */
    @SuppressWarnings("ResourceType")
    public static boolean backFromWindowFull(Context context) {
        boolean backFrom = false;
        ViewGroup vp = (ViewGroup) (CommonUtil.scanForActivity(context)).findViewById(Window.ID_ANDROID_CONTENT);
        View oldF = vp.findViewById(FULLSCREEN_ID);
        if (oldF != null) {
            backFrom = true;
            CommonUtil.hideNavKey(context);
            if (VideoManager.instance().lastListener() != null) {
                VideoManager.instance().lastListener().onBackFullscreen();
            }
        }
        return backFrom;
    }

    /**
     * 页面销毁了记得调用是否所有的video
     */
    public static void releaseAllVideos() {
        if (VideoManager.instance().listener() != null) {
            VideoManager.instance().listener().onCompletion();
        }
        VideoManager.instance().releaseMediaPlayer();
    }


    /**
     * 暂停播放
     */
    public static void onPause() {
        if (VideoManager.instance().listener() != null) {
            VideoManager.instance().listener().onVideoPause();
        }
    }

    /**
     * 恢复播放
     */
    public static void onResume() {
        if (VideoManager.instance().listener() != null) {
            VideoManager.instance().listener().onVideoResume();
        }
    }


    /**
     * 恢复暂停状态
     *
     * @param seek 是否产生seek动作,直播设置为false
     */
    public static void onResume(boolean seek) {
        if (VideoManager.instance().listener() != null) {
            VideoManager.instance().listener().onVideoResume(seek);
        }
    }

    /**
     * 当前是否全屏状态
     *
     * @return 当前是否全屏状态， true代表是。
     */
    @SuppressWarnings("ResourceType")
    public static boolean isFullState(Activity activity) {
        ViewGroup vp = (ViewGroup) (CommonUtil.scanForActivity(activity)).findViewById(Window.ID_ANDROID_CONTENT);
        final View full = vp.findViewById(FULLSCREEN_ID);
        BaseVideoPlayer mVideoPlayer = null;
        if (full != null) {
            mVideoPlayer = (BaseVideoPlayer) full;
        }
        return mVideoPlayer != null;
    }

}