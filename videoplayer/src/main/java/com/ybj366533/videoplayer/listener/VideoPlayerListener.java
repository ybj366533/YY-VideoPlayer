package com.ybj366533.videoplayer.listener;

public interface VideoPlayerListener {

    //开始加载，object[0]是当前所处播放器
    void onVideoStartSet(String url, Object... objects);

    //完成准备，object[0]是当前所处播放器
    void onVideoCompletion(String url, Object... objects);

    //加载成功，object[0]是当前所处播放器
    void onVideoPrepared(String url, Object... objects);

    //视图更新，object[0]是当前所处播放器
    void onVideoBufferingUpdate(String url, Object... objects);

    //开始播放，object[0]是当前所处播放器
    void onVideoStart(String url, Object... objects);

    //开始播放失败，object[0]是当前所处播放器
    void onVideoStartError(String url, Object... objects);

    //播放状态下--->停止，object[0]是当前所处播放器
    void onVideoPlayerStop(String url, Object... objects);

    //暂停状态下--->播放，object[0]是当前所处播放器
    void onVideoPlayerResume(String url, Object... objects);

    //播放完了，object[0]是当前所处播放器
    void onVideoAutoComplete(String url, Object... objects);

    //播放错误，object[0]是当前所处播放器
    void onVideoPlayError(String url, Object... objects);

}
