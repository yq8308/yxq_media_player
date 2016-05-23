package com.test.yxq.yxq_media_player.controllers;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/5/3.
 */
public interface MyController {
    public static final int STATE_HIDE = 0;         //已隐藏
    public static final int STATE_TRANS = 1;        //切换中
    public static final int STATE_SHOW = 2;         //已显示

    void setAnchorView(ViewGroup view);
    View makeControllerView();
    void show();
    void hide();
    int getShowState();
    void setTag(Object tag);
    void detach();
}
