package com.inftyloop.indulger.adapter;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.request.RequestOptions;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.listener.VideoStateListenerAdapter;
import com.inftyloop.indulger.model.entity.News;
import com.inftyloop.indulger.ui.MyJzVideoPlayer;
import com.inftyloop.indulger.util.GlideApp;
import com.inftyloop.indulger.viewholder.BaseRecyclerViewHolder;

import java.util.List;

import cn.jzvd.JzvdStd;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class VideoListAdapter extends BaseRecyclerViewAdapter<News, BaseRecyclerViewHolder> {

    private Context mContext;

    public VideoListAdapter(Context context, @NonNull List<News> data) {
        super(data);
        mContext = context;
    }

    @NonNull
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int itemType) {
        BaseRecyclerViewHolder vh;
        switch (itemType) {
            case News.LOAD_MORE_FOOTER:
                vh = new BaseRecyclerViewHolder(viewGroup, R.layout.load_more_footer);
                return vh;
            case News.VIDEO_NEWS:
                vh = new BaseRecyclerViewHolder(viewGroup, R.layout.item_video_list);
                return vh;
            default:
                vh = new BaseRecyclerViewHolder(viewGroup, R.layout.no_more_footer);
                return vh;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerViewHolder vh, int position) {
        if (getItemViewType(position) != News.VIDEO_NEWS)
            return;
        News news = getData().get(position);

        vh.findViewById(R.id.ll_title).setVisibility(VISIBLE);
        vh.findViewById(R.id.ll_duration).setVisibility(VISIBLE);
        ((TextView) vh.findViewById(R.id.tv_title)).setText(news.getNewsEntry().getTitle());
        ((TextView) vh.findViewById(R.id.tv_duration)).setText("2:00");
        ((TextView) vh.findViewById(R.id.tv_author)).setText(news.getNewsEntry().getPublisherName());

        MyJzVideoPlayer videoPlayer = vh.findViewById(R.id.video_player);

        GlideApp.with(mContext)
                .setDefaultRequestOptions(new RequestOptions().centerCrop())
                .load(news.getNewsEntry().getVideoUrl())
                .into(videoPlayer.thumbImageView); // pic
//        Glide.with(mContext).load(news.getNewsEntry().getPublisherAvatarUrl()).into((ImageView) vh.findViewById(R.id.iv_avatar));

        videoPlayer.setAllControlsVisiblity(GONE, GONE, VISIBLE, GONE, VISIBLE, GONE, GONE);
        videoPlayer.tinyBackImageView.setVisibility(GONE);
        videoPlayer.titleTextView.setText("");  // clear title
        videoPlayer.setVideoStateListener(new VideoStateListenerAdapter() {
            @Override
            public void onStartClick() {
                videoPlayer.setUp(news.getNewsEntry().getVideoUrl(), news.getNewsEntry().getTitle(), JzvdStd.SCREEN_NORMAL);
                videoPlayer.startVideo();

                videoPlayer.setAllControlsVisiblity(GONE, GONE, GONE, VISIBLE, VISIBLE, GONE, GONE);
                vh.findViewById(R.id.ll_duration).setVisibility(GONE);
                vh.findViewById(R.id.ll_title).setVisibility(GONE);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return getData().get(position).getType();
    }
}
