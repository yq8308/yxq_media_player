package com.test.yxq.yxq_media_player.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.test.yxq.yxq_media_player.R;
import com.test.yxq.yxq_media_player.beans.GiftAtrr;
import com.test.yxq.yxq_media_player.beans.GiftShowData;
import com.test.yxq.yxq_media_player.utils.ImageNumberSwitcher;
import com.test.yxq.yxq_media_player.utils.common.CustomXmlLoader;
import com.test.yxq.yxq_media_player.utils.common.DisplayUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Created by Administrator on 2016/5/19.
 */
public class GiftShowItem extends FrameLayout {

    private Context mContext;

    private ImageView gift_show_bg;
    private ImageView gift_show_tx;
    private TextView gift_show_user_name;
    private TextView gift_show_gift_name;
    private ImageView gift_show_gift_icon;
    private ImageNumberSwitcher gift_show_ins;

    private List<GiftAtrr> mGiftList;

    public GiftShowItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = inflater.inflate(R.layout.gift_show_layout, null);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(mContext, 50));
        addView(root, lp);

        gift_show_bg = (ImageView) root.findViewById(R.id.gift_show_bg);
        gift_show_tx = (ImageView) root.findViewById(R.id.gift_show_tx);
        gift_show_user_name = (TextView) root.findViewById(R.id.gift_show_user_name);
        gift_show_gift_name = (TextView) root.findViewById(R.id.gift_show_gift_name);
        gift_show_gift_icon = (ImageView) root.findViewById(R.id.gift_show_gift_icon);
        gift_show_ins = (ImageNumberSwitcher) root.findViewById(R.id.gift_show_imageNumberSwitcher);
        gift_show_ins.setInAnimation(mContext, R.anim.live_combo_num_in);
        gift_show_ins.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageNumber in = new ImageNumber(mContext, null);
                in.setSpacing(0);           //设置数字和数字之间的间距 dp
                in.setScaleRatio(1.4f);     //设置数字切换时的缩放比例
                in.setDp(12, 16);            //设置数字的宽高 dp
                in.setPrefixX(true);
                return in;
            }
        });
        gift_show_ins.setValue(0);

        mGiftList = CustomXmlLoader.loadGiftAttr(mContext, R.xml.gift_attribution);
    }

    public void setData(GiftShowData gsd) {
        String tx_url = gsd.tx_url;
        String user_name = gsd.user_name;
        int gift_type = gsd.gift_type;
        String gift_name = null;
        String gift_icon = null;
        for (int i = 0; i < mGiftList.size(); i++) {
            GiftAtrr attr = mGiftList.get(i);
            if (attr.index == gift_type){
                gift_name = attr.name;
                gift_icon = attr.iconName;
            }
        }
        int clicks = gsd.clicks;

        gift_show_user_name.setText(user_name);
        gift_show_gift_name.setText(gift_name);
        gift_show_gift_icon.setImageResource(getResources().getIdentifier(gift_icon, "drawable", mContext.getPackageName()));
        gift_show_ins.setValue(clicks);

        if (!tx_url.equals("")) {
            new AsyncTask<String, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(String... params) {
                    try {
                        InputStream in = new URL(params[0]).openConnection().getInputStream();
                        Bitmap bmp = BitmapFactory.decodeStream(in);
                        return bmp;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    if (bitmap != null)
                        gift_show_tx.setImageBitmap(bitmap);
                }
            }.execute(tx_url);
        }
    }

    public String getUserName() {
        return String.valueOf(gift_show_user_name.getText());
    }

    public String getGiftName() {
        return String.valueOf(gift_show_gift_name.getText());
    }

}
