package com.ybj366533.yy_videoplayer.ui;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ybj366533.videoplayer.VideoManager;
import com.ybj366533.videoplayer.listener.VideoPlayerListener;
import com.ybj366533.videoplayer.video.FullScreenVideoView;
import com.ybj366533.videoplayer.widget.viewpager.OnViewPagerListener;
import com.ybj366533.videoplayer.widget.viewpager.ViewPagerLayoutManager;
import com.ybj366533.yy_videoplayer.R;
import com.ybj366533.yy_videoplayer.adapter.RecycleViewAdapter;
import com.ybj366533.yy_videoplayer.model.PlayerVideoModel;
import com.ybj366533.yy_videoplayer.utils.ViewPlageCalculatorHelper;

import java.util.ArrayList;
import java.util.Random;

/**
 * ViewPagerLayoutManager
 */
public class ViewPagerActivity extends AppCompatActivity {
    private static final String TAG = "ViewPagerActivity";
    private RecyclerView mRecyclerView;
    private RecycleViewAdapter mAdapter;

    private ViewPagerLayoutManager mLayoutManager;
    private ViewPlageCalculatorHelper viewPageCalculatorHelper;

    private ArrayList<PlayerVideoModel> mData = new ArrayList<>();
    private String[] pathList = {
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018060619/26618252601954834.mp4",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018060620/26619957888050553.mp4",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018061216/27124379319179984.mp4",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018061215/27122597566373351.mp4",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018061215/27121796210146622.mp4",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018060821/26795757747010413.mp4",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018060821/26795700884473216.mp4",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018060719/26703489777967114.mp4",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018060718/26699239029380704.mp4",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018060520/26535899179808137.mp4",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018060519/26531989234740047.mp4",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018060509/26494827326450521.mp4",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018060417/26438035488103070.mp4",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018060417/26435678433761049.mp4",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018060414/26428035455673878.mp4",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018060414/26427975373544364.mp4",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018060310/26326191386171885.mp4",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018053022/26023141694915642.mp4",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018053022/26022100946326325.mp4",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018053021/26018769670193680.mp4",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018052915/25911786584930421.mp4",
    };

    private String[] imgPath = {
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018060619/6d48e1225c2644c3b75f2f15b6a2c45a.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018060620/f072a748570e4770a5ae81fc818c4851.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018061216/3b8d3dce2f77434ba7fa4873f71ba5d4.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018061215/321448d321e44237bdcf6db0864514c3.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018061215/f780f94ba48049cabd28a1e5210752f0.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018060821/93c2042a3af64383be2a3b719a39290b.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018060821/5ecd659ec3254aebb3ec7ae43900cf62.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018060719/ddc464c033144894b4a01baadd47661d.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018060718/54bedc859fb0496d9d036ec71afc121d.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018060520/6d7a38b4bcf447988253c39dc53a6c00.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018060519/5102a7da50494868b22c31c0d1e278e2.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018060509/4a0faf4f0ae94733a7608df0ed3d6c17.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018060417/1bb7ba0307bc4bd6a81942a65ba4dc6b.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018060417/07d5732078654170bc3cd5133bc8fbda.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018060414/722f4e206b6240ed8a52efbebd98d424.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018060414/6ecfe803c80c4e86bc285f1199f74fa7.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018060310/a16e69f8989c498e97895a467bcadeed.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018053022/ef4e81a60bad4aebba780c7745483d06.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018053022/85e06a1f60be467697a6cceb817b79ad.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018053021/63107893d36148ada1dc2a3e4390279b.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018052915/ad59bf922ad1484f84c4cbc62e1ffbc1.jpg"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager_layout_manager);
        // 1. 沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
                getWindow()
                        .getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            } else {
                getWindow()
                        .setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
        initView();
        resolveData();
        initListener();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recycler);
        mLayoutManager = new ViewPagerLayoutManager(this, OrientationHelper.VERTICAL);
        mAdapter = new RecycleViewAdapter(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initListener() {
        viewPageCalculatorHelper = new ViewPlageCalculatorHelper(R.id.video_view);
        mLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onPageRelease(boolean isNext, int position) {
                Log.e(TAG, "释放位置:" + position + " 下一页:" + isNext);
                viewPageCalculatorHelper.onScrollReleaseChanged(mRecyclerView, position);
//                if (isNext) {
//                    if (position + 2 < mData.size())
//                        GTVPlayerCacheHelper.getInstance().doPreload(mData.get(position + 2).getPath(), 0);
//                } else {
//                    if (position - 2 > 0)
//                        GTVPlayerCacheHelper.getInstance().doPreload(mData.get(position - 2).getPath(), 0);
//                }
                releaseVideo(position);
            }

            @Override
            public void onPageSelected(int position, boolean isBottom) {
                Log.e(TAG, "选中位置:" + position + "  是否是滑动到底部:" + isBottom);
                if (isBottom) {
//                    resolveData();
                } else {
                   playVideo(position);
                }

            }

            @Override
            public void onLayoutComplete() {
                Log.e(TAG, "onLayoutComplete:");
                if (mAdapter.getShowItemId() < 0) {
                    playVideo(0);
                }
//
            }

        });
    }

    private void playVideo(int position) {
        mAdapter.setShowItemId(position);
        viewPageCalculatorHelper.onScrollSelectChanged(mRecyclerView, position);
        if (position + 1 < mData.size()) {

        }

    }

    private void releaseVideo(int index) {
//        View itemView = mLayoutManager.findViewByPosition(index);
//        ImageView imgThumb = itemView.findViewById(R.id.img_thumb);
//        ImageView imgPlay = itemView.findViewById(R.id.img_play);
//        imgThumb.animate().alpha(1).start();
//        imgPlay.animate().alpha(0f).start();
    }

    private void resolveData() {
        for (int i = 0; i < 20; i++) {
            PlayerVideoModel videoModel = new PlayerVideoModel();
//            int random = getRandom();
            String videoPath = "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018072416/30754509996582105.mp4?st=WeIcus-zkKdvaeMIx5wXfw&e=1537607337";
            videoModel.setPath(videoPath);
            videoModel.setHd_path(videoPath);
            videoModel.setImgPath(imgPath[i]);
            videoModel.setName("测试" + i);
            mData.add(videoModel);
        }
        mAdapter.setData(mData);

        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    private int getRandom() {
        int max = 4;
        int min = 0;

        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    @Override
    public void onBackPressed() {
        if (VideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        VideoManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        VideoManager.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoManager.releaseAllVideos();
    }


}
