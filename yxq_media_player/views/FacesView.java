package com.test.yxq.yxq_media_player.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.Matrix;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.test.yxq.yxq_media_player.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/12.
 */
public class FacesView extends FrameLayout {


    private final Context mContext;
    private String[] fileList;

    private ViewPager face_viewPager;
    private LinearLayout face_tipbar;
    private List<ImageView> tipBarList;

    private EditText contentReciver;

    private int faceCount;
    private int pageNum;

    private final static String FACE_MID_ASSET_PATH = "face/png/face-mid";
    private final static String FACE_MAX_ASSET_PATH = "face/png/face-max";
    private final static int PER_PAGER_SHOW_COUNT = 18;  // 17个表情+1个删除图标
    private final static int GRID_SHOW_COLUMS = 6;          // 6列

    private final static int bq_width = 110;
    private final static int bq_height = 110;

    private Bitmap seekbar_bmp = BitmapFactory.decodeResource(getResources(), R.drawable.iamge_seekbar);


    public FacesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        iniView();
    }

    /**
     * 初始化界面
     */
    private void iniView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mRoot = inflater.inflate(R.layout.faces_grid_layout, null);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(mRoot, lp);

        face_viewPager = (ViewPager) mRoot.findViewById(R.id.face_viewPager);
        face_tipbar = (LinearLayout) mRoot.findViewById(R.id.face_tipbar);

        initViewPager();
        initTipBar();

        face_viewPager.addOnPageChangeListener(onPageChangeListener);
    }

    private Bitmap makeBmpGray(Bitmap bmp) {
        Bitmap updateBitmap = Bitmap.createBitmap(bmp.getWidth(),
                bmp.getHeight(), bmp.getConfig());
        Canvas canvas = new Canvas(updateBitmap);
        Paint paint = new Paint();
        final ColorMatrix cm = new ColorMatrix();
        //0xFF888888
        cm.set(new float[] { 0.5f, 0, 0, 0, 0,// 红色值
                0, 0.5f, 0, 0, 0,// 绿色值
                0, 0, 0.5f, 0, 0,// 蓝色值
                0, 0, 0, 1, 0 // 透明度
        });
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        paint.setColor(Color.GRAY);
        paint.setAntiAlias(true);
        final Matrix matrix = new Matrix();
        canvas.drawBitmap(bmp, matrix, paint);

        return updateBitmap;
    }

    private void initTipBar() {
        tipBarList = new ArrayList<ImageView>();
        for (int i = 0; i < pageNum; i++) {
            ImageView iv = new ImageView(mContext, null);
            if (i==0){
                iv.setImageResource(R.drawable.point_selected);
            } else {
                iv.setImageResource(R.drawable.point_normal);
            }
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            iv.setLayoutParams(lp);
            face_tipbar.addView(iv);
            tipBarList.add(iv);
        }
    }

    private void initViewPager() {
        faceCount = getFacesCount();
        pageNum = (int) Math.ceil((float) faceCount * 1.00 / (PER_PAGER_SHOW_COUNT - 1));
        List<View> list = new ArrayList<View>();
        for (int i = 0; i < pageNum; i++) {
            GridView pg = new GridView(mContext, null);
            list.add(pg);
            String[] list1 = new String[PER_PAGER_SHOW_COUNT - 1];
            for (int j = 0; j < PER_PAGER_SHOW_COUNT - 1; j++) {
                if (i * (PER_PAGER_SHOW_COUNT - 1) + j >= faceCount) {
                    list1[j] = null;
                } else {
                    list1[j] = fileList[i * (PER_PAGER_SHOW_COUNT - 1) + j];
                }
            }
            pg.setNumColumns(GRID_SHOW_COLUMS);
            pg.setHorizontalSpacing(15);
            pg.setVerticalSpacing(25);
            pg.setPadding(20, 40, 20, 40);
            pg.setGravity(Gravity.CENTER);
            pg.setBackgroundColor(Color.WHITE);
            pg.setAdapter(new MyGridAdpater(mContext, list1));
            pg.setOnItemClickListener(onItemClickListener);
        }
        face_viewPager.setAdapter(new MyPageAdpater(list));
    }

    public void setContentReciver(EditText view) {
        contentReciver = view;
    }

    /**
     * 获取指定路径下的face文件名，获取face的数量
     */
    private int getFacesCount() {

        int count = 0;
        try {
            fileList = mContext.getResources().getAssets().list(FACE_MAX_ASSET_PATH);
            count = fileList.length;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    public void resetPos() {
        face_viewPager.setCurrentItem(0);
    }


    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            for (int i = 0; i < tipBarList.size(); i++) {
                if (i==position){
                    tipBarList.get(i).setImageResource(R.drawable.point_selected);
                } else {
                    tipBarList.get(i).setImageResource(R.drawable.point_normal);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (contentReciver == null) return;
            if (position != PER_PAGER_SHOW_COUNT - 1) {
                String tag = (String) view.getTag();
                try {
                    InputStream is = mContext.getResources().getAssets().open(FACE_MID_ASSET_PATH + "/" + tag);
                    Drawable d = Drawable.createFromStream(is, tag);
                    d.setBounds(0, 0, 70, 70);
                    ImageSpan span = new ImageSpan(d);
                    CharSequence source = "<i" + tag + ">";
                    SpannableString spannableString = new SpannableString(source);
                    spannableString.setSpan(span, 0, source.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    contentReciver.append(spannableString);
                    System.out.println("onItemClick: " + spannableString);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Editable edit = contentReciver.getText();
                int pos = contentReciver.getSelectionStart();
                System.out.println("onItemClick: " + pos);
                if (pos >= 8) {
                    String last = edit.toString().substring(pos - 8, pos);
                    System.out.println(last);
                    String last_pref = last.substring(0, 4);
                    System.out.println(last_pref);
                    char last_end = last.charAt(last.length() - 1);
                    System.out.println(last_end);
                    if (last_pref.equals("<idy") && last_end == '>') {
                        edit.delete(pos - 8, pos);
                    } else {
                        edit.delete(pos - 1, pos);
                    }
                } else {
                    if (pos == 0) return;
                    edit.delete(pos - 1, pos);
                }
            }
        }
    };

    private class MyGridAdpater extends BaseAdapter {

        private final Context context;
        private String[] mList;

        public MyGridAdpater(Context context, String[] list) {
            this.context = context;
            this.mList = list;
        }

        @Override
        public int getCount() {
            return mList.length + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position == PER_PAGER_SHOW_COUNT - 1) {
                return "icon_del.png";
            }
            return mList[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new ImageView(mContext, null);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(bq_width,bq_height);
                convertView.setLayoutParams(lp);
            }
            ImageView iv = (ImageView) convertView;
            String fileName = null;
            if (getItem(position) == null) {
                return  iv;
            } else {
                if (position == PER_PAGER_SHOW_COUNT - 1) {
                    fileName = "face/" + getItem(position);
                } else {
                    fileName = FACE_MAX_ASSET_PATH + "/" + getItem(position);
                }
            }

            InputStream is;
            Bitmap bitmap;
                try {
                    is = context.getResources().getAssets().open(fileName);
                    bitmap = BitmapFactory.decodeStream(is);
                    iv.setImageBitmap(bitmap);
                    iv.setTag(getItem(position));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            return iv;
        }
    }

    private class MyPageAdpater extends PagerAdapter {

        private List<View> mList;

        public MyPageAdpater(List<View> list) {
            this.mList = list;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mList.get(position);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(view, lp);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mList.get(position));
        }
    }
}
