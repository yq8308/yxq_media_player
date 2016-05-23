package com.test.yxq.yxq_media_player.beans;

/**
 * Created by Administrator on 2016/5/4.
 */
public class ChatMessage{

    public int type;
    public String nickName;
    public String msg;

    public ChatMessage() {}

    /**
     *
     * @param type  消息类型 0表示系统 1为普通 2为广播
     * @param nickName  用户昵称，如果type为0，此为null
     * @param msg   消息内容
     */
    public ChatMessage(int type, String nickName, String msg) {
        this.type = type;
        this.nickName = nickName;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

}
