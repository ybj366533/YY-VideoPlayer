package com.ybj366533.yy_videoplayer.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.ybj366533.videoplayer.base.BaseVideoPlayer;
import com.ybj366533.videoplayer.utils.CommonUtil;
import com.ybj366533.videoplayer.utils.NetworkUtils;
import com.ybj366533.videoplayer.video.FullScreenVideoView;
import com.ybj366533.videoplayer.video.base.MiGuVideoPlayer;
import com.ybj366533.yy_videoplayer.R;

/**
 * 计算滑动，自动播放的帮助类
 */

public class ViewPlageCalculatorHelper {

    private int selectId = -1;
    private int releaseId = -1;
    private int playId;
    private PlayRunnable runnable;

    private Handler playHandler = new Handler();

    public ViewPlageCalculatorHelper(int playId) {
        this.playId = playId;
    }

    public void onScrollSelectChanged(RecyclerView view, int selectId) {
        this.selectId = selectId;
        playVideo(view);

    }

    public void onScrollReleaseChanged(RecyclerView view, int releaseId) {
        this.releaseId = releaseId;
        stopVideo(view);

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

        if (layoutManager.findViewByPosition(releaseId) != null) {
            FullScreenVideoView player = (FullScreenVideoView) layoutManager.findViewByPosition(releaseId).findViewById(playId);
            if ((player.getCurrentPlayer().getCurrentState() == BaseVideoPlayer.CURRENT_STATE_PLAYING
                    || player.getCurrentPlayer().getCurrentState() == BaseVideoPlayer.CURRENT_STATE_PLAYING_BUFFERING_START)) {
                player.onVideoPause();
            }
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
        if (!CommonUtil.isWifiConnected(context)) {
            //这里判断是否wifi
            showWifiDialog(baseVideoPlayer, context);
            return;
        }
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
