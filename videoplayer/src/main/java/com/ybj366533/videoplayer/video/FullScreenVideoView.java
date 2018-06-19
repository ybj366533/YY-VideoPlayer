package com.ybj366533.videoplayer.video;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.Toast;

import com.ybj366533.videoplayer.R;
import com.ybj366533.videoplayer.base.BaseVideoPlayer;
import com.ybj366533.videoplayer.utils.Debuger;
import com.ybj366533.videoplayer.utils.NetworkUtils;
import com.ybj366533.videoplayer.video.base.MiGuVideoPlayer;
import com.ybj366533.videoplayer.video.base.PageVideoControlView;
import com.ybj366533.videoplayer.view.ENDownloadView;
import com.ybj366533.videoplayer.view.ENPlayView;


/**
 * 标准播放器，继承之后实现一些ui显示效果，如显示／隐藏ui，播放按键等
 */

public class FullScreenVideoView extends PageVideoControlView {

    public FullScreenVideoView(Context context) {
        super(context);
    }

    public FullScreenVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void init(Context context) {
        super.init(context);

    }

    /**
     * 继承后重写可替换为你需要的布局
     *
     * @return
     */
    @Override
    public int getLayoutId() {
        return R.layout.video_layout_page;
    }

    /**
     * 显示wifi确定框
     */
    @Override
    public void startPlayLogic() {
        prepareVideo();
    }

    /**
     * 显示wifi确定框，如需要自定义继承重写即可
     */
    @Override
    protected void showWifiDialog() {
        if (!NetworkUtils.isAvailable(mContext)) {
            Toast.makeText(mContext, getResources().getString(R.string.no_net), Toast.LENGTH_LONG).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
        builder.setMessage(getResources().getString(R.string.tips_not_wifi));
        builder.setPositiveButton(getResources().getString(R.string.tips_not_wifi_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startPlayLogic();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.tips_not_wifi_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    /********************************各类UI的状态显示*********************************************/

    /**
     * 点击触摸显示和隐藏逻辑
     */
    @Override
    protected void onClickUiToggle() {

    }


    /**
     * 正常
     */
    @Override
    protected void changeUiToNormal() {

    }

    /**
     * 准备中
     */
    @Override
    protected void changeUiToPreparingShow() {

    }

    /**
     * 播放中
     */
    @Override
    protected void changeUiToPlayingShow() {

    }

    /**
     * 暂停中
     */
    @Override
    protected void changeUiToPauseShow() {
        updatePauseCover();
    }

    /**
     * 开始缓冲
     */
    @Override
    protected void changeUiToPlayingBufferingShow() {
    }

    /**
     * 自动播放结束
     */
    @Override
    protected void changeUiToCompleteShow() {

    }

    /**
     * 错误状态
     */
    @Override
    protected void changeUiToError() {

    }

    @Override
    public void onBackFullscreen() {

    }

    /**
     * 获取当前长在播放的播放控件
     */
    public BaseVideoPlayer getCurrentPlayer() {
        if (getFullWindowPlayer() != null) {
            return getFullWindowPlayer();
        }
        if (getSmallWindowPlayer() != null) {
            return getSmallWindowPlayer();
        }
        return this;
    }

}
