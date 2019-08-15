package com.inftyloop.indulger.adapter;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import com.inftyloop.indulger.viewholder.BaseRecyclerViewHolder;

import java.util.List;


public class NewsListAdapter extends RecyclerView.Adapter<BaseRecyclerViewHolder> {
    private final int NOTIFICATION_HEADER = -2;
    private final int LOAD_MORE_FOOTER = -1;
    private final int TEXT_NEWS = 0;
    private final int SINGLE_IMAGE_NEWS = 1;
    private final int THREE_IMAGES_NEWS = 2;

    private Activity mContext;
    private List<News> mData;

    public NewsListAdapter(Activity context, @NonNull List<News> data) {
        mContext = context;
        mData = data;
    }

    @NonNull
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int itemType) {
        BaseRecyclerViewHolder vh;
        switch (itemType) {
            case TEXT_NEWS:
                vh = new BaseRecyclerViewHolder(viewGroup, R.layout.item_text_news);
                break;
            case SINGLE_IMAGE_NEWS:
                vh = new BaseRecyclerViewHolder(viewGroup, R.layout.item_pic_video_news);
                break;
            case THREE_IMAGES_NEWS:
                vh = new BaseRecyclerViewHolder(viewGroup, R.layout.item_three_pic_news);
                break;
            case LOAD_MORE_FOOTER:
                vh = new BaseRecyclerViewHolder(viewGroup, R.layout.load_more_footer);
                return vh;
            default:    // header
                vh = new BaseRecyclerViewHolder(viewGroup, R.layout.notification_header);
                return vh;
        }

        // init delete icon
        ImageButton crossIcon = (ImageButton) vh.findViewById(R.id.news_list_clear_icon);
        crossIcon.setOnClickListener((View view) -> {
            View popupView = LayoutInflater.from(mContext).inflate(R.layout.block_popup_layout, null, false);
            PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
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
            popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
            popupWindow.showAsDropDown(crossIcon, 0, 0, Gravity.LEFT);

            Button deleteButton = popupView.findViewById(R.id.popup_delete_button);
            deleteButton.setOnClickListener((View v) -> {
                popupWindow.dismiss();
                mData.remove(vh.getAdapterPosition());
                notifyItemRemoved(vh.getAdapterPosition());
            });
        });

        // init onclick listener
        vh.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vh.getAdapterPosition() >= mData.size() || mData.get(vh.getAdapterPosition()) == null)
                    return;
                Toast.makeText(mContext, "displaying " + mData.get(vh.getAdapterPosition()).title, Toast.LENGTH_SHORT).show();
            }
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
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerViewHolder vh, int position) {
        if (position >= mData.size() || mData.get(position) == null)
            return;
        News item = mData.get(position);

        ((TextView) vh.findViewById(R.id.tv_title)).setText(item.title);
        ((TextView) vh.findViewById(R.id.tv_author)).setText(item.author);
        ((TextView) vh.findViewById(R.id.tv_time)).setText(item.time);
        switch (getItemViewType(position)) {
            case SINGLE_IMAGE_NEWS:
                ((ImageView) vh.findViewById(R.id.iv_img)).setImageResource(item.image1);
                break;
            case THREE_IMAGES_NEWS:
                ((ImageView) vh.findViewById(R.id.iv_img1)).setImageResource(item.image1);
                ((ImageView) vh.findViewById(R.id.iv_img2)).setImageResource(item.image2);
                ((ImageView) vh.findViewById(R.id.iv_img3)).setImageResource(item.image3);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= mData.size())
            return LOAD_MORE_FOOTER;
        if (mData.get(position) == null)
            return NOTIFICATION_HEADER;
        if (mData.get(position).image1 == null)
            return TEXT_NEWS;
        if (mData.get(position).image3 == null)
            return SINGLE_IMAGE_NEWS;
        return THREE_IMAGES_NEWS;
    }

    public List<News> getData() {
        return mData;
    }
}
