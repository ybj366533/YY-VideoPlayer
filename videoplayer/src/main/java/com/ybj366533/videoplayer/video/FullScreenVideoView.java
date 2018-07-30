package com.ybj366533.videoplayer.video;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.Toast;

import com.danikula.videocache.HttpProxyCacheServer;
import com.ybj366533.videoplayer.R;
import com.ybj366533.videoplayer.VideoManager;
import com.ybj366533.videoplayer.base.BaseVideoPlayer;
import com.ybj366533.videoplayer.listener.MediaPlayerListener;
import com.ybj366533.videoplayer.utils.NetworkUtils;
import com.ybj366533.videoplayer.video.base.PageVideoControlView;
import com.ybj366533.videoplayer.widget.InfoHudViewHolder;

import java.io.File;

import tv.danmaku.ijk.media.player.IMediaPlayer;


/**
 * 标准播放器，继承之后实现一些ui显示效果，如显示／隐藏ui，播放按键等
 */

public class FullScreenVideoView extends PageVideoControlView {
    private String TAG = "FullScreenVideoView";

    private TableLayout mHudView;
    private InfoHudViewHolder mHudViewHolder;
    private VideoManager mManager;
    private long mPrepareStartTime = 0;
    private long mOpenInputEndTime = 0;
    private long mFindStreamEndTime = 0;
    private long mOpenComponentTime = 0;
    private long mPrepareEndTime = 0;
    private long mFirstVideoPktTime = 0;
    private long mFirstVideoDecodeEndTime = 0;
    private long mFirstVideoDisplayEndTime = 0;

    public FullScreenVideoView(Context context) {
        super(context);
    }

    public FullScreenVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void init(Context context) {
        super.init(context);
        mHudView = (TableLayout) findViewById(R.id.hud_view);
        setHudView(mHudView);
    }

    /**
     * 继承后重写可替换为你需要的布局
     *
     * @return
     */
    @Override
    public int getLayoutId() {
        return R.layout.video_layout_page;
    }

    public void setHudView(TableLayout tableLayout) {
        mHudViewHolder = new InfoHudViewHolder(getContext(), tableLayout);
//        if (mHudViewHolder != null)
//            mHudViewHolder.setMediaPlayer(MiGuVideoViewBridge);
    }

    /**
     * 显示wifi确定框
     */
    @Override
    public void startPlayLogic() {
        prepareVideo();
    }

