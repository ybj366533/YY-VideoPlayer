package com.ybj366533.yy_videoplayer.ui;


import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import com.ybj366533.videoplayer.VideoManager;
import com.ybj366533.yy_videoplayer.R;
import com.ybj366533.yy_videoplayer.adapter.VideoPlayerListAdapter;
import com.ybj366533.yy_videoplayer.model.PlayerVideoModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoPlayerListActivity extends AppCompatActivity {


    @BindView(R.id.list_item_recycler)
    RecyclerView videoList;
    @BindView(R.id.btn_back)
    ImageButton btnBack;

    GridLayoutManager gridLayoutManager;

    VideoPlayerListAdapter mAdapter;

    List<PlayerVideoModel> dataList = new ArrayList<>();

    private String[] pathList = {"http://ugc.strms.migudm.cn/Client/ugc/funshoot/product/test/GTV_20180510_174839_HLS/security_GTV_20180510_174839.m3u8?st=LL5ri75oBYy-86Ym0rMsWQ&e=1531133409"
            ,
            "http://ugc.strms.migudm.cn/Client/ugc/funshoot/product/test/GTV_20180510_175204_HLS/security_GTV_20180510_175204.m3u8?st=X8j52yyHi9bJCqhce6zd-Q&e=1531133428"
            ,
            "http://ugc.strms.migudm.cn/Client/ugc/funshoot/product/test/GTV_20180510_175552_HLS/security_GTV_20180510_175552.m3u8?st=8RGocpnWbpCmlJhPPGCyKw&e=1531133443"
            ,
            "http://ugc.strms.migudm.cn/Client/ugc/funshoot/product/test/GTV_20180510_175736_HLS/security_GTV_20180510_175736.m3u8?st=s5LRAnycG-e-Y0E0J2LJfA&e=1531133457"};

    private int[] imgPath = {R.drawable.images_0, R.drawable.images_1,R.drawable.images_2,R.drawable.images_3,R.drawable.images_4,R.drawable.images_5,R.drawable.images_6,R.drawable.images_7,R.drawable.images_8,R.drawable.images_9,};
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
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        resolveData();

        mAdapter = new VideoPlayerListAdapter(this, dataList);
        gridLayoutManager = new GridLayoutManager(this,2);
        videoList.setLayoutManager(gridLayoutManager);
        videoList.setAdapter(mAdapter);

        videoList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            //int firstVisibleItem, lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                firstVisibleItem   = gridLayoutManager.findFirstVisibleItemPosition();
//                lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();
                //大于0说明有播放
//                if (VideoManager.instance().getPlayPosition() >= 0) {
//                    //当前播放的位置
//                    int position = VideoManager.instance().getPlayPosition();
//                    //对应的播放列表TAG
//                    if (VideoManager.instance().getPlayTag().equals(RecyclerItemNormalHolder.TAG)
//                            && (position < firstVisibleItem || position > lastVisibleItem)) {
//
//                        //如果滑出去了上面和下面就是否，和今日头条一样
//                        //是否全屏
//                        if(!VideoManager.isFullState(VideoPlayerListActivity.this)) {
//                            VideoManager.releaseAllVideos();
//                            recyclerNormalAdapter.notifyDataSetChanged();
//                        }
//                    }
//                }
            }
        });

    }

    @Override
    public void onBackPressed() {
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
            videoModel.setPath(getPath());
            videoModel.setImgPath(getImg());
            dataList.add(videoModel);
        }
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    private String getPath(){
        int max = pathList.length;
        int min = 0;

        Random random = new Random();
        int s = random.nextInt(max)%(max-min+1) + min;
        return pathList[s];
    }

    private int getImg(){
        int max = imgPath.length;
        int min = 0;
        Random random = new Random();
        int s = random.nextInt(max)%(max-min+1) + min;
        return imgPath[s];
    }

}
