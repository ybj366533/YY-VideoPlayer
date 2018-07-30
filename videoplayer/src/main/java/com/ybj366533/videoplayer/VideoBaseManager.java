package com.ybj366533.videoplayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.file.Md5FileNameGenerator;
import com.danikula.videocache.headers.HeaderInjector;
import com.ybj366533.videoplayer.listener.MediaPlayerListener;
import com.ybj366533.videoplayer.model.VideoModel;
import com.ybj366533.videoplayer.model.VideoOptionModel;
import com.ybj366533.videoplayer.player.EXO2PlayerManager;
import com.ybj366533.videoplayer.player.IJKPlayerManager;
import com.ybj366533.videoplayer.player.IPlayerManager;
import com.ybj366533.videoplayer.player.SystemPlayerManager;
import com.ybj366533.videoplayer.utils.CommonUtil;
import com.ybj366533.videoplayer.utils.Debuger;
import com.ybj366533.videoplayer.utils.FileOperation;
import com.ybj366533.videoplayer.utils.FileUtils;
import com.ybj366533.videoplayer.utils.MyFileNameGenerator;
import com.ybj366533.videoplayer.utils.StorageUtils;
import com.ybj366533.videoplayer.utils.VideoType;
import com.ybj366533.videoplayer.base.VideoViewBridge;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkLibLoader;

/**
 * 基类管理器
 */

public abstract class VideoBaseManager implements IMediaPlayer.OnPreparedListener, IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnSeekCompleteListener, IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnVideoSizeChangedListener, IMediaPlayer.OnInfoListener,CacheListener, VideoViewBridge {

    public static String TAG = "VideoBaseManager";
    //准备
    private static final int HANDLER_PREPARE = 0;
    //显示
    private static final int HANDLER_SETDISPLAY = 1;
    //发布
    private static final int HANDLER_RELEASE = 2;
    //释放表面
    private static final int HANDLER_RELEASE_SURFACE = 3;

    private static final int BUFFER_TIME_OUT_ERROR = -192;//外部超时错误码

    //单例模式实在不好给instance()加参数，还是直接设为静态变量吧
    //自定义so包加载类
    protected static IjkLibLoader ijkLibLoader;

    protected MediaHandler mMediaHandler;

    protected Handler mainThreadHandler;

    protected WeakReference<MediaPlayerListener> listener;

    protected WeakReference<MediaPlayerListener> lastListener;

    //配置ijk option
    protected List<VideoOptionModel> optionModelList;

    //视频代理
    protected HttpProxyCacheServer proxy;

    //是否需要的自定义缓冲路径
    protected File cacheFile;

    //播放的tag，防止错位置，因为普通的url也可能重复
    protected String playTag = "";

    //header for cache
    protected Map<String, String> mMapHeadData;

    protected Context context;

    protected IPlayerManager playerManager;

    //当前播放的视频宽的高
    protected int currentVideoWidth = 0;

    //当前播放的视屏的高
    protected int currentVideoHeight = 0;

    //当前视频的最后状态
    protected int lastState;

    //播放的tag，防止错位置，因为普通的url也可能重复
    protected int playPosition = -22;

    //缓冲比例
    protected int buffterPoint;

    //播放超时
    protected int timeOut = 8 * 1000;

    //播放类型，默认IJK
    protected int videoType = VideoType.IJKPLAYER;

    //是否需要静音
    protected boolean needMute = false;

    //是否需要外部超时判断
    protected boolean needTimeOutOther;

    private long mPrepareStartTime = 0;
    private long mOpenInputEndTime = 0;
    private long mFindStreamEndTime = 0;
    private long mOpenComponentTime = 0;
    private long mPrepareEndTime = 0;
    private long mFirstVideoPktTime = 0;
    private long mFirstVideoDecodeEndTime = 0;
    private long mFirstVideoDisplayEndTime = 0;


    /**
     * 设置自定义so包加载类
     * 需要在instance之前设置
     */
    public static void setIjkLibLoader(IjkLibLoader libLoader) {
        IJKPlayerManager.setIjkLibLoader(libLoader);
        ijkLibLoader = libLoader;
    }


    public static IjkLibLoader getIjkLibLoader() {
        return ijkLibLoader;
    }


