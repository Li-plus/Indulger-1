package com.inftyloop.indulger.listener;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class ScrollingGestureDetector extends GestureDetector.SimpleOnGestureListener {
    public interface GestureListenerCallback {
        void onShow();
        void onHide();
    }

    private GestureListenerCallback mListener;

    public ScrollingGestureDetector(GestureListenerCallback listener) {
        mListener = listener;
    }

    public void setListener(GestureListenerCallback listener) {
        mListener = listener;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(e1 == null || e2 == null) return false;
        if(e1.getPointerCount() > 1 || e2.getPointerCount() > 1) return false;
        else {
            try {
                if(e1.getY() - e2.getY() > 20) {
                    // hide top bar
                    if(mListener != null)
                        mListener.onHide();
                    return false;
                }
                else if(e2.getY() - e1.getY() > 20) {
                    // show top bar
                    if(mListener != null)
                        mListener.onShow();
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
