package com.test.yxq.yxq_media_player.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.test.yxq.yxq_media_player.R;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Administrator on 2016/5/9.
 */
public class ZhuboInfoView extends FrameLayout {
    private static final String TAG = "ZhuboInfo";
    private Context mContext;

    private ImageView zb_tv_icon;
    private TextView zb_tv_title;
    private TextView zb_tv_name;
    private TextView zb_tv_weight;
    private TextView zb_tv_channel;
    private ToggleButton zb_toggle_btn;
    private TextView zb_tv_gonggao;

    private View friendButton;

    public ZhuboInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.zhubo_info_layout, null);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(view,lp);

        zb_tv_icon = (ImageView) view.findViewById(R.id.zb_tv_icon);
        zb_tv_title = (TextView) view.findViewById(R.id.zb_tv_title);
        zb_tv_name = (TextView) view.findViewById(R.id.zb_tv_name);
        zb_tv_weight = (TextView) view.findViewById(R.id.zb_tv_weight);
        zb_tv_channel = (TextView) view.findViewById(R.id.zb_tv_channel);
        zb_tv_gonggao = (TextView) view.findViewById(R.id.zb_tv_gonggao);
        zb_toggle_btn = (ToggleButton) view.findViewById(R.id.zb_toggle_btn);

        zb_toggle_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //开或关
            }
        });
    }

    public class ZhuboInfo {
        public String icon_url;
        public String title;
        public String name;
        public String weight;
        public String channel;
        public String gonggao;
        public boolean isFoucsed;
    }

    //提供一个方法给外部调用，填充内容信息
    public void setZhuboInfo (ZhuboInfo info) {
        zb_tv_title.setText(info.title);
        zb_tv_name.setText(info.name);
        zb_tv_weight.setText(info.weight);
        zb_tv_channel.setText(info.channel);
        zb_tv_gonggao.setText(info.gonggao);
        zb_toggle_btn.setChecked(info.isFoucsed);

        //下载主播头像icon
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                String url = params[0];
                try {
                    URLConnection connection = new URL(url).openConnection();
                    Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                    return bitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                zb_tv_icon.setImageBitmap(bitmap);
            }

        }.execute(info.icon_url);
    }

    public void setFriendButton(View view) {
        friendButton = view;
    }
}
