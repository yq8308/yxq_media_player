package com.test.yxq.yxq_media_player.activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.test.yxq.yxq_media_player.R;
import com.test.yxq.yxq_media_player.utils.ChatMessageAdapter;
import com.test.yxq.yxq_media_player.utils.DanmakuHelper;
import com.test.yxq.yxq_media_player.utils.MsgManager;
import com.test.yxq.yxq_media_player.views.MyVideoPagerView;
import com.test.yxq.yxq_media_player.views.MyVideoView;

import master.flame.danmaku.ui.widget.DanmakuView;

public class PlayerActivity extends Activity {

    private static final String TAG = "PlayerActivity";

    private MyVideoView mVideoView;
    private MyVideoPagerView my_pager_view;

    private Uri mUri;

    private int currentOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    private DanmakuHelper dm_helper;

    private Button test_btn;

    private MsgManager mm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: " + TAG);
        setContentView(R.layout.activity_player);
        initView();
    }

    private void initView() {

        mUri = Uri.parse(getIntent().getExtras().getString("url"));
        mVideoView = (MyVideoView) findViewById(R.id.video_view);
        mVideoView.setTag(this);
        mVideoView.setVideoUri(mUri);
        setVideSize();

        my_pager_view = (MyVideoPagerView) findViewById(R.id.my_pager_view);

        //先加载弹幕view，在加载控制层
        initDanmakuView();
        mVideoView.initMediaController();
    }

    private void initDanmakuView() {
        dm_helper = DanmakuHelper.instance();
        DanmakuView dv = (DanmakuView) dm_helper.createNewDanmaku(this);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mVideoView.addView(dv, lp);
    }

    private void setVideSize() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int w, h;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            //竖屏，以widht为准 16：9
            w = width;
            h = width * 9 / 16;
        } else {
            //横屏， 全屏
            w = width;
            h = height;
        }

        Log.d(TAG, String.format("setVideSize: width = %d , height = %d , currentOrientation = %d", w, h, currentOrientation));

        ViewGroup.LayoutParams lp = mVideoView.getLayoutParams();
        lp.width = w;
        lp.height = h;
        mVideoView.setLayoutParams(lp);
        mVideoView.requestLayout();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //设置全屏隐藏状态栏
             //this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //取消全屏
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);            //取消全屏
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);           //显示状态栏
        }
    }

    public void changeOrientationToPortrait() {
        Log.d(TAG, "changeOrientationToPortrait: portrait");
        currentOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(currentOrientation);
        mVideoView.setMediaController(currentOrientation);
        setVideSize();
        dm_helper.stop();
        my_pager_view.setVisibility(View.VISIBLE);
    }

    public void changeOrientationToLandspace() {
        Log.d(TAG, "changeOrientationToPortrait: landscape");
        currentOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
        setRequestedOrientation(currentOrientation);
        mVideoView.setMediaController(currentOrientation);
        setVideSize();
        dm_helper.start();
        my_pager_view.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            if(my_pager_view.isIMEVisible()){
                my_pager_view.closeIME();
                return;
            }
            super.onBackPressed();
        } else {
            changeOrientationToPortrait();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mm = MsgManager.instance();
        mm.setChatMessageAdapter(ChatMessageAdapter.cmAdapter);
        mm.start();

        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mm.stop();
        mm = null;
        mVideoView.release();

        Log.d(TAG, "onStop: ");
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float y = event.getY();
            if (y<my_pager_view.getIMEHeight()){
                if (my_pager_view.isIMEVisible()){
                    my_pager_view.closeIME();
                    return true;
                }
            }
        }

        return super.onTouchEvent(event);
    }
}
