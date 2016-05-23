package com.test.yxq.yxq_media_player.beans;

/**
 * Created by Administrator on 2016/5/14.
 */
public class TopAnnouceData {
    private int giftType;
    private String from;
    private String to;

    public TopAnnouceData(int type, String from, String to) {
        this.giftType = type;
        this.from = from;
        this.to = to;
    }

    public int getType() {
        return giftType;
    }

    public void setType(int type) {
        this.giftType = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
