package com.ybj366533.videoplayer.render.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

import com.ybj366533.videoplayer.listener.VideoShotListener;
import com.ybj366533.videoplayer.listener.VideoShotSaveListener;
import com.ybj366533.videoplayer.render.RenderView;
import com.ybj366533.videoplayer.render.effect.NoEffect;
import com.ybj366533.videoplayer.render.glrender.VideoGLViewBaseRender;
import com.ybj366533.videoplayer.render.glrender.VideoGLViewSimpleRender;
import com.ybj366533.videoplayer.render.view.listener.IMiGuSurfaceListener;
import com.ybj366533.videoplayer.render.view.listener.MiGuGLSurfaceListener;
import com.ybj366533.videoplayer.render.view.listener.VideoGLRenderErrorListener;
import com.ybj366533.videoplayer.utils.Debuger;
import com.ybj366533.videoplayer.utils.FileUtils;
import com.ybj366533.videoplayer.utils.MeasureHelper;

import java.io.File;


/**
 * 在videffects的基础上调整的
 * <p>
 * 原 @author sheraz.khilji
 */
@SuppressLint("ViewConstructor")
public class MiguVideoGLView extends GLSurfaceView implements MiGuGLSurfaceListener, IMiGuRenderView, MeasureHelper.MeasureFormVideoParamsListener {

    private static final String TAG = MiguVideoGLView.class.getName();
    /**
     * 利用布局计算大小
     */
    public static final int MODE_LAYOUT_SIZE = 0;
    /**
     * 利用Render计算大小
     */
    public static final int MODE_RENDER_SIZE = 1;

    private VideoGLViewBaseRender mRenderer;

    private Context mContext;

    private ShaderInterface mEffect = new NoEffect();

    private MeasureHelper.MeasureFormVideoParamsListener mVideoParamsListener;

    private MeasureHelper measureHelper;

    private MiGuGLSurfaceListener mOnGSYSurfaceListener;

    private IMiGuSurfaceListener mIMiGuSurfaceListener;

    private float[] mMVPMatrix;

    private int mMode = MODE_LAYOUT_SIZE;

    public interface ShaderInterface {
        String getShader(GLSurfaceView mGlSurfaceView);
    }

    public MiguVideoGLView(Context context) {
        super(context);
        init(context);
    }

