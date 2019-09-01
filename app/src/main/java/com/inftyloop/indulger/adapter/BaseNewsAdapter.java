package com.inftyloop.indulger.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.request.RequestOptions;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.fragment.NewsDetailFragment;
import com.inftyloop.indulger.listener.VideoStateListenerAdapter;
import com.inftyloop.indulger.model.entity.News;
import com.inftyloop.indulger.model.entity.NewsEntry;
import com.inftyloop.indulger.model.entity.RecommendWords;
import com.inftyloop.indulger.ui.MyJzVideoPlayer;
import com.inftyloop.indulger.util.ConfigManager;
import com.inftyloop.indulger.util.DateUtils;
import com.inftyloop.indulger.util.GlideApp;
import com.inftyloop.indulger.util.GlideImageLoader;
import com.inftyloop.indulger.viewholder.BaseRecyclerViewHolder;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;
import com.qmuiteam.qmui.util.QMUIResHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import cn.jzvd.JzvdStd;
import org.litepal.LitePal;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


abstract public class BaseNewsAdapter extends BaseRecyclerViewAdapter<News, BaseRecyclerViewHolder> {
    public static NewsEntry currentNewsEntry = new NewsEntry();
    protected Activity mContext;

    public BaseNewsAdapter(Activity context, List<News> data) {
        super(data);
        mContext = context;
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
            // update recommendation
            boolean in = false;
            for(String word : item.getNewsEntry().getKeywords()) {
                if(in)
                    break;
                in = true;
                RecommendWords entry = LitePal.where("word = ?", word).findFirst(RecommendWords.class);
                if(entry == null) {
                    entry = new RecommendWords();
                    entry.setCnt(1);
                    entry.setWord(word);
                } else
                    entry.setCnt(entry.getCnt() + 1);
                entry.save();
            }

            ((TextView) vh.findViewById(R.id.tv_title)).setTextColor(QMUIResHelper.getAttrColor(mContext, R.attr.clicked_text_color));
            currentNewsEntry = item.getNewsEntry();

            NewsDetailFragment fragment = new NewsDetailFragment();
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

        ((TextView) vh.findViewById(R.id.tv_title)).setText(news.getNewsEntry().getTitle());
        ((TextView) vh.findViewById(R.id.tv_author)).setText(news.getNewsEntry().getPublisherName());
        ((TextView) vh.findViewById(R.id.tv_time)).setText(DateUtils.getShortTime(mContext, news.getNewsEntry().getPublishTime()));
        vh.findViewById(R.id.tv_fav_time).setVisibility(GONE);

        switch (getItemViewType(position)) {
            case News.SINGLE_IMAGE_NEWS:
                GlideImageLoader.create(vh.findViewById(R.id.iv_img)).loadImage(news.getNewsEntry().getImageUrls().get(0), R.color.placeholder_color, null);
                break;
            case News.THREE_IMAGES_NEWS:
                GlideImageLoader.create(vh.findViewById(R.id.iv_img1)).loadImage(news.getNewsEntry().getImageUrls().get(0), R.color.placeholder_color, null);
                GlideImageLoader.create(vh.findViewById(R.id.iv_img2)).loadImage(news.getNewsEntry().getImageUrls().get(1), R.color.placeholder_color, null);
                GlideImageLoader.create(vh.findViewById(R.id.iv_img3)).loadImage(news.getNewsEntry().getImageUrls().get(2), R.color.placeholder_color, null);
                break;
            case News.VIDEO_NEWS:
                MyJzVideoPlayer videoPlayer = vh.findViewById(R.id.video_player);
                GlideApp.with(mContext)
                        .setDefaultRequestOptions(new RequestOptions().centerCrop())
                        .load(news.getNewsEntry().getVideoUrl())
                        .into(videoPlayer.thumbImageView); // pic
                videoPlayer.setAllControlsVisiblity(GONE, GONE, VISIBLE, GONE, VISIBLE, GONE, GONE);
                videoPlayer.tinyBackImageView.setVisibility(GONE);
                videoPlayer.titleTextView.setText("");  // clear title
                videoPlayer.setVideoStateListener(new VideoStateListenerAdapter() {
                    @Override
                    public void onStartClick() {
                        videoPlayer.setUp(news.getNewsEntry().getVideoUrl(), "", JzvdStd.SCREEN_NORMAL);
                        videoPlayer.startVideo();
                        videoPlayer.setAllControlsVisiblity(GONE, GONE, GONE, VISIBLE, VISIBLE, GONE, GONE);
                    }
                });
                break;
            default: // text news
                break;
        }
    }

    abstract protected void initCrossIcon(BaseRecyclerViewHolder vh);

    @Override
    public int getItemViewType(int position) {
        return getData().get(position).getType();
    }
}
