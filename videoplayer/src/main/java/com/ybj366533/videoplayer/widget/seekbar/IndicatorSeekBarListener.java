package com.ybj366533.videoplayer.widget.seekbar;


/**
 * 进度条监听回调
 */

public interface IndicatorSeekBarListener {
//    void onSeekBarClick();
//
//    void onSeekBarStart();
//
//    void onSeekBarStop();

    void onSeekTo(float progress);

    void onProgressChanged(float progress);
}
