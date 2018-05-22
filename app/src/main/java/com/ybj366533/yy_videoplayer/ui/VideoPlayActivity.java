package com.ybj366533.yy_videoplayer.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.view.View;
import android.widget.ImageView;

import com.ybj366533.videoplayer.VideoManager;
import com.ybj366533.yy_videoplayer.R;
import com.ybj366533.yy_videoplayer.listener.OnTransitionListener;
import com.ybj366533.yy_videoplayer.model.SwitchVideoModel;
import com.ybj366533.yy_videoplayer.video.SampleVideo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 单独的视频播放页面
 */
public class VideoPlayActivity extends AppCompatActivity {

    public final static String IMG_TRANSITION = "IMG_TRANSITION";
    public final static String TRANSITION = "TRANSITION";

    @BindView(R.id.video_player)
    SampleVideo videoPlayer;
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
        String url = "http://ugc.strms.migudm.cn/Client/ugc/funshoot/product/test/GTV_20180510_174839_HLS/security_GTV_20180510_174839.m3u8?st=LL5ri75oBYy-86Ym0rMsWQ&e=1531133409\n" +
                "\n" +
                "http://ugc.strms.migudm.cn/Client/ugc/funshoot/product/test/GTV_20180510_175204_HLS/security_GTV_20180510_175204.m3u8?st=X8j52yyHi9bJCqhce6zd-Q&e=1531133428\n" +
                "\n" +
                "http://ugc.strms.migudm.cn/Client/ugc/funshoot/product/test/GTV_20180510_175552_HLS/security_GTV_20180510_175552.m3u8?st=8RGocpnWbpCmlJhPPGCyKw&e=1531133443\n" +
                "\n" +
                "http://ugc.strms.migudm.cn/Client/ugc/funshoot/product/test/GTV_20180510_175736_HLS/security_GTV_20180510_175736.m3u8?st=s5LRAnycG-e-Y0E0J2LJfA&e=1531133457";

        //String url = "http://7xse1z.com1.z0.glb.clouddn.com/1491813192";
        //需要路径的
        //videoPlayer.setUp(url, true, new File(FileUtils.getPath()), "");

        //借用了jjdxm_ijkplayer的URL
        String source1 = "http://ugc.strms.migudm.cn/Client/ugc/funshoot/product/test/GTV_20180510_174839_HLS/security_GTV_20180510_174839.m3u8?st=LL5ri75oBYy-86Ym0rMsWQ&e=1531133409";
        String name = "普通";
        SwitchVideoModel switchVideoModel = new SwitchVideoModel(name, source1);

        String source2 = "http://ugc.strms.migudm.cn/Client/ugc/funshoot/product/test/GTV_20180510_174839_HLS/security_GTV_20180510_174839.m3u8?st=LL5ri75oBYy-86Ym0rMsWQ&e=1531133409";
        String name2 = "清晰";
        SwitchVideoModel switchVideoModel2 = new SwitchVideoModel(name2, source2);

        List<SwitchVideoModel> list = new ArrayList<>();
        list.add(switchVideoModel);
        list.add(switchVideoModel2);

        videoPlayer.setUp(list, true, "测试视频");

        //增加封面
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setImageResource(R.drawable.migu);
        videoPlayer.setThumbImageView(imageView);
        //增加title
        videoPlayer.getTitleTextView().setVisibility(View.GONE);
        //videoPlayer.setShowPauseCover(false);

        //videoPlayer.setSpeed(2f);

        //设置返回键
        videoPlayer.getBackButton().setVisibility(View.VISIBLE);


        //是否可以滑动调整
        videoPlayer.setIsTouchWiget(true);

        //设置返回按键功能
        videoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //过渡动画
        initTransition();
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
        videoPlayer.setVideoAllCallBack(null);
        VideoManager.releaseAllVideos();
        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.onBackPressed();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                }
            }, 500);
        }
    }


    private void initTransition() {
        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
            ViewCompat.setTransitionName(videoPlayer, IMG_TRANSITION);
            addTransitionListener();
            startPostponedEnterTransition();
        } else {
            videoPlayer.startPlayLogic();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean addTransitionListener() {
        transition = getWindow().getSharedElementEnterTransition();
        if (transition != null) {
            transition.addListener(new OnTransitionListener(){
                @Override
                public void onTransitionEnd(Transition transition) {
                    super.onTransitionEnd(transition);
                    videoPlayer.startPlayLogic();
                    transition.removeListener(this);
                }
            });
            return true;
        }
        return false;
    }

}
