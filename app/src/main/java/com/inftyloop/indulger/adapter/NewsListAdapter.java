package com.inftyloop.indulger.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.method.Touch;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.inftyloop.indulger.R;
import com.inftyloop.indulger.model.entity.News;
import com.inftyloop.indulger.viewholder.BaseRecyclerViewHolder;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.util.List;


public class NewsListAdapter extends RecyclerView.Adapter<BaseRecyclerViewHolder> {
    private final int LOAD_MORE_FOOTER = -1;
    private final int TEXT_NEWS = 0;
    private final int SINGLE_IMAGE_NEWS = 1;
    private final int THREE_IMAGES_NEWS = 2;

    private Context mContext;
    private List<News> mData;

    public NewsListAdapter(Context context, @NonNull List<News> data) {
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
            default:
                vh = new BaseRecyclerViewHolder(viewGroup, R.layout.load_more_footer);
                return vh;
        }

        // init delete icon
        ImageButton deleteButton = (ImageButton) vh.getView(R.id.news_list_clear_icon);
        deleteButton.setOnClickListener((View view) -> {
            new QMUIDialog.MessageDialogBuilder(mContext)
                    .setMessage(mContext.getString(R.string.news_list_confirm_delete))
                    .addAction(mContext.getString(R.string.settings_cancel), (QMUIDialog dialog, int index) -> {
                        dialog.dismiss();
                    })
                    .addAction(0, mContext.getString(R.string.settings_delete), QMUIDialogAction.ACTION_PROP_NEGATIVE,
                            (QMUIDialog dialog, int index) -> {
                                mData.remove(vh.getAdapterPosition());
                                notifyItemRemoved(vh.getAdapterPosition());
                                dialog.dismiss();
                            })
                    .create(R.style.QMUI_Dialog).show();
        });
        final View parent = (View) deleteButton.getParent();
        parent.post(() -> {
            final Rect rect = new Rect();
            deleteButton.getHitRect(rect);
            rect.top -= 50;    // increase top hit area
            rect.left -= 50;   // increase left hit area
            rect.bottom += 50; // increase bottom hit area
            rect.right += 50;  // increase right hit area
            parent.setTouchDelegate(new TouchDelegate(rect, deleteButton));
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerViewHolder vh, int position) {
        if (position >= mData.size())
            return;
        News item = mData.get(position);

        ((TextView) vh.getView(R.id.tv_title)).setText(item.title);
        ((TextView) vh.getView(R.id.tv_author)).setText(item.author);
        ((TextView) vh.getView(R.id.tv_time)).setText(item.time);
        switch (getItemViewType(position)) {
            case SINGLE_IMAGE_NEWS:
                ((ImageView) vh.getView(R.id.iv_img)).setImageResource(item.image1);
                break;
            case THREE_IMAGES_NEWS:
                ((ImageView) vh.getView(R.id.iv_img1)).setImageResource(item.image1);
                ((ImageView) vh.getView(R.id.iv_img2)).setImageResource(item.image2);
                ((ImageView) vh.getView(R.id.iv_img3)).setImageResource(item.image3);
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
