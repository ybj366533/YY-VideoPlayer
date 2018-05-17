package com.ybj366533.yy_videoplayer.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.ybj366533.yy_videoplayer.R;


public class LoadingDialog extends Dialog {

    private Context context;

    public LoadingDialog(Context context) {
        super(context, R.style.dialog_style);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_loading_dialog, null);
        setContentView(view);

        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }
}