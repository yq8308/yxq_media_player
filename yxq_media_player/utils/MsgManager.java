package com.test.yxq.yxq_media_player.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.yxq.yxq_media_player.beans.ChatMessage;
import com.test.yxq.yxq_media_player.beans.GiftMessage;
import com.test.yxq.yxq_media_player.beans.MyMessage;
import com.test.yxq.yxq_media_player.beans.SystemMessage;
import com.test.yxq.yxq_media_player.beans.TopAnnouceData;
import com.test.yxq.yxq_media_player.tcp.TCPClient;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/4.
 * <p/>
 * 实现一个消息管理器，用于消息 发送/接收/分发
 * 当有消息过来时，添加进消息列表，消息显示到界面后，将其删除
 * 消息显示方式有2种：竖屏状态下的listview显示 和 横屏状态下的弹幕显示
 * 在横屏状态下，消息除了在弹幕中显示（弹幕开的状态下）外，也希望在listview中显示，虽然listview已经不可见
 */
public class MsgManager {

    private static final String TAG = "MsgManager";

    // 消息管理器对象
    public static MsgManager mm = null;

    // 消息列表
    private List<MyMessage> mList;

    // 弹幕辅助对象,用于接收消息并显示
    private DanmakuHelper dm_helper;

    // 用于接收消息的ChatMessageAdapter，提供给listview显示
    private ChatMessageAdapter cma;

    // 管理联网消息收发
    private TCPClient tcp;

    // 线程
    private MsgAsyncTask mat;

    // 火箭飞机消息监听
    private OnAddNewTopAnnouceListener onAddNewTopAnnouceListener;

    // 刷礼物消息监听
    private GiftMessageListener giftMessageListener;