    public MiguVideoGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setEGLContextClientVersion(2);
        mRenderer = new VideoGLViewSimpleRender();
        measureHelper = new MeasureHelper(this, this);
        mRenderer.setSurfaceView(MiguVideoGLView.this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRenderer != null) {
            mRenderer.initRenderSize();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMode == MODE_RENDER_SIZE) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            measureHelper.prepareMeasure(widthMeasureSpec, heightMeasureSpec, (int) getRotation());
            initRenderMeasure();
        } else {
            measureHelper.prepareMeasure(widthMeasureSpec, heightMeasureSpec, (int) getRotation());
            setMeasuredDimension(measureHelper.getMeasuredWidth(), measureHelper.getMeasuredHeight());
        }
    }

    @Override
    public IMiGuSurfaceListener getIGSYSurfaceListener() {
        return mIMiGuSurfaceListener;
    }

    @Override
    public void setIGSYSurfaceListener(IMiGuSurfaceListener surfaceListener) {
        setOnGSYSurfaceListener(this);
        mIMiGuSurfaceListener = surfaceListener;
    }

    @Override
    public void onSurfaceAvailable(Surface surface) {
        if (mIMiGuSurfaceListener != null) {
            mIMiGuSurfaceListener.onSurfaceAvailable(surface);
        }
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
    @Override
    public void taskShotPic(VideoShotListener videoShotListener, boolean shotHigh) {
        if (videoShotListener != null) {
            setGSYVideoShotListener(videoShotListener, shotHigh);
            takeShotPic();

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
        setGSYVideoShotListener(videoShotListener, high);
        takeShotPic();
    }

    @Override
    public View getRenderView() {
        return this;
    }


    @Override
    public void onRenderResume() {
        requestLayout();
        onResume();
    }

    @Override
    public void onRenderPause() {
        requestLayout();
        onPause();

    }

    @Override
    public void releaseRenderAll() {
        requestLayout();
        releaseAll();

    }

    @Override
    public void setRenderMode(int mode) {
        setMode(mode);
    }


    @Override
    public void setRenderTransform(Matrix transform) {
        Debuger.printfLog(getClass().getSimpleName() + " not support setRenderTransform now");
    }

    @Override
    public void setGLRenderer(VideoGLViewBaseRender renderer) {
        setCustomRenderer(renderer);
    }

    @Override
    public void setGLMVPMatrix(float[] MVPMatrix) {
        setMVPMatrix(MVPMatrix);
    }

    /**
     * 设置滤镜效果
     */
    @Override
    public void setGLEffectFilter(MiguVideoGLView.ShaderInterface effectFilter) {
        setEffect(effectFilter);
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

    protected void initRenderMeasure() {
        if (mVideoParamsListener != null && mMode == MODE_RENDER_SIZE) {
            try {
                int videoWidth = mVideoParamsListener.getCurrentVideoWidth();
                int videoHeight = mVideoParamsListener.getCurrentVideoHeight();
                if (this.mRenderer != null) {
                    this.mRenderer.setCurrentViewWidth(measureHelper.getMeasuredWidth());
                    this.mRenderer.setCurrentViewHeight(measureHelper.getMeasuredHeight());
                    this.mRenderer.setCurrentVideoWidth(videoWidth);
                    this.mRenderer.setCurrentVideoHeight(videoHeight);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void initRender() {
        setRenderer(mRenderer);
    }


    public void setGSYVideoGLRenderErrorListener(VideoGLRenderErrorListener videoGLRenderErrorListener) {
        this.mRenderer.setGSYVideoGLRenderErrorListener(videoGLRenderErrorListener);
    }

    /**
     * 设置自定义的render，其他自定义设置会被取消，需要重新设置
     * 在initRender() 前设置才会生效
     *
     * @param CustomRender
     */
    public void setCustomRenderer(VideoGLViewBaseRender CustomRender) {
        this.mRenderer = CustomRender;
        mRenderer.setSurfaceView(MiguVideoGLView.this);
        initRenderMeasure();
    }

    public void setOnGSYSurfaceListener(MiGuGLSurfaceListener mGSYSurfaceListener) {
        this.mOnGSYSurfaceListener = mGSYSurfaceListener;
        mRenderer.setGSYSurfaceListener(this.mOnGSYSurfaceListener);
    }

    public void setEffect(ShaderInterface shaderEffect) {
        if (shaderEffect != null) {
            mEffect = shaderEffect;
            mRenderer.setEffect(mEffect);
        }
    }

    public void setMVPMatrix(float[] MVPMatrix) {
        if (MVPMatrix != null) {
            mMVPMatrix = MVPMatrix;
            mRenderer.setMVPMatrix(MVPMatrix);
        }
    }

    public void takeShotPic() {
        mRenderer.takeShotPic();
    }


    public void setGSYVideoShotListener(VideoShotListener listener, boolean high) {
        this.mRenderer.setGSYVideoShotListener(listener, high);
    }

    public int getMode() {
        return mMode;
    }

    /**
     * @param mode MODE_LAYOUT_SIZE = 0,  MODE_RENDER_SIZE = 1
     */
    public void setMode(int mode) {
        this.mMode = mode;
    }

    public void releaseAll() {
        if (mRenderer != null) {
            mRenderer.releaseAll();
        }
    }

    public VideoGLViewBaseRender getRenderer() {
        return mRenderer;
    }

    public ShaderInterface getEffect() {
        return mEffect;
    }

    public float[] getMVPMatrix() {
        return mMVPMatrix;
    }

    /**
     * 添加播放的view
     */
    public static MiguVideoGLView addGLView(final Context context, final ViewGroup textureViewContainer, final int rotate,
                                            final IMiGuSurfaceListener gsySurfaceListener,
                                            final MeasureHelper.MeasureFormVideoParamsListener videoParamsListener,
                                            final MiguVideoGLView.ShaderInterface effect, final float[] transform,
                                            final VideoGLViewBaseRender customRender, final int renderMode) {
        if (textureViewContainer.getChildCount() > 0) {
            textureViewContainer.removeAllViews();
        }
        final MiguVideoGLView miguVideoGLView = new MiguVideoGLView(context);
        if (customRender != null) {
            miguVideoGLView.setCustomRenderer(customRender);
        }
        miguVideoGLView.setEffect(effect);
        miguVideoGLView.setVideoParamsListener(videoParamsListener);
        miguVideoGLView.setRenderMode(renderMode);
        miguVideoGLView.setIGSYSurfaceListener(gsySurfaceListener);
        miguVideoGLView.setRotation(rotate);
        miguVideoGLView.initRender();
        miguVideoGLView.setGSYVideoGLRenderErrorListener(new VideoGLRenderErrorListener() {
            @Override
            public void onError(VideoGLViewBaseRender render, String Error, int code, boolean byChangedRenderError) {
                if (byChangedRenderError)
                    addGLView(context,
                            textureViewContainer,
                            rotate,
                            gsySurfaceListener,
                            videoParamsListener,
                            render.getEffect(),
                            render.getMVPMatrix(),
                            render, renderMode);

            }
        });
        if (transform != null && transform.length == 16) {
            miguVideoGLView.setMVPMatrix(transform);
        }
        RenderView.addToParent(textureViewContainer, miguVideoGLView);
        return miguVideoGLView;
    }


}
