package com.ybj366533.videoplayer.listener;


import java.io.File;

/**
 * 截屏保存结果
 */

public interface VideoShotSaveListener {
    void result(boolean success, File file);
}
