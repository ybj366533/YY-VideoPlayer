package com.ybj366533.yy_videoplayer.ui;

import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.ybj366533.videoplayer.VideoManager;
import com.ybj366533.videoplayer.builder.VideoOptionBuilder;
import com.ybj366533.videoplayer.listener.LockClickListener;
import com.ybj366533.videoplayer.listener.SampleCallBack;
import com.ybj366533.videoplayer.listener.VideoProgressListener;
import com.ybj366533.videoplayer.utils.CommonUtil;
import com.ybj366533.videoplayer.utils.Debuger;
import com.ybj366533.videoplayer.utils.OrientationUtils;
import com.ybj366533.videoplayer.video.base.MVideoPlayer;
import com.ybj366533.yy_videoplayer.R;
import com.ybj366533.yy_videoplayer.listener.AppBarStateChangeListener;
import com.ybj366533.yy_videoplayer.video.LandLayoutVideo;

/**
 * CollapsingToolbarLayout的播放页面
 * 额，有点懒，细节上没处理
 */
public class ScrollingActivity extends AppCompatActivity {

    private boolean isPlay;
    private boolean isPause;
    private boolean isSamll;

    private OrientationUtils orientationUtils;
    private LandLayoutVideo detailPlayer;
    private AppBarLayout appBar;
    private FloatingActionButton fab;
    private CoordinatorLayout root;
    private CollapsingToolbarLayout toolBarLayout;

    private AppBarStateChangeListener.State curState;
    //TODO 相关视频URL由于防盗链问题已过期，需要展示的话可以网上爬一下。
    private String[] m3u8Path = {
            "http://ugc.strms.migudm.cn/Client/ugc/ugcDest2018/20180519/78061570/file/3DQU4HdXl8XaNQP7UhjnkiHLSdmn480pDOWNLOAD_HLS/security_3DQU4HdXl8XaNQP7UhjnkiHLSdmn480pDOWNLOAD.m3u8?st=talDZ9kwZHgDQCWVkW1jiw&e=1532846162",
            "http://ugc.strms.migudm.cn/Client/ugc/ugcDest2018/20180420/11344280/file/1oZqpnQ55bPX8rwztp0qCjHLSdmn480pDOWNLOAD_HLS/security_1oZqpnQ55bPX8rwztp0qCjHLSdmn480pDOWNLOAD.m3u8?st=g2OB8FxeM8j1B4ENSeFdrQ&e=1532846180",
            "http://ugc.strms.migudm.cn/Client/ugc/ugcDest2018/20180417/563709968/file/2p9qkVbylaPG0vmKa0PB2QHLSdmn480pDOWNLOAD_HLS/security_2p9qkVbylaPG0vmKa0PB2QHLSdmn480pDOWNLOAD.m3u8?st=AKlL2TLvjKhZAj6-LUvNjQ&e=1532846195"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        initView();

        String url = "http://ugc.dls.migudm.cn/Client/ugc/ugcDest2018/20180709/1480226614118/file/36WF3nmzh2Z87ZEO6X5h0DHLSdmn480pDOWNLOAD.mp4?st=s5buspQkVDHNU96t2fsBSQ&e=1537516565";

        //增加封面
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        imageView.setImageResource(R.drawable.migu);

        resolveNormalVideoUI();

        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(this, detailPlayer);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);

