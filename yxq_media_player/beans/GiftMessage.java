package com.test.yxq.yxq_media_player.beans;

/**
 * Created by Administrator on 2016/5/20.
 */
public class GiftMessage{
    public int type;
    public String fromUser;
    public String toUser;
    public int num;

    public GiftMessage(int type, String fromUser, String toUser, int num) {
        this.type = type;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.num = num;
    }
}
