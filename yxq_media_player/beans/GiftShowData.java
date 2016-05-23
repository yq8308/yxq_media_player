package com.test.yxq.yxq_media_player.beans;

/**
 * Created by Administrator on 2016/5/19.
 */
public class GiftShowData {
    public String tx_url;
    public String user_name;
    public int gift_type;
    public int clicks;         //连击数

    public GiftShowData(String tx_url, String user_name, int gift_type, int clicks) {
        this.tx_url = tx_url;
        this.user_name = user_name;
        this.gift_type = gift_type;
        this.clicks = clicks;
    }
}