    TCPClient.TCPClientEventListener tcpClientEventListener = new TCPClient.TCPClientEventListener() {
        @Override
        public void recvMsg(ByteArrayOutputStream read) {
            final String info;
            try {
                info = new String(read.toString(TCPClient.BUFF_FORMAT));
                System.out.println("reving" + info);
                Gson gson = new Gson();
                MyMessage message = gson.fromJson(info.trim(), new TypeToken<MyMessage>() {
                }.getType());
                addMsg(message);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    };

    public static MsgManager instance() {
        if (mm == null) {
            mm = new MsgManager();
        }
        return mm;
    }

    public MsgManager() {
        mList = new ArrayList<MyMessage>();
        dm_helper = DanmakuHelper.instance();
    }

    public void start() {
        if (tcp == null) {
            tcp = TCPClient.instance();
            tcp.setOnConnectedListener(onConnectedListener);
            tcp.connect("192.168.1.102", 6000, tcpClientEventListener);  // 设置服务器ip/port ，并添加接收消息的事件监听
            ChatMessage cm = new ChatMessage(0, null, "正在连接服务器...");
            MyMessage msg = new MyMessage(1, null, cm, null);
            cm.type = 0;
            mList.add(msg);

            mat = new MsgAsyncTask();
            mat.execute();
        } else {
            tcp.reConnect();
        }
    }

    public void stop() {
        System.out.println("--------stop");
        if (tcp != null) {
            tcp.close();
            tcp = null;
        }
        mList.clear();
        if (mat != null) {
            mat = null;
        }
        if (cma != null) {
            cma.clearMessage();
            cma = null;
        }
        if (dm_helper != null) {
            dm_helper.release();
            dm_helper = null;
        }
    }

    TCPClient.OnConnectedListener onConnectedListener = new TCPClient.OnConnectedListener() {
        @Override
        public void onConnectSuccess() {
            ChatMessage cm = new ChatMessage(0, null, "连接服务器成功");
            MyMessage msg = new MyMessage(1, null, cm, null);
            mList.add(msg);
        }

        @Override
        public void onConnectFaild() {
            //连接服务器失败
            ChatMessage cm = new ChatMessage(0, null, "连接服务器失败");
            MyMessage msg = new MyMessage(1, null, cm, null);
            mList.add(msg);
        }
    };

    public interface OnAddNewTopAnnouceListener {
        void addItem(TopAnnouceData annouce);
    }

    public void setOnAddTopNewAnnouceListener(OnAddNewTopAnnouceListener listener) {
        onAddNewTopAnnouceListener = listener;
    }

    public interface GiftMessageListener {
        void showGift(GiftMessage gm);
    }

    public void setGiftMessageListener(GiftMessageListener listener) {
        giftMessageListener = listener;
    }

    /**
     * 启动一个循环，给外部推消息
     */
    class MsgAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            while (!isCancelled()) {
                if (mList.size() > 0) {
                    // 先入先出
                    MyMessage msg = mList.get(0);
                    Log.d(TAG, "doInBackground: " + msg.type + " " + msg.sm + " " + msg.cm + " " + msg.gm);
                    switch (msg.type) {
                        case 0:         // 系统消息
                            SystemMessage sm = msg.sm;
                            //...
                            break;
                        case 1:         // 聊天消息
                            ChatMessage cm = msg.cm;
                            pushOneToChatView(cm);
                            if (dm_helper != null && dm_helper.getDanmakuState()) {
                                pushOneToDanmaku(cm);
                            }
                            break;
                        case 2:         // 礼物消息
                            GiftMessage gm = msg.gm;
                            pushOneToGiftShowContainor(gm);
                            if (gm.type == 0 || gm.type == 1) {  //如果是火箭或者飞机，在顶端显示
                                TopAnnouceData tad = new TopAnnouceData(gm.type, gm.fromUser, gm.toUser);
                                pushOneToTopAnnouce(tad);
                            }
                            break;
                    }
                    removeMsg(0);
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    /**
     * 添加消息，给TCPClient用的
     *
     * @param msg
     */
    public void addMsg(MyMessage msg) {
        mList.add(msg);
    }

    /**
     * 移除消息
     *
     * @param pos
     */
    public void removeMsg(int pos) {
        mList.remove(pos);
    }

    /**
     * 移除所有消息
     */
    public void removeAllMsg() {
        mList.clear();
    }

    public void setChatMessageAdapter(ChatMessageAdapter adapter) {
        cma = adapter;
    }

    /**
     * 推一条聊天消息给listview的数据源
     */
    protected void pushOneToChatView(ChatMessage cm) {
        if (cma != null) {
            System.out.println("pushOneToChatView");
            cma.addMessage(cm);
        }
    }

    /**
     * 推一条聊天消息给弹幕view
     *
     * @param cm
     */
    protected void pushOneToDanmaku(ChatMessage cm) {
        if (dm_helper.getDanmaku() != null) {
            dm_helper.addDanmaku(cm.msg);
        }
    }

    /**
     * 推一条礼物消息
     */
    protected void pushOneToGiftShowContainor(final GiftMessage gm) {
        giftMessageListener.showGift(gm);
    }

    /**
     * 推一条礼物消息给topannouce 火箭或者飞机
     */
    protected void pushOneToTopAnnouce(final TopAnnouceData tad) {
        onAddNewTopAnnouceListener.addItem(tad);
    }

    /**
     * 给服务器发送聊天消息,必须开启一个新线程
     *
     * @param cm
     */
    public void sendChatMsg(final ChatMessage cm) {
        if (tcp != null && tcp.isConnect()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Gson gson = new Gson();
                    MyMessage message = new MyMessage(1, null, cm, null);
                    tcp.sendMsg(gson.toJson(message));
                }
            }).start();
        }
    }


    /**
     * 发送礼物消息
     *
     * @param gm
     */
    public void sendGiftMsg(final GiftMessage gm) {
        if (tcp != null && tcp.isConnect()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Gson gson = new Gson();
                    MyMessage message = new MyMessage(2, null, null, gm);
                    tcp.sendMsg(gson.toJson(message));
                }
            }).start();
        }
    }
}
