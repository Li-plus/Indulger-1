package com.inftyloop.indulger.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;

import cn.jzvd.JzvdStd;
import com.bumptech.glide.request.RequestOptions;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.fragment.NewsDetailFragment;
import com.inftyloop.indulger.listener.VideoStateListenerAdapter;
import com.inftyloop.indulger.model.entity.News;
import com.inftyloop.indulger.model.entity.NewsFavEntry;
import com.inftyloop.indulger.ui.MyJzVideoPlayer;
import com.inftyloop.indulger.util.DateUtils;
import com.inftyloop.indulger.util.GlideApp;
import com.inftyloop.indulger.util.GlideImageLoader;
import com.inftyloop.indulger.viewholder.BaseRecyclerViewHolder;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;
import com.qmuiteam.qmui.util.QMUIResHelper;
import org.litepal.LitePal;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


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
            LitePal.deleteAll(NewsFavEntry.class, "uuid = ?", newsEntry.getUuid());
            removeItemImmediately(position);
        });
    }

    @NonNull
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int itemType) {
        BaseRecyclerViewHolder vh;
        switch (itemType) {
            case News.VIDEO_NEWS:
                vh = new BaseRecyclerViewHolder(viewGroup, R.layout.item_center_video_news);
                break;
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
        News news = getData().get(position);

        if (news.getType() != News.TEXT_NEWS && news.getType() != News.THREE_IMAGES_NEWS
                && news.getType() != News.SINGLE_IMAGE_NEWS && news.getType() != News.VIDEO_NEWS) {
            return;
        }

        if (news.getIsRead()) {
            ((TextView) vh.findViewById(R.id.tv_title)).setTextColor(QMUIResHelper.getAttrColor(mContext, R.attr.clicked_text_color));
        } else {
            ((TextView) vh.findViewById(R.id.tv_title)).setTextColor(QMUIResHelper.getAttrColor(mContext, R.attr.foreground_text_color));
        }
        ((TextView) vh.findViewById(R.id.tv_title)).setText(news.getFavEntry().getTitle());
        ((TextView) vh.findViewById(R.id.tv_author)).setText(news.getFavEntry().getPublisherName());
        ((TextView) vh.findViewById(R.id.tv_time)).setText(DateUtils.getShortTime(mContext, news.getFavEntry().getPublishTime()));
        ((TextView) vh.findViewById(R.id.tv_fav_time)).setText(mContext.getString(R.string.faved_prefix, DateUtils.getShortTime(mContext, news.getFavEntry().getMarkFavoriteTime())).toLowerCase());
        switch (getItemViewType(position)) {
            case News.SINGLE_IMAGE_NEWS:
                GlideImageLoader.create(vh.findViewById(R.id.iv_img)).loadImage(news.getFavEntry().getImageUrls().get(0), R.color.placeholder_color, null);
                break;
            case News.THREE_IMAGES_NEWS:
                GlideImageLoader.create(vh.findViewById(R.id.iv_img1)).loadImage(news.getFavEntry().getImageUrls().get(0), R.color.placeholder_color, null);
                GlideImageLoader.create(vh.findViewById(R.id.iv_img2)).loadImage(news.getFavEntry().getImageUrls().get(1), R.color.placeholder_color, null);
                GlideImageLoader.create(vh.findViewById(R.id.iv_img3)).loadImage(news.getFavEntry().getImageUrls().get(2), R.color.placeholder_color, null);
                break;
            case News.VIDEO_NEWS:
                MyJzVideoPlayer videoPlayer = vh.findViewById(R.id.video_player);
                GlideApp.with(mContext)
                        .setDefaultRequestOptions(new RequestOptions().centerCrop())
                        .load(news.getFavEntry().getVideoUrl())
                        .into(videoPlayer.thumbImageView); // pic
                videoPlayer.setAllControlsVisiblity(GONE, GONE, VISIBLE, GONE, VISIBLE, GONE, GONE);
                videoPlayer.tinyBackImageView.setVisibility(GONE);
                videoPlayer.titleTextView.setText("");  // clear title
                videoPlayer.setVideoStateListener(new VideoStateListenerAdapter() {
                    @Override
                    public void onStartClick() {
                        videoPlayer.setUp(news.getFavEntry().getVideoUrl(), "", JzvdStd.SCREEN_NORMAL);
                        videoPlayer.startVideo();
                        videoPlayer.setAllControlsVisiblity(GONE, GONE, GONE, VISIBLE, VISIBLE, GONE, GONE);
                    }
                });
                break;
            default: // text news
                break;
        }
    }
}
