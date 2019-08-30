package com.inftyloop.indulger.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.inftyloop.indulger.R;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.fragment.NewsDetailFragment;
import com.inftyloop.indulger.model.entity.News;
import com.inftyloop.indulger.model.entity.NewsFavEntry;
import com.inftyloop.indulger.util.DateUtils;
import com.inftyloop.indulger.util.GlideImageLoader;
import com.inftyloop.indulger.viewholder.BaseRecyclerViewHolder;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;
import com.qmuiteam.qmui.util.QMUIResHelper;

import java.util.List;


public class FavoriteItemAdapter extends BaseNewsAdapter {
    public static NewsFavEntry currentFavEntry = null;

    public FavoriteItemAdapter(Activity context, @NonNull List<News> data) {
        super(context, data);
    }

    @Override
    protected void initCrossIcon(BaseRecyclerViewHolder vh) {
        vh.findViewById(R.id.news_list_clear_icon).setOnClickListener((View view) -> {
            int position = vh.getAdapterPosition();
            NewsFavEntry newsEntry = getData().get(position).getFavEntry();
            newsEntry.deleteAsync();
            removeItemImmediately(position);
        });
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
            default: // footer no more
                vh = new BaseRecyclerViewHolder(viewGroup, R.layout.no_more_footer);
                return vh;
        }
        // init cross icon
        initCrossIcon(vh);

        // init onclick listener
        vh.getView().setOnClickListener((View view) -> {
            News item = getData().get(vh.getAdapterPosition());
            item.setIsRead(true);

            ((TextView) vh.findViewById(R.id.tv_title)).setTextColor(QMUIResHelper.getAttrColor(mContext, R.attr.clicked_text_color));
            currentFavEntry = item.getFavEntry();

            Bundle bundle = new Bundle();
            bundle.putBoolean(Definition.IS_FAV_ADAPTER, true);
            NewsDetailFragment fragment = new NewsDetailFragment();
            fragment.setArguments(bundle);
            ((QMUIFragmentActivity) mContext).startFragment(fragment);
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerViewHolder vh, int position) {
        News item = getData().get(position);
        if (item.getType() != News.TEXT_NEWS && item.getType() != News.THREE_IMAGES_NEWS && item.getType() != News.SINGLE_IMAGE_NEWS)
            return;

        if (item.getIsRead()) {
            ((TextView) vh.findViewById(R.id.tv_title)).setTextColor(QMUIResHelper.getAttrColor(mContext, R.attr.clicked_text_color));
        } else {
            ((TextView) vh.findViewById(R.id.tv_title)).setTextColor(QMUIResHelper.getAttrColor(mContext, R.attr.foreground_text_color));
        }
        ((TextView) vh.findViewById(R.id.tv_title)).setText(item.getFavEntry().getTitle());
        ((TextView) vh.findViewById(R.id.tv_author)).setText(item.getFavEntry().getPublisherName());
        ((TextView) vh.findViewById(R.id.tv_time)).setText(DateUtils.getShortTime(mContext, item.getFavEntry().getPublishTime()));
        ((TextView) vh.findViewById(R.id.tv_fav_time)).setText(mContext.getString(R.string.faved_prefix, DateUtils.getShortTime(mContext, item.getFavEntry().getMarkFavoriteTime())).toLowerCase());
        switch (getItemViewType(position)) {
            case News.SINGLE_IMAGE_NEWS:
                GlideImageLoader.create(vh.findViewById(R.id.iv_img)).loadImage(item.getFavEntry().getImgUrls().get(0), R.color.placeholder_color, null);
                break;
            case News.THREE_IMAGES_NEWS:
                GlideImageLoader.create(vh.findViewById(R.id.iv_img1)).loadImage(item.getFavEntry().getImgUrls().get(0), R.color.placeholder_color, null);
                GlideImageLoader.create(vh.findViewById(R.id.iv_img2)).loadImage(item.getFavEntry().getImgUrls().get(1), R.color.placeholder_color, null);
                GlideImageLoader.create(vh.findViewById(R.id.iv_img3)).loadImage(item.getFavEntry().getImgUrls().get(2), R.color.placeholder_color, null);
                break;
            default: // text news
                break;
        }
    }
}