    /**
     * 创建缓存代理服务,带文件目录的.
     */
    public HttpProxyCacheServer newProxy(Context context, File file) {
        if (!file.exists()) {
            file.mkdirs();
        }
        HttpProxyCacheServer.Builder builder = new HttpProxyCacheServer.Builder(context);
        builder.cacheDirectory(file);
        builder.fileNameGenerator(new MyFileNameGenerator());
        builder.headerInjector(new UserAgentHeadersInjector());
        cacheFile = file;
        return builder.build();
    }

    public void setProxy(HttpProxyCacheServer proxy) {
        this.proxy = proxy;
    }

    /**
     * 创建缓存代理服务
     */
    public HttpProxyCacheServer newProxy(Context context) {
        return new HttpProxyCacheServer.Builder(context.getApplicationContext())
                .headerInjector(new UserAgentHeadersInjector()).build();
    }

    /***
     * @param libLoader 是否使用外部动态加载so
     * */
    protected void init(IjkLibLoader libLoader) {
        playerManager = getPlayManager(VideoType.IJKPLAYER);
        IJKPlayerManager.setIjkLibLoader(libLoader);
        HandlerThread mediaHandlerThread = new HandlerThread(TAG);
        mediaHandlerThread.start();
        mMediaHandler = new MediaHandler((mediaHandlerThread.getLooper()));
        mainThreadHandler = new Handler();
    }

    protected IPlayerManager getPlayManager(int videoType) {
        switch (videoType) {
            case VideoType.IJKEXOPLAYER2:
                return new EXO2PlayerManager();
            case VideoType.SYSTEMPLAYER:
                return new SystemPlayerManager();
            case VideoType.IJKPLAYER:
            default:
                return new IJKPlayerManager();
        }
    }

    @Override
    public MediaPlayerListener listener() {
        if (listener == null)
            return null;
        return listener.get();
    }

    @Override
    public MediaPlayerListener lastListener() {
        if (lastListener == null)
            return null;
        return lastListener.get();
    }

    @Override
    public void setListener(MediaPlayerListener listener) {
        if (listener == null)
            this.listener = null;
        else
            this.listener = new WeakReference<>(listener);
    }

    @Override
    public void setLastListener(MediaPlayerListener lastListener) {
        if (lastListener == null)
            this.lastListener = null;
        else
            this.lastListener = new WeakReference<>(lastListener);
    }

    @Override
    public void setSpeed(float speed, boolean soundTouch) {
        if (playerManager != null) {
            playerManager.setSpeed(speed, soundTouch);
        }
    }

    @Override
    public void prepare(final String url, final Map<String, String> mapHeadData, boolean loop, float speed) {
        if (TextUtils.isEmpty(url)) return;
        Message msg = new Message();
        msg.what = HANDLER_PREPARE;
        mMapHeadData = mapHeadData;
        VideoModel fb = new VideoModel(url, mapHeadData, loop, speed);
        msg.obj = fb;
        mMediaHandler.sendMessage(msg);
        if (needTimeOutOther) {
            startTimeOutBuffer();
        }
    }

    @Override
    public void releaseMediaPlayer() {
        Message msg = new Message();
        msg.what = HANDLER_RELEASE;
        mMediaHandler.sendMessage(msg);
        playTag = "";
        playPosition = -22;
    }

    @Override
    public void setDisplay(Surface holder) {
        Message msg = new Message();
        msg.what = HANDLER_SETDISPLAY;
        msg.obj = holder;
        mMediaHandler.sendMessage(msg);
    }

