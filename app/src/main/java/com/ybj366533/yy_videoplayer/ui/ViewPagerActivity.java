package com.ybj366533.yy_videoplayer.ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.ybj366533.videoplayer.VideoManager;
import com.ybj366533.videoplayer.widget.viewpager.OnViewPagerListener;
import com.ybj366533.videoplayer.widget.viewpager.ViewPagerLayoutManager;
import com.ybj366533.yy_videoplayer.R;
import com.ybj366533.yy_videoplayer.adapter.RecycleViewAdapter;
import com.ybj366533.yy_videoplayer.model.VideoModel;
import com.ybj366533.yy_videoplayer.utils.ViewPlageCalculatorHelper;

import java.util.ArrayList;

/**
 * ViewPagerLayoutManager
 */
public class ViewPagerActivity extends AppCompatActivity {
    private static final String TAG = "ViewPagerActivity";
    private RecyclerView mRecyclerView;
    private RecycleViewAdapter mAdapter;

    private ViewPagerLayoutManager mLayoutManager;
    private ViewPlageCalculatorHelper viewPageCalculatorHelper;

    private ArrayList<VideoModel> mData = new ArrayList<>();
    //TODO 相关视频URL由于防盗链问题已过期，需要展示的话可以网上爬一下。

    private String[] pathList = {

            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018072422/30774787262565300.mp4?st=K3eQ8_ERIr4Jerd13WvtGw&e=1532939226",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018072914/31179783896405858.mp4?st=Ur_86FlCn5OdXH2jeIf-sw&e=1532939228",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018072913/31174882025343267.mp4?st=IOq4WOcnTD70ceqZrVB7Sg&e=1532939228",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018072522/30861541127356921.mp4?st=tJi5UfLuLQXNgvvjzcjZJw&e=1532939249",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018072422/30773708793362098.mp4?st=R6uXwfxbK02rlL2lQPQkGA&e=1532939249",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018072222/30602930692480860.mp4?st=jmajdPKn_SBJFbMMz67jdw&e=1532939249",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018071821/30254919776521484.mp4?st=ShrlVGF0AFv92t7qWPsAmA&e=1532939249",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018071820/30249788658136597.mp4?st=h7jcm5oc3NbRyb0xk6FLhg&e=1532939249",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018071722/30169397410851894.mp4?st=byvbbwRTlQBRgvSmt8D4lw&e=1532939249",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018071721/30168311555264035.mp4?st=ClhLMKQfAAfsLErqUG4RVA&e=1532939249",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018071422/29912946066951198.mp4?st=lqfVHKjovc5dYldJp2mveQ&e=1532939249",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018071417/29894529721569463.mp4?st=xcozEjdt3FWaM1FunbynSA&e=1532939249",
            "http://ugc.dls.migudm.cn/Client/ugc/funshoot/product/2018071121/29649652122488698.mp4?st=bm5Gz13_by6fk3WPZgN-pw&e=1532939249",


    };

    private String[] imgPath = {
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018053117/32396e99e99d48df9801dff74f749f78.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018053119/31ea63d72d81448bbec97eb825e5c5a5.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018053123/ae6cb0b3ecf5440bbd106840f7488517.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018060115/290d873411f64476a0207dad5cfa238c.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018060116/2e2baf784f5d4e4d80bfade60b97207a.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018060117/92fb5a12332b42408ef9b1da55c1d70c.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018060118/0b703fe085414031ac8c2875417e34e2.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018060218/61811ee7d3a74f8a9bbab2ca7fc6851f.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018060223/e9330c2ae4c34fb09f2c4c5f10550257.jpg",
            "http://ugc.dls.migudm.cn/Client/image/funshoot/product/2018060316/a8d3aee4f636416da391a3781694560f.jpg",};


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
        initDate();
        initEven();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recycler);
        mLayoutManager = new ViewPagerLayoutManager(this, OrientationHelper.VERTICAL);
        mAdapter = new RecycleViewAdapter(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initEven() {
        viewPageCalculatorHelper = new ViewPlageCalculatorHelper(ViewPagerActivity.this, R.id.video_view);
        mLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onPageRelease(boolean isNext, int position) {
                Log.e(TAG, "释放位置:" + position + " 下一页:" + isNext);
                viewPageCalculatorHelper.onScrollReleaseChanged(mRecyclerView, position);
                if (isNext) {//下滑
                } else {//上滑

                }
            }

            @Override
            public void onPageSelected(int position, boolean isBottom) {
                Log.e(TAG, "选中位置:" + position + "  是否是滑动到底部:" + isBottom);
                if (isBottom) {
                    // TODO 到底部添加数据
                    initDate();
                }
                playVideo(position);

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
        mAdapter.setPageVideoListener(new RecycleViewAdapter.PageVideoListener() {
            @Override
            public void onPrepared() {
                int sid = mAdapter.getShowItemId();
                if (sid + 1 < mData.size()) {
                    if (!mData.get(sid + 1).getCache()) {
//                        PreloaderVideo(mData.get(sid + 1).getVideoPath());
                        viewPageCalculatorHelper.onVideoCache(mRecyclerView, mData.get(sid + 1).getVideoPath());
                        mData.get(sid + 1).setCache(true);
                    }

                }
            }
        });
    }

    private void playVideo(int position) {
        mAdapter.setShowItemId(position);
        viewPageCalculatorHelper.onScrollSelectChanged(mRecyclerView, position);

    }

    private void initDate() {
        for (int i = 0; i < 10; i++) {
            VideoModel videoModel = new VideoModel();
            String videoPath = pathList[i];
            videoModel.setVideoPath(videoPath);
            videoModel.setImgPath(imgPath[i]);
            videoModel.setCache(false);
            videoModel.setName("测试" + i);
            mData.add(videoModel);
        }
        mAdapter.setData(mData);

        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
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
        viewPageCalculatorHelper.onScrollDestroy();
    }


}
