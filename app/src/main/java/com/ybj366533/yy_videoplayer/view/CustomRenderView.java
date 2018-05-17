package com.ybj366533.yy_videoplayer.view;

import android.content.Context;
import android.view.ViewGroup;

import com.migu.videoplayer.render.RenderView;
import com.migu.videoplayer.render.glrender.VideoGLViewBaseRender;
import com.migu.videoplayer.render.view.MiguVideoGLView;
import com.migu.videoplayer.render.view.listener.IMiGuSurfaceListener;
import com.migu.videoplayer.utils.MeasureHelper;

/**
 * 自定义代理渲染层
 * Created by guoshuyu on 2018/1/30.
 */

public class CustomRenderView extends RenderView {

    @Override
    public void addView(Context context, ViewGroup textureViewContainer, int rotate, IMiGuSurfaceListener gsySurfaceListener, MeasureHelper.MeasureFormVideoParamsListener videoParamsListener, MiguVideoGLView.ShaderInterface effect, float[] transform, VideoGLViewBaseRender customRender, int mode) {
        mShowView = CustomTextureSurface.addSurfaceView(context, textureViewContainer, rotate, gsySurfaceListener, videoParamsListener);
    }
}
