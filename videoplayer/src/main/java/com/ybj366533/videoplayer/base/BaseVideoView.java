package com.ybj366533.videoplayer.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.danikula.videocache.HttpProxyCacheServer;
import com.ybj366533.videoplayer.listener.MediaPlayerListener;
import com.ybj366533.videoplayer.listener.VideoPlayerListener;
import com.ybj366533.videoplayer.utils.CommonUtil;
import com.ybj366533.videoplayer.utils.Debuger;
import com.ybj366533.videoplayer.utils.NetInfoModule;
import com.ybj366533.videoplayer.video.base.MiGuVideoViewBridge;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;


/**
 * 视频回调与状态处理等相关层
 * Created by Summer on 2018/6/2.
 */

public abstract class BaseVideoView extends TextureRenderView implements MediaPlayerListener {

    //正常
    public static final int CURRENT_STATE_NORMAL = 0;
    //准备中
    public static final int CURRENT_STATE_PREPAREING = 1;
    //播放中
    public static final int CURRENT_STATE_PLAYING = 2;
    //开始缓冲
    public static final int CURRENT_STATE_PLAYING_BUFFERING_START = 3;
    //暂停
    public static final int CURRENT_STATE_PAUSE = 5;
    //自动播放结束
    public static final int CURRENT_STATE_AUTO_COMPLETE = 6;
    //错误状态
    public static final int CURRENT_STATE_ERROR = 7;
    //避免切换时频繁setup
    public static final int CHANGE_DELAY_TIME = 2000;

    //当前的播放状态
    protected int mCurrentState = -1;

    //播放的tag，防止错误，因为普通的url也可能重复
    protected int mPlayPosition = -22;

    //屏幕宽度
    protected int mScreenWidth;

    //屏幕高度
    protected int mScreenHeight;

    //缓存进度
    protected int mBuffterPoint;

    //备份缓存前的播放状态
    protected int mBackUpPlayingBufferState = -1;

    //从哪个开始播放
    protected long mSeekOnStart = -1;

    //当前的播放位置
    protected long mCurrentPosition;

    //保存切换时的时间，避免频繁契合
    protected long mSaveChangeViewTIme = 0;

    //播放速度
    protected float mSpeed = 1;

    //是否播边边缓冲
    protected boolean mCache = false;

    //当前是否全屏
    protected boolean mIfCurrentIsFullscreen = false;

    //循环
    protected boolean mLooping = false;

    //是否播放过
    protected boolean mHadPlay = false;

    //是否是缓存的文件
    protected boolean mCacheFile = false;

    //是否发送了网络改变
    protected boolean mNetChanged = false;

    //是否不变调
    protected boolean mSoundTouch = false;

    //是否需要显示暂停锁定效果
    protected boolean mShowPauseCover = true;

    //是否准备完成前调用了暂停
    protected boolean mPauseBeforePrepared = false;

    //Prepared之后是否自动开始播放
    protected boolean mStartAfterPrepared = true;

    //Prepared
    protected boolean mHadPrepared = false;

    //是否播放器当失去音频焦点
    protected boolean mReleaseWhenLossAudio = true;

    //音频焦点的监听
    protected AudioManager mAudioManager;

    //播放的tag，防止错误，因为普通的url也可能重复
    protected String mPlayTag = "";

    //上下文
    protected Context mContext;

    //原来的url
    protected String mOriginUrl;

    //转化后的URL
    protected String mUrl;

    //网络状态
    protected String mNetSate = "NORMAL";

    //缓存路径，可不设置
    protected File mCachePath;

    //视频回调
    protected VideoPlayerListener playerListener;

    //http request header
    protected Map<String, String> mMapHeadData = new HashMap<>();

    //网络监听
    protected NetInfoModule mNetInfoModule;

    public BaseVideoView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public BaseVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseVideoView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public BaseVideoView(Context context, Boolean fullFlag) {
        super(context);
        mIfCurrentIsFullscreen = fullFlag;
        init(context);
    }

