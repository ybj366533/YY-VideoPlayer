package com.ybj366533.yy_videoplayer.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.ybj366533.videoplayer.listener.VideoShotListener;
import com.ybj366533.videoplayer.listener.VideoShotSaveListener;
import com.ybj366533.videoplayer.render.RenderView;
import com.ybj366533.videoplayer.render.glrender.VideoGLViewBaseRender;
import com.ybj366533.videoplayer.render.view.IMiGuRenderView;
import com.ybj366533.videoplayer.render.view.MiguVideoGLView;
import com.ybj366533.videoplayer.render.view.listener.IMiGuSurfaceListener;
import com.ybj366533.videoplayer.utils.MeasureHelper;

import java.io.File;

/**
 * 自定义渲染层
 * Created by guoshuyu on 2018/1/30.
 */

public class CustomTextureSurface extends SurfaceView implements IMiGuRenderView, SurfaceHolder.Callback2, MeasureHelper.MeasureFormVideoParamsListener {

    private IMiGuSurfaceListener mIMiGuSurfaceListener;

    private MeasureHelper measureHelper;

    private MeasureHelper.MeasureFormVideoParamsListener mVideoParamsListener;

    public CustomTextureSurface(Context context) {
        super(context);
        init();
    }

    public CustomTextureSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTextureSurface(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        measureHelper = new MeasureHelper(this, this);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureHelper.prepareMeasure(widthMeasureSpec, heightMeasureSpec, (int) getRotation());
        setMeasuredDimension(measureHelper.getMeasuredWidth(), measureHelper.getMeasuredHeight());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mIMiGuSurfaceListener != null) {
            mIMiGuSurfaceListener.onSurfaceAvailable(holder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mIMiGuSurfaceListener != null) {
            mIMiGuSurfaceListener.onSurfaceSizeChanged(holder.getSurface(), width, height);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //清空释放
        if (mIMiGuSurfaceListener != null) {
            mIMiGuSurfaceListener.onSurfaceDestroyed(holder.getSurface());
        }
    }

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {

    }

    @Override
    public IMiGuSurfaceListener getIGSYSurfaceListener() {
        return mIMiGuSurfaceListener;
    }

    @Override
    public void setIGSYSurfaceListener(IMiGuSurfaceListener surfaceListener) {
        getHolder().addCallback(this);
        this.mIMiGuSurfaceListener = surfaceListener;
    }


    @Override
    public int getSizeH() {
        return measureHelper.getMeasuredHeight();
    }

    @Override
    public int getSizeW() {
        return measureHelper.getMeasuredWidth();
    }

    @Override
    public void taskShotPic(VideoShotListener videoShotListener, boolean shotHigh) {

    }

    @Override
    public void saveFrame(File file, boolean high, VideoShotSaveListener videoShotSaveListener) {

    }

    @Override
    public View getRenderView() {
        return this;
    }

    @Override
    public Bitmap initCover() {
        return null;
    }

    @Override
    public Bitmap initCoverHigh() {
        return null;
    }

    @Override
    public void onRenderResume() {

    }

    @Override
    public void onRenderPause() {

    }

    @Override
    public void releaseRenderAll() {

    }

    @Override
    public void setRenderMode(int mode) {

    }

    @Override
    public void setRenderTransform(Matrix transform) {

    }

    @Override
    public void setGLRenderer(VideoGLViewBaseRender renderer) {

    }

    @Override
    public void setGLMVPMatrix(float[] MVPMatrix) {

    }

    @Override
    public void setGLEffectFilter(MiguVideoGLView.ShaderInterface effectFilter) {

    }

    @Override
    public void setVideoParamsListener(MeasureHelper.MeasureFormVideoParamsListener listener) {
        mVideoParamsListener = listener;
    }

    @Override
    public int getCurrentVideoWidth() {
        if (mVideoParamsListener != null) {
            return mVideoParamsListener.getCurrentVideoWidth();
        }
        return 0;
    }

    @Override
    public int getCurrentVideoHeight() {
        if (mVideoParamsListener != null) {
            return mVideoParamsListener.getCurrentVideoHeight();
        }
        return 0;
    }

    @Override
    public int getVideoSarNum() {
        if (mVideoParamsListener != null) {
            return mVideoParamsListener.getVideoSarNum();
        }
        return 0;
    }

    @Override
    public int getVideoSarDen() {
        if (mVideoParamsListener != null) {
            return mVideoParamsListener.getVideoSarDen();
        }
        return 0;
    }

    /**
     * 添加播放的view
     */
    public static CustomTextureSurface addSurfaceView(Context context, ViewGroup textureViewContainer, int rotate,
                                                      final IMiGuSurfaceListener gsySurfaceListener,
                                                      final MeasureHelper.MeasureFormVideoParamsListener videoParamsListener) {
        if (textureViewContainer.getChildCount() > 0) {
            textureViewContainer.removeAllViews();
        }
        CustomTextureSurface showSurfaceView = new CustomTextureSurface(context);
        showSurfaceView.setIGSYSurfaceListener(gsySurfaceListener);
        showSurfaceView.setRotation(rotate);
        showSurfaceView.setVideoParamsListener(videoParamsListener);
        RenderView.addToParent(textureViewContainer, showSurfaceView);
        return showSurfaceView;
    }
}
