package com.test.yxq.yxq_media_player.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.yxq.yxq_media_player.R;

import java.util.ArrayList;
import java.util.List;

public class MyVideoPagerView extends FrameLayout implements View.OnClickListener{

    private static final String TAG = "MyVideoPagerView";

    private Context mContext;

    private ViewPager vp;
    private TextView nav_btn_talk;
    private TextView nav_btn_zhubo;
    private TextView nav_btn_rank;
    private TextView nav_btn_foucs;

    private ImageView nav_scroll_bar;

    private ChatView cv;
    private ZhuboInfoView ziv;
    private RankView rv;

    private int color_orange;
    private int color_gray;
    private int color_black;

    private int screen_width;
    private int nav_scroll_bar_width;

    private boolean hasFoucsed = false;

    public MyVideoPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater infater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = infater.inflate(R.layout.player_pager_view_layout, null);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(v, lp);

        Resources res = getResources();
        color_orange = res.getColor(R.color.orange);
        color_gray = res.getColor(R.color.gray);
        color_black = res.getColor(R.color.black);

        nav_btn_talk = (TextView) v.findViewById(R.id.nav_btn_talk);
        nav_btn_zhubo = (TextView) v.findViewById(R.id.nav_btn_zhubo);
        nav_btn_rank = (TextView) v.findViewById(R.id.nav_btn_rank);
        nav_btn_foucs = (TextView) v.findViewById(R.id.nav_btn_foucs);

        initFouceBtn(false);

        nav_btn_talk.setOnClickListener(this);
        nav_btn_zhubo.setOnClickListener(this);
        nav_btn_rank.setOnClickListener(this);
        nav_btn_foucs.setOnClickListener(this);

        nav_scroll_bar = (ImageView) v.findViewById(R.id.nav_scroll_bar);

        nav_scroll_bar_width = nav_scroll_bar.getLayoutParams().width;
        screen_width = this.getResources().getDisplayMetrics().widthPixels;
        LinearLayout.MarginLayoutParams margin=new LinearLayout.MarginLayoutParams(nav_scroll_bar.getLayoutParams());
        margin.setMargins(screen_width / 8 - nav_scroll_bar_width / 2, 0, 0, 0);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(margin);
        nav_scroll_bar.setLayoutParams(layoutParams);

        nav_btn_talk.setTextColor(color_orange);

        vp = (ViewPager) v.findViewById(R.id.view_pager);
        cv = new ChatView(mContext,null);
        ziv = new ZhuboInfoView(mContext,null);
        rv = new RankView(mContext,null);
        List<View> list = new ArrayList<View>();
        list.add(cv);
        list.add(ziv);
        list.add(rv);
        MyPagerViewAdpater mpva = new MyPagerViewAdpater(list);
        vp.setAdapter(mpva);

        vp.addOnPageChangeListener(onPageChangeListener);

        //绑定关注按钮
        ziv.setFriendButton(nav_btn_foucs);

