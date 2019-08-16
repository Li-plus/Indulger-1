package com.inftyloop.indulger.adapter;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Rect;
import android.support.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.inftyloop.indulger.R;
import com.inftyloop.indulger.model.entity.News;
import com.inftyloop.indulger.util.DisplayHelper;
import com.inftyloop.indulger.viewholder.BaseRecyclerViewHolder;
import com.qmuiteam.qmui.util.QMUIResHelper;

import java.util.List;


public class NewsListAdapter extends BaseRecyclerViewAdapter<News, BaseRecyclerViewHolder> {

    private Activity mContext;

    public NewsListAdapter(Activity context, @NonNull List<News> data) {
        super(data);
        mContext = context;
    }

    @NonNull
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int itemType) {
        BaseRecyclerViewHolder vh;
        switch (itemType) {
            case News.TEXT_NEWS:
                vh = new BaseRecyclerViewHolder(viewGroup, R.layout.item_text_news);
                break;
            case News.SINGLE_IMAGE_NEWS:
                vh = new BaseRecyclerViewHolder(viewGroup, R.layout.item_pic_video_news);
                break;
            case News.THREE_IMAGES_NEWS:
                vh = new BaseRecyclerViewHolder(viewGroup, R.layout.item_three_pic_news);
                break;
            case News.LOAD_MORE_FOOTER:
                vh = new BaseRecyclerViewHolder(viewGroup, R.layout.load_more_footer);
                return vh;
            case News.NOTIFICATION_HEADER:
                vh = new BaseRecyclerViewHolder(viewGroup, R.layout.notification_header);
                return vh;
            default:
                throw new RuntimeException("bad type");
        }

        initCrossIcon(vh);

        // init onclick listener
        vh.getView().setOnClickListener((View view) -> {
            News item = getData().get(vh.getAdapterPosition());
            if (item.type != News.SINGLE_IMAGE_NEWS && item.type != News.TEXT_NEWS && item.type != News.THREE_IMAGES_NEWS)
                return;

            ((TextView) vh.findViewById(R.id.tv_title)).setTextColor(QMUIResHelper.getAttrColor(mContext, R.attr.clicked_text_color));
            Toast.makeText(mContext, "displaying " + getData().get(vh.getAdapterPosition()).title, Toast.LENGTH_SHORT).show();
        });

        return vh;
    }

    private ImageButton initCrossIcon(BaseRecyclerViewHolder vh) {
        ImageButton crossIcon = (ImageButton) vh.findViewById(R.id.news_list_clear_icon);
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
                removeItemImmediately(vh.getAdapterPosition());
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
        return crossIcon;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerViewHolder vh, int position) {
        News item = getData().get(position);

        if (item.type != News.TEXT_NEWS && item.type != News.THREE_IMAGES_NEWS && item.type != News.SINGLE_IMAGE_NEWS)
            return;

        ((TextView) vh.findViewById(R.id.tv_title)).setText(item.title);
        ((TextView) vh.findViewById(R.id.tv_author)).setText(item.author);
        ((TextView) vh.findViewById(R.id.tv_time)).setText(item.time);
        switch (getItemViewType(position)) {
            case News.SINGLE_IMAGE_NEWS:
                ((ImageView) vh.findViewById(R.id.iv_img)).setImageResource(item.image1);
                break;
            case News.THREE_IMAGES_NEWS:
                ((ImageView) vh.findViewById(R.id.iv_img1)).setImageResource(item.image1);
                ((ImageView) vh.findViewById(R.id.iv_img2)).setImageResource(item.image2);
                ((ImageView) vh.findViewById(R.id.iv_img3)).setImageResource(item.image3);
                break;
            default: // text news
                break;
        }
    }

    @Override
    public int getItemCount() {
        return getData().size();
    }

    @Override
    public int getItemViewType(int position) {
        return getData().get(position).type;
    }

}
