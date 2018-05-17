package com.ybj366533.yy_videoplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ybj366533.yy_videoplayer.R;
import com.ybj366533.yy_videoplayer.holder.RecyclerItemBaseHolder;
import com.ybj366533.yy_videoplayer.model.PlayerVideoModel;
import com.ybj366533.yy_videoplayer.ui.DetailsPlayerActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class VideoPlayerListAdapter extends RecyclerView.Adapter {

    private final static String TAG = "VideoPlayerListAdapter";

    private List<PlayerVideoModel> itemDataList = null;

    private Context context = null;

    public VideoPlayerListAdapter(Context context, List<PlayerVideoModel> itemDataList) {
        this.itemDataList = itemDataList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_video_plsyer_item, parent, false);
        final RecyclerView.ViewHolder holder = new ItemViewHolder(context, v);
        return holder;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder recyclerItemViewHolder = (ItemViewHolder) holder;
        recyclerItemViewHolder.setRecyclerBaseAdapter(this);
        recyclerItemViewHolder.onBind(position, itemDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }


    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    public void setListData(List<PlayerVideoModel> data) {
        itemDataList = data;
        notifyDataSetChanged();
    }


    public class ItemViewHolder extends RecyclerItemBaseHolder {

        public final static String TAG = "RecyclerView2List";

        protected Context context = null;

        @BindView(R.id.list_item)
        ImageView listItem;

        @BindView(R.id.list_item_btn)
        ImageView listItemBtn;


        public ItemViewHolder(Context context, View v) {
            super(v);
            this.context = context;
            ButterKnife.bind(this, v);
        }

        public void onBind(final int position, final PlayerVideoModel videoModel) {

            //增加封面
            listItem.setImageDrawable(context.getResources().getDrawable(videoModel.getImgPath()));

            listItemBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailsPlayerActivity.class);
                    intent.putExtra("video", videoModel.getPath());
                    context.startActivity(intent);
                }
        });
        }

    }


}
