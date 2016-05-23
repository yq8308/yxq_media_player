package com.test.yxq.yxq_media_player.beans;

/**
 * Created by Administrator on 2016/5/20.
 */
public class MyMessage {
    public int type;        // 消息类型 0:系统 1:聊天 2:礼物
    public SystemMessage sm;   //系统消息
    public ChatMessage cm;  // 聊天消息
    public GiftMessage gm;  // 礼物消息

    public MyMessage(int type, SystemMessage sm, ChatMessage cm, GiftMessage gm) {
        this.type = type;
        this.sm = sm;
        this.cm = cm;
        this.gm = gm;
    }
}
