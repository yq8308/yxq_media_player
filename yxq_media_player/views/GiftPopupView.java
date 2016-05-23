package com.test.yxq.yxq_media_player.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.test.yxq.yxq_media_player.R;
import com.test.yxq.yxq_media_player.beans.GiftAtrr;
import com.test.yxq.yxq_media_player.beans.GiftMessage;
import com.test.yxq.yxq_media_player.utils.MsgManager;
import com.test.yxq.yxq_media_player.utils.common.CustomXmlLoader;
import com.test.yxq.yxq_media_player.utils.common.LLog;

import java.util.List;

/**
 * Created by Administrator on 2016/5/13.
 */
public class GiftPopupView extends PopupWindow implements View.OnClickListener{

    private Context mContext;

    private List<GiftAtrr> mListGift;

    private GridView popup_gift_grid_view;
    private Button popup_gift_chongzhi_btn;
    private TextView popup_gift_yuwan_num_tv;
    private TextView popup_gift_yuchi_num_tv;
    private ImageButton popup_gift_close_btn;

    public GiftPopupView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mRoot = inflater.inflate(R.layout.gift_popup_layout, null);
        setContentView(mRoot);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);

        ColorDrawable dw = new ColorDrawable(Color.WHITE);
        setBackgroundDrawable(dw);

        popup_gift_grid_view = (GridView) mRoot.findViewById(R.id.popup_gift_grid_view);
        popup_gift_chongzhi_btn = (Button) mRoot.findViewById(R.id.popup_gift_chongzhi_btn);
        popup_gift_yuwan_num_tv = (TextView) mRoot.findViewById(R.id.popup_gift_yuwan_num_tv);
        popup_gift_yuchi_num_tv = (TextView) mRoot.findViewById(R.id.popup_gift_yuchi_num_tv);
        popup_gift_close_btn = (ImageButton) mRoot.findViewById(R.id.popup_gift_close_btn);

        /**
         * 通过用户信息初始化 下面2个textview
         */
        popup_gift_yuwan_num_tv.setText(""+998);
        popup_gift_yuchi_num_tv.setText("" + 9998);

        popup_gift_chongzhi_btn.setOnClickListener(this);
        popup_gift_close_btn.setOnClickListener(this);

        mListGift = CustomXmlLoader.loadGiftAttr(mContext, R.xml.gift_attribution);
        MyGiftGridViewAdapter adapter = new MyGiftGridViewAdapter(mContext,mListGift);
        popup_gift_grid_view.setAdapter(adapter);
        popup_gift_grid_view.setOnItemClickListener(onItemClickListener);
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //Toast.makeText(mContext,"------------"+position,Toast.LENGTH_SHORT).show();
            GiftMessage message = new GiftMessage(position,"老王","张三",1);
            MsgManager.instance().sendGiftMsg(message);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.popup_gift_chongzhi_btn:
                dismiss();
                break;
            case R.id.popup_gift_close_btn:
                dismiss();
                break;
        }
    }

    private class MyGiftGridViewAdapter extends BaseAdapter {
        private Context ct;
        private List<GiftAtrr> mList;
        public MyGiftGridViewAdapter(Context context ,List<GiftAtrr> list) {
            this.ct = context;
            this.mList = list;
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
            Holder holder = null;
            if (convertView==null) {
                convertView = View.inflate(ct,R.layout.popup_gift_grid_item,null);
                holder = new Holder();
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.gift_item_iv);
                holder.tv_name = (TextView) convertView.findViewById(R.id.gift_item_tv1);
                holder.tv_value = (TextView) convertView.findViewById(R.id.gift_item_tv2);
                convertView.setTag(holder);
            } else {
                holder = (Holder)convertView.getTag();
            }

            GiftAtrr gift = mList.get(position);
            LLog.format("filename:%s ,name:%s,value:%s type:%d",gift.iconName,gift.name,gift.value,gift.type);
            holder.iv_icon.setImageResource(ct.getResources().getIdentifier(gift.iconName, "drawable", ct.getPackageName()));
            holder.tv_name.setText(gift.name);
            String typeName = null;
            if (gift.type==0){
                typeName = "鱼丸";
            }else if (gift.type==1) {
                typeName = "鱼翅";
            }
            holder.tv_value.setText(gift.value+typeName);

            return convertView;
        }

        class Holder{
            ImageView iv_icon;
            TextView tv_name;
            TextView tv_value;
        }
    }
}
