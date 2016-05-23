package com.test.yxq.yxq_media_player.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.test.yxq.yxq_media_player.R;
import com.test.yxq.yxq_media_player.beans.ChatMessage;
import com.test.yxq.yxq_media_player.utils.ChatMessageAdapter;
import com.test.yxq.yxq_media_player.utils.MsgManager;

/**
 * Created by Administrator on 2016/5/5.
 */
public class ChatView extends FrameLayout implements View.OnClickListener {

    private static final String TAG = "ChatView";

    private Context mContext;
    private ListView lv;
    private TopAnnouceView chat_top_annouce;
    private GiftShowContainor gift_show_containor;
    private LinearLayout input_containor;
    private EditText chat_msg_send;
    private ImageButton test_send_btn;
    private ImageButton face_btn;
    private ImageButton chat_chognzhi_btn;
    private ImageButton chat_gift_btn;


    private FacesView chat_face_view;
    private InputMethodManager imm;
    private boolean canScroll = true;
    boolean isSoftInputShowing = false;         // 输入法状态

    int IMEHeight = 0;


    public ChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mRoot = inflater.inflate(R.layout.chat_view_layout,null);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mRoot, lp);

        lv = (ListView) mRoot.findViewById(R.id.chat_list_view);
        lv.setBackgroundColor(getResources().getColor(R.color.colorBg));
        lv.setVerticalScrollBarEnabled(false);
        lv.setDivider(null);

        ChatMessageAdapter cmAdapter = ChatMessageAdapter.instance(mContext,null);
        lv.setAdapter(cmAdapter);

        lv.setOnItemClickListener(onItemClickListener);
        lv.setOnScrollListener(onScrollListener);

        chat_top_annouce = (TopAnnouceView) mRoot.findViewById(R.id.chat_top_annouce);
        MsgManager.instance().setOnAddTopNewAnnouceListener(chat_top_annouce);

        gift_show_containor = (GiftShowContainor) mRoot.findViewById(R.id.gift_show_containor);
        gift_show_containor.setInAnimation(mContext, R.anim.gift_show_in);       //设置显示动画
        gift_show_containor.setOutAnimation(mContext,R.anim.gift_show_out);     //设置消失动画
        MsgManager.instance().setGiftMessageListener(gift_show_containor);

        input_containor = (LinearLayout) mRoot.findViewById(R.id.input_containor);
        test_send_btn = (ImageButton) mRoot.findViewById(R.id.test_send_btn);
        chat_msg_send = (EditText) mRoot.findViewById(R.id.chat_msg_send);
        face_btn = (ImageButton) mRoot.findViewById(R.id.face_btn);
        chat_chognzhi_btn = (ImageButton) mRoot.findViewById(R.id.chat_chognzhi_btn);
        chat_gift_btn = (ImageButton) mRoot.findViewById(R.id.chat_gift_btn);
        test_send_btn.setOnClickListener(this);
        chat_msg_send.setOnClickListener(this);
        face_btn.setOnClickListener(this);
        chat_chognzhi_btn.setOnClickListener(this);
        chat_gift_btn.setOnClickListener(this);

        chat_face_view = (FacesView) mRoot.findViewById(R.id.chat_face_view);
        chat_face_view.setVisibility(View.GONE);
        chat_face_view.setContentReciver(chat_msg_send);

        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        chat_msg_send.setOnEditorActionListener(onEditorActionListener);
    }

    private TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                String msg = chat_msg_send.getText().toString();
                if (msg!=null && !msg.equals("")){
                    ChatMessage message = new ChatMessage(1,"老王",msg);
                    MsgManager.instance().sendChatMsg(message);
                    Log.d(TAG, "onEditorAction: " + msg);
                    chat_msg_send.setText("");
                }
                closeIME();
                return true;
            }
            return false;
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TextView tv = (TextView) view.findViewById(R.id.chat_msg_item_content);
            Toast toast = new Toast(mContext);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.makeText(mContext, tv.getText(), Toast.LENGTH_SHORT).show();
        }
    };

    private AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
        private int pos_first;
        private int pos_visible;
        private int total_count;
        private int current_scrollState;

        private boolean isToBottom = false;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            Log.d(TAG, "onScrollStateChanged: " + scrollState);
            current_scrollState = scrollState;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            Log.d(TAG, "onScroll: " + firstVisibleItem + " " + visibleItemCount + " " + totalItemCount);
            if (isCanScroll()) {
                pos_first = firstVisibleItem;
                pos_visible = visibleItemCount;
                total_count = totalItemCount;
                if (current_scrollState == 0) {
                    if (isToBottom){
                        lv.smoothScrollToPosition(total_count);
                    } else {
                        if ((pos_first + pos_visible) >= (total_count - 1)) {
                            lv.smoothScrollToPosition(total_count);
                            isToBottom = true;
                        } else if ((pos_first + pos_visible) < (total_count - 1)) {
                            //底部有新消息
                        }
                    }
                } else if (current_scrollState == 1) {
                    isToBottom = false;
                }
            }
        }
    };


    public boolean isCanScroll() {
        return canScroll;
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (chat_face_view.getVisibility() == View.VISIBLE) {
            if (ev.getX()>lv.getLeft() && ev.getX()<lv.getRight() && ev.getY()<lv.getBottom() && ev.getY()>lv.getTop()) {
                hideFaces();
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.test_send_btn:
                String msg = chat_msg_send.getText().toString();
                if (msg!=null && !msg.equals("")) {
                    ChatMessage message = new ChatMessage(1,"老王",msg);
                    MsgManager.instance().sendChatMsg(message);
                    chat_msg_send.setText("");
                    closeIME();
                }
                break;
            case R.id.chat_msg_send:
                if (!isSoftInputShowing) {
                    Log.d(TAG, "onClick: show");
                    if (chat_face_view.getVisibility() == View.VISIBLE) {
                        hideFaces();
                    }
                    imm.showSoftInput(chat_msg_send, InputMethodManager.SHOW_IMPLICIT);
                }
                break;
            case R.id.face_btn:
                if (chat_face_view.getVisibility() == View.VISIBLE) {
                    hideFaces();
                    imm.showSoftInput(chat_msg_send, InputMethodManager.SHOW_IMPLICIT);
                }else{
                    chat_face_view.setVisibility(View.VISIBLE);
                    face_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_selector_keyboard));
                    if (isSoftInputShowing) {
                        imm.hideSoftInputFromWindow(chat_msg_send.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
                break;

            case R.id.chat_chognzhi_btn:

                break;
            case R.id.chat_gift_btn:
                GiftPopupView gift_popup = new GiftPopupView(mContext,null);
                gift_popup.setAnimationStyle(R.style.Animation_gift_popup_fade);
                gift_popup.showAtLocation(this, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

                //gift_popup.dismiss();
                break;
        }
    }

    private void hideFaces() {
        face_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_selector_face));
        chat_face_view.resetPos();
        chat_face_view.setVisibility(View.GONE);
    }

    public void closeIME() {
        hideFaces();
        imm.hideSoftInputFromWindow(chat_msg_send.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public boolean isInputContainorVisible() {
        return input_containor.getVisibility() == View.VISIBLE;
    }

    public boolean isIMEVisible() {
        return chat_face_view.getVisibility() == View.VISIBLE || isSoftInputShowing;
    }

    public int getIMEHeight() {
        return IMEHeight;
    }

    public void hideInputContainor() {
        closeIME();
        input_containor.setVisibility(View.GONE);
    }

    public void showInputContainor() {
        input_containor.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if ((oldh-h)>oldh/3) {
            if (chat_face_view.getVisibility() == View.VISIBLE) {
                hideFaces();
            }
            isSoftInputShowing = true;
            if (IMEHeight==0) {
                IMEHeight = oldh-h+input_containor.getHeight();
            }
        }else{
            if (oldh==0 || h >= oldh) {
                isSoftInputShowing = false;
            }
        }
        requestLayout();
        Log.d(TAG, "onSizeChanged: " + w + " " + h + " " + oldw + " " + oldh);
    }
}
