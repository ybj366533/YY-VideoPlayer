package com.ybj366533.videoplayer.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

import com.ybj366533.videoplayer.listener.VideoGifSaveListener;
import com.ybj366533.videoplayer.listener.VideoShotSaveListener;
import com.ybj366533.videoplayer.video.StandardVideoPlayer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * gif截图

 */

public class GifCreateHelper {

    private boolean mSaveShotBitmapSuccess = true;

    private StandardVideoPlayer mPlayer;

    private TaskLocal mTimerTask;

    private Timer mTimer = new Timer();

    private List<String> mPicList = new ArrayList<>();

    private File mTmpPath;

    private VideoGifSaveListener mVideoGifSaveListener;

    //gif的帧之间延时
    private int mDelay = 0;

    //采样率
    private int mSampleSize = 1;

    //缩放比例
    private int mScaleSize = 5;

    //截图频率，毫秒
    private int mFrequencyCount = 50;

    public GifCreateHelper(StandardVideoPlayer standardGSYVideoPlayer, VideoGifSaveListener videoGifSaveListener) {
        this(standardGSYVideoPlayer, videoGifSaveListener, 0, 1, 5, 50);
    }

    /**
     * @param delay          每一帧之间的延时
     * @param inSampleSize   采样率，越大图片越小，越大图片越模糊，需要处理的时长越短
     * @param scaleSize      缩减尺寸比例，对生成的截图进行缩减，越大图片越模糊，需要处理的时长越短
     * @param frequencyCount 截图频率，毫秒，用在定时获取帧图片，越大获取的越少
     */
    public GifCreateHelper(StandardVideoPlayer standardGSYVideoPlayer, VideoGifSaveListener videoGifSaveListener,
                           int delay, int inSampleSize, int scaleSize, int frequencyCount) {
        mPlayer = standardGSYVideoPlayer;
        mVideoGifSaveListener = videoGifSaveListener;
        mDelay = delay;
        mSampleSize = inSampleSize;
        mScaleSize = scaleSize;
        mFrequencyCount = frequencyCount;
    }

    /**
     * 开始gif截图
     *
     * @param tmpPicPath 临时缓存图路径
     */
    public void startGif(File tmpPicPath) {
        mTmpPath = tmpPicPath;
        cancelTask();
        mPicList.clear();
        mTimerTask = new TaskLocal();
        //频率可以稍微控制下
        mTimer.schedule(mTimerTask, 0, mFrequencyCount);
    }

    /**
     * 生成gif
     *
     * @param path gif图保存路径
     */
    public void stopGif(final File path) {
        cancelTask();
        mSaveShotBitmapSuccess = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mPicList.size() > 2) {
                    // inSampleSize  采样率，越大图片越小，越大图片越模糊，需要处理的时长越短
                    // scaleSize 缩减尺寸比例，对生成的截图进行缩减，越大图片越模糊，需要处理的时长越短
                    createGif(path, mPicList, mDelay, mSampleSize, mScaleSize, mVideoGifSaveListener);
                } else {
                    mVideoGifSaveListener.result(false, null);
                }
            }
        }).start();
    }

    /**
     * 取消帧图片定时任务
     */
    public void cancelTask() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    /**
     * 开始保存帧图片
     */
    private void startSaveBitmap() {
        // 保存的文件路径，请确保文件夹目录已经创建
        File file = new File(mTmpPath, "GSY-TMP-FRAME" + System.currentTimeMillis() + ".tmp");
        mPlayer.saveFrame(file, new VideoShotSaveListener() {
            @Override
            public void result(boolean success, final File file) {
                mSaveShotBitmapSuccess = true;
                if (success) {
                    Debuger.printfError(" SUCCESS CREATE FILE " + file.getAbsolutePath());
                    mPicList.add(file.getAbsolutePath());
                }
            }
        });
    }


    /**
     * 生成gif图
     *
     * @param file                    保存的文件路径，请确保文件夹目录已经创建
     * @param pics                    需要转化的bitmap本地路径集合
     * @param delay                   每一帧之间的延时
     * @param inSampleSize            采样率，越大图片越小，越大图片越模糊，需要处理的时长越短
     * @param scaleSize               缩减尺寸比例，对生成的截图进行缩减，越大图片越模糊，需要处理的时长越短
     * @param videoGifSaveListener 结果回调
     */
    public void createGif(File file, List<String> pics, int delay, int inSampleSize, int scaleSize,
                          final VideoGifSaveListener videoGifSaveListener) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AnimatedGifEncoder localAnimatedGifEncoder = new AnimatedGifEncoder();
        localAnimatedGifEncoder.start(baos);//start
        localAnimatedGifEncoder.setRepeat(0);//设置生成gif的开始播放时间。0为立即开始播放
        localAnimatedGifEncoder.setDelay(delay);
        for (int i = 0; i < pics.size(); i++) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = inSampleSize;
            options.inJustDecodeBounds = true; // 先获取原大小
            BitmapFactory.decodeFile(pics.get(i), options);
            double w = (double) options.outWidth / scaleSize;
            double h = (double) options.outHeight / scaleSize;
            options.inJustDecodeBounds = false; // 获取新的大小
            Bitmap bitmap = BitmapFactory.decodeFile(pics.get(i), options);
            Bitmap pic = ThumbnailUtils.extractThumbnail(bitmap, (int) w, (int) h);
            localAnimatedGifEncoder.addFrame(pic);
            bitmap.recycle();
            pic.recycle();
            videoGifSaveListener.process(i + 1, pics.size());
        }
        localAnimatedGifEncoder.finish();//finish
        try {
            FileOutputStream fos = new FileOutputStream(file.getPath());
            baos.writeTo(fos);
            baos.flush();
            fos.flush();
            baos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            videoGifSaveListener.result(false, file);
            return;
        }
        videoGifSaveListener.result(true, file);
    }

    /**
     * 保存帧图片定时任务
     */
    private class TaskLocal extends TimerTask {
        @Override
        public void run() {
            if (mSaveShotBitmapSuccess) {
                mSaveShotBitmapSuccess = false;
                startSaveBitmap();
            }
        }
    }
}
