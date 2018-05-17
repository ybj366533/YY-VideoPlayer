package com.ybj366533.yy_videoplayer.video;

import android.content.Context;
import android.util.AttributeSet;

import com.migu.videoplayer.video.NormalVideoPlayer;
import com.ybj366533.yy_videoplayer.view.CustomRenderView;

/**
 * 自定义渲染控件
 */

public class CustomRenderVideoPlayer extends NormalVideoPlayer {
    public CustomRenderVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public CustomRenderVideoPlayer(Context context) {
        super(context);
    }

    public CustomRenderVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void addTextureView() {
        mTextureView = new CustomRenderView();
        mTextureView.addView(getContext(), mTextureViewContainer, mRotate, this, this, mEffectFilter, mMatrixGL, mRenderer, mMode);
    }
}
