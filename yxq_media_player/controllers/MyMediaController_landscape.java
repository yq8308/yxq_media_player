package com.test.yxq.yxq_media_player.controllers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.yxq.yxq_media_player.R;
import com.test.yxq.yxq_media_player.activities.PlayerActivity;
import com.test.yxq.yxq_media_player.beans.ChatMessage;
import com.test.yxq.yxq_media_player.utils.DanmakuHelper;
import com.test.yxq.yxq_media_player.utils.MsgManager;

/**
 * Created by Administrator on 2016/5/3.
 */
public class MyMediaController_landscape  extends FrameLayout implements MyController,View.OnClickListener{
    private static final String TAG = "MyMediaController";

    private ViewGroup mAnchor;
    private View mRoot;
    private Context mContext;
    private Object obj;

    private int show_state = STATE_SHOW;            //初始化时为显示状态
    private int TIME_AUTO_HIDE = 5000;
    private static final int MEDIA_CONTROLLER_SHOW = 1;
    private static final int MEDIA_CONTROLLER_HIDE = 0;

    private View controller_land_top;
    private View controller_land_bottom;
    private ImageView status_bar_bg;
    private ImageButton btn_land_back;
    private TextView tv_land_title;
    private ImageButton btn_land_bamboo;
    private ImageButton btn_land_setting;
    private ImageButton btn_land_qingxidu;
    private ImageButton btn_land_play;
    private ImageButton btn_land_refresh;
    private EditText et_land_msg;
    private Button btn_land_send;
    private ImageButton btn_land_danmu_full;
    private ImageButton btn_land_gift;

    private RelativeLayout top;

    private InputMethodManager imm;

    public MyMediaController_landscape(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
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
        mRoot = inflate.inflate(R.layout.media_controller_land, null);

        initControllerView(mRoot);

        return mRoot;
    }

