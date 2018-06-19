package com.ybj366533.yy_videoplayer.ui;

import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ybj366533.videoplayer.VideoManager;
import com.ybj366533.videoplayer.utils.OrientationUtils;
import com.ybj366533.yy_videoplayer.R;
import com.ybj366533.yy_videoplayer.model.SwitchVideoModel;
import com.ybj366533.yy_videoplayer.video.SampleVideo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 播放详情页
 */

public class DetailsPlayerActivity extends AppCompatActivity {

    public final static String IMG_TRANSITION = "IMG_TRANSITION";
    public final static String TRANSITION = "TRANSITION";

    @BindView(R.id.video_player)
    SampleVideo videoPlayer;

    OrientationUtils orientationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_player);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        String url = "http://ugc.strms.migudm.cn/Client/ugc/funshoot/product/test/GTV_20180510_174839_HLS/security_GTV_20180510_174839.m3u8?st=LL5ri75oBYy-86Ym0rMsWQ&e=1531133409";

        //String url = "http://7xse1z.com1.z0.glb.clouddn.com/1491813192";
        //需要路径的
        //videoPlayer.setUp(url, true, new File(FileUtils.getPath()), "");

        //借用了jjdxm_ijkplayer的URL
        String path = getIntent().getStringExtra("video");
        List<SwitchVideoModel> list = new ArrayList<>();
        if (path == null || path.equals("")) {
            SwitchVideoModel switchVideoModel = new SwitchVideoModel("普通", url);
            SwitchVideoModel switchVideoModel2 = new SwitchVideoModel("清晰", url);
            list.add(switchVideoModel);
            list.add(switchVideoModel2);
        } else {

            SwitchVideoModel switchVideoModel = new SwitchVideoModel("普通", path);
            SwitchVideoModel switchVideoModel2 = new SwitchVideoModel("清晰", path);

            list.add(switchVideoModel);
            list.add(switchVideoModel2);
        }


        videoPlayer.setUp(list, true, "测试视频");

        //增加封面
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setImageResource(R.drawable.migu);
        videoPlayer.setThumbImageView(imageView);
        //增加title
        videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        //videoPlayer.setShowPauseCover(false);

        //videoPlayer.setSpeed(2f);

        //设置返回键
        videoPlayer.getBackButton().setVisibility(View.VISIBLE);
        //设置旋转
        orientationUtils = new OrientationUtils(this, videoPlayer);

        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orientationUtils.resolveByClick();
            }
        });
        //是否可以滑动调整
        videoPlayer.setIsTouchWiget(true);

        //设置返回按键功能
        videoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoPlayer.onVideoResume();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }

    @Override
    public void onBackPressed() {
        //先返回正常状态
        if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            videoPlayer.getFullscreenButton().performClick();
            return;
        }
        //释放所有
        videoPlayer.setVideoAllCallBack(null);
        VideoManager.releaseAllVideos();

        finish();
    }


}
