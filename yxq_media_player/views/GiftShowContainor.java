package com.test.yxq.yxq_media_player.views;

import android.animation.LayoutTransition;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.test.yxq.yxq_media_player.beans.GiftMessage;
import com.test.yxq.yxq_media_player.beans.GiftShowData;
import com.test.yxq.yxq_media_player.utils.MsgManager;
import com.test.yxq.yxq_media_player.utils.common.LLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/19.
 */
public class GiftShowContainor extends LinearLayout implements MsgManager.GiftMessageListener{

    private Context mContext;
    private Animation anim_in;
    private Animation anim_out;

    private final static int DELAY_TIME_OUT = 2000;
    private final static int EVENT_SHOW_MSG = 0;
    private final static int EVENT_HIDE_MSG = 1;

    private List<Recoder> mList;

    public GiftShowContainor(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        setOrientation(VERTICAL);
        mList = new ArrayList<Recoder>();

        LayoutTransition transition = new LayoutTransition();
        setLayoutTransition(transition);
    }

    /**
     * 进入动画
     * @param in
     */
    public void setInAnimation(Animation in) {
        anim_in = in;
    }

    public void setInAnimation(Context context,int animId) {
        setInAnimation(AnimationUtils.loadAnimation(context,animId));
    }

    /**
     * 推出动画
     * @param out
     */
    public void setOutAnimation(Animation out) {
        anim_out = out;
    }

    public void setOutAnimation(Context context,int animId) {
        setOutAnimation(AnimationUtils.loadAnimation(context, animId));
    }

    /**
     * 传入 礼物消息，判断是否已经在界面中显示，如果显示那么就更新，没有就添加
     * @param gsd
     */
    public void addGiftShowItem(GiftShowData gsd) {
        for (int i=0;i<mList.size();i++) {
            Recoder r = mList.get(i);
            if (gsd.user_name.equals(r.user_name)
                    && gsd.gift_type == r.gift_type) {
                //保存的列表中已经存在，只需要更新即可
                updateOldItem(r.item,gsd);
                return;
            }
        }

        showNewItem(gsd);
    }

    private void showNewItem(GiftShowData gsd) {
        LLog.d("showNewItem");
        GiftShowItem item = new GiftShowItem(mContext,null);
        item.setData(gsd);
        addView(item);
        Recoder r = new Recoder(gsd.user_name, gsd.gift_type, item);
        mList.add(r);
        item.setTag(r);
        if (anim_in!=null) {
            item.startAnimation(anim_in);
        }
        Message msg = mHandler.obtainMessage(EVENT_HIDE_MSG,item);
        mHandler.sendMessageDelayed(msg,DELAY_TIME_OUT);
    }

    private void updateOldItem(GiftShowItem item,GiftShowData gsd) {
        LLog.d("updateOldItem");
        item.setData(gsd);
        mHandler.removeMessages(EVENT_HIDE_MSG,item);
        Message msg = mHandler.obtainMessage(EVENT_HIDE_MSG,item);
        mHandler.sendMessageDelayed(msg, DELAY_TIME_OUT);
    }


    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_HIDE_MSG:
                    final View v = (View) msg.obj;
                    if (anim_out!=null) {
                        v.startAnimation(anim_out);
                    }
                    v.setVisibility(View.GONE);
                    mList.remove(v.getTag());
                    removeView(v);
                    break;
                case EVENT_SHOW_MSG:
                    GiftShowData gsd = (GiftShowData) msg.obj;
                    addGiftShowItem(gsd);
                    break;
            }
        }
    };

    @Override
    public void showGift(GiftMessage gm) {
        GiftShowData gsd = new GiftShowData("",gm.fromUser,gm.type,gm.num);
        Message msg = mHandler.obtainMessage(EVENT_SHOW_MSG,gsd);
        mHandler.sendMessage(msg);
    }

    /**
     * 保存显示记录
     * 保存成员变量item可以使得很容易找到需要更新的GiftShowItem
     */
    private class Recoder{
        public String user_name;
        public int gift_type;
        public GiftShowItem item;

        public Recoder(String name,int type,GiftShowItem gsi) {
            user_name = name;
            gift_type = type;
            item = gsi;
        }
    }
}
