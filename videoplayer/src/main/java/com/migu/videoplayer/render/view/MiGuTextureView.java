package com.migu.videoplayer.render.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.migu.videoplayer.listener.VideoShotListener;
import com.migu.videoplayer.listener.VideoShotSaveListener;
import com.migu.videoplayer.render.RenderView;
import com.migu.videoplayer.render.glrender.VideoGLViewBaseRender;
import com.migu.videoplayer.render.view.listener.IMiGuSurfaceListener;
import com.migu.videoplayer.utils.Debuger;
import com.migu.videoplayer.utils.FileUtils;
import com.migu.videoplayer.utils.MeasureHelper;

import java.io.File;

/**
 * 用于显示video的，做了横屏与竖屏的匹配，还有需要rotation需求的
 * Created by shuyu on 2016/11/11.
 */

public class MiGuTextureView extends TextureView implements TextureView.SurfaceTextureListener, IMiGuRenderView, MeasureHelper.MeasureFormVideoParamsListener {

    private IMiGuSurfaceListener mIMiGuSurfaceListener;

    private MeasureHelper.MeasureFormVideoParamsListener mVideoParamsListener;

    private MeasureHelper measureHelper;

    private Surface mSurface;

    public MiGuTextureView(Context context) {
        super(context);
        init();
    }

    public MiGuTextureView(Context context, AttributeSet attrs) {
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
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurface = new Surface(surface);
        if (mIMiGuSurfaceListener != null) {
            mIMiGuSurfaceListener.onSurfaceAvailable(mSurface);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        if (mIMiGuSurfaceListener != null) {
            mIMiGuSurfaceListener.onSurfaceSizeChanged(mSurface, width, height);
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        //清空释放
        if (mIMiGuSurfaceListener != null) {
            mIMiGuSurfaceListener.onSurfaceDestroyed(mSurface);
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        //如果播放的是暂停全屏了
        if (mIMiGuSurfaceListener != null) {
            mIMiGuSurfaceListener.onSurfaceUpdated(mSurface);
        }
    }

    @Override
    public IMiGuSurfaceListener getIGSYSurfaceListener() {
        return mIMiGuSurfaceListener;
    }

    @Override
    public void setIGSYSurfaceListener(IMiGuSurfaceListener surfaceListener) {
        setSurfaceTextureListener(this);
        mIMiGuSurfaceListener = surfaceListener;
    }

    @Override
    public int getSizeH() {
        return getHeight();
    }

    @Override
    public int getSizeW() {
        return getWidth();
    }

    /**
     * 暂停时初始化位图
     */
    @Override
    public Bitmap initCover() {
        Bitmap bitmap = Bitmap.createBitmap(
                getSizeW(), getSizeH(), Bitmap.Config.RGB_565);
        return getBitmap(bitmap);

    }

    /**
     * 暂停时初始化位图
     */
    @Override
    public Bitmap initCoverHigh() {
        Bitmap bitmap = Bitmap.createBitmap(
                getSizeW(), getSizeH(), Bitmap.Config.ARGB_8888);
        return getBitmap(bitmap);

    }


    /**
     * 获取截图
     *
     * @param shotHigh 是否需要高清的
     */
    @Override
    public void taskShotPic(VideoShotListener videoShotListener, boolean shotHigh) {
        if (shotHigh) {
            videoShotListener.getBitmap(initCoverHigh());
        } else {
            videoShotListener.getBitmap(initCover());
        }
    }

    /**
     * 保存截图
     *
     * @param high 是否需要高清的
     */
    @Override
    public void saveFrame(final File file, final boolean high, final VideoShotSaveListener videoShotSaveListener) {
        VideoShotListener videoShotListener = new VideoShotListener() {
            @Override
            public void getBitmap(Bitmap bitmap) {
                if (bitmap == null) {
                    videoShotSaveListener.result(false, file);
                } else {
                    FileUtils.saveBitmap(bitmap, file);
                    videoShotSaveListener.result(true, file);
                }
            }
        };
        if (high) {
            videoShotListener.getBitmap(initCoverHigh());
        } else {
            videoShotListener.getBitmap(initCover());
        }

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
        setTransform(transform);
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
    public static MiGuTextureView addTextureView(Context context, ViewGroup textureViewContainer, int rotate,
                                                 final IMiGuSurfaceListener gsySurfaceListener,
                                                 final MeasureHelper.MeasureFormVideoParamsListener videoParamsListener) {
        if (textureViewContainer.getChildCount() > 0) {
            textureViewContainer.removeAllViews();
        }
        MiGuTextureView miGuTextureView = new MiGuTextureView(context);
        miGuTextureView.setIGSYSurfaceListener(gsySurfaceListener);
        miGuTextureView.setVideoParamsListener(videoParamsListener);
        miGuTextureView.setRotation(rotate);
        RenderView.addToParent(textureViewContainer, miGuTextureView);
        return miGuTextureView;
    }
}