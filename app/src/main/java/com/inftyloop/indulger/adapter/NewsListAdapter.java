package com.inftyloop.indulger.adapter;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Rect;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;

import com.inftyloop.indulger.R;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.model.entity.News;
import com.inftyloop.indulger.util.ConfigManager;
import com.inftyloop.indulger.util.DisplayHelper;
import com.inftyloop.indulger.viewholder.BaseRecyclerViewHolder;

import java.util.HashSet;
import java.util.List;


public class NewsListAdapter extends BaseNewsAdapter {
    public static final String TAG = NewsListAdapter.class.getSimpleName();

    public NewsListAdapter(Activity context, @NonNull List<News> data) {
        super(context, data);
    }

    @Override
    protected void initCrossIcon(BaseRecyclerViewHolder vh) {
        ImageButton crossIcon = vh.findViewById(R.id.news_list_clear_icon);

        crossIcon.setOnClickListener((View view) -> {
            View popupView = LayoutInflater.from(mContext).inflate(R.layout.block_popup_layout, null);

            PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setTouchable(true);
            popupWindow.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return false;
                }
            });
            final Window window = mContext.getWindow();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(1f, 0.5f);
            valueAnimator.setDuration(500);
            valueAnimator.addUpdateListener((ValueAnimator animation) -> {
                WindowManager.LayoutParams params = window.getAttributes();
                params.alpha = (Float) animation.getAnimatedValue();
                window.setAttributes(params);
            });
            valueAnimator.start();
            popupWindow.setOnDismissListener(() -> {
                WindowManager.LayoutParams params = window.getAttributes();
                params.alpha = 1f;
                valueAnimator.cancel();
                window.setAttributes(params);
            });

            int top = DisplayHelper.getLocationOnScreen(crossIcon).y;

            popupWindow.showAtLocation(crossIcon, Gravity.TOP | Gravity.START, 0, 100);

            if (top + crossIcon.getHeight() + DisplayHelper.getMeasuredHeight(popupView) > DisplayHelper.getScreenHeight(mContext)) {
                popupWindow.update(0, top - DisplayHelper.getMeasuredHeight(popupView), popupWindow.getWidth(), popupWindow.getHeight());
            } else {
                popupWindow.update(0, top + crossIcon.getHeight(), popupWindow.getWidth(), popupWindow.getHeight());
            }

            Button deleteButton = popupView.findViewById(R.id.popup_delete_button);
            deleteButton.setOnClickListener((View v) -> {
                popupWindow.dismiss();
                HashSet<String> blockKeys = (HashSet<String>) ConfigManager.getStringSet(Definition.BLOCKED_KEYS, new HashSet<>());
                blockKeys.addAll(getData().get(vh.getAdapterPosition()).getNewsEntry().getKeywords());
                ConfigManager.putStringSetNow(Definition.BLOCKED_KEYS, blockKeys);
                removeItemImmediately(vh.getAdapterPosition());
                for (String keyword : blockKeys) {
                    Log.d(TAG, "add blocking keyword " + keyword);
                }
            });
        });

        // expand cross icon hit area
        final View parent = (View) crossIcon.getParent();
        parent.post(() -> {
            final Rect rect = new Rect();
            crossIcon.getHitRect(rect);
            rect.top -= 50;    // increase top hit area
            rect.left -= 50;   // increase left hit area
            rect.bottom += 50; // increase bottom hit area
            rect.right += 50;  // increase right hit area
            parent.setTouchDelegate(new TouchDelegate(rect, crossIcon));
        });
    }
}