        rv.setRankInfo();
    }

    private void initFouceBtn(boolean b) {
        // 发送关注/取消关注请求，服务器返回结果后调用
        if (!b) {
            nav_btn_foucs.setBackgroundColor(color_orange);
            nav_btn_foucs.setTextColor(Color.BLACK);
            nav_btn_foucs.setText("关注\n21998");
        }
        else{
            // 关注后切换到主播信息页面
            nav_btn_foucs.setBackgroundColor(color_gray);
            nav_btn_foucs.setTextColor(Color.WHITE);
            nav_btn_foucs.setText("已关注\n21999");

            vp.setCurrentItem(1);
        }
        hasFoucsed = b;


    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        /**
         * This method will be invoked when the current page is scrolled, either as part
         * of a programmatically initiated smooth scroll or a user initiated touch scroll.
         *
         * @param position Position index of the first page currently being displayed.
         *                 Page position+1 will be visible if positionOffset is nonzero.
         * @param positionOffset Value from [0, 1) indicating the offset from the page at position.
         * @param positionOffsetPixels Value in pixels indicating the offset from position.
         */

        int lastPage = 0;
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //Log.d(TAG, "onPageScrolled: "+position+" "+positionOffset+" "+positionOffsetPixels);
            if (position>lastPage){
                lastPage = position;
                if (positionOffset==0){
                    LinearLayout.MarginLayoutParams margin=new LinearLayout.MarginLayoutParams(nav_scroll_bar.getLayoutParams());
                    margin.setMargins(screen_width / 8 - nav_scroll_bar_width / 2 + position*screen_width/4, 0, 0, 0);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(margin);
                    nav_scroll_bar.setLayoutParams(layoutParams);
                    return;
                }
            }else{
                LinearLayout.MarginLayoutParams margin=new LinearLayout.MarginLayoutParams(nav_scroll_bar.getLayoutParams());
                margin.setMargins(screen_width / 8 - nav_scroll_bar_width / 2+ position*screen_width/4 +  positionOffsetPixels/4 , 0, 0, 0);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(margin);
                nav_scroll_bar.setLayoutParams(layoutParams);
            }
        }

        /**
         * This method will be invoked when a new page becomes selected. Animation is not
         * necessarily complete.
         *
         * @param position Position index of the new selected page.
         */
        @Override
        public void onPageSelected(int position) {
            Log.d(TAG, "onPageSelected: "+position);
            switch (position) {
                case 0:
                    cv.showInputContainor();

                    nav_btn_talk.setTextColor(color_orange);
                    nav_btn_zhubo.setTextColor(color_black);
                    nav_btn_rank.setTextColor(color_black);
                    nav_btn_foucs.setTextColor(color_black);
                    break;
                case 1:
                    cv.hideInputContainor();
                    nav_btn_talk.setTextColor(color_black);
                    nav_btn_zhubo.setTextColor(color_orange);
                    nav_btn_rank.setTextColor(color_black);
                    nav_btn_foucs.setTextColor(color_black);
                    break;
                case 2:
                    cv.hideInputContainor();
                    nav_btn_talk.setTextColor(color_black);
                    nav_btn_zhubo.setTextColor(color_black);
                    nav_btn_rank.setTextColor(color_orange);
                    nav_btn_foucs.setTextColor(color_black);
                    break;
            }
        }


        /**
         * @param state The new scroll state.
         * @see ViewPager#SCROLL_STATE_IDLE
         * @see ViewPager#SCROLL_STATE_DRAGGING
         * @see ViewPager#SCROLL_STATE_SETTLING
         * @param state
         */
        @Override
        public void onPageScrollStateChanged(int state) {
            Log.d(TAG, "onPageScrollStateChanged: "+state);
            if (state == ViewPager.SCROLL_STATE_IDLE ){
                cv.setCanScroll(true);
            }else{
                cv.setCanScroll(false);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.nav_btn_talk:
                vp.setCurrentItem(0);
                break;
            case R.id.nav_btn_zhubo:
                vp.setCurrentItem(1);
                break;
            case R.id.nav_btn_rank:
                vp.setCurrentItem(2);
                break;
            case R.id.nav_btn_foucs:
                //vp.setCurrentItem(3);
                //initFouceBtn(!hasFoucsed);
                break;

        }
    }

    class MyPagerViewAdpater extends PagerAdapter{

        private List<View> mList;

        public MyPagerViewAdpater(List<View> mList) {
            this.mList = mList;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mList.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mList.get(position));
        }
    }

    public boolean isIMEVisible() {
        if (cv!=null) {
            return cv.isIMEVisible();
        }
        return false;
    }


    public void closeIME() {
        if (cv!=null && cv.isIMEVisible()){
            cv.closeIME();
        }
    }

    public int getIMEHeight() {
        if (cv!=null) {
            return cv.getIMEHeight();
        }
        return 0;
    }
}
