package com.test.yxq.yxq_media_player.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ViewSwitcher;

import com.test.yxq.yxq_media_player.R;
import com.test.yxq.yxq_media_player.beans.GiftShowData;
import com.test.yxq.yxq_media_player.beans.TopAnnouceData;
import com.test.yxq.yxq_media_player.utils.ImageNumberSwitcher;
import com.test.yxq.yxq_media_player.views.GiftShowContainor;
import com.test.yxq.yxq_media_player.views.ImageNumber;
import com.test.yxq.yxq_media_player.views.TopAnnouceView;

/**
 * Created by Administrator on 2016/5/14.
 */
public class TestActivity extends Activity{

    private TopAnnouceView topAnnouce;
    ImageNumberSwitcher ins1;
    ImageNumberSwitcher ins2;
    private GiftShowContainor gift_show_containor;

    int num1 = 0;
    int num2 = 0;

    int gift_num = 0;
    int gift_num2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        //1
        topAnnouce = (TopAnnouceView) findViewById(R.id.annouce_top);

        //2
        ins1 = (ImageNumberSwitcher) findViewById(R.id.image_number_switcher1);
        ins1.setInAnimation(this, R.anim.live_combo_num_in);
        ins1.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageNumber in = new ImageNumber(TestActivity.this, null);
                in.setSpacing(0);           //设置数字和数字之间的间距 dp
                in.setScaleRatio(1.4f);     //设置数字切换时的缩放比例
                in.setDp(18, 25);           //设置数字的宽高 dp
                return in;
            }
        });
        ins1.setValue(0);

        ins2 = (ImageNumberSwitcher) findViewById(R.id.image_number_switcher2);
        ins2.setInAnimation(this, R.anim.live_combo_num_in);
        ins2.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageNumber in = new ImageNumber(TestActivity.this, null);
                in.setSpacing(0);           //设置数字和数字之间的间距 dp
                in.setScaleRatio(1.4f);     //设置数字切换时的缩放比例
                in.setDp(18, 25);           //设置数字的宽高 dp
                in.setPrefixX(true);        //带x
                return in;
            }
        });
        ins2.setValue(0);

        //3
        gift_show_containor = (GiftShowContainor) findViewById(R.id.gift_show_containor);
        gift_show_containor.setInAnimation(this, R.anim.gift_show_in);       //设置显示动画
        gift_show_containor.setOutAnimation(this,R.anim.gift_show_out);     //设置消失动画
    }

    @Override
    protected void onStop() {
        super.onStop();
        topAnnouce.release();
    }

    public void add(View v) {
        topAnnouce.addTopAnnouce(new TopAnnouceData(0,"老王1", "张三1"));
        topAnnouce.addTopAnnouce(new TopAnnouceData(1,"老王2", "张三2"));
        topAnnouce.addTopAnnouce(new TopAnnouceData(0, "老王3", "张三3"));
        topAnnouce.addTopAnnouce(new TopAnnouceData(1, "老王4", "张三4"));
    }

    public void addNum1(View v) {
        num1++;
        ins1.setValue(num1);
    }

    public void addNum2(View v) {
        num2++;
        ins2.setValue(num2);
    }

    public void showGift(View v) {
        gift_num++;
        GiftShowData gsd = new GiftShowData("", "余小擎", 5, gift_num);
        gift_show_containor.addGiftShowItem(gsd);
    }

    public void showGift2(View v) {
        gift_num2++;
        GiftShowData gsd = new GiftShowData("", "张晴", 4, gift_num2);
        gift_show_containor.addGiftShowItem(gsd);
    }
}