    @Override
    public void releaseSurface(Surface holder) {
        Message msg = new Message();
        msg.what = HANDLER_RELEASE_SURFACE;
        msg.obj = holder;
        mMediaHandler.sendMessage(msg);
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        mPrepareEndTime = System.currentTimeMillis();

        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                cancelTimeOutBuffer();
                Log.e(TAG, "onPrepared");
                if (listener() != null) {
                    listener().onPrepared();
                }
            }
        });
    }

    @Override
    public void onCompletion(IMediaPlayer mp) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                cancelTimeOutBuffer();
                Log.e(TAG, "onCompletion");
                if (listener() != null) {
                    listener().onAutoCompletion();
                }
            }
        });
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer mp, final int percent) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {

                if (listener() != null) {
//                    Log.e(TAG, "onBufferingUpdate");
                    if (percent > buffterPoint) {
                        listener().onBufferingUpdate(percent);
                    } else {
                        listener().onBufferingUpdate(buffterPoint);
                    }
                }
            }
        });
    }

    @Override
    public void onSeekComplete(IMediaPlayer mp) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                cancelTimeOutBuffer();

                if (listener() != null) {
                    listener().onSeekComplete();
                }
            }
        });
    }

    @Override
    public boolean onError(IMediaPlayer mp, final int what, final int extra) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                cancelTimeOutBuffer();
                Log.e(TAG, "onError");
                if (listener() != null) {
                    listener().onError(what, extra);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onInfo(IMediaPlayer mp, final int what, final int extra) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {

                if (needTimeOutOther) {
                    if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                        startTimeOutBuffer();
                    } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                        cancelTimeOutBuffer();
                    }
                }

                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_OPEN_INPUT:
                        mOpenInputEndTime = System.currentTimeMillis();
//                        mHudViewHolder.updateOpenInputCost(mOpenInputEndTime - mPrepareStartTime);
                        long OpenInputCost = mOpenInputEndTime - mPrepareStartTime;
                        Log.e(TAG, "PERF: MEDIA_INFO_OPEN_INPUT\n" + "updateOpenInputCost = " + OpenInputCost);
                        break;
                    case IMediaPlayer.MEDIA_INFO_FIND_STREAM_INFO:
                        mFindStreamEndTime = System.currentTimeMillis();
//                        IjkMediaPlayer ijk = (IjkMediaPlayer) mMediaPlayer;
//                        mHudViewHolder.updateFindStreamCost(mFindStreamEndTime - mPrepareStartTime, ijk.getTrafficStatisticByteCount());
                        long FindStreamCost = mFindStreamEndTime - mPrepareStartTime;
                        Log.e(TAG, "PERF: MEDIA_INFO_FIND_STREAM_INFO\n" + "FindStreamCost = " + FindStreamCost);
                        break;
                    case IMediaPlayer.MEDIA_INFO_COMPONENT_OPEN:
                        mOpenComponentTime = System.currentTimeMillis();
//                        mHudViewHolder.updateOpenComponentCost(mOpenComponentTime - mPrepareStartTime);
                        long OpenComponentCost = mOpenComponentTime - mPrepareStartTime;
                        Log.e(TAG, "PERF: MEDIA_INFO_COMPONENT_OPEN\n" + "OpenComponentCost = " + OpenComponentCost);
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_FIRSTPKT_GOT:
                        mFirstVideoPktTime = System.currentTimeMillis();
//                        mHudViewHolder.updateFirstVideoPktCost(mFirstVideoPktTime - mPrepareStartTime);
                        long FirstVideoPktCost = mFirstVideoPktTime - mPrepareStartTime;
                        Log.e(TAG, "PERF: MEDIA_INFO_VIDEO_FIRSTPKT_GOT\n" + "FirstVideoPktCost = " + FirstVideoPktCost);
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_DECODED_START:
                        mFirstVideoDecodeEndTime = System.currentTimeMillis();
