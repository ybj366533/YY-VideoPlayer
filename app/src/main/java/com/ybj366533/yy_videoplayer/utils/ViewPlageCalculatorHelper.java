package com.ybj366533.yy_videoplayer.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.ybj366533.videoplayer.VideoManager;
import com.ybj366533.videoplayer.base.BaseVideoPlayer;
import com.ybj366533.videoplayer.utils.CommonUtil;
import com.ybj366533.videoplayer.utils.NetworkUtils;
import com.ybj366533.videoplayer.video.FullScreenVideoView;
import com.ybj366533.videoplayer.video.base.MiGuVideoPlayer;
import com.ybj366533.yy_videoplayer.R;

import java.io.File;

/**
 * 计算滑动，自动播放的帮助类
 */

public class ViewPlageCalculatorHelper {

    private int selectId = -1;
    private int releaseId = -1;
    private int playId;
    private Context context;
    private PlayRunnable runnable;
    private Handler playHandler;

    public ViewPlageCalculatorHelper(Context context, int playId) {
        this.context = context;
        this.playId = playId;
        playHandler = new Handler();
    }

    public void onScrollSelectChanged(RecyclerView view, int selectId) {
        this.selectId = selectId;
        playVideo(view);

    }

    public void onScrollReleaseChanged(RecyclerView view, int releaseId) {
        this.releaseId = releaseId;
        stopVideo(view);

    }

    public void onVideoCache(RecyclerView view, String url) {
        chaheVideo(view, url);
    }

    public void onScrollDestroy() {
        this.selectId = -1;
        this.releaseId = -1;
        this.playId = 0;
        runnable = null;
        playHandler.removeCallbacksAndMessages(null);
    }

    private void playVideo(RecyclerView view) {
        if (view == null) {
            return;
        }
        RecyclerView.LayoutManager layoutManager = view.getLayoutManager();
        BaseVideoPlayer baseVideoPlayer = null;
        boolean needPlay = false;

        if (layoutManager.findViewByPosition(selectId) != null) {
            FullScreenVideoView player = (FullScreenVideoView) layoutManager.findViewByPosition(selectId).findViewById(playId);
            baseVideoPlayer = player;
            if ((player.getCurrentPlayer().getCurrentState() == BaseVideoPlayer.CURRENT_STATE_NORMAL
                    || player.getCurrentPlayer().getCurrentState() == BaseVideoPlayer.CURRENT_STATE_ERROR)) {
                needPlay = true;
            }
        }

        if (baseVideoPlayer != null && needPlay) {
            if (runnable != null) {
                BaseVideoPlayer tmpPlayer = runnable.baseVideoPlayer;
                playHandler.removeCallbacks(runnable);
                runnable = null;
                if (tmpPlayer == baseVideoPlayer) {
                    return;
                }
            }
            runnable = new PlayRunnable(baseVideoPlayer);
            //降低频率
            playHandler.post(runnable);
        }

    }

    private void stopVideo(RecyclerView view) {
        if (view == null) {
            return;
        }
        RecyclerView.LayoutManager layoutManager = view.getLayoutManager();

        if (layoutManager.findViewByPosition(releaseId) != null && releaseId != selectId) {
            FullScreenVideoView player = (FullScreenVideoView) layoutManager.findViewByPosition(releaseId).findViewById(playId);
            if ((player.getCurrentPlayer().getCurrentState() == BaseVideoPlayer.CURRENT_STATE_PLAYING
                    || player.getCurrentPlayer().getCurrentState() == BaseVideoPlayer.CURRENT_STATE_PLAYING_BUFFERING_START)) {
                player.onVideoPause();
            }
        }

    }

    private void chaheVideo(RecyclerView view, String parh) {
        if (view == null) {
            return;
        }
        RecyclerView.LayoutManager layoutManager = view.getLayoutManager();
        if (layoutManager.findViewByPosition(selectId) != null) {
            FullScreenVideoView player = (FullScreenVideoView) layoutManager.findViewByPosition(selectId).findViewById(playId);
            player.resolveStartChange(parh);
//            HttpProxyCacheServer proxy = VideoManager.instance().newPreloadProxy(context, new File(Environment.getExternalStorageDirectory() + File.separator + "gtvijk/cache"));
//            if (proxy != null) {
//                //此处转换了url，然后再赋值给mUrl。
//                String url = proxy.getProxyUrl(parh);
//                boolean mCacheFile = (!url.startsWith("http"));
//                //注册上缓冲监听
//                if (!mCacheFile && VideoManager.instance() != null) {
//                    proxy.registerCacheListener(new CacheListener() {
//                        @Override
//                        public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
//                            Log.e("####", url);
//                        }
//                    }, parh);
//                }
//            }

        }
    }

    private class PlayRunnable implements Runnable {

        BaseVideoPlayer baseVideoPlayer;

        public PlayRunnable(BaseVideoPlayer baseVideoPlayer) {
            this.baseVideoPlayer = baseVideoPlayer;
        }

        @Override
        public void run() {
            //如果未播放，需要播放
            if (baseVideoPlayer != null) {
                int[] screenPosition = new int[2];
                baseVideoPlayer.getLocationOnScreen(screenPosition);
                startPlayLogic(baseVideoPlayer, baseVideoPlayer.getContext());
            }
        }
    }


    /***************************************自动播放的点击播放确认******************************************/
    private void startPlayLogic(BaseVideoPlayer baseVideoPlayer, Context context) {
//        if (!CommonUtil.isWifiConnected(context)) {
//            //这里判断是否wifi
//            showWifiDialog(baseVideoPlayer, context);
//            return;
//        }
        baseVideoPlayer.startPlayLogic();
    }

    private void showWifiDialog(final BaseVideoPlayer baseVideoPlayer, Context context) {
        if (!NetworkUtils.isAvailable(context)) {
            Toast.makeText(context, context.getResources().getString(R.string.no_net), Toast.LENGTH_LONG).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getResources().getString(R.string.tips_not_wifi));
        builder.setPositiveButton(context.getResources().getString(R.string.tips_not_wifi_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                baseVideoPlayer.startPlayLogic();
            }
        });
        builder.setNegativeButton(context.getResources().getString(R.string.tips_not_wifi_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

}
