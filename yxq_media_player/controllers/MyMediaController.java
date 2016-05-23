package com.test.yxq.yxq_media_player.controllers;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.test.yxq.yxq_media_player.R;
import com.test.yxq.yxq_media_player.activities.PlayerActivity;
import com.test.yxq.yxq_media_player.utils.IjkMediaPlayerDelegate;

/**
 * Created by Administrator on 2016/5/3.
 */
public class MyMediaController extends FrameLayout implements MyController,View.OnClickListener{


    private ViewGroup mAnchor;
    private View mRoot;
    private Context mContext;
    private Object obj;
    private int show_state = STATE_SHOW;
    private int time_out = 5000;
    private static final int MEDIA_CONTROLLER_SHOW = 1;
    private static final int MEDIA_CONTROLLER_HIDE = 0;

    private ImageButton btn_back;
    private ImageButton btn_share;
    private ImageButton btn_full;
    private ImageButton btn_media_playpause;

    public MyMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    public void setAnchorView(ViewGroup view) {
        if (this.getParent()!=null || view==null)
        {
            return;
        }
        mAnchor = view;
        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        removeAllViews();
        View v = makeControllerView();
        addView(v, frameParams);
        mAnchor.addView(this);
    }

    @Override
    public View makeControllerView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.media_controller, null);

        initControllerView(mRoot);

        return mRoot;
    }

    private void initControllerView(View view) {
        btn_back = (ImageButton) mRoot.findViewById(R.id.btn_media_back);
        btn_share = (ImageButton) mRoot.findViewById(R.id.btn_media_share);
        btn_full = (ImageButton) mRoot.findViewById(R.id.btn_media_full);
        btn_media_playpause = (ImageButton) mRoot.findViewById(R.id.btn_media_playpause);
        btn_back.setOnClickListener(this);
        btn_share.setOnClickListener(this);
        btn_full.setOnClickListener(this);
        btn_media_playpause.setOnClickListener(this);
    }

    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MEDIA_CONTROLLER_SHOW:
                    break;
                case MEDIA_CONTROLLER_HIDE:
                    hide();
                    break;
            }
        }
    };

    @Override
    public void show() {
        Log.d("1", "show: 111111111");
        show_state = MyController.STATE_SHOW;
        this.setVisibility(View.VISIBLE);
        mHandler.removeMessages(MEDIA_CONTROLLER_HIDE);
        mHandler.sendEmptyMessageDelayed(MEDIA_CONTROLLER_HIDE, time_out);
    }

    @Override
    public void hide() {
        show_state = MyController.STATE_HIDE;
        this.setVisibility(View.GONE);
    }

    @Override
    public int getShowState() {
        return show_state;
    }

    @Override
    public void setTag(Object tag) {
        if (obj!=tag)
            obj = tag;
    }

    @Override
    public void detach() {
        mAnchor.removeView(this);
        mHandler.removeMessages(MEDIA_CONTROLLER_HIDE);
        hide();
        mAnchor = null;
    }

    @Override
    public void onClick(View v) {
        PlayerActivity player = (PlayerActivity)obj;
        switch (v.getId()) {
            case R.id.btn_media_back:
                player.onBackPressed();
                break;
            case R.id.btn_media_share:
                break;
            case R.id.btn_media_full:
                player.changeOrientationToLandspace();
                break;
            case R.id.btn_media_playpause:
                if (IjkMediaPlayerDelegate.isPlaying()) {
                    IjkMediaPlayerDelegate.pause();
                    v.setBackgroundResource(R.drawable.btn_selector_play_small);
                }else {
                    IjkMediaPlayerDelegate.play();
                    v.setBackgroundResource(R.drawable.btn_selector_pause_small);
                }
                break;
        }
    }
}
