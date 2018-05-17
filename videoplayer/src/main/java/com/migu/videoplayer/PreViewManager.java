package com.migu.videoplayer;

import android.media.AudioManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Surface;

import com.migu.videoplayer.model.VideoModel;

import java.io.IOException;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkLibLoader;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 小窗口预览管理
 */
@Deprecated
public class PreViewManager implements IMediaPlayer.OnPreparedListener, IjkMediaPlayer.OnSeekCompleteListener {

    public static String TAG = "PreViewManager";

    private static PreViewManager videoManager;

    private static final int HANDLER_PREPARE = 0;
    private static final int HANDLER_SETDISPLAY = 1;
    private static final int HANDLER_RELEASE = 2;

    private IjkMediaPlayer mediaPlayer;
    private HandlerThread mMediaHandlerThread;
    private MediaHandler mMediaHandler;

    private boolean seekToComplete = true;
    private static IjkLibLoader ijkLibLoader; //自定义so包加载类

    public static synchronized PreViewManager instance() {
        if (videoManager == null) {
            videoManager = new PreViewManager();
        }
        return videoManager;
    }

    private PreViewManager() {
        IjkLibLoader libLoader = VideoManager.getIjkLibLoader();
        mediaPlayer = (libLoader == null) ? new IjkMediaPlayer() : new IjkMediaPlayer(libLoader);
        ijkLibLoader = libLoader;

        mMediaHandlerThread = new HandlerThread(TAG);
        mMediaHandlerThread.start();
        mMediaHandler = new MediaHandler((mMediaHandlerThread.getLooper()));
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
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                    }
                    break;
            }
        }

    }

    private void initVideo(Message msg) {
        try {
            mediaPlayer.release();

            initIJKPlayer(msg);

            mediaPlayer.setOnPreparedListener(PreViewManager.this);
            mediaPlayer.setOnSeekCompleteListener(this);
            mediaPlayer.setVolume(0, 0);
            mediaPlayer.prepareAsync();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initIJKPlayer(Message msg) {
        mediaPlayer = (ijkLibLoader == null) ? new IjkMediaPlayer() : new IjkMediaPlayer(ijkLibLoader);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(((VideoModel) msg.obj).getUrl(), ((VideoModel) msg.obj).getMapHeadData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showDisplay(Message msg) {
        if (msg.obj == null && mediaPlayer != null) {
            mediaPlayer.setSurface(null);
        } else {
            Surface holder = (Surface) msg.obj;
            if (mediaPlayer != null && holder.isValid()) {
                mediaPlayer.setSurface(holder);
            }
        }
    }


    @Override
    public void onPrepared(IMediaPlayer mp) {
        mp.pause();
        seekToComplete = true;
    }

    @Override
    public void onSeekComplete(IMediaPlayer mp) {
        seekToComplete = true;
    }

    public void prepare(final String url, final Map<String, String> mapHeadData, boolean loop, float speed) {
        if (TextUtils.isEmpty(url)) return;
        Message msg = new Message();
        msg.what = HANDLER_PREPARE;
        VideoModel fb = new VideoModel(url, mapHeadData, loop, speed);
        msg.obj = fb;
        mMediaHandler.sendMessage(msg);
    }

    public void releaseMediaPlayer() {
        Message msg = new Message();
        msg.what = HANDLER_RELEASE;
        mMediaHandler.sendMessage(msg);
    }

    public void setDisplay(Surface holder) {
        Message msg = new Message();
        msg.what = HANDLER_SETDISPLAY;
        msg.obj = holder;
        mMediaHandler.sendMessage(msg);
    }

    public IjkMediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public boolean isSeekToComplete() {
        return seekToComplete;
    }

    public void setSeekToComplete(boolean seekToComplete) {
        this.seekToComplete = seekToComplete;
    }
}