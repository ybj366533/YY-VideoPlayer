package com.ybj366533.yy_videoplayer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import android.os.Parcelable;

import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;


public class VideoSeekBar extends View implements ViewTreeObserver.OnGlobalLayoutListener, ViewTreeObserver.OnScrollChangedListener {

    private float mTickRadius;
    private Rect mCoverRect;
    private int[] mLocation;
    private Context mContext;
    private Paint mStockPaint;
    private TextPaint mTextPaint;
    private float mTouchX;
    private float mTrackY;
    private float mSeekLength;
    private float mSeekStart;
    private float mSeekEnd;
    private Rect mRect;
    private int mPaddingLeft;
    private int mPaddingRight;
    private int mMeasuredWidth;
    private float mCustomDrawableMaxHeight;
    private float mScreenWidth = -1;
    private float maxTime = -1;

    private List<Float> spotsList = new ArrayList<>();

    public VideoSeekBar(Context context) {
        this(context, null);
    }


    public VideoSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initAttrs(mContext, attrs);
        initData();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
    }

    private void initData() {
        initStrokePaint();
        initDefaultPadding();
    }

    private void calculateProgressTouchX() {

    }

    float getTouchX() {
        calculateProgressTouchX();
        return mTouchX;
    }


    private void initEndTexts() {

    }

    private void initDefaultPadding() {
    }

    private void initStrokePaint() {
        if (mStockPaint == null) {
            mStockPaint = new Paint();
        }
        mStockPaint.setAntiAlias(true);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = Math.round(mCustomDrawableMaxHeight + .5f + getPaddingTop() + getPaddingBottom());
//        setMeasuredDimension(resolveSize(IndicatorUtils.dp2px(mContext, 170), widthMeasureSpec), height + mTextHeight);
//        initSeekBarInfo();
//        if (p.mShowIndicator && mIndicator == null) {
//            mIndicator = new Indicator(mContext, this, p);
//        }

    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //draw 2th track
//        mStockPaint.setColor(p.mProgressTrackColor);
//        if (!mDrawAgain) {
//            //progress
//            float touchX = (p.mProgress - p.mMin) * mSeekLength / (p.mMax - p.mMin) + mPaddingLeft;
//            calculateTouchX(touchX);
//            mDrawAgain = true;
//        }
//        float thumbX = getThumbX();
        //draw progress track
//        mStockPaint.setStrokeWidth(p.mProgressTrackSize);
//        canvas.drawLine(mSeekStart, mTrackY, thumbX, mTrackY, mStockPaint);
//        //draw BG track
//        mStockPaint.setStrokeWidth(p.mBackgroundTrackSize);
//        mStockPaint.setColor(p.mBackgroundTrackColor);
//        canvas.drawLine(thumbX + mThumbRadius, mTrackY, mSeekEnd, mTrackY, mStockPaint);
//        //draw tick
//        drawTicks(canvas, thumbX);
//        //draw text below tick
//        drawText(canvas);
//        //drawThumbText
//        drawThumbText(canvas, thumbX);
//        //drawThumb
//        drawThumb(canvas, thumbX);

//        if (p.mShowIndicator && p.mIndicatorStay && !mIndicator.isShowing()) {
//            if (!isCover()) {
//                calculateProgressTouchX();
//                mIndicator.show(mTouchX);
//            }
//
//        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
//        if (!p.mShowIndicator) {
//            return;
//        }
//        if (View.GONE == visibility || View.INVISIBLE == visibility) {
//            if (mIndicator != null) {
//                mIndicator.forceHide();
//            }
//        }
    }

    boolean isCover() {
        if (mCoverRect == null) {
            mCoverRect = new Rect();
        }
        if (this.getGlobalVisibleRect(mCoverRect)) {
            if (mCoverRect.width() >= this.getMeasuredWidth() && mCoverRect.height() >= this.getMeasuredHeight()) {
                if (mScreenWidth < 0) {
                    initScreenWidth();
                }
                if (mScreenWidth > 0) {
                    int left = mCoverRect.left;
                    int top = mCoverRect.top;
                    if (mLocation == null) {
                        mLocation = new int[2];
                    }
                    this.getLocationInWindow(mLocation);
                    if (left == mLocation[0] && top == mLocation[1]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void initScreenWidth() {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager systemService = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (systemService != null) {
            systemService.getDefaultDisplay().getMetrics(metric);
            mScreenWidth = metric.widthPixels;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                performClick();
                float mX = event.getX();
                float mY = event.getY();
//                if (isTouchSeekBar(mX, mY) && !p.mForbidUserSeek && isEnabled()) {
//                    if (p.mTouchToSeek || isTouchThumb(mX)) {
//                        if (mListener != null) {
//                            mListener.onStartTrackingTouch(this, getThumbPosOnTick());
//                        }
//                        refreshSeekBar(event, true);
//                        return true;
//                    }
//                }
                break;
            case MotionEvent.ACTION_MOVE:
                refreshSeekBar(event, false);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
//                if (mListener != null) {
//                    mListener.onStopTrackingTouch(this);
//                }
//                mIsTouching = false;
//                invalidate();
//                if (p.mShowIndicator) {
//                    mIndicator.hide();
//                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.dispatchTouchEvent(event);
    }


    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled == isEnabled()) {
            return;
        }
        super.setEnabled(enabled);
        if (isEnabled()) {
            setAlpha(1.0f);
        } else {
            setAlpha(0.3f);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
        checkIndicatorLoc();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    @Override
    public void onGlobalLayout() {
        checkIndicatorLoc();
    }

    @Override
    public void onScrollChanged() {
        checkIndicatorLoc();
    }

    private void checkIndicatorLoc() {
//        if (mIndicator == null || !p.mShowIndicator) {
//            return;
//        }
//        if (p.mIndicatorStay) {
//            if (mIndicator.isShowing()) {
//                mIndicator.update();
//            } else {
//                mIndicator.show();
//            }
//        } else {
//            mIndicator.forceHide();
//        }
    }

    private void refreshSeekBar(MotionEvent event, boolean isDownTouch) {
        calculateTouchX(adjustTouchX(event));
        calculateProgress();
//        mIsTouching = true;
//        if (isDownTouch) {
//            if (lastProgress != p.mProgress) {
//                setListener(true);
//            }
//            invalidate();
//            if (p.mShowIndicator) {
//                if (mIndicator.isShowing()) {
//                    mIndicator.update(mTouchX);
//                } else {
//                    mIndicator.show(mTouchX);
//                }
//            }
//        } else {
//            if (lastProgress != p.mProgress) {
//                setListener(true);
//                invalidate();
//                if (p.mShowIndicator) {
//                    mIndicator.update(mTouchX);
//                }
//            }
//        }
    }

    private float adjustTouchX(MotionEvent event) {
        float mTouchXCache;
        if (event.getX() < mPaddingLeft) {
            mTouchXCache = mPaddingLeft;
        } else if (event.getX() > mMeasuredWidth - mPaddingRight) {
            mTouchXCache = mMeasuredWidth - mPaddingRight;
        } else {
            mTouchXCache = event.getX();
        }
        return mTouchXCache;
    }

    private void calculateProgress() {
//        lastProgress = p.mProgress;
//        p.mProgress = p.mMin + (p.mMax - p.mMin) * (mTouchX - mPaddingLeft) / mSeekLength;
    }

    private void calculateTouchX(float touchX) {
//        int touchBlockSize = Math.round((touchX - mPaddingLeft) / mSeekBlockLength);
//        mTouchX = mSeekBlockLength * touchBlockSize + mPaddingLeft;
    }




}