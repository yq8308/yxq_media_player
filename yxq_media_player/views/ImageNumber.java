package com.test.yxq.yxq_media_player.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.test.yxq.yxq_media_player.R;
import com.test.yxq.yxq_media_player.utils.common.DisplayUtil;
import com.test.yxq.yxq_media_player.utils.common.LLog;

/**
 * Created by Administrator on 2016/5/16.
 * <p/>
 * 需要实现一个根据对应的数字生成相应的图片显示
 */
public class ImageNumber extends View {
    private Context mContext;
    private int mValue;
    private float mScaleRatio;
    private float mSpacing;
    private boolean prefix_x = false;
    private float width = 0;
    private float height = 0;
    private float widthdp = 0;
    private float heightdp = 0;

    private float mScaleX = 1.0f;
    private float mScaleY = 1.0f;

    public ImageNumber(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public ImageNumber(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageNumber, defStyleAttr, 0);
        LLog.d(" ------------- length: " + typedArray.length());
        for (int i = 0; i < typedArray.length(); i++) {
            int rid = typedArray.getIndex(i);
            if (rid == R.styleable.ImageNumber_value) {
                mValue = typedArray.getInt(rid, 0);
            }
            if (rid == R.styleable.ImageNumber_scaleRatio) {
                mScaleRatio = typedArray.getDimension(rid, 1.0f);
            }
            if (rid == R.styleable.ImageNumber_spacing) {
                mSpacing = typedArray.getDimension(rid, 0.0f);
            }
            if (rid == R.styleable.ImageNumber_layout_width) {
                widthdp = typedArray.getDimension(rid, 18.0f);
            }
            if (rid == R.styleable.ImageNumber_layout_height) {
                heightdp = typedArray.getDimension(rid, 25.0f);
            }
            if (rid == R.styleable.ImageNumber_prefix_x) {
                prefix_x = typedArray.getBoolean(rid, false);
            }
        }
        LLog.d(" ------------- : " + mValue + " " + mSpacing);
        typedArray.recycle();
    }

    public void setValue(int var) {
        this.mValue = var;
    }

    public void setScaleRatio(float ratio) {
        this.mScaleRatio = ratio;
    }

    public void setSpacing(float var) {
        mSpacing = DisplayUtil.dip2px(mContext, var);
    }

    public void setPrefixX(boolean b) {
        prefix_x = b;
    }

    public void setDp(float wdp, float hdp) {
        widthdp = DisplayUtil.dip2px(mContext, wdp);
        heightdp = DisplayUtil.dip2px(mContext, hdp);
    }

    private int getCount() {
        int count = 0;

        if (mValue == 0) {
            count = 1;
        } else {
            int divisor = 1;
            while (mValue / divisor > 0) {
                count += 1;
                divisor *= 10;
            }
        }

        return count;
    }

    private int getSourceWidth() {
        Drawable drawable = getResources().getDrawable(R.drawable.live_combo_0);
        return drawable.getIntrinsicWidth();
    }

    private int getSourceHeight() {
        Drawable drawable = getResources().getDrawable(R.drawable.live_combo_0);
        return drawable.getIntrinsicHeight();
    }

    private int getPrefixXWidth() {
        Drawable drawable = getResources().getDrawable(R.drawable.live_combo_x);
        return drawable.getIntrinsicWidth();
    }

    private int getPrefixXHeight() {
        Drawable drawable = getResources().getDrawable(R.drawable.live_combo_x);
        return drawable.getIntrinsicHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (widthdp == 0 || heightdp == 0) {
            mScaleX = 1.0f;
            mScaleY = 1.0f;
        } else {
            mScaleX = widthdp / getSourceWidth();
            mScaleY = heightdp / getSourceHeight();
        }

        width = (getSourceWidth() * getCount() + mSpacing * (getCount() - 1)) * mScaleRatio * mScaleX;
        height = getSourceHeight() * mScaleRatio * mScaleY;

        if (prefix_x) {
            width += (getPrefixXWidth() + mSpacing)* mScaleRatio * mScaleX;
        }

        setMeasuredDimension((int)width, (int)height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Paint paint = new Paint();
//        paint.setColor(Color.GREEN);
//        canvas.drawRect(0,0,width,height,paint);
        int x = 0;
        float startX;
        float startY;
        if (prefix_x) {
            startX = (width - (getSourceWidth() * getCount() + mSpacing * getCount() + getPrefixXWidth()) * mScaleX) / 2;
        } else {
            startX = (width - (getSourceWidth() * getCount() + mSpacing * (getCount() - 1)) * mScaleX) / 2;
        }
        startY = (height - getSourceHeight() * mScaleY) / 2;
        int len = prefix_x==true?getCount()+1:getCount();
        for (int i = 0; i < len; i++) {
            int resId;
            Bitmap bmp = null;
            Matrix matrix;
            if (prefix_x && i==0) {
                resId = getResources().getIdentifier("live_combo_x", "drawable", mContext.getPackageName());
                bmp = BitmapFactory.decodeResource(getResources(), resId);
                matrix = new Matrix();
                matrix.postScale(mScaleX, mScaleY);
                matrix.postTranslate(startX + x, startY+(getSourceHeight()-getPrefixXHeight())*mScaleY);
                canvas.drawBitmap(bmp, matrix, null);
            } else {
                int var;
                if (i == len - 1) {
                    var = mValue % 10;
                } else {
                    var = (int) (mValue / (Math.pow(10, len - 1 - i))) % 10;
                }
                LLog.d("-------> width = "+width+"   startX = " + startX + "  startY = " + startY + " value: " + mValue + "   i: " + i + "   var: " + var);
                resId = getResources().getIdentifier("live_combo_" + var, "drawable", mContext.getPackageName());
                bmp = BitmapFactory.decodeResource(getResources(), resId);
                matrix = new Matrix();
                matrix.postScale(mScaleX, mScaleY);
                matrix.postTranslate(startX + x, startY);
                canvas.drawBitmap(bmp, matrix, null);
            }
            x += (bmp.getWidth() + mSpacing) * mScaleX;
        }
    }
}
