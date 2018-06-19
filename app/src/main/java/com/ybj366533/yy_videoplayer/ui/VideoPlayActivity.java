package com.ybj366533.yy_videoplayer.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;

import com.ybj366533.videoplayer.VideoManager;
import com.ybj366533.videoplayer.video.FullScreenVideoView;
import com.ybj366533.yy_videoplayer.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 单独的视频播放页面
 */
public class VideoPlayActivity extends AppCompatActivity {

    public final static String IMG_TRANSITION = "IMG_TRANSITION";
    public final static String TRANSITION = "TRANSITION";

    @BindView(R.id.video_player)
    FullScreenVideoView videoPlayer;
    private boolean isTransition;

    private Transition transition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        ButterKnife.bind(this);
        isTransition = getIntent().getBooleanExtra(TRANSITION, false);
        init();
    }

    private void init() {
//        String url = "http://ugc.strms.migudm.cn/Client/ugc/funshoot/product/test/GTV_20180510_174839_HLS/security_GTV_20180510_174839.m3u8?st=LL5ri75oBYy-86Ym0rMsWQ&e=1531133409\n" +
//                "\n" +
//                "http://ugc.strms.migudm.cn/Client/ugc/funshoot/product/test/GTV_20180510_175204_HLS/security_GTV_20180510_175204.m3u8?st=X8j52yyHi9bJCqhce6zd-Q&e=1531133428\n" +
//                "\n" +
//                "http://ugc.strms.migudm.cn/Client/ugc/funshoot/product/test/GTV_20180510_175552_HLS/security_GTV_20180510_175552.m3u8?st=8RGocpnWbpCmlJhPPGCyKw&e=1531133443\n" +
//                "\n" +
//                "http://ugc.strms.migudm.cn/Client/ugc/funshoot/product/test/GTV_20180510_175736_HLS/security_GTV_20180510_175736.m3u8?st=s5LRAnycG-e-Y0E0J2LJfA&e=1531133457";
//


        String url = "http://ugc.strms.migudm.cn/Client/ugc/funshoot/product/test/GTV_20180510_174839_HLS/security_GTV_20180510_174839.m3u8?st=LL5ri75oBYy-86Ym0rMsWQ&e=1531133409";


        videoPlayer.setUp(url, true);

        videoPlayer.startPlayLogic();
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

    }

    @Override
    public void onBackPressed() {
        //释放所有
        videoPlayer.setVideoPlayerListener(null);
        VideoManager.releaseAllVideos();
        super.onBackPressed();
    }

}
