package com.test.yxq.yxq_media_player.views;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.test.yxq.yxq_media_player.controllers.MyController;
import com.test.yxq.yxq_media_player.controllers.MyMediaController;
import com.test.yxq.yxq_media_player.controllers.MyMediaController_landscape;
import com.test.yxq.yxq_media_player.utils.IjkMediaPlayerDelegate;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * TODO: document your custom view class.
 */
public class MyVideoView extends FrameLayout {

    private static final String TAG = "MyVideoView";

    private Context mContext;
    private Uri mUri;

    private IjkMediaPlayer mMediaPlayer;

    private MyController mMediaController;

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private LinearLayout video_tip_layer;
    private TextView video_buff_percent;
    private IjkMediaPlayer mediaPlayer;
    private MyController mMeidaController_portriat;
    private MyController mMediaController_landscape;

    private static final int EVENT_GET_NET_SPEED = 1;
    private static final int EVENT_GET_NET_DELAY = 2;
    private static final int TIME_SEPERATE = 2000;

    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initSurfaceView();
        initMediaTipLayer();
    }


    public void initMediaController() {

        mMeidaController_portriat = new MyMediaController(mContext, null);
        mMediaController_landscape = new MyMediaController_landscape(mContext, null);

        setMediaController(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void initSurfaceView() {
        mSurfaceView = new SurfaceView(mContext);
        mSurfaceView.getHolder().addCallback(mSHCallback);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mSurfaceView, lp);
    }

    private void initMediaTipLayer() {
        video_tip_layer = new LinearLayout(mContext);
        video_tip_layer.setGravity(Gravity.CENTER);
        video_tip_layer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addView(video_tip_layer, lp);

        video_buff_percent = new TextView(mContext);
        video_buff_percent.setTextColor(Color.WHITE);
        video_buff_percent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        LinearLayout.LayoutParams tv_lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv_lp.setMargins(0, 0, 0, 5);
        video_tip_layer.addView(video_buff_percent, tv_lp);

        ProgressBar progressBar = new ProgressBar(mContext);
        video_tip_layer.addView(progressBar);

        video_tip_layer.setVisibility(GONE);
    }

    private void initPlay() {
        if (mSurfaceHolder == null || mUri == null) {
            Log.d(TAG, "play: surfaceholder null or uri null");
            return;
        }
        if (mediaPlayer != null) {
            IjkMediaPlayerDelegate.destroy();
        }
        mediaPlayer = IjkMediaPlayerDelegate.getMediaPlayer();
        mediaPlayer.setOnPreparedListener(onPreparedListener);
        mediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
        mediaPlayer.setOnInfoListener(onInfoListener);
        IjkMediaPlayerDelegate.openVideo(mediaPlayer, mSurfaceHolder, mUri.toString());
    }

    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mSurfaceHolder = holder;
            initPlay();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    };

    IMediaPlayer.OnPreparedListener onPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            Log.d(TAG, "OnPreparedListener :onPrepared");
            mp.start();
        }
    };

    IMediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            Log.d(TAG, "onBufferingUpdate: " + percent);  //如果是本地文件，则从0-100，如果是网络视频则一直是0
        }
    };

    IMediaPlayer.OnInfoListener onInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {
            Log.d(TAG, "onInfo: " + what + " " + extra);
            switch (what) {
                // 监听buffering start 和end消息，控制显示缓冲进度显示与隐藏
                case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                    Log.d(TAG, "onInfo: MEDIA_INFO_BUFFERING_START " + extra);
                    video_tip_layer.setVisibility(View.VISIBLE);
                    Message message = mHandler.obtainMessage();
                    message.what = EVENT_GET_NET_SPEED;
                    message.arg1 = getNetSpeed();
                    mHandler.sendMessage(message);
                    break;
                case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                    Log.d(TAG, "onInfo: MEDIA_INFO_BUFFERING_END " + extra);
                    video_tip_layer.setVisibility(View.GONE);
                    mHandler.removeMessages(EVENT_GET_NET_SPEED);
                    mHandler.removeMessages(EVENT_GET_NET_DELAY);
                    break;
            }
            return false;
        }
    };

    public void setVideoPath(String path) {
        setVideoUri(Uri.parse(path));
    }

    public void setVideoUri(Uri uri) {
        mUri = uri;
        initPlay();
    }

    public void setMediaController(int orientation) {
        if (mMediaController != null) {
            mMediaController.detach();
        }
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            mMediaController = mMeidaController_portriat;
        } else {
            mMediaController = mMediaController_landscape;
        }

        mMediaController.setTag(this.getTag());     //必须放在setAnchorView前面
        mMediaController.setAnchorView(this);
        mMediaController.show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mMediaController != null) {
            toggleMediaControlsVisiblity();
        }
        return false;
    }

    private void toggleMediaControlsVisiblity() {
        if (mMediaController.getShowState() == MyController.STATE_SHOW) {
            mMediaController.hide();
        } else if (mMediaController.getShowState() == MyController.STATE_HIDE) {
            mMediaController.show();
        }
    }

    public void release() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer = null;
        IjkMediaPlayerDelegate.destroy();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_GET_NET_SPEED:
                    if (msg.arg1 > 1024) {
                        video_buff_percent.setText((int)(msg.arg1 / 1024) + " kb/s");
                    } else {
                        video_buff_percent.setText((int) msg.arg1 + " b/s");
                    }
                    sendEmptyMessageDelayed(EVENT_GET_NET_DELAY, TIME_SEPERATE);
                    break;
                case EVENT_GET_NET_DELAY:
                    Message message = obtainMessage();
                    message.what = EVENT_GET_NET_SPEED;
                    message.arg1 = getNetSpeed();
                    sendMessage(message);
                    break;
            }
        }
    };

    private long total_data = TrafficStats.getTotalRxBytes();

    /**
     * 核心方法，得到当前网速
     *
     * @return
     */
    private int getNetSpeed() {
        long traffic_data = TrafficStats.getTotalRxBytes() - total_data;
        total_data = TrafficStats.getTotalRxBytes();
        return (int) traffic_data / TIME_SEPERATE * 1000;
    }
}