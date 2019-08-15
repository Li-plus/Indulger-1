package com.inftyloop.indulger.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ViewPagerBugFixed extends android.support.v4.view.ViewPager {

    public ViewPagerBugFixed(Context context) {
        super(context);
    }

    public ViewPagerBugFixed(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}