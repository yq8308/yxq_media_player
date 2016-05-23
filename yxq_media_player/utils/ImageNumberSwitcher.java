package com.test.yxq.yxq_media_player.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewSwitcher;

import com.test.yxq.yxq_media_player.views.ImageNumber;

/**
 * Created by Administrator on 2016/5/16.
 */
public class ImageNumberSwitcher extends ViewSwitcher {

    ViewFactory mFactory;

    public ImageNumberSwitcher(Context context) {
        super(context);
    }

    public ImageNumberSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (!(child instanceof ImageNumber)) {
            throw new IllegalArgumentException(
                    "ImageNumberSwitcher children must be instances of ImageNumber");
        }

        super.addView(child, index, params);
    }

    public void setValue(int var) {
        final ImageNumber in = (ImageNumber) getNextView();
        in.setValue(var);
        showNext();
    }

    public void setCurrentNum(int var) {
        ((ImageNumber)getCurrentView()).setValue(var);
    }

    public void setFactory(ViewFactory factory) {
        mFactory = factory;
        obtainView();
        obtainView();
    }

    private View obtainView() {
        View child = mFactory.makeView();
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }
        addView(child, lp);
        return child;
    }
}
