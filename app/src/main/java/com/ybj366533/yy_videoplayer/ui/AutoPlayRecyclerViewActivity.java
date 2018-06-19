package com.ybj366533.yy_videoplayer.ui;


import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.util.Log;
import android.view.Window;

import com.ybj366533.videoplayer.VideoManager;
import com.ybj366533.videoplayer.utils.CommonUtil;
import com.ybj366533.videoplayer.widget.viewpager.OnViewPagerListener;
import com.ybj366533.videoplayer.widget.viewpager.ViewPagerLayoutManager;
import com.ybj366533.yy_videoplayer.R;
import com.ybj366533.yy_videoplayer.adapter.RecycleViewAdapter;
import com.ybj366533.yy_videoplayer.adapter.RecyclerBaseAdapter;
import com.ybj366533.yy_videoplayer.adapter.RecyclerNormalAdapter;
import com.ybj366533.yy_videoplayer.model.PlayerVideoModel;
import com.ybj366533.yy_videoplayer.utils.ScrollCalculatorHelper;
import com.ybj366533.yy_videoplayer.utils.ViewPlageCalculatorHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 类似微博视频，滑动到具体位置自动播放
 */
public class AutoPlayRecyclerViewActivity extends AppCompatActivity {

    private String TAG = "AutoPlayRecyclerViewActivity";
    @BindView(R.id.list_item_recycler)
    RecyclerView videoList;

    private ViewPagerLayoutManager mLayoutManager;

    private RecyclerNormalAdapter mAdapter;

    private List<PlayerVideoModel> dataList = new ArrayList<>();

    boolean mFull = false;
    int firstVisibleItem, lastVisibleItem;

//    ScrollCalculatorHelper scrollCalculatorHelper;
    private ViewPlageCalculatorHelper viewPageCalculatorHelper;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 设置一个exit transition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        ButterKnife.bind(this);

        resolveData();


        //限定范围为屏幕一半的上下偏移180
        int playTop = CommonUtil.getScreenHeight(this) / 2 - CommonUtil.dip2px(this, 180);
        int playBottom = CommonUtil.getScreenHeight(this) / 2 + CommonUtil.dip2px(this, 180);
        //自定播放帮助类
//        scrollCalculatorHelper = new ScrollCalculatorHelper(R.id.video_item_player, playTop, playBottom);
        viewPageCalculatorHelper = new ViewPlageCalculatorHelper(R.id.video_item_player);
        mAdapter = new RecyclerNormalAdapter(this, dataList);
        mLayoutManager = new ViewPagerLayoutManager(this, OrientationHelper.VERTICAL);
        videoList.setLayoutManager(mLayoutManager);
        videoList.setAdapter(mAdapter);

        videoList.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                scrollCalculatorHelper.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
//                scrollCalculatorHelper.onScroll(recyclerView, firstVisibleItem, lastVisibleItem, lastVisibleItem - firstVisibleItem);

            }
        });
        mLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onPageRelease(boolean isNext, int position) {
                Log.e(TAG, "释放位置:" + position + " 下一页:" + isNext);
                lastVisibleItem = position;
//                if (isNext)
//                    releaseVideo(position);
            }

            @Override
            public void onPageSelected(int position, boolean isBottom) {
                Log.e(TAG, "选中位置:" + position + "  是否是滑动到底部:" + isBottom);
                firstVisibleItem = position;
                viewPageCalculatorHelper.onScrollSelectChanged(videoList, position);
//                playVideo(position);
            }

            @Override
            public void onLayoutComplete() {
//                playVideo(0);
            }

        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (newConfig.orientation != ActivityInfo.SCREEN_ORIENTATION_USER) {
            mFull = false;
        } else {
            mFull = true;
        }

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


    private void resolveData() {
        for (int i = 0; i < 20; i++) {
            PlayerVideoModel videoModel = new PlayerVideoModel();
            int random = getRandom();
            String videoPath = pathList[random];
            videoModel.setPath(videoPath);
            videoModel.setHd_path(videoPath);
            videoModel.setImgPath(imgPath[random]);
            videoModel.setName(title[random]);
            dataList.add(videoModel);
        }
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    private int getRandom() {
        int max = 5;
        int min = 0;

        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

}
