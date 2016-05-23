package com.test.yxq.yxq_media_player.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.test.yxq.yxq_media_player.R;
import com.test.yxq.yxq_media_player.beans.ChatMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/5.
 */
public class ChatMessageAdapter extends BaseAdapter {

    public static ChatMessageAdapter cmAdapter = null;
    private List<ChatMessage> mList;
    private Context mContext;

    private static final int NEW_DATA = 1;
    private static final int CLEAR_DATA = 2;


    public static ChatMessageAdapter instance(Context context, List<ChatMessage> list) {
        if (cmAdapter == null) {
            cmAdapter = new ChatMessageAdapter(context, list);
        }
        return cmAdapter;
    }

    public ChatMessageAdapter(Context context, List<ChatMessage> list) {
        if (list == null) {
            this.mList = new ArrayList<ChatMessage>();
        } else {
            this.mList = list;
        }
        this.mContext = context;
    }

    public void addMessage(final ChatMessage cm) {
        System.out.println("收到服务器消息--->:" + cm.msg);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = mHandler.obtainMessage(NEW_DATA, cm);
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    public void clearMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(CLEAR_DATA);
            }
        }).start();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageHolder mh = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chat_message_item, null);
            mh = new MessageHolder();
            mh.tv_msg = (TextView) convertView.findViewById(R.id.chat_msg_item_content);
            convertView.setTag(mh);
        } else {
            mh = (MessageHolder) convertView.getTag();
        }

        String s_prefix = "<msg>";
        String content = null;
        ChatMessage cm = mList.get(position);
        if (cm.type == 0) {
            content = s_prefix + "<c0>"+cm.msg+"</c0>";
        } else if (cm.type == 1) {
            String nickName = "<c1>" + cm.nickName + "</c1>";
            String maohao = ":";
            String msg = "<c2>" + cm.msg + "</c2>";
            content = s_prefix + nickName + maohao + msg;
        }
        mh.tv_msg.setText(ChatMessageHelper.instance(mContext).createSpanned(content));
        //mh.tv_msg.setMovementMethod(LinkMovementMethod.getInstance());
        return convertView;
    }

    private class MessageHolder {
        public TextView tv_msg;
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NEW_DATA:
                    ChatMessage cm = (ChatMessage) msg.obj;
                    mList.add(cm);
                    notifyDataSetChanged();
                    break;
                case CLEAR_DATA:
                    mList.clear();
                    notifyDataSetChanged();
            }
        }
    };
}