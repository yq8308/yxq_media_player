package com.test.yxq.yxq_media_player.utils.common;

import android.util.Log;

/**
 * Created by Administrator on 2016/5/13.
 */
public class LLog {

    private static final String TAG = "yxq_media_player";

    public static void d(String cs) {
        Log.d(TAG,cs);
    }

    public static void format(String format,Object... params) {
        Log.d(TAG,String.format(format,params));
    }
}