    private void initControllerView(View view) {

        controller_land_top = mRoot.findViewById(R.id.controller_land_top);
        controller_land_bottom = mRoot.findViewById(R.id.controller_land_bottom);

        status_bar_bg = (ImageView) mRoot.findViewById(R.id.status_bar_bg);
        btn_land_back = (ImageButton)mRoot.findViewById(R.id.btn_land_back);
        tv_land_title = (TextView)mRoot.findViewById(R.id.tv_land_title);
        btn_land_bamboo = (ImageButton)mRoot.findViewById(R.id.btn_land_bamboo);
        btn_land_setting = (ImageButton)mRoot.findViewById(R.id.btn_land_setting);
        btn_land_qingxidu = (ImageButton)mRoot.findViewById(R.id.btn_land_qingxidu);
        btn_land_play = (ImageButton)mRoot.findViewById(R.id.btn_land_play);
        btn_land_refresh = (ImageButton)mRoot.findViewById(R.id.btn_land_refresh);
        et_land_msg = (EditText)mRoot.findViewById(R.id.et_land_msg);
        btn_land_send = (Button)mRoot.findViewById(R.id.btn_land_send);
        btn_land_danmu_full = (ImageButton)mRoot.findViewById(R.id.btn_land_danmu_full);
        btn_land_gift = (ImageButton)mRoot.findViewById(R.id.btn_land_gift);
        btn_land_back.setOnClickListener(this);
        btn_land_bamboo.setOnClickListener(this);
        btn_land_setting.setOnClickListener(this);
        btn_land_qingxidu.setOnClickListener(this);
        btn_land_play.setOnClickListener(this);
        btn_land_refresh.setOnClickListener(this);
        et_land_msg.setOnClickListener(this);
        btn_land_send.setOnClickListener(this);
        btn_land_danmu_full.setOnClickListener(this);
        btn_land_gift.setOnClickListener(this);

        tv_land_title.setText("岛国动作片");
        et_land_msg.clearFocus();
        et_land_msg.setFocusable(false);

        top= (RelativeLayout) mRoot.findViewById(R.id.controller_land_top);

        RelativeLayout.LayoutParams status_bar_lp = (RelativeLayout.LayoutParams) status_bar_bg.getLayoutParams();
        status_bar_lp.height = getStatusHeight(mContext);
        System.out.println("--11--" + status_bar_lp.height);
        status_bar_bg.setLayoutParams(status_bar_lp);

        final MarginLayoutParams margin=new MarginLayoutParams(top.getLayoutParams());
        margin.setMargins(margin.leftMargin, getStatusHeight(mContext), margin.rightMargin, getStatusHeight(mContext) + margin.height);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
        top.setLayoutParams(layoutParams);
        et_land_msg.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String msg = String.valueOf(et_land_msg.getText());
                    ChatMessage cm = new ChatMessage(1, "老王", msg);
                    MsgManager.instance().sendChatMsg(cm);
                    et_land_msg.setText("");
                    imm.hideSoftInputFromWindow(et_land_msg.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    et_land_msg.clearFocus();
                    et_land_msg.setFocusable(false);
                    notifyControllerHide(TIME_AUTO_HIDE);
                    return true;
                }
                return false;
            }
        });

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

    private void notifyControllerHide(int time) {
        mHandler.removeMessages(MEDIA_CONTROLLER_HIDE);
        mHandler.sendEmptyMessageDelayed(MEDIA_CONTROLLER_HIDE, time);
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void ViewShowAnim() {
        status_bar_bg.setVisibility(View.VISIBLE);
        Log.d(TAG, "ViewShowAnim: ");
        show_state = STATE_TRANS;
        PropertyValuesHolder p_Alpha = PropertyValuesHolder.ofFloat("Alpha", 0.0f, 1.0f);
        PropertyValuesHolder p_top_TranslationY = PropertyValuesHolder.ofFloat("TranslationY",-80,0);
        PropertyValuesHolder p_bottom_TranslationY = PropertyValuesHolder.ofFloat("TranslationY",80,0);

        ObjectAnimator animator1 = ObjectAnimator.ofPropertyValuesHolder(controller_land_top, p_Alpha ,p_top_TranslationY);
        animator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                controller_land_top.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                show_state = STATE_SHOW;
            }
        });
        animator1.setDuration(500);
        animator1.start();
        ObjectAnimator animator2 = ObjectAnimator.ofPropertyValuesHolder(controller_land_bottom, p_Alpha ,p_bottom_TranslationY);
        animator2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                controller_land_bottom.setVisibility(View.VISIBLE);
            }
        });
        animator2.setDuration(500);
        animator2.start();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void ViewHideAnim() {
        status_bar_bg.setVisibility(View.GONE);
        Log.d(TAG, "ViewHideAnim: ");
        show_state = STATE_TRANS;
        PropertyValuesHolder p_Alpha = PropertyValuesHolder.ofFloat("Alpha", 1.0f, 0.0f);
        PropertyValuesHolder p_top_TranslationY = PropertyValuesHolder.ofFloat("TranslationY",0,-80);
        PropertyValuesHolder p_bottom_TranslationY = PropertyValuesHolder.ofFloat("TranslationY",0,80);
        ObjectAnimator animator1 = ObjectAnimator.ofPropertyValuesHolder(controller_land_top, p_Alpha ,p_top_TranslationY);
        animator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationStart(animation);
                controller_land_top.setVisibility(View.GONE);
                show_state = STATE_HIDE;
            }
        });
        animator1.setDuration(500);
        animator1.start();
        ObjectAnimator animator2 = ObjectAnimator.ofPropertyValuesHolder(controller_land_bottom, p_Alpha, p_bottom_TranslationY);
        animator2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationStart(animation);
                controller_land_bottom.setVisibility(View.GONE);
            }
        });
        animator2.setDuration(500);
        animator2.start();
    }

    @Override
    public void show() {
        if (mAnchor==null)return;
        PlayerActivity player = (PlayerActivity) obj;
        Window window = player.getWindow();
        WindowManager.LayoutParams attr = window.getAttributes();
        attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setAttributes(attr);
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);

        if (getShowState()==MyController.STATE_HIDE){
            ViewShowAnim();
        }else if (getShowState()==MyController.STATE_SHOW){
            show_state = STATE_SHOW;
            this.setVisibility(View.VISIBLE);
            //if (!et_land_msg.isFocused()){
                notifyControllerHide(TIME_AUTO_HIDE);
            //}
        }
    }

    @Override
    public void hide() {
        if (mAnchor==null)return;
        if (et_land_msg.isFocused()){
            return;
        }
        PlayerActivity player = (PlayerActivity) obj;
        Window window = player.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        window.setAttributes(lp);
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);

        if (getShowState()==MyController.STATE_SHOW) {
            ViewHideAnim();
        }
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
        Log.d("ccc", "detach: controller landscape");
        mAnchor.removeView(this);
        mAnchor = null;
    }

    private DanmakuHelper getDanmakuHelper() {
        return DanmakuHelper.instance();
    }

    @Override
    public void onClick(View v) {
        PlayerActivity player = (PlayerActivity)obj;
        switch (v.getId()) {
            case R.id.btn_land_back:
                player.onBackPressed();
                break;
            case R.id.btn_land_bamboo:
                break;
            case R.id.btn_land_setting:
                break;
            case R.id.btn_land_qingxidu:
                break;
            case R.id.btn_land_play:
                break;
            case R.id.btn_land_refresh:
                break;
            case R.id.et_land_msg:
                Log.d(TAG, "onClick: et_land_msg");
                et_land_msg.setFocusable(true);
                et_land_msg.setFocusableInTouchMode(true);
                et_land_msg.requestFocus();
                imm.showSoftInput(et_land_msg, InputMethodManager.SHOW_IMPLICIT);
                break;
            case R.id.btn_land_send:
                String msg = String.valueOf(et_land_msg.getText());
                if (msg!="") {
                    ChatMessage cm = new ChatMessage(1,"老王",msg);
                    MsgManager.instance().sendChatMsg(cm);
                    et_land_msg.setText("");
                }
                et_land_msg.clearFocus();
                et_land_msg.setFocusable(false);
                notifyControllerHide(TIME_AUTO_HIDE);
                break;
            case R.id.btn_land_danmu_full:
                if (!getDanmakuHelper().getDanmakuState()) {
                    getDanmakuHelper().start();
                    btn_land_danmu_full.setBackgroundResource(R.drawable.danmu_icon_closed);
                }
                else{
                    getDanmakuHelper().stop();
                    btn_land_danmu_full.setBackgroundResource(R.drawable.danmu_icon);
                }
                break;
            case R.id.btn_land_gift:
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        PlayerActivity player = (PlayerActivity) obj;
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isShouldHideInput(et_land_msg, ev)) {
                if (imm != null) {
                    imm.hideSoftInputFromWindow(et_land_msg.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    et_land_msg.clearFocus();
                    et_land_msg.setFocusable(false);
                    //notifyControllerHide(TIME_AUTO_HIDE);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public  boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                Log.d(TAG, "isShouldHideInput: false");
                return false;
            } else {
                Log.d(TAG, "isShouldHideInput: true");
                return true;
            }
        }
        return false;
    }
}
