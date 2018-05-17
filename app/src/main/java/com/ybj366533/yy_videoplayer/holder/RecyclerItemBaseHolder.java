package com.ybj366533.yy_videoplayer.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 */

public class RecyclerItemBaseHolder extends RecyclerView.ViewHolder {

    RecyclerView.Adapter recyclerBaseAdapter;

    public RecyclerItemBaseHolder(View itemView) {
        super(itemView);
    }

    public RecyclerView.Adapter getRecyclerBaseAdapter() {
        return recyclerBaseAdapter;
    }

    public void setRecyclerBaseAdapter(RecyclerView.Adapter recyclerBaseAdapter) {
        this.recyclerBaseAdapter = recyclerBaseAdapter;
    }
}
