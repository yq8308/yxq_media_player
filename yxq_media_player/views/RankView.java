package com.test.yxq.yxq_media_player.views;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.test.yxq.yxq_media_player.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/9.
 */
public class RankView extends FrameLayout implements View.OnClickListener {

    private static final String TAG = "RankView";

    private Context mContext;
    private RadioButton left_btn;
    private RadioButton right_btn;
    private ViewPager vp;
    private MyRankViewPagerAdpater adpater;

    public static final int RANK_STATE_BALANCE = 0;
    public static final int RANK_STATE_UP = 1;
    public static final int RANK_STATE_DOWN = 2;
    private ArrayList<SingleRankInfo> list_1;
    private ArrayList<SingleRankInfo> list_2;

    public RankView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.rank_view_layout, null);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(view, lp);

        view.setBackgroundColor(getResources().getColor(R.color.colorBg2));

        left_btn = (RadioButton) view.findViewById(R.id.left_rank_btn);
        right_btn = (RadioButton) view.findViewById(R.id.right_rank_btn);
        vp = (ViewPager) view.findViewById(R.id.rank_pager_view);

        left_btn.setOnClickListener(this);
        right_btn.setOnClickListener(this);

        vp.addOnPageChangeListener(onPageChangeListener);
    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                left_btn.setChecked(true);
            } else if (position == 1) {
                right_btn.setChecked(true);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_rank_btn:
                vp.setCurrentItem(0);
                break;
            case R.id.right_rank_btn:
                vp.setCurrentItem(1);
                break;
        }
    }

    public class SingleRankInfo {
        public int index;
        public int lv;
        public String name;
        public int state;

        public SingleRankInfo(int index, int lv, String name, int state) {
            this.index = index;
            this.lv = lv;
            this.name = name;
            this.state = state;
        }
    }

    //提供一个供外部使用的方法，填充内容
    public void setRankInfo() {

        list_1 = new ArrayList<SingleRankInfo>();
        list_1.add(new SingleRankInfo(1, 25, "名字1", 1));
        list_1.add(new SingleRankInfo(2, 20, "名字2", 2));
        list_1.add(new SingleRankInfo(3, 15, "名字3", 1));
        list_1.add(new SingleRankInfo(4, 22, "名字4", 2));
        list_1.add(new SingleRankInfo(5, 26, "名字5", 0));
        list_1.add(new SingleRankInfo(6, 13, "名字6", 0));
        list_1.add(new SingleRankInfo(7, 4, "名字7", 0));
        list_1.add(new SingleRankInfo(8, 6, "名字8", 1));
        list_1.add(new SingleRankInfo(9, 8, "名字9", 1));
        list_1.add(new SingleRankInfo(10, 1, "名字10", 1));

        list_2 = new ArrayList<SingleRankInfo>();
        list_2.add(new SingleRankInfo(1, 18, "名字11", 1));
        list_2.add(new SingleRankInfo(2, 25, "名字12", 2));
        list_2.add(new SingleRankInfo(3, 32, "名字13", 1));
        list_2.add(new SingleRankInfo(4, 23, "名字14", 2));
        list_2.add(new SingleRankInfo(5, 14, "名字15", 0));
        list_2.add(new SingleRankInfo(6, 25, "名字16", 0));
        list_2.add(new SingleRankInfo(7, 16, "名字17", 0));
        list_2.add(new SingleRankInfo(8, 7, "名字18", 1));
        list_2.add(new SingleRankInfo(9, 28, "名字19", 1));
        list_2.add(new SingleRankInfo(10, 9, "名字20", 1));

        MyRankViewPagerAdpater adpater = new MyRankViewPagerAdpater();
        vp.setAdapter(adpater);
    }

    private class MyRankViewPagerAdpater extends PagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Log.d(TAG, "instantiateItem: ---------");
            List<SingleRankInfo> mList = null;
            if (position == 0) {
                mList = list_1;
            } else if (position == 1) {
                mList = list_2;
            }
            LinearLayout mRoot = new LinearLayout(mContext);
            mRoot.setOrientation(LinearLayout.VERTICAL);
            for (int i = 0; i < mList.size(); i++) {
                Log.d(TAG, "instantiateItem: " + i);
                View item = View.inflate(mContext, R.layout.rank_info_item, null);
                ImageView rank_item_index = (ImageView) item.findViewById(R.id.rank_item_index);
                ImageView rank_item_level = (ImageView) item.findViewById(R.id.rank_item_level);
                TextView rank_item_name = (TextView) item.findViewById(R.id.rank_item_name);
                ImageView rank_item_state = (ImageView) item.findViewById(R.id.rank_item_state);
                rank_item_index.setImageResource(mContext.getResources().getIdentifier(getIndex(mList.get(i).index), "drawable", mContext.getPackageName()));
                rank_item_level.setImageResource(mContext.getResources().getIdentifier(getDuanwei(mList.get(i).lv), "drawable", mContext.getPackageName()));
                rank_item_name.setText(mList.get(i).name);
                rank_item_state.setImageResource(mContext.getResources().getIdentifier(getState(mList.get(i).state), "drawable", mContext.getPackageName()));
                LinearLayout.LayoutParams item_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                item_lp.weight = 1;
                mRoot.addView(item, item_lp);

                //分割线
                if (i != mList.size() - 1) {
                    ImageView iv = new ImageView(mContext);
                    LinearLayout.LayoutParams iv_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                    iv_lp.height = 1;
                    iv_lp.setMargins(65,0,65,0);
                    mRoot.addView(iv);
                    iv.setLayoutParams(iv_lp);
                    iv.setBackgroundColor(0x66000000);
                }
            }
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(mRoot, params);
            return mRoot;
        }

        private String getIndex(int m) {
            return String.format("gift_rank_%02d", m);
        }

        private String getDuanwei(int m) {
            String s_level = null;
            int duanwei = (m-1) / 5;
            int jibie = m % 5;
            if (jibie == 0) jibie = 5;
            switch (duanwei) {
                case 0:
                    s_level = "gift_rank_qingtong";
                    break;
                case 1:
                    s_level = "gift_rank_baiyin";
                    break;
                case 2:
                    s_level = "gift_rank_huangjin";
                    break;
                case 3:
                    s_level = "gift_rank_bojin";
                    break;
                case 4:
                    s_level = "gift_rank_zuanshi";
                    break;
                case 5:
                    s_level = "gift_rank_wangzhe";
                    break;

                case 6:
                    s_level = "gift_rank_zongshi";
                    break;
            }
            return String.format("%s%02d", s_level, jibie);
        }

        private String getState(int m) {
            switch (m) {
                case RANK_STATE_BALANCE:
                    return "balance_icon";
                case RANK_STATE_UP:
                    return "up_icon";
                case RANK_STATE_DOWN:
                    return "down_icon";
            }

            return null;
        }
    }


}
