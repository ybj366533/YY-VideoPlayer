package com.ybj366533.videoplayer.render.view;

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
import com.ybj366533.videoplayer.render.view.listener.IMiGuSurfaceListener;
import com.ybj366533.videoplayer.utils.Debuger;
import com.ybj366533.videoplayer.utils.MeasureHelper;

import java.io.File;

/**
 * SurfaceView
 * Created by guoshuyu on 2017/8/26.
 */

public class MiGuSurfaceView extends SurfaceView implements SurfaceHolder.Callback2, IMiGuRenderView, MeasureHelper.MeasureFormVideoParamsListener {

    private IMiGuSurfaceListener mIMiGuSurfaceListener;

    private MeasureHelper.MeasureFormVideoParamsListener mVideoParamsListener;

    private MeasureHelper measureHelper;

    public MiGuSurfaceView(Context context) {
        super(context);
        init();
    }

    public MiGuSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
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
        return getHeight();
    }

    @Override
    public int getSizeW() {
        return getWidth();
    }

    @Override
    public Bitmap initCover() {
        Debuger.printfLog(getClass().getSimpleName() + " not support initCover now");
        return null;
    }

    @Override
    public Bitmap initCoverHigh() {
        Debuger.printfLog(getClass().getSimpleName() + " not support initCoverHigh now");
        return null;
    }

    /**
     * 获取截图
     *
     * @param shotHigh 是否需要高清的
     */
    public void taskShotPic(VideoShotListener videoShotListener, boolean shotHigh) {
        Debuger.printfLog(getClass().getSimpleName() + " not support taskShotPic now");
    }

    /**
     * 保存截图
     *
     * @param high 是否需要高清的
     */
    public void saveFrame(final File file, final boolean high, final VideoShotSaveListener videoShotSaveListener) {
        Debuger.printfLog(getClass().getSimpleName() + " not support saveFrame now");
    }

    @Override
    public View getRenderView() {
        return this;
    }

    @Override
    public void onRenderResume() {
        Debuger.printfLog(getClass().getSimpleName() + " not support onRenderResume now");
    }

    @Override
    public void onRenderPause() {
        Debuger.printfLog(getClass().getSimpleName() + " not support onRenderPause now");
    }

    @Override
    public void releaseRenderAll() {
        Debuger.printfLog(getClass().getSimpleName() + " not support releaseRenderAll now");
    }

    @Override
    public void setRenderMode(int mode) {
        Debuger.printfLog(getClass().getSimpleName() + " not support setRenderMode now");
    }


    @Override
    public void setRenderTransform(Matrix transform) {
        Debuger.printfLog(getClass().getSimpleName() + " not support setRenderTransform now");
    }

    @Override
    public void setGLRenderer(VideoGLViewBaseRender renderer) {
        Debuger.printfLog(getClass().getSimpleName() + " not support setGLRenderer now");
    }

    @Override
    public void setGLMVPMatrix(float[] MVPMatrix) {
        Debuger.printfLog(getClass().getSimpleName() + " not support setGLMVPMatrix now");
    }

    /**
     * 设置滤镜效果
     */
    @Override
    public void setGLEffectFilter(MiguVideoGLView.ShaderInterface effectFilter) {
        Debuger.printfLog(getClass().getSimpleName() + " not support setGLEffectFilter now");
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
    public static MiGuSurfaceView addSurfaceView(Context context, ViewGroup textureViewContainer, int rotate,
                                                 final IMiGuSurfaceListener gsySurfaceListener,
                                                 final MeasureHelper.MeasureFormVideoParamsListener videoParamsListener) {
        if (textureViewContainer.getChildCount() > 0) {
            textureViewContainer.removeAllViews();
        }
        MiGuSurfaceView showSurfaceView = new MiGuSurfaceView(context);
        showSurfaceView.setIGSYSurfaceListener(gsySurfaceListener);
        showSurfaceView.setVideoParamsListener(videoParamsListener);
        showSurfaceView.setRotation(rotate);
        RenderView.addToParent(textureViewContainer, showSurfaceView);
        return showSurfaceView;
    }

}
