package com.ybj366533.yy_videoplayer.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.ybj366533.videoplayer.VideoManager;
import com.ybj366533.videoplayer.builder.PageOptionBuilder;
import com.ybj366533.videoplayer.builder.VideoOptionBuilder;
import com.ybj366533.videoplayer.listener.SampleCallBack;
import com.ybj366533.videoplayer.listener.VideoPlayerListener;
import com.ybj366533.videoplayer.video.FullScreenVideoView;
import com.ybj366533.videoplayer.video.StandardVideoPlayer;
import com.ybj366533.yy_videoplayer.R;
import com.ybj366533.yy_videoplayer.model.PlayerVideoModel;

import java.util.ArrayList;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.VideoViewHolder> {

    public final static String TAG = "RecycleViewAdapter";
    private Context mContext;
    private LayoutInflater mInflate;

    private ArrayList<PlayerVideoModel> mData;
    private int showItemId = -1;

    private PageVideoListener listener;

    public RecycleViewAdapter(Context context) {
        mContext = context;
        mInflate = LayoutInflater.from(mContext);
        mData = new ArrayList<>();

    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflate.inflate(R.layout.item_view_pager, parent, false);
        return new VideoViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final VideoViewHolder holder, final int position) {
        if (holder != null) {
            holder.onBind(position, mData.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public PlayerVideoModel getItemData(int position) {
        return mData.get(position);
    }

    public ArrayList<PlayerVideoModel> getData() {
        return mData;
    }

    public void setData(ArrayList<PlayerVideoModel> data) {
        this.mData = data;
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView img_thumb;
        FullScreenVideoView videoView;
        ImageView img_play;
        RelativeLayout rootView;
        //底部进度调
        ProgressBar mBottomProgressBar;

        private PageOptionBuilder videoOptionBuilder;

        VideoViewHolder(View itemView) {
            super(itemView);
            videoOptionBuilder = new PageOptionBuilder();
            img_thumb = itemView.findViewById(R.id.img_thumb);
            videoView = itemView.findViewById(R.id.video_view);
            img_play = itemView.findViewById(R.id.img_play);
            rootView = itemView.findViewById(R.id.root_view);
            mBottomProgressBar = itemView.findViewById(R.id.bottom_progressbar);
        }

        public void onBind(final int position, PlayerVideoModel videoModel) {

            PlayerVideoModel model = mData.get(position);
            Glide.with(mContext)
                    .load(model.getImgPath())
                    .into(img_thumb);
            img_thumb.animate().alpha(1f).start();
            videoOptionBuilder
                    .setIsTouchWiget(false)
                    .setUrl(model.getPath())
                    .setLooping(true)
                    .setSetUpLazy(true)//lazy可以防止滑动卡顿
                    .setLockLand(true)
                    .setPlayTag(TAG)
                    .setPlayPosition(position)
                    .setVideoPlayerListener(new VideoPlayerListener() {
                        @Override
                        public void onVideoStartSet(String url, Object... objects) {
                            Log.e(TAG, "onVideoStartSet");
                            img_thumb.animate().alpha(1f).start();
                        }

                        @Override
                        public void onVideoCompletion(String url, Object... objects) {
                            Log.e(TAG, "onVideoCompletion");
                            img_thumb.animate().alpha(1f).start();
                        }

                        @Override
                        public void onVideoPrepared(String url, Object... objects) {
                            Log.e(TAG, "onVideoPrepared");
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    // do something
                                    img_thumb.animate().alpha(0f).start();
                                }

                            }, 100);

                        }

                        @Override
                        public void onVideoBufferingUpdate(String url, Object... objects) {
                            Log.e(TAG, "onVideoBufferingUpdate");
                        }

                        @Override
                        public void onVideoStart(String url, Object... objects) {
                            Log.e(TAG, "onVideoStart");
                        }

                        @Override
                        public void onVideoStartError(String url, Object... objects) {
                            Log.e(TAG, "onVideoStartError");
                        }

                        @Override
                        public void onVideoPlayerStop(String url, Object... objects) {
                            Log.e(TAG, "onVideoPlayerStop");
                        }

                        @Override
                        public void onVideoPlayerResume(String url, Object... objects) {
                            Log.e(TAG, "onVideoPlayerResume");
                        }

                        @Override
                        public void onVideoAutoComplete(String url, Object... objects) {
                            Log.e(TAG, "onVideoAutoComplete");
                        }

                        @Override
                        public void onVideoPlayError(String url, Object... objects) {
                            Log.e(TAG, "onVideoPlayError");
                            img_thumb.animate().alpha(1f).start();
                        }


                    }).build(videoView);

        }
    }

    public void setPageVideoListener(PageVideoListener listener) {
        this.listener = listener;
    }

    public interface PageVideoListener {
        void onPrepared(PlayerVideoModel videoModel);
    }

    public int getShowItemId() {
        return showItemId;
    }

    public void setShowItemId(int showItemId) {
        this.showItemId = showItemId;
    }
}