    /**
     * 显示wifi确定框，如需要自定义继承重写即可
     */
    @Override
    protected void showWifiDialog() {
        if (!NetworkUtils.isAvailable(mContext)) {
            Toast.makeText(mContext, getResources().getString(R.string.no_net), Toast.LENGTH_LONG).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
        builder.setMessage(getResources().getString(R.string.tips_not_wifi));
        builder.setPositiveButton(getResources().getString(R.string.tips_not_wifi_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startPlayLogic();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.tips_not_wifi_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void onAutoCompletion() {
        super.onAutoCompletion();
        releasemManager();
    }

    @Override
    public void onCompletion() {
        super.onCompletion();
        releasemManager();
    }

    private void resolveChangeUrl(boolean cacheWithPlay, File cachePath, String url) {
        if (mManager != null) {
            mCache = cacheWithPlay;
            mCachePath = cachePath;
            mOriginUrl = url;
            if (cacheWithPlay && url.startsWith("http") && !url.contains("127.0.0.1") && !url.contains(".m3u8")) {
                HttpProxyCacheServer proxy = (cachePath != null) ?
                        mManager.newProxy(getActivityContext().getApplicationContext(), cachePath) : mManager.newProxy(getActivityContext().getApplicationContext());
                //此处转换了url，然后再赋值给mUrl。
                Log.e("CalculatorHelper###", url);
                url = proxy.getProxyUrl(url);
                mCacheFile = (!url.startsWith("http"));
                mManager.setProxy(proxy);
                //注册上缓冲监听
                if (!mCacheFile && VideoManager.instance() != null) {
                    proxy.registerCacheListener(VideoManager.instance(), mOriginUrl);
                }
            } else if (!cacheWithPlay && (!url.startsWith("http") && !url.startsWith("rtmp")
                    && !url.startsWith("rtsp") && !url.contains(".m3u8"))) {
                mCacheFile = true;
            }
            this.mUrl = url;
        }
    }


    private MediaPlayerListener mMediaPlayerListener = new MediaPlayerListener() {
        @Override
        public void onPrepared() {
//            Log.e(TAG, "onPrepared");
        }

        @Override
        public void onAutoCompletion() {
//            Log.e(TAG, "onAutoCompletion");
        }

        @Override
        public void onCompletion() {
//            Log.e(TAG, "onCompletion");
        }

        @Override
        public void onBufferingUpdate(int percent) {
//            Log.e(TAG, "onBufferingUpdate");
            if (mManager != null)
                mManager.getMediaPlayer().stop();
        }

        @Override
        public void onSeekComplete() {
            Log.e(TAG, "onSeekComplete");
        }

        @Override
        public void onError(int what, int extra) {
//            Log.e(TAG, "onError");
            releasemManager();
        }

        @Override
        public void onInfo(int what, int extra) {
            if (what == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                if (mManager != null)
                    mManager.getMediaPlayer().stop();
//                releasemManager();
            }
//            Log.e(TAG, "onInfo");
        }

        @Override
        public void onVideoSizeChanged() {
//            Log.e(TAG, "onVideoSizeChanged");
        }

        @Override
        public void onBackFullscreen() {
//            Log.e(TAG, "onBackFullscreen");
        }

        @Override
        public void onVideoPause() {
//            Log.e(TAG, "onVideoPause");
        }

        @Override
        public void onVideoResume() {
//            Log.e(TAG, "onVideoResume");
        }

        @Override
        public void onVideoResume(boolean seek) {
//            Log.e(TAG, "onVideoResume");
        }
    };

    public void resolveStartChange(String url) {
        if (VideoManager.instance().getMediaPlayer() != null) {
            //创建临时管理器执行加载播放
            mManager = VideoManager.tmpInstance(mMediaPlayerListener);
            resolveChangeUrl(mCache, mCachePath, url);
            mManager.prepare(mUrl, mMapHeadData, mLooping, mSpeed);
            changeUiToPlayingBufferingShow();
        }
    }

    private void releasemManager() {
        if (mManager != null) {
            mManager.listener().onCompletion();
            mManager.releaseMediaPlayer();
            mManager = null;

        }
    }

    /********************************各类UI的状态显示*********************************************/

    /**
     * 点击触摸显示和隐藏逻辑
     */
    @Override
    protected void onClickUiToggle() {

    }


    /**
     * 正常
     */
    @Override
    protected void changeUiToNormal() {

    }

    /**
     * 准备中
     */
    @Override
    protected void changeUiToPreparingShow() {

    }

    /**
     * 播放中
     */
    @Override
    protected void changeUiToPlayingShow() {

    }

    /**
     * 暂停中
     */
    @Override
    protected void changeUiToPauseShow() {
        updatePauseCover();
    }

    /**
     * 开始缓冲
     */
    @Override
    protected void changeUiToPlayingBufferingShow() {
    }

    /**
     * 自动播放结束
     */
    @Override
    protected void changeUiToCompleteShow() {

    }

    /**
     * 错误状态
     */
    @Override
    protected void changeUiToError() {

    }

    @Override
    public void onBackFullscreen() {

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
    public void onPrepared() {
        super.onPrepared();
        mPrepareEndTime = System.currentTimeMillis();
        mHudViewHolder.updateLoadCost(mPrepareEndTime - mPrepareStartTime);
    }

    @Override
    public void onInfo(int what, int extra) {
        super.onInfo(what, extra);

        switch (what) {
            case IMediaPlayer.MEDIA_INFO_OPEN_INPUT:
                mOpenInputEndTime = System.currentTimeMillis();
                mHudViewHolder.updateOpenInputCost(mOpenInputEndTime - mPrepareStartTime);
                Log.d(TAG, "PERF: MEDIA_INFO_OPEN_INPUT\n");
                break;
            case IMediaPlayer.MEDIA_INFO_FIND_STREAM_INFO:
                mFindStreamEndTime = System.currentTimeMillis();
//                IjkMediaPlayer ijk = (IjkMediaPlayer);
//                mHudViewHolder.updateFindStreamCost(mFindStreamEndTime - mPrepareStartTime, ijk.getTrafficStatisticByteCount());
                Log.d(TAG, "PERF: MEDIA_INFO_FIND_STREAM_INFO\n");
                break;
            case IMediaPlayer.MEDIA_INFO_COMPONENT_OPEN:
                mOpenComponentTime = System.currentTimeMillis();
                mHudViewHolder.updateOpenComponentCost(mOpenComponentTime - mPrepareStartTime);
                Log.d(TAG, "PERF: MEDIA_INFO_COMPONENT_OPEN\n");
                break;
            case IMediaPlayer.MEDIA_INFO_VIDEO_FIRSTPKT_GOT:
                mFirstVideoPktTime = System.currentTimeMillis();
                mHudViewHolder.updateFirstVideoPktCost(mFirstVideoPktTime - mPrepareStartTime);
                Log.d(TAG, "PERF: MEDIA_INFO_VIDEO_FIRSTPKT_GOT\n");
                break;
            case IMediaPlayer.MEDIA_INFO_VIDEO_DECODED_START:
                mFirstVideoDecodeEndTime = System.currentTimeMillis();
                mHudViewHolder.updateFirstVideoDecodeCost(mFirstVideoDecodeEndTime - mPrepareStartTime);
                Log.d(TAG, "PERF: MEDIA_INFO_VIDEO_DECODED_START\n");
                break;
            case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                Log.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING:");
                break;
            case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                mFirstVideoDisplayEndTime = System.currentTimeMillis();
                mHudViewHolder.updateFirstVideoDisplayCost(mFirstVideoDisplayEndTime - mPrepareStartTime);
                Log.d(TAG, "PERF: MEDIA_INFO_VIDEO_RENDERING_START:");
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                Log.d(TAG, "MEDIA_INFO_BUFFERING_START:");
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                Log.d(TAG, "MEDIA_INFO_BUFFERING_END:");
                break;
            case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                Log.d(TAG, "MEDIA_INFO_NETWORK_BANDWIDTH: " + extra);
                break;
            case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING:");
                break;
            case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE:");
                break;
            case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE:");
                break;
            case IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                Log.d(TAG, "MEDIA_INFO_UNSUPPORTED_SUBTITLE:");
                break;
            case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                Log.d(TAG, "MEDIA_INFO_SUBTITLE_TIMED_OUT:");
                break;
            case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
//                mVideoRotationDegree = arg2;
                Log.d(TAG, "MEDIA_INFO_VIDEO_ROTATION_CHANGED: " + extra);
//                if (mRenderView != null)
//                    mRenderView.setVideoRotation(arg2);
                break;
            case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                Log.d(TAG, "MEDIA_INFO_AUDIO_RENDERING_START:");
                break;
        }
    }
}
