package com.ybj366533.ijkplayer_java.listener;

import java.io.File;

/**
 * Gif图创建的监听
 * Created by guoshuyu on 2017/10/10.
 */

public interface GSYVideoGifSaveListener {

    void process(int curPosition, int total);

    void result(boolean success, File file);
}
