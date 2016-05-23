package com.test.yxq.yxq_media_player.beans;

/**
 * Created by Administrator on 2016/5/13.
 */
public class GiftAtrr {
    public int index;
    public String iconName;
    public String smallIconName;
    public String name;
    public String value;
    public int type;           //0表示鱼丸 1表示鱼翅

    public GiftAtrr() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String fileName) {
        this.iconName = fileName;
    }

    public String getSmallIconName() {
        return smallIconName;
    }

    public void setSmallIconName(String smallIconName) {
        this.smallIconName = smallIconName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
