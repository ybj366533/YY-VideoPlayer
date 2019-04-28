package com.ybj366533.yy_videoplayer.ui;


import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.ybj366533.videoplayer.VideoManager;
import com.ybj366533.videoplayer.listener.VideoGifSaveListener;
import com.ybj366533.videoplayer.listener.VideoShotListener;
import com.ybj366533.videoplayer.utils.CommonUtil;
import com.ybj366533.videoplayer.utils.FileUtils;
import com.ybj366533.videoplayer.utils.GifCreateHelper;
import com.ybj366533.videoplayer.utils.OrientationUtils;
import com.ybj366533.yy_videoplayer.R;
import com.ybj366533.yy_videoplayer.adapter.VideoPlayerListAdapter;
import com.ybj366533.yy_videoplayer.model.PlayerVideoModel;
import com.ybj366533.yy_videoplayer.video.MListVideoPlayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoPlayerListActivity extends AppCompatActivity {
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.btn_speed)
    Button btnSpeed;
    @BindView(R.id.btn_shot)
    Button btnShot;
    @BindView(R.id.btn_start_gif)
    Button btnStartGIF;
    @BindView(R.id.btn_stop_gif)
    Button btnStopGIF;
    @BindView(R.id.video_player)
    MListVideoPlayer videoPlayer;
    @BindView(R.id.list_item_recycler)
    RecyclerView videoList;

    OrientationUtils orientationUtils;

    GridLayoutManager gridLayoutManager;

    VideoPlayerListAdapter mAdapter;

    List<PlayerVideoModel> dataList = new ArrayList<>();

    private GifCreateHelper mGifCreateHelper;

    private float speed = 1;
    //TODO 相关视频URL由于防盗链问题已过期，需要展示的话可以网上爬一下。

    private String[] pathList = {"http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018060414/26428035455673878.mp4?st=T3V5sa52Sf0NtbzJ7kOWmQ&e=15282831354",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018060416/26434037802427631.mp4?st=38VuCklTtfCM1-EFva0Shg&e=1528283135",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018060320/26363351098017897.mp4?st=3N-wOFUj39ATpCD3Cu965Q&e=1528283135",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018060323/26371423973555302.mp4?st=mNH6Y4iPv5LCSNdorsg84g&e=1528283135",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018060316/26348747686490431.mp4?st=ef7w3axLyPsZJWZ1whuvcg&e=1528283135",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018060115/26169193405561610.mp4?st=dJj9tcqsb6nR2w41LUBeXw&e=1528283135"};

    private String[] imgPath = {"http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018060414/722f4e206b6240ed8a52efbebd98d424.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018060416/075301629fbc4144b88a6cb510e05ee8.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018060320/cabab7d87a304ce4a8f28ee6f9555bcf.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018060323/37161c4fca5c49848b86e6c18ffd7992.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018060316/a8d3aee4f636416da391a3781694560f.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018060115/290d873411f64476a0207dad5cfa238c.jpg"};
    private String[] title = {"腿不够长手来凑", "黑猫警长单身原因", "精致女孩应该这样", "我们不一样", "不归路！", "啦啦啦"};

    private String[] mp4Path = {"http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018050700/23956949706089368.mp4?st=y6aj2Kh86Rp0ZKyi9SKy0Q&e=1530253476",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018050708/23985808557768117.mp4?st=2xBqXZffSr0Y7qH8PoQI5g&e=1530253487",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018050711/23995822851808499.mp4?st=whMrRurOkOc7nqZjrHWwjw&e=1530253500",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018050711/23996843548906685.mp4?st=AjiXzpoXb7ic8yZi9ru7bQ&e=1530253512",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018050714/24007145444917506.mp4?st=PcR4Rxzs7CHsT8dMhXp6Gw&e=1530253524",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018050709/23989729816727636.mp4?st=lzTvyHtKbjTbX2SJEclb9g&e=1530253533",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018050710/23992720588140603.mp4?st=l88Cqied-Z2NOCnQGhu96w&e=1530253544"};

    private String[] m3u8Path = {
            "http://ugc.strms.migudm.cn/Client/ugc/ugcDest2018/20180519/78061570/file/3DQU4HdXl8XaNQP7UhjnkiHLSdmn480pDOWNLOAD_HLS/security_3DQU4HdXl8XaNQP7UhjnkiHLSdmn480pDOWNLOAD.m3u8?st=talDZ9kwZHgDQCWVkW1jiw&e=1532846162",
            "http://ugc.strms.migudm.cn/Client/ugc/ugcDest2018/20180420/11344280/file/1oZqpnQ55bPX8rwztp0qCjHLSdmn480pDOWNLOAD_HLS/security_1oZqpnQ55bPX8rwztp0qCjHLSdmn480pDOWNLOAD.m3u8?st=g2OB8FxeM8j1B4ENSeFdrQ&e=1532846180",
            "http://ugc.strms.migudm.cn/Client/ugc/ugcDest2018/20180417/563709968/file/2p9qkVbylaPG0vmKa0PB2QHLSdmn480pDOWNLOAD_HLS/security_2p9qkVbylaPG0vmKa0PB2QHLSdmn480pDOWNLOAD.m3u8?st=AKlL2TLvjKhZAj6-LUvNjQ&e=1532846195"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 设置一个exit transition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player_list);
        ButterKnife.bind(this);

        initView();
//        resolveData();
        resolveTextData();
        initPlayer();
        initGifHelper();

    }

    private void initView() {
        mAdapter = new VideoPlayerListAdapter(this, dataList);
        gridLayoutManager = new GridLayoutManager(this, 2);
        videoList.setLayoutManager(gridLayoutManager);
        videoList.setAdapter(mAdapter);
        mAdapter.setItemClickListener(new VideoPlayerListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(PlayerVideoModel videoModel) {

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoPlayer.playNext();
            }
        });

        btnSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resolveTypeUI();
            }
        });

        btnShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shotImage();
            }
        });

        btnStartGIF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGif();
            }
        });

        btnStopGIF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopGif();
            }
        });
    }

    private void initPlayer() {
        String url = "http://ugc.strms.migudm.cn/Client/ugc/funshoot/product/test/GTV_20180510_174839_HLS/security_GTV_20180510_174839.m3u8?st=LL5ri75oBYy-86Ym0rMsWQ&e=1531133409";

        videoPlayer.setUp(dataList, true, 0);

        //增加封面
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        imageView.setImageResource(R.drawable.migu);
        videoPlayer.setThumbImageView(imageView);
        //增加title
        videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        //videoPlayer.setShowPauseCover(false);

//        videoPlayer.setSpeed(2f);
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
//        GTVPlayerCacheHelper.getInstance().deleteCache();
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


    private void resolveData() {
        for (int i = 0; i < 20; i++) {
            PlayerVideoModel videoModel = new PlayerVideoModel();
            String videoPath = getPath();
            videoModel.setPath(videoPath);
            videoModel.setHd_path(videoPath);
            videoModel.setImgPath(getImg());
            videoModel.setName("测试视频" + i);
            dataList.add(videoModel);
        }
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    private void resolveTextData() {
        for (int i = 0; i < 20; i++) {
            PlayerVideoModel videoModel = new PlayerVideoModel();
            String videoPath = m3u8Path[0];
            String url = "http://strms.migudm.cn/Client/cartoon/sub_opftp_001/20160203/CP1599/CP1599000001481871/gddlc_ptpwq_g000006_HLS/security_gddlc_ptpwq_g000006.m3u8?st=fc4m-7wqCKZlC7-lI27GSw&e=1537516488";
            videoModel.setPath(url);
            videoModel.setHd_path(url);
            videoModel.setImgPath(getImg());
            videoModel.setName("测试视频" + i);
            dataList.add(videoModel);
        }
//        if (mAdapter != null)
//            mAdapter.notifyDataSetChanged();
    }

    private String getPath() {
        int max = pathList.length;
        int min = 0;

        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return pathList[s];
    }

    private String getImg() {
        int max = imgPath.length;
        int min = 0;
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return imgPath[s];
    }

    /**
     * 设置播放速度
     */
    private void resolveTypeUI() {
        if (speed == 1) {
            speed = 1.5f;
        } else if (speed == 1.5f) {
            speed = 2f;
        } else if (speed == 2) {
            speed = 0.5f;
        } else if (speed == 0.5f) {
            speed = 0.25f;
        } else if (speed == 0.25f) {
            speed = 1;
        }
        btnSpeed.setText("播放速度：" + speed);
        videoPlayer.setSpeedPlaying(speed, true);
    }

    private void initGifHelper() {
        mGifCreateHelper = new GifCreateHelper(videoPlayer, new VideoGifSaveListener() {
            @Override
            public void result(boolean success, File file) {
                videoPlayer.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(videoPlayer.getContext(), "创建成功", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void process(int curPosition, int total) {
                Log.e("initGifHelper", " current " + curPosition + " total " + total);
            }
        });
    }


    /**
     * 开始gif截图
     */
    void startGif() {
        //开始缓存各个帧
        mGifCreateHelper.startGif(new File(FileUtils.getPath()));

    }

    /**
     * 生成gif
     */
    void stopGif() {
        mGifCreateHelper.stopGif(new File(FileUtils.getPath(), "GSY-Z-" + System.currentTimeMillis() + ".gif"));
    }

    /**
     * 视频截图
     * 这里没有做读写本地sd卡的权限处理，记得实际使用要加上
     */
    void shotImage() {
        //获取截图
        videoPlayer.taskShotPic(new VideoShotListener() {
            @Override
            public void getBitmap(Bitmap bitmap) {
                if (bitmap != null) {
                    try {
                        CommonUtil.saveBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        Log.e("shotImage", "save fail ");
                        e.printStackTrace();
                        return;
                    }
                    Log.e("shotImage", "save success ");
                } else {
                    Log.e("shotImage", "get bitmap fail ");
                }
            }
        });

    }
}
