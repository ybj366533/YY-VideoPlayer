package com.migu.videoplayer.render.view.listener;

import com.migu.videoplayer.render.glrender.VideoGLViewBaseRender;

/**
 * GL渲染错误
 * Created by guoshuyu on 2018/1/14.
 */
public interface VideoGLRenderErrorListener {
    /**
     *
     * @param render
     * @param Error 错误文本
     * @param code 错误代码
     * @param byChangedRenderError 错误是因为切换effect导致的
     */
    void onError(VideoGLViewBaseRender render, String Error, int code, boolean byChangedRenderError);
}
