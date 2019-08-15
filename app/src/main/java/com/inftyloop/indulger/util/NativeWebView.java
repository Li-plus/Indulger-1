package com.inftyloop.indulger.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.WebView;

public class NativeWebView extends WebView {
    private GestureDetector gestureDetector;

    public NativeWebView(Context ctx) {
        super(ctx);
    }

    public NativeWebView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
    }

    public NativeWebView(Context ctx, AttributeSet attrs, int defStyle) {
        super(ctx, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(gestureDetector != null)
            return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
        else
            return super.onTouchEvent(event);
    }

    public void setGestureDetector(GestureDetector g) {
        gestureDetector = g;
    }
}
