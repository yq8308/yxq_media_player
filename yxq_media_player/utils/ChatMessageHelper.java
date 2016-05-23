package com.test.yxq.yxq_media_player.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;

import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/5/11.
 */
public class ChatMessageHelper {
    public static ChatMessageHelper cmh = null;
    private Context mContext;

    public static ChatMessageHelper instance(Context context) {
        if (cmh==null) {
            cmh = new ChatMessageHelper(context);
        }
        return cmh;
    }

    public ChatMessageHelper(Context context) {
        mContext = context;
    }

    public Spanned createSpanned(String source) {
        if (source==null) return null;
        return Html.fromHtml(source,null,tagHandler);
    }

    private Html.TagHandler tagHandler = new Html.TagHandler() {

        @Override
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
            if (opening) {
                //开始标记

                if (tag.charAt(0) == 'c' && tag.length()>1) {
                    //颜色
                    startCMColor(output, tag);
                } else if (tag.charAt(0) == 'i' && tag.length()>1) {
                    //图像
                    startCMImage(output,tag);
                }
            } else {
                //结束标记
                if (tag.charAt(0) == 'c' && tag.length()>1) {
                    endCMColor(output, tag);
                }
            }
        }

        private void startCMColor(Editable output,String tag) {
            int len = output.length();
            output.setSpan(new CMColor(tag), len, len, Spannable.SPAN_MARK_MARK);
        }

        private void endCMColor(Editable output,String tag) {
            int len = output.length();
            Object obj = getLast(output, CMColor.class);
            int where = output.getSpanStart(obj);
            output.removeSpan(obj);

            while (len > where && output.charAt(len - 1) == '\n') {
                len--;
            }

            if (where != len) {
                CMColor cmObject = (CMColor) obj;
                Resources res = mContext.getResources();
                int colorId = res.getIdentifier(cmObject.color,"color",mContext.getPackageName());
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(res.getColor(colorId));
                output.setSpan(colorSpan, where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        private void startCMImage(Editable output, String tag) {
            int len = output.length();
            Drawable d = null;
            String fileName = tag.substring(1,tag.length());
            String filePath = "face/png/face-min/" + fileName;
            System.out.println("----cmi:" + fileName);
            try {
                InputStream is = mContext.getResources().getAssets().open(filePath);
//                d = mContext.getResources().getDrawable(mContext.getResources().getIdentifier("dy001", "drawable", mContext.getPackageName()));
                d = Drawable.createFromStream(is, fileName);
                d.setBounds(0, 0, 70, 70);
                output.append("\uFFFC");
                output.setSpan(new ImageSpan(d), len, output.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private Object getLast(Spanned text, Class kind) {
            Object[] objs = text.getSpans(0, text.length(), kind);

            if (objs.length == 0) {
                return null;
            } else {
                return objs[objs.length - 1];
            }
        }

        class CMColor {
            public String color;
            public CMColor(String tag) {
                color = tag;
            }
        }
    };
}
