package com.ybj366533.videoplayer.video;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ybj366533.videoplayer.video.base.BaseVideoView;

/**

 * 使用正常播放按键和loading的播放器
 */

public class NormalVideoPlayer extends StandardVideoPlayer {

    public NormalVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public NormalVideoPlayer(Context context) {
        super(context);
    }

    public NormalVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return com.ybj366533.videoplayer.R.layout.video_layout_normal;
    }

    @Override
    protected void updateStartImage() {
        if(mStartButton instanceof ImageView) {
            ImageView imageView = (ImageView) mStartButton;
            if (mCurrentState == BaseVideoView.CURRENT_STATE_PLAYING) {
                imageView.setImageResource(com.ybj366533.videoplayer.R.drawable.video_click_pause_selector);
            } else if (mCurrentState == BaseVideoView.CURRENT_STATE_ERROR) {
                imageView.setImageResource(com.ybj366533.videoplayer.R.drawable.video_click_play_selector);
            } else {
                imageView.setImageResource(com.ybj366533.videoplayer.R.drawable.video_click_play_selector);
            }
        }
    }
}