    @Override
    protected void showPauseCover() {
        if (mCurrentState == CURRENT_STATE_PAUSE && mFullPauseBitmap != null
                && !mFullPauseBitmap.isRecycled() && mShowPauseCover
                && mSurface != null && mSurface.isValid()) {
            try {
                RectF rectF = new RectF(0, 0, mTextureView.getWidth(), mTextureView.getHeight());
                Canvas canvas = mSurface.lockCanvas(new Rect(0, 0, mTextureView.getWidth(), mTextureView.getHeight()));
                if (canvas != null) {
                    canvas.drawBitmap(mFullPauseBitmap, null, rectF, null);
                    mSurface.unlockCanvasAndPost(canvas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void releasePauseCover() {
        try {
            if (mCurrentState != CURRENT_STATE_PAUSE && mFullPauseBitmap != null
                    && !mFullPauseBitmap.isRecycled() && mShowPauseCover) {
                mFullPauseBitmap.recycle();
                mFullPauseBitmap = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCurrentVideoWidth() {
        if (getVideoManager().getMediaPlayer() != null) {
            return getVideoManager().getMediaPlayer().getVideoWidth();
        }
        return 0;
    }

    @Override
    public int getCurrentVideoHeight() {
        if (getVideoManager().getMediaPlayer() != null) {
            return getVideoManager().getMediaPlayer().getVideoHeight();
        }
        return 0;
    }

    @Override
    public int getVideoSarNum() {
        if (getVideoManager().getMediaPlayer() != null) {
            return getVideoManager().getMediaPlayer().getVideoSarNum();
        }
        return 0;
    }

    @Override
    public int getVideoSarDen() {
        if (getVideoManager().getMediaPlayer() != null) {
            return getVideoManager().getMediaPlayer().getVideoSarDen();
        }
        return 0;
    }

    protected void updatePauseCover() {
        if ((mFullPauseBitmap == null || mFullPauseBitmap.isRecycled()) && mShowPauseCover) {
            try {
                initCover();
            } catch (Exception e) {
                e.printStackTrace();
                mFullPauseBitmap = null;
            }
        }
    }

    protected Context getActivityContext() {
        return CommonUtil.getActivityContext(getContext());
    }

    protected void init(Context context) {

        if (getActivityContext() != null) {
            this.mContext = getActivityContext();
        } else {
            this.mContext = context;
        }

        initInflate(mContext);

        mTextureViewContainer = (ViewGroup) findViewById(com.ybj366533.videoplayer.R.id.surface_container);
        if (isInEditMode())
            return;
        mScreenWidth = getActivityContext().getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getActivityContext().getResources().getDisplayMetrics().heightPixels;
        mAudioManager = (AudioManager) getActivityContext().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

    }

    protected void initInflate(Context context) {
        try {
            View.inflate(context, getLayoutId(), this);
        } catch (InflateException e) {
            if (e.toString().contains("GSYImageCover")) {
                Debuger.printfError("********************\n" +
                        "*****   注意   *****" +
                        "********************\n" +
                        "*该版本需要清除布局文件中的GSYImageCover\n" +
                        "****  Attention  ***\n" +
                        "*Please remove GSYImageCover from Layout in this Version\n" +
                        "********************\n");
                e.printStackTrace();
                throw new InflateException("该版本需要清除布局文件中的GSYImageCover，please remove GSYImageCover from your layout");
            } else {
                e.printStackTrace();
            }
        }
    }

    /**
     * 开始播放逻辑
     */
//    protected void startButtonLogic() {
//        Log.e("player", "onClickStart");
//        if (playerListener != null && mCurrentState == CURRENT_STATE_NORMAL) {
//            Debuger.printfLog("onClickStartIcon");
//            playerListener.onClickStartIcon(mOriginUrl,  this);
//        } else if (playerListener != null) {
//            Debuger.printfLog("onClickStartError");
//            playerListener.onClickStartError(mOriginUrl,  this);
//        }
//        prepareVideo();
//    }

    /**
     * 开始状态视频播放
     */
    protected void prepareVideo() {
        startPrepare();
    }

    protected void startPrepare() {
//        Log.e("player", "onStartPrepared");
//        startTime = System.currentTimeMillis();
        if (getVideoManager().listener() != null) {
            getVideoManager().listener().onCompletion();
        }
        if (playerListener != null) {
            playerListener.onVideoStartSet(mOriginUrl, this);
        }
        getVideoManager().setListener(this);
        getVideoManager().setPlayTag(mPlayTag);
        getVideoManager().setPlayPosition(mPlayPosition);
        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        ((Activity) getActivityContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mBackUpPlayingBufferState = -1;
        getVideoManager().prepare(mUrl, (mMapHeadData == null) ? new HashMap<String, String>() : mMapHeadData, mLooping, mSpeed);
        setStateAndUi(CURRENT_STATE_PREPAREING);
    }

    /**
     * 监听是否有外部其他多媒体开始播放
     */
    protected AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    post(new Runnable() {
                        @Override
                        public void run() {
                            if (mReleaseWhenLossAudio) {
                                releaseVideos();
                            } else {
                                onVideoPause();
                            }
                        }
                    });
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    try {
                        onVideoPause();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    break;
            }
        }
    };


    /**
     * 设置播放URL
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @return
     */
    public boolean setUp(String url, boolean cacheWithPlay) {
        return setUp(url, cacheWithPlay, ((File) null));
    }


    /**
     * 设置播放URL
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param cachePath     缓存路径，如果是M3U8或者HLS，请设置为false
     * @param mapHeadData   头部信息
     * @return
     */
    public boolean setUp(String url, boolean cacheWithPlay, File cachePath, Map<String, String> mapHeadData) {
        if (setUp(url, cacheWithPlay, cachePath)) {
            if (this.mMapHeadData != null) {
                this.mMapHeadData.clear();
            } else {
                this.mMapHeadData = new HashMap<>();
            }
            if (mapHeadData != null) {
                this.mMapHeadData.putAll(mapHeadData);
            }
            return true;
        }
        return false;
    }

    /**
     * 设置播放URL
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param cachePath     缓存路径，如果是M3U8或者HLS，请设置为false
     * @return
     */
    public boolean setUp(String url, boolean cacheWithPlay, File cachePath) {
        return setUp(url, cacheWithPlay, cachePath, true);
    }

    /**
     * 设置播放URL
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param cachePath     缓存路径，如果是M3U8或者HLS，请设置为false
     * @param changeState   是否修改状态
     * @return
     */
    protected boolean setUp(String url, boolean cacheWithPlay, File cachePath, boolean changeState) {
        mCache = cacheWithPlay;
        mCachePath = cachePath;
        mOriginUrl = url;
        if (isCurrentMediaListener() &&
                (System.currentTimeMillis() - mSaveChangeViewTIme) < CHANGE_DELAY_TIME)
            return false;
        mCurrentState = CURRENT_STATE_NORMAL;
        if (cacheWithPlay && url.startsWith("http") && !url.contains("127.0.0.1") && !url.contains(".m3u8")) {
            HttpProxyCacheServer proxy = getProxy(getActivityContext().getApplicationContext(), cachePath);
            if (proxy != null) {
                //此处转换了url，然后再赋值给mUrl。
                url = proxy.getProxyUrl(url);
                mCacheFile = (!url.startsWith("http"));
                //注册上缓冲监听
                if (!mCacheFile && getVideoManager() != null) {
                    proxy.registerCacheListener(getVideoManager().getCacheListener(), mOriginUrl);
                }
            }
        } else if (!cacheWithPlay && (!url.startsWith("http") && !url.startsWith("rtmp")
                && !url.startsWith("rtsp") && !url.contains(".m3u8"))) {
            mCacheFile = true;
        }
        this.mUrl = url;
        if (changeState)
            setStateAndUi(CURRENT_STATE_NORMAL);
        return true;
    }


    /**
     * 重置
     */
    public void onVideoReset() {
        setStateAndUi(CURRENT_STATE_NORMAL);
    }

    /**
     * 暂停状态
     */
    @Override
    public void onVideoPause() {
        if (mCurrentState == CURRENT_STATE_PREPAREING) {
            mPauseBeforePrepared = true;
        }
        try {
            if (getVideoManager().getMediaPlayer() != null &&
                    getVideoManager().getMediaPlayer().isPlaying()) {
                setStateAndUi(CURRENT_STATE_PAUSE);
                mCurrentPosition = getVideoManager().getMediaPlayer().getCurrentPosition();
                if (getVideoManager().getMediaPlayer() != null)
                    getVideoManager().getMediaPlayer().pause();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 恢复暂停状态
     */
    @Override
    public void onVideoResume() {
        onVideoResume(true);
    }

    /**
     * 恢复暂停状态
     *
     * @param seek 是否产生seek动作
     */
    @Override
    public void onVideoResume(boolean seek) {
        mPauseBeforePrepared = false;
        if (mCurrentState == CURRENT_STATE_PAUSE) {
            try {
                if (mCurrentPosition > 0 && getVideoManager().getMediaPlayer() != null) {
                    if (seek) {
                        getVideoManager().getMediaPlayer().seekTo(mCurrentPosition);
                    }
                    getVideoManager().getMediaPlayer().start();
                    setStateAndUi(CURRENT_STATE_PLAYING);
                    if (mAudioManager != null && !mReleaseWhenLossAudio) {
                        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                    }
                    mCurrentPosition = 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理因切换网络而导致的问题
     */
    protected void netWorkErrorLogic() {
        final long currentPosition = getCurrentPositionWhenPlaying();
        Debuger.printfError("******* Net State Changed. renew player to connect *******" + currentPosition);
        getVideoManager().releaseMediaPlayer();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setSeekOnStart(currentPosition);
                startPlayLogic();
            }
        }, 500);
    }


    /**
     * 播放错误的时候，删除缓存文件
     */
    protected void deleteCacheFileWhenError() {
        clearCurrentCache();
        Debuger.printfError("Link Or mCache Error, Please Try Again " + mOriginUrl);
        if (mCache) {
            Debuger.printfError("mCache Link " + mUrl);
        }
        mUrl = mOriginUrl;
    }

    @Override
    public void onPrepared() {
        if (mCurrentState != CURRENT_STATE_PREPAREING) return;

        mHadPrepared = true;

        if (playerListener != null && isCurrentMediaListener()) {
            playerListener.onVideoPrepared(mOriginUrl, this);
        }

        if (!mStartAfterPrepared) {
            setStateAndUi(CURRENT_STATE_PAUSE);
            return;
        }

        startAfterPrepared();
    }
    @Override
    public void onAutoCompletion() {
        setStateAndUi(CURRENT_STATE_AUTO_COMPLETE);

        mSaveChangeViewTIme = 0;

        if (mTextureViewContainer.getChildCount() > 0) {
            mTextureViewContainer.removeAllViews();
        }

        if (!mIfCurrentIsFullscreen)
            getVideoManager().setLastListener(null);
        mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
        ((Activity) getActivityContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        releaseNetWorkState();

        if (playerListener != null && isCurrentMediaListener()) {
            Debuger.printfLog("onAutoComplete");
            playerListener.onVideoAutoComplete(mOriginUrl, this);
        }
    }

    @Override
    public void onCompletion() {
        //make me normal first
        setStateAndUi(CURRENT_STATE_NORMAL);

        mSaveChangeViewTIme = 0;
        if (playerListener != null) {
            playerListener.onVideoCompletion(mOriginUrl, this);
        }

        if (mTextureViewContainer.getChildCount() > 0) {
            mTextureViewContainer.removeAllViews();
        }

        if (!mIfCurrentIsFullscreen) {
            getVideoManager().setListener(null);
            getVideoManager().setLastListener(null);
        }
        getVideoManager().setCurrentVideoHeight(0);
        getVideoManager().setCurrentVideoWidth(0);

        mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
        ((Activity) getActivityContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        releaseNetWorkState();

    }

    @Override
    public void onSeekComplete() {

    }

    @Override
    public void onError(int what, int extra) {

        if (mNetChanged) {
            mNetChanged = false;
            netWorkErrorLogic();
            if (playerListener != null) {
                playerListener.onVideoPlayError(mOriginUrl, this);
            }
            return;
        }

        if (what != 38 && what != -38) {
            setStateAndUi(CURRENT_STATE_ERROR);
            deleteCacheFileWhenError();
            if (playerListener != null) {
                playerListener.onVideoPlayError(mOriginUrl, this);
            }
        }
    }

    @Override
    public void onInfo(int what, int extra) {
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            mBackUpPlayingBufferState = mCurrentState;
            //避免在onPrepared之前就进入了buffering，导致一只loading
            if (mHadPlay && mCurrentState != CURRENT_STATE_PREPAREING && mCurrentState > 0)
                setStateAndUi(CURRENT_STATE_PLAYING_BUFFERING_START);

        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            if (mBackUpPlayingBufferState != -1) {
                if (mBackUpPlayingBufferState == CURRENT_STATE_PLAYING_BUFFERING_START) {
                    mBackUpPlayingBufferState = CURRENT_STATE_PLAYING;
                }
                if (mHadPlay && mCurrentState != CURRENT_STATE_PREPAREING && mCurrentState > 0)
                    setStateAndUi(mBackUpPlayingBufferState);

                mBackUpPlayingBufferState = -1;
            }
        } else if (what == IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED) {
            mRotate = extra;
            Debuger.printfLog("Video Rotate Info " + extra);
            if (mTextureView != null)
                mTextureView.setRotation(mRotate);
        }
    }

    @Override
    public void onVideoSizeChanged() {
        int mVideoWidth = getVideoManager().getCurrentVideoWidth();
        int mVideoHeight = getVideoManager().getCurrentVideoHeight();
        if (mVideoWidth != 0 && mVideoHeight != 0 && mTextureView != null) {
            mTextureView.requestLayout();
        }
    }

    @Override
    protected void setDisplay(Surface surface) {
        getVideoManager().setDisplay(surface);
    }

    @Override
    protected void releaseSurface(Surface surface) {
        getVideoManager().releaseSurface(surface);
    }

    /**
     * 清除当前缓存
     */
    public void clearCurrentCache() {
        //只有都为true时，才是缓存文件
        if (mCacheFile && mCache) {
            //是否为缓存文件
            Debuger.printfError(" mCacheFile Local Error " + mUrl);
            //可能是因为缓存文件除了问题
            CommonUtil.deleteFile(mUrl.replace("file://", ""));
            mUrl = mOriginUrl;
        }

    }

    /**
     * 获取当前播放进度
     */
    public int getCurrentPositionWhenPlaying() {
        int position = 0;
        if (mCurrentState == CURRENT_STATE_PLAYING || mCurrentState == CURRENT_STATE_PAUSE) {
            try {
                position = (int) getVideoManager().getMediaPlayer().getCurrentPosition();
            } catch (Exception e) {
                e.printStackTrace();
                return position;
            }
        }
        if (position == 0 && mCurrentPosition > 0) {
            return (int) mCurrentPosition;
        }
        return position;
    }

    /**
     * 获取当前总时长
     */
    public int getDuration() {
        int duration = 0;
        try {
            duration = (int) getVideoManager().getMediaPlayer().getDuration();
        } catch (Exception e) {
            e.printStackTrace();
            return duration;
        }
        return duration;
    }

    /**
     * 释放吧
     */
    public void release() {
        mSaveChangeViewTIme = 0;
        if (isCurrentMediaListener() &&
                (System.currentTimeMillis() - mSaveChangeViewTIme) > CHANGE_DELAY_TIME) {
            releaseVideos();
        }
    }

    /**
     * prepared成功之后会开始播放
     */
    public void startAfterPrepared() {

        if (!mHadPrepared) {
            prepareVideo();
        }

        try {
            if (getVideoManager().getMediaPlayer() != null) {
                getVideoManager().getMediaPlayer().start();
            }

            setStateAndUi(CURRENT_STATE_PLAYING);

            if (getVideoManager().getMediaPlayer() != null && mSeekOnStart > 0) {
                getVideoManager().getMediaPlayer().seekTo(mSeekOnStart);
                mSeekOnStart = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        addTextureView();

        createNetWorkState();

        listenerNetWorkState();

        mHadPlay = true;

        if (mTextureView != null) {
            mTextureView.onResume();
        }

        if (mPauseBeforePrepared) {
            onVideoPause();
            mPauseBeforePrepared = false;
        }
    }

    protected boolean isCurrentMediaListener() {
        return getVideoManager().listener() != null
                && getVideoManager().listener() == this;
    }

    /**
     * 创建网络监听
     */
    protected void createNetWorkState() {
        if (mNetInfoModule == null) {
            mNetInfoModule = new NetInfoModule(getActivityContext().getApplicationContext(), new NetInfoModule.NetChangeListener() {
                @Override
                public void changed(String state) {
                    if (!mNetSate.equals(state)) {
                        Debuger.printfError("******* change network state ******* " + state);
                        mNetChanged = true;
                    }
                    mNetSate = state;
                }
            });
            mNetSate = mNetInfoModule.getCurrentConnectionType();
        }
    }

    /**
     * 监听网络状态
     */
    protected void listenerNetWorkState() {
        if (mNetInfoModule != null) {
            mNetInfoModule.onHostResume();
        }
    }

    /**
     * 取消网络监听
     */
    protected void unListenerNetWorkState() {
        if (mNetInfoModule != null) {
            mNetInfoModule.onHostPause();
        }
    }

    /**
     * 释放网络监听
     */
    protected void releaseNetWorkState() {
        if (mNetInfoModule != null) {
            mNetInfoModule.onHostPause();
            mNetInfoModule = null;
        }
    }

    /************************* 需要继承处理部分 *************************/

    /**
     * 释放播放器
     */
    protected abstract void releaseVideos();

    /**
     * 设置播放显示状态
     *
     * @param state
     */
    protected abstract void setStateAndUi(int state);

    /**
     * 获取管理器桥接的实现
     */
    public abstract MiGuVideoViewBridge getVideoManager();

    /**
     * 当前UI
     */
    public abstract int getLayoutId();

    /**
     * 开始播放
     */
    public abstract void startPlayLogic();


    /************************* 公开接口 *************************/

    /**
     * 获取代理服务
     *
     * @param file 文件可以为空
     * @return 如果不需要可以为空
     */
    protected abstract HttpProxyCacheServer getProxy(Context context, File file);
    /**
     * 获取当前播放状态
     */
    public int getCurrentState() {
        return mCurrentState;
    }

    /**
     * 根据状态判断是否播放中
     */
    public boolean isInPlayingState() {
        return (mCurrentState >= 0 && mCurrentState != CURRENT_STATE_NORMAL
                && mCurrentState != CURRENT_STATE_AUTO_COMPLETE && mCurrentState != CURRENT_STATE_ERROR);
    }

    /**
     * 播放tag防止错误，因为普通的url也可能重复
     */
    public String getPlayTag() {
        return mPlayTag;
    }

    /**
     * 播放tag防止错误，因为普通的url也可能重复
     *
     * @param playTag 保证不重复就好
     */
    public void setPlayTag(String playTag) {
        this.mPlayTag = playTag;
    }


    public int getPlayPosition() {
        return mPlayPosition;
    }

    /**
     * 设置播放位置防止错位
     */
    public void setPlayPosition(int playPosition) {
        this.mPlayPosition = playPosition;
    }

    /**
     * 网络速度
     * 注意，这里如果是开启了缓存，因为读取本地代理，缓存成功后还是存在速度的
     * 再打开已经缓存的本地文件，网络速度才会回0.因为是播放本地文件了
     */
    public long getNetSpeed() {
        if (getVideoManager().getMediaPlayer() != null
                && (getVideoManager().getMediaPlayer() instanceof IjkMediaPlayer)) {
            try {
                return ((IjkMediaPlayer) getVideoManager().getMediaPlayer()).getTcpSpeed();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return -1;
        } else {
            return -1;
        }

    }

    /**
     * 网络速度
     * 注意，这里如果是开启了缓存，因为读取本地代理，缓存成功后还是存在速度的
     * 再打开已经缓存的本地文件，网络速度才会回0.因为是播放本地文件了
     */
    public String getNetSpeedText() {
        long speed = getNetSpeed();
        return CommonUtil.getTextSpeed(speed);
    }

    public long getSeekOnStart() {
        return mSeekOnStart;
    }

    /**
     * 从哪里开始播放
     * 目前有时候前几秒有跳动问题，毫秒
     * 需要在startPlayLogic之前，即播放开始之前
     */
    public void setSeekOnStart(long seekOnStart) {
        this.mSeekOnStart = seekOnStart;
    }

    /**
     * 缓冲进度/缓存进度
     */
    public int getBuffterPoint() {
        return mBuffterPoint;
    }

    /**
     * 是否全屏
     */
    public boolean isIfCurrentIsFullscreen() {
        return mIfCurrentIsFullscreen;
    }

    public void setIfCurrentIsFullscreen(boolean ifCurrentIsFullscreen) {
        this.mIfCurrentIsFullscreen = ifCurrentIsFullscreen;
    }

    public boolean isLooping() {
        return mLooping;
    }

    /**
     * 设置循环
     */
    public void setLooping(boolean looping) {
        this.mLooping = looping;
    }


    /**
     * 设置播放过程中的回调
     *
     * @param playerListener
     */
    public void setVideoPlayerListener(VideoPlayerListener playerListener) {
        this.playerListener = playerListener;
    }

    public float getSpeed() {
        return mSpeed;
    }

    /**
     * 播放速度
     */
    public void setSpeed(float speed) {
        setSpeed(speed, false);
    }

    /**
     * 播放速度
     *
     * @param speed      速度
     * @param soundTouch 是否对6.0下开启变速不变调
     */
    public void setSpeed(float speed, boolean soundTouch) {
        this.mSpeed = speed;
        this.mSoundTouch = soundTouch;
        if (getVideoManager().getMediaPlayer() != null) {
            getVideoManager().setSpeed(speed, soundTouch);
        }
    }

    /**
     * 播放中生效的播放数据
     *
     * @param speed
     * @param soundTouch
     */
    public void setSpeedPlaying(float speed, boolean soundTouch) {
        setSpeed(speed, soundTouch);
        if (getVideoManager().getMediaPlayer() != null) {
            if (getVideoManager().getMediaPlayer() instanceof IjkMediaPlayer) {
                IjkMediaPlayer ijkMediaPlayer = (IjkMediaPlayer) getVideoManager().getMediaPlayer();
                try {
                    ijkMediaPlayer.setSpeed(speed);
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", (soundTouch) ? 1 : 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isShowPauseCover() {
        return mShowPauseCover;
    }

    /**
     * 是否需要加载显示暂停的cover图片
     * 打开状态下，暂停退到后台，再回到前台不会显示黑屏，但可以对某些机型有概率出现OOM
     * 关闭情况下，暂停退到后台，再回到前台显示黑屏
     *
     * @param showPauseCover 默认true
     */
    public void setShowPauseCover(boolean showPauseCover) {
        this.mShowPauseCover = showPauseCover;
    }

    /**
     * seekto what you want
     */
    public void seekTo(long position) {
        try {
            if (getVideoManager().getMediaPlayer() != null && position > 0) {
                getVideoManager().getMediaPlayer().seekTo(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isStartAfterPrepared() {
        return mStartAfterPrepared;
    }

    /**
     * 准备成功之后立即播放
     *
     * @param startAfterPrepared 默认true，false的时候需要在prepared后调用startAfterPrepared()
     */
    public void setStartAfterPrepared(boolean startAfterPrepared) {
        this.mStartAfterPrepared = startAfterPrepared;
    }

    public boolean isReleaseWhenLossAudio() {
        return mReleaseWhenLossAudio;
    }

    /**
     * 长时间失去音频焦点，暂停播放器
     *
     * @param releaseWhenLossAudio 默认true，false的时候只会暂停
     */
    public void setReleaseWhenLossAudio(boolean releaseWhenLossAudio) {
        this.mReleaseWhenLossAudio = releaseWhenLossAudio;
    }

    public Map<String, String> getMapHeadData() {
        return mMapHeadData;
    }

    /**
     * 单独设置mapHeader
     *
     * @param headData
     */
    public void setMapHeadData(Map<String, String> headData) {
        if (headData != null) {
            this.mMapHeadData = headData;
        }
    }
}
