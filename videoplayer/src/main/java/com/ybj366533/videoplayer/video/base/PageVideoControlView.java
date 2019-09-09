package com.ybj366533.videoplayer.video.base;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ybj366533.videoplayer.R;
import com.ybj366533.videoplayer.base.BaseVideoPlayer;
import com.ybj366533.videoplayer.utils.Debuger;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 播放UI的显示、控制层、手势处理等
 * Created by guoshuyu on 2017/8/2.
 */

public abstract class PageVideoControlView extends BaseVideoPlayer{


    //lazy的setup
    protected boolean mSetUpLazy = false;

    //进度定时器
    protected Timer updateProcessTimer;

    //定时器任务
    protected ProgressTimerTask mProgressTimerTask;

    public PageVideoControlView(@NonNull Context context) {
        super(context);
    }

    public PageVideoControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PageVideoControlView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void init(Context context) {
        super.init(context);
        if (isInEditMode())
            return;

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelProgressTimer();
    }

    @Override
    public void onAutoCompletion() {
        super.onAutoCompletion();

    }

    @Override
    public void onError(int what, int extra) {
        super.onError(what, extra);

    }

    /**
     * 设置播放显示状态
     *
     * @param state
     */
    @Override
    protected void setStateAndUi(int state) {
        mCurrentState = state;

        if ((state == CURRENT_STATE_NORMAL && isCurrentMediaListener())
                || state == CURRENT_STATE_AUTO_COMPLETE || state == CURRENT_STATE_ERROR) {
            mHadPrepared = false;
        }

        switch (mCurrentState) {
            case CURRENT_STATE_NORMAL:
                if (isCurrentMediaListener()) {
                    cancelProgressTimer();
                    getVideoManager().releaseMediaPlayer();
                    releasePauseCover();
                    mBuffterPoint = 0;
                    mSaveChangeViewTIme = 0;
                }
                if (mAudioManager != null) {
                    mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
                }
                releaseNetWorkState();
                break;
            case CURRENT_STATE_PREPAREING:
//                resetProgressAndTime();
                break;
            case CURRENT_STATE_PLAYING:
                startProgressTimer();
                break;
            case CURRENT_STATE_PAUSE:
                startProgressTimer();
                break;
            case CURRENT_STATE_ERROR:
                if (isCurrentMediaListener()) {
                    getVideoManager().releaseMediaPlayer();
                }
                break;
            case CURRENT_STATE_AUTO_COMPLETE:
                cancelProgressTimer();
                break;
        }
        resolveUIState(state);
    }

    /**
     * 设置播放URL
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @return
     */
    @Override
    public boolean setUp(String url, boolean cacheWithPlay) {
        return setUp(url, cacheWithPlay, null);
    }

    /**
     * 设置播放URL
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param cachePath     缓存路径，如果是M3U8或者HLS，请设置为false
     * @return
     */
    @Override
    public boolean setUp(String url, boolean cacheWithPlay, File cachePath) {
        return super.setUp(url, cacheWithPlay, cachePath);
    }



    @Override
    public void onPrepared() {
        super.onPrepared();
        if (mCurrentState != CURRENT_STATE_PREPAREING) return;
        startProgressTimer();
    }


    @Override
    public void onBufferingUpdate(int percent) {
        if (mCurrentState != CURRENT_STATE_NORMAL && mCurrentState != CURRENT_STATE_PREPAREING) {
            if (percent != 0) {
                setTextAndProgress(percent);
                mBuffterPoint = percent;
                Debuger.printfLog("Net speed: " + getNetSpeedText() + " percent " + percent);
            }

        }
    }

    /**
     * 增对列表优化，在播放前的时候才进行setup
     */
    @Override
    protected void prepareVideo() {
        if (mSetUpLazy) {
            super.setUp(mOriginUrl,
                    mCache,
                    mCachePath,
                    mMapHeadData);
        }
        super.prepareVideo();
    }


    /**
     * 处理控制显示
     *
     * @param state
     */
    protected void resolveUIState(int state) {
        switch (state) {
            case CURRENT_STATE_NORMAL://正常
                changeUiToNormal();
                break;
            case CURRENT_STATE_PREPAREING://准备中
                changeUiToPreparingShow();
                break;
            case CURRENT_STATE_PLAYING://播放中
                changeUiToPlayingShow();
                break;
            case CURRENT_STATE_PAUSE://暂停
                changeUiToPauseShow();
                break;
            case CURRENT_STATE_ERROR: //错误状态
                changeUiToError();
                break;
            case CURRENT_STATE_AUTO_COMPLETE://自动播放结束
                changeUiToCompleteShow();
                break;
            case CURRENT_STATE_PLAYING_BUFFERING_START://开始缓冲
                changeUiToPlayingBufferingShow();
                break;
        }
    }


    protected void startProgressTimer() {
        cancelProgressTimer();
        updateProcessTimer = new Timer();
        mProgressTimerTask = new ProgressTimerTask();
        updateProcessTimer.schedule(mProgressTimerTask, 0, 300);
    }

    protected void cancelProgressTimer() {
        if (updateProcessTimer != null) {
            updateProcessTimer.cancel();
            updateProcessTimer = null;
        }
        if (mProgressTimerTask != null) {
            mProgressTimerTask.cancel();
            mProgressTimerTask = null;
        }

    }

    protected void setTextAndProgress(int secProgress) {
        int position = getCurrentPositionWhenPlaying();
        int duration = getDuration();
        int progress = position * 100 / (duration == 0 ? 1 : duration);
    }



    protected void setViewShowState(View view, int visibility) {
        if (view != null) {
            view.setVisibility(visibility);
        }
    }



    private class ProgressTimerTask extends TimerTask {
        @Override
        public void run() {
            if (mCurrentState == CURRENT_STATE_PLAYING || mCurrentState == CURRENT_STATE_PAUSE) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        setTextAndProgress(0);
                    }
                });
            }
        }
    }



    /************************* 继承之后可自定义ui与显示隐藏 *************************/

    protected abstract void showWifiDialog();

    protected abstract void onClickUiToggle();

    protected abstract void changeUiToNormal();

    protected abstract void changeUiToPreparingShow();

    protected abstract void changeUiToPlayingShow();

    protected abstract void changeUiToPauseShow();

    protected abstract void changeUiToError();

    protected abstract void changeUiToCompleteShow();

    protected abstract void changeUiToPlayingBufferingShow();


}
