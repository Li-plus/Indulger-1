package com.inftyloop.indulger.listener;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inftyloop.indulger.R;
import com.inftyloop.indulger.ui.MyJzVideoPlayer;

import cn.jzvd.Jzvd;

public class OnChildAttachStateChangeCallback implements RecyclerView.OnChildAttachStateChangeListener {
    @Override
    public void onChildViewAttachedToWindow(@NonNull View view) {
    }

    @Override
    public void onChildViewDetachedFromWindow(@NonNull View view) {
        MyJzVideoPlayer detachJzvd = view.findViewById(R.id.video_player);
        Jzvd curJzvd = Jzvd.CURRENT_JZVD;
        if (detachJzvd != null && detachJzvd.jzDataSource != null &&
                curJzvd != null && curJzvd.jzDataSource != null &&
                detachJzvd.jzDataSource.containsTheUrl(curJzvd.jzDataSource.getCurrentUrl()) &&
                curJzvd.screen != Jzvd.SCREEN_FULLSCREEN) {
            Jzvd.releaseAllVideos();
        }
    }
}
