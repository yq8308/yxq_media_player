package com.test.yxq.yxq_media_player.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.yxq.yxq_media_player.R;
import com.test.yxq.yxq_media_player.beans.GiftAtrr;
import com.test.yxq.yxq_media_player.beans.TopAnnouceData;
import com.test.yxq.yxq_media_player.utils.MsgManager;
import com.test.yxq.yxq_media_player.utils.common.CustomXmlLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/5/14.
 */
public class TopAnnouceView extends RelativeLayout implements MsgManager.OnAddNewTopAnnouceListener {

    private Context mContext;
    private List<View> mList;
    private Timer timer;
    private int MOVE_DURATION = 7000;
    private int SHOW_NEXT_TIME = 4000;
    private int WAIT_HIDE_TIME = 1000;

    private int current = 0;
    private int done = 0;
    private boolean isHiding = false;
    private boolean sorry = false;

    private final static int ADD_ITEM = 0;
    private final static int TIME_HIDE = 1;
    private final static int ITEM_SCROLL = 2;

    private final static boolean ITEM_GG = true;

    private Animation hide_anim;

    private List<GiftAtrr> mGiftList;

    public TopAnnouceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mList = new ArrayList<View>();
        setAlpha(0);
        mGiftList = CustomXmlLoader.loadGiftAttr(mContext, R.xml.gift_attribution);
        this.setGravity(Gravity.CENTER_VERTICAL);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ADD_ITEM:
                    TopAnnouceData tad = (TopAnnouceData) msg.obj;
                    addTopAnnouce(tad);
                    break;
                case TIME_HIDE:
                    hide();
                    break;
                case ITEM_SCROLL:
                    View v = (View) msg.obj;
                    itemScroll(v);
                    break;
            }
        }
    };

    public void show() {
        Log.d("1111", "show: ");
        mHandler.removeMessages(TIME_HIDE);
        setAlpha(1);
    }

    public void hide() {
        isHiding = true;
        hide_anim = AnimationUtils.loadAnimation(mContext, R.anim.out_annouce_top);
        hide_anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d("1111", "onAnimationEnd: hide " + sorry);
                if (!sorry) {
                    removeAllViews();
                    mList.clear();
                    current = 0;
                    done = 0;
                    setAlpha(0);
                    timer.cancel();
                    timer = null;
                }
                sorry = false;
                isHiding = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        startAnimation(hide_anim);
    }

    public void release() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setTag(ITEM_GG);
        }
        removeAllViews();
        mList.clear();
        isHiding = false;
        current = 0;
        done = 0;
        setAlpha(0);
    }


    private void itemScroll(final View v) {
        float toPosX = -(v.getRight() - v.getLeft());
        Log.d("1111", "itemScroll: toPosX =" + toPosX);
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "TranslationX", toPosX);
        animator.setInterpolator(null);
        animator.setDuration(MOVE_DURATION);
        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (v.getTag() != ITEM_GG) {
                    done += 1;
                    Log.d("11111", "onAnimationEnd: done =" + done);
                    if (getAlpha() == 1 && current == done && !isHiding) {
                        mHandler.sendEmptyMessageDelayed(TIME_HIDE, WAIT_HIDE_TIME);
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void showAction() {
        Log.d(":11111", "showAction: list size:" + mList.size() + " current: " + current + " done: " + done);
        if (mList.size() > current) {
            final View v = mList.get(current);
            current += 1;
            Message msg = mHandler.obtainMessage(ITEM_SCROLL, v);
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void addItem(TopAnnouceData annouce) {
        Message msg = mHandler.obtainMessage(ADD_ITEM,annouce);
        mHandler.sendMessage(msg);
    }


    public void addTopAnnouce(TopAnnouceData annouce) {
        if (timer == null) {
            timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    showAction();
                }
            };
            timer.schedule(task, 1000, SHOW_NEXT_TIME);
        }

        View view = View.inflate(mContext, R.layout.annouce_top_item, null);
        ImageView gift_icon = (ImageView) view.findViewById(R.id.gift_icon);
        TextView gift_from = (TextView) view.findViewById(R.id.gift_from);
        TextView gift_to = (TextView) view.findViewById(R.id.gift_to);
        TextView gift_name = (TextView) view.findViewById(R.id.gift_name);

        int giftType = annouce.getType();
        String smallIconName = null;
        String giftName = null;
        for (int i=0;i<mGiftList.size();i++) {
            GiftAtrr attr = mGiftList.get(i);
            if(attr.index == giftType) {
                smallIconName = attr.smallIconName;
                giftName = attr.name;
                break;
            }
        }

        gift_icon.setImageResource(getResources().getIdentifier(smallIconName,"drawable",mContext.getPackageName()));
        gift_from.setText(annouce.getFrom());
        gift_to.setText(annouce.getTo());
        gift_name.setText(giftName);

        int width = this.getRight() - this.getLeft();
        view.setX(width);
        addView(view);
        mList.add(view);

        if (hide_anim != null) {
            if (isHiding) {
                sorry = true;
            }
            clearAnimation();
//            hide_anim.cancel();
//            hide_anim = null;
        }

        show();
    }
}
