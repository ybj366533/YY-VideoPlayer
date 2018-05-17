package com.ybj366533.yy_videoplayer.utils.floatUtil;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * https://github.com/yhaolpz
 */

public class Screen {
    public static final int width = 0;
    public static final int height = 1;

    @IntDef({width, height})
    @Retention(RetentionPolicy.SOURCE)
    @interface screenType {
    }
}
