package com.test.yxq.yxq_media_player.utils.common;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

import com.test.yxq.yxq_media_player.beans.GiftAtrr;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/13.
 */
public class CustomXmlLoader {

    public static List<GiftAtrr> loadGiftAttr(Context context, int xmlID) {
        Resources res = context.getResources();
        XmlResourceParser xmlParser = res.getXml(xmlID);
        List<GiftAtrr> gifts = new ArrayList<GiftAtrr>();

        try {
            int eventType = xmlParser.getEventType();
            // 判断是否到了文件的结尾
            while (eventType != XmlResourceParser.END_DOCUMENT) {

                //文件的内容的起始标签开始，注意这里的起始标签是xml文件里面<gift>标签下面的第一个标签
                if (eventType == XmlResourceParser.START_TAG) {
                    String tagname = xmlParser.getName();
                    if (tagname.endsWith("gift")) {
                        GiftAtrr gift = new GiftAtrr();
                        gift.index = Integer.parseInt(xmlParser.getAttributeValue(null, "index"));
                        gift.iconName = xmlParser.getAttributeValue(null, "iconName");
                        gift.smallIconName = xmlParser.getAttributeValue(null,"smallIconName");
                        gift.name = xmlParser.getAttributeValue(null, "name");
                        gift.value = xmlParser.getAttributeValue(null, "value");
                        gift.type = Integer.parseInt(xmlParser.getAttributeValue(null, "type"));
                        gifts.add(gift);
                    }
                }
                else if (eventType == XmlResourceParser.END_TAG) {
                } else if (eventType == XmlResourceParser.TEXT) {
                }
                eventType = xmlParser.next();
            }
        }catch (XmlPullParserException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
        xmlParser.close();
        return gifts;
    }
}