        VideoOptionBuilder gsyVideoOption = new VideoOptionBuilder();
        gsyVideoOption.setThumbImageView(imageView)
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                .setSeekRatio(1)
                .setUrl(url)
                .setCacheWithPlay(true)
                .setVideoTitle("测试视频")
                .setVideoAllCallBack(new SampleCallBack() {

                    @Override
                    public void onPrepared(String url, Object... objects) {
                        Debuger.printfError("***** onPrepared **** " + objects[0]);
                        Debuger.printfError("***** onPrepared **** " + objects[1]);
                        super.onPrepared(url, objects);
                        //开始播放了才能旋转和全屏
                        orientationUtils.setEnable(true);
                        isPlay = true;
                        root.removeView(fab);
                    }

                    @Override
                    public void onEnterFullscreen(String url, Object... objects) {
                        super.onEnterFullscreen(url, objects);
                        Debuger.printfError("***** onEnterFullscreen **** " + objects[0]);//title
                        Debuger.printfError("***** onEnterFullscreen **** " + objects[1]);//当前全屏player
                    }

                    @Override
                    public void onAutoComplete(String url, Object... objects) {
                        super.onAutoComplete(url, objects);
                    }

                    @Override
                    public void onClickStartError(String url, Object... objects) {
                        super.onClickStartError(url, objects);
                    }

                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                        Debuger.printfError("***** onQuitFullscreen **** " + objects[0]);//title
                        Debuger.printfError("***** onQuitFullscreen **** " + objects[1]);//当前非全屏player
                        if (orientationUtils != null) {
                            orientationUtils.backToProtVideo();
                        }
                    }
                })
                .setLockClickListener(new LockClickListener() {
                    @Override
                    public void onClick(View view, boolean lock) {
                        if (orientationUtils != null) {
                            //配合下方的onConfigurationChanged
                            orientationUtils.setEnable(!lock);
                        }
                    }
                })
                .setGSYVideoProgressListener(new VideoProgressListener() {
                    @Override
                    public void onProgress(int progress, int secProgress, int currentPosition, int duration) {
                        Debuger.printfLog(" progress " + progress + " secProgress " + secProgress + " currentPosition " + currentPosition + " duration " + duration);
                    }
                })
                .build(detailPlayer);

        detailPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                orientationUtils.resolveByClick();

                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                detailPlayer.startWindowFullscreen(ScrollingActivity.this, true, true);
            }
        });

    }

    @Override
    public void onBackPressed() {

        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }

        if (VideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }


    @Override
    protected void onPause() {
        getCurPlay().onVideoPause();
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onResume() {
        getCurPlay().onVideoResume();
        appBar.addOnOffsetChangedListener(appBarStateChangeListener);
        super.onResume();
        isPause = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isPlay) {
            getCurPlay().release();
        }
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            detailPlayer.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
        }
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        detailPlayer = (LandLayoutVideo) findViewById(R.id.detail_player);
        root = (CoordinatorLayout) findViewById(R.id.root_layout);

        setSupportActionBar(toolbar);
        toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailPlayer.startPlayLogic();
                root.removeView(fab);
            }
        });

        appBar = (AppBarLayout) findViewById(R.id.app_bar);
        appBar.addOnOffsetChangedListener(appBarStateChangeListener);
    }

    private void resolveNormalVideoUI() {
        //增加title
        detailPlayer.getTitleTextView().setVisibility(View.GONE);
        detailPlayer.getBackButton().setVisibility(View.GONE);
        //设置返回按键功能
        detailPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private MVideoPlayer getCurPlay() {
        if (detailPlayer.getFullWindowPlayer() != null) {
            return detailPlayer.getFullWindowPlayer();
        }
        return detailPlayer;
    }

    AppBarStateChangeListener appBarStateChangeListener = new AppBarStateChangeListener() {
        @Override
        public void onStateChanged(AppBarLayout appBarLayout, AppBarStateChangeListener.State
        state) {
            if (state == AppBarStateChangeListener.State.EXPANDED) {
                //展开状态
                curState = state;
                toolBarLayout.setTitle("");
            } else if (state == AppBarStateChangeListener.State.COLLAPSED) {
                //折叠状态
                //如果是小窗口就不需要处理
                toolBarLayout.setTitle("Title");
                if (!isSamll && isPlay) {
                    isSamll = true;
                    int size = CommonUtil.dip2px(ScrollingActivity.this, 150);
                    detailPlayer.showSmallVideo(new Point(size, size), true, true);
                    orientationUtils.setEnable(false);
                }
                curState = state;
            } else {
                if (curState == AppBarStateChangeListener.State.COLLAPSED) {
                    //由折叠变为中间状态
                    toolBarLayout.setTitle("");
                    if (isSamll) {
                        isSamll = false;
                        orientationUtils.setEnable(true);
                        //必须
                        detailPlayer.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                detailPlayer.hideSmallVideo();
                            }
                        }, 50);
                    }
                }
                curState = state;
                //中间状态
            }
        }
    };

}