//                        mHudViewHolder.updateFirstVideoDecodeCost(mFirstVideoDecodeEndTime - mPrepareStartTime);
                        long FirstVideoDecodeCost = mFirstVideoDecodeEndTime - mPrepareStartTime;
                        Log.e(TAG, "PERF: MEDIA_INFO_VIDEO_DECODED_START\n" + "FirstVideoDecodeCost = " + FirstVideoDecodeCost);
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                        Log.e(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING:");
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        mFirstVideoDisplayEndTime = System.currentTimeMillis();
                        long FirstVideoDisplayCost = mFirstVideoDisplayEndTime - mPrepareStartTime;
//                        mHudViewHolder.updateFirstVideoDisplayCost(mFirstVideoDisplayEndTime - mPrepareStartTime);

                        String log = FileOperation.getTime() + ">>>>>>>>>"+getMediaPlayer().getDataSource()+"\nPERF: MEDIA_INFO_VIDEO_RENDERING_START:" + "FirstVideoDisplayCost = " + FirstVideoDisplayCost + "\n";
                        if (FileOperation.saveTxt(log, context, Environment.getExternalStorageDirectory() + File.separator + "gtvIjk")) {
                            Log.e(TAG, "PERF: MEDIA_INFO_VIDEO_RENDERING_START:" + "FirstVideoDisplayCost = " + FirstVideoDisplayCost);
                        }
                        Log.e(TAG, "PERF: MEDIA_INFO_VIDEO_RENDERING_START:" + "FirstVideoDisplayCost = " + FirstVideoDisplayCost);

                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        Log.e(TAG, "MEDIA_INFO_BUFFERING_START:");
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        Log.e(TAG, "MEDIA_INFO_BUFFERING_END:");
                        break;
                    case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                        Log.e(TAG, "MEDIA_INFO_NETWORK_BANDWIDTH: " + extra);
                        break;
                    case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                        Log.e(TAG, "MEDIA_INFO_BAD_INTERLEAVING:");
                        break;
                    case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                        Log.e(TAG, "MEDIA_INFO_NOT_SEEKABLE:");
                        break;
                    case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                        Log.e(TAG, "MEDIA_INFO_METADATA_UPDATE:");
                        break;
                    case IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                        Log.e(TAG, "MEDIA_INFO_UNSUPPORTED_SUBTITLE:");
                        break;
                    case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                        Log.e(TAG, "MEDIA_INFO_SUBTITLE_TIMED_OUT:");
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
//                        mVideoRotationDegree = arg2;
                        Log.d(TAG, "MEDIA_INFO_VIDEO_ROTATION_CHANGED: " + extra);
//                        if (mRenderView != null)
//                            mRenderView.setVideoRotation(arg2);
                        break;
                    case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                        Log.d(TAG, "MEDIA_INFO_AUDIO_RENDERING_START:");
                        break;
                }
                if (listener() != null) {
                    listener().onInfo(what, extra);
                }
            }
        });
        return false;
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
        currentVideoWidth = mp.getVideoWidth();
        currentVideoHeight = mp.getVideoHeight();
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (listener() != null) {
                    listener().onVideoSizeChanged();
                }
            }
        });
    }

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
//        Log.e(TAG,url);
        buffterPoint = percentsAvailable;
    }

    @Override
    public CacheListener getCacheListener() {
        return this;
    }

    @Override
    public IMediaPlayer getMediaPlayer() {
        if (playerManager != null) {
            return playerManager.getMediaPlayer();
        }
        return null;
    }


    @Override
    public int getLastState() {
        return lastState;
    }

    @Override
    public void setLastState(int lastState) {
        this.lastState = lastState;
    }

    @Override
    public int getCurrentVideoWidth() {
        return currentVideoWidth;
    }

    @Override
    public int getCurrentVideoHeight() {
        return currentVideoHeight;
    }

    @Override
    public void setCurrentVideoHeight(int currentVideoHeight) {
        this.currentVideoHeight = currentVideoHeight;
    }

    @Override
    public void setCurrentVideoWidth(int currentVideoWidth) {
        this.currentVideoWidth = currentVideoWidth;
    }

    @Override
    public String getPlayTag() {
        return playTag;
    }

    @Override
    public void setPlayTag(String playTag) {
        this.playTag = playTag;
    }

    @Override
    public int getPlayPosition() {
        return playPosition;
    }

    @Override
    public void setPlayPosition(int playPosition) {
        this.playPosition = playPosition;
    }


    private class MediaHandler extends Handler {

        MediaHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_PREPARE:
                    initVideo(msg);
                    break;
                case HANDLER_SETDISPLAY:
                    showDisplay(msg);
                    break;
                case HANDLER_RELEASE:
                    if (playerManager != null) {
                        playerManager.release();
                    }
                    setNeedMute(false);
                    if (proxy != null) {
                        proxy.unregisterCacheListener(VideoBaseManager.this);
                    }
                    buffterPoint = 0;
                    cancelTimeOutBuffer();
                    break;
                case HANDLER_RELEASE_SURFACE:
                    releaseSurface(msg);
                    break;
            }
        }

    }

    private void initVideo(Message msg) {
        try {
            currentVideoWidth = 0;
            currentVideoHeight = 0;

            if (playerManager != null) {
                playerManager.release();
            }

            playerManager = getPlayManager(videoType);

            playerManager.initVideoPlayer(context, msg, optionModelList);
            setNeedMute(needMute);
            IMediaPlayer mediaPlayer = playerManager.getMediaPlayer();
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnSeekCompleteListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnInfoListener(this);
            mediaPlayer.setOnVideoSizeChangedListener(this);

            mPrepareStartTime = System.currentTimeMillis();

            mediaPlayer.prepareAsync();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 启动十秒的定时器进行 缓存操作
     */
    private void startTimeOutBuffer() {
        // 启动定时
        Debuger.printfError("startTimeOutBuffer");
        mainThreadHandler.postDelayed(mTimeOutRunnable, timeOut);

    }

    /**
     * 取消 十秒的定时器进行 缓存操作
     */
    private void cancelTimeOutBuffer() {
        Debuger.printfError("cancelTimeOutBuffer");
        // 取消定时
        if (needTimeOutOther)
            mainThreadHandler.removeCallbacks(mTimeOutRunnable);
    }


    private Runnable mTimeOutRunnable = new Runnable() {
        @Override
        public void run() {
            if (listener != null) {
                Debuger.printfError("time out for error listener");
                listener().onError(BUFFER_TIME_OUT_ERROR, BUFFER_TIME_OUT_ERROR);
            }
        }
    };

    private void releaseSurface(Message msg) {
        if (msg.obj != null) {
            if (playerManager != null) {
                playerManager.releaseSurface();
            }
        }
    }

    /**
     * 后面再修改设计模式吧，现在先用着
     */
    private void showDisplay(Message msg) {
        if (playerManager != null) {
            playerManager.showDisplay(msg);
        }
    }

    /**
     * for android video cache header
     */
    private class UserAgentHeadersInjector implements HeaderInjector {

        @Override
        public Map<String, String> addHeaders(String url) {
            Log.e("HeaderInjector",url);
            return (mMapHeadData == null) ? new HashMap<String, String>() : mMapHeadData;
        }
    }

    public int getVideoType() {
        return videoType;
    }


    /**
     * 设置了视频的播放类型
     * IJKPLAYER = 0; 默认IJK
     * IJKEXOPLAYER2 = 2;EXOPlayer2 (最好配合VideoType.SUFRACE)
     * SYSTEMPLAYER = 4;系统播放器 (最好配合VideoType.SUFRACE)
     */
    public void setVideoType(Context context, int videoType) {
        this.context = context.getApplicationContext();
        this.videoType = videoType;
    }

    /**
     * 打开raw播放支持
     *
     * @param context
     */
    public void enableRawPlay(Context context) {
        this.context = context.getApplicationContext();
    }

    public List<VideoOptionModel> getOptionModelList() {
        return optionModelList;
    }

    /**
     * 设置IJK视频的option
     */
    public void setOptionModelList(List<VideoOptionModel> optionModelList) {
        this.optionModelList = optionModelList;
    }

    public boolean isNeedMute() {
        return needMute;
    }

    /**
     * 是否需要静音
     */
    public void setNeedMute(boolean needMute) {
        this.needMute = needMute;
        if (playerManager != null) {
            playerManager.setNeedMute(needMute);
        }
    }

    public int getTimeOut() {
        return timeOut;
    }

    public boolean isNeedTimeOutOther() {
        return needTimeOutOther;
    }

    /**
     * 是否需要在buffer缓冲时，增加外部超时判断
     * <p>
     * 超时后会走onError接口，播放器通过onPlayError回调出
     * <p>
     * 错误码为 ： BUFFER_TIME_OUT_ERROR = -192
     * <p>
     * 由于onError之后执行VideoPlayer的OnError，如果不想触发错误，
     * 可以重载onError，在super之前拦截处理。
     * <p>
     * public void onError(int what, int extra){
     * do you want before super and return;
     * super.onError(what, extra)
     * }
     *
     * @param timeOut          超时时间，毫秒 默认8000
     * @param needTimeOutOther 是否需要延时设置，默认关闭
     */
    public void setTimeOut(int timeOut, boolean needTimeOutOther) {
        this.timeOut = timeOut;
        this.needTimeOutOther = needTimeOutOther;
    }

    /**
     * 设置log输入等级
     */
    public void setLogLevel(int logLevel) {
        IJKPlayerManager.setLogLevel(logLevel);
    }

}
