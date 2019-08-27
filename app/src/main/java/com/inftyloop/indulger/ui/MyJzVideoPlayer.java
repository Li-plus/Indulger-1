package com.inftyloop.indulger.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import com.inftyloop.indulger.listener.VideoStateListener;

import cn.jzvd.JzvdStd;


public class MyJzVideoPlayer extends JzvdStd {

    public MyJzVideoPlayer(Context context) {
        super(context);
    }

    public MyJzVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == cn.jzvd.R.id.thumb || v.getId() == cn.jzvd.R.id.start) {
            if (state == STATE_IDLE || state == STATE_NORMAL) {
                if (mListener != null) {
                    mListener.onStartClick();
                    return;
                }
            }
        }
//        else if (v.getId() == cn.jzvd.R.id.fullscreen) {
//
//        }
        super.onClick(v);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mListener != null) {
            mListener.onTouch();
        }
        return super.onTouch(v, event);
    }

    @Override
    public void startVideo() {
        super.startVideo();
        Log.i(TAG, "startVideo...");
        if (mListener != null) {
            mListener.onStart();
        }
    }

    @Override
    public void onStateNormal() {
        super.onStateNormal();
        if (mListener != null) {
            mListener.onStateNormal();
        }
    }

    @Override
    public void onStatePreparing() {
        super.onStatePreparing();
        Log.i(TAG, "onStatePreparing...");
        if (mListener != null) {
            mListener.onPreparing();
        }
    }

    @Override
    public void onStatePlaying() {
        super.onStatePlaying();
        Log.i(TAG, "onStatePlaying...");
        if (mListener != null) {
            mListener.onPlaying();
        }
    }

    @Override
    public void onStatePause() {
        super.onStatePause();
        Log.i(TAG, "onStatePause...");
        if (mListener != null) {
            mListener.onPause();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        super.onProgressChanged(seekBar, progress, fromUser);
        if (mListener != null) {
            mListener.onProgressChanged(progress);
        }
    }

    @Override
    public void onStateError() {
        super.onStateError();
    }

    @Override
    public void onStateAutoComplete() {
        super.onStateAutoComplete();
        if (mListener != null) {
            mListener.onComplete();
        }
    }

    @Override
    public void onInfo(int what, int extra) {
        super.onInfo(what, extra);
        Log.i(TAG, "onInfo...");
    }

    @Override
    public void onError(int what, int extra) {
        super.onError(what, extra);
    }

    @Override
    public void startDismissControlViewTimer() {
        super.startDismissControlViewTimer();
        Log.i(TAG, "startDismissControlViewTimer...");
        if (mListener != null) {
            mListener.onStartDismissControlViewTimer();
        }
    }

    private VideoStateListener mListener;

    public VideoStateListener getListener() {
        return mListener;
    }

    public void setVideoStateListener(VideoStateListener listener) {
        mListener = listener;
    }
}
