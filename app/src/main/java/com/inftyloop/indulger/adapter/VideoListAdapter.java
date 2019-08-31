package com.inftyloop.indulger.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.Toast;
import androidx.annotation.NonNull;

import com.bumptech.glide.request.RequestOptions;
import com.inftyloop.indulger.MainApplication;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.listener.VideoStateListenerAdapter;
import com.inftyloop.indulger.model.entity.News;
import com.inftyloop.indulger.ui.MyJzVideoPlayer;
import com.inftyloop.indulger.util.*;
import com.inftyloop.indulger.viewholder.BaseRecyclerViewHolder;

import java.util.List;

import cn.jzvd.JzvdStd;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import org.w3c.dom.Text;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class VideoListAdapter extends BaseRecyclerViewAdapter<News, BaseRecyclerViewHolder> {
    public static final String TAG = VideoListAdapter.class.getSimpleName();

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
        (vh.findViewById(R.id.iv_share)).setOnClickListener(v->{
            Log.w(TAG, "share!");
        });
        ((TextView) vh.findViewById(R.id.tv_title)).setText(news.getNewsEntry().getTitle());
        ((TextView) vh.findViewById(R.id.tv_author)).setText(news.getNewsEntry().getPublisherName());
        if(news.getVideoDuration() > 0) {
            ((TextView) vh.findViewById(R.id.tv_duration)).setText(DateUtils.secToTime(news.getVideoDuration()));
        } else
            vh.findViewById(R.id.ll_duration).setVisibility(GONE);

        MyJzVideoPlayer videoPlayer = vh.findViewById(R.id.video_player);
        if(news.getVideoThumbUrl() != null) {
            GlideImageLoader.loadNormal(mContext, news.getVideoThumbUrl(), videoPlayer.thumbImageView, R.color.color_d8d8d8);
        }

        if(news.getNewsEntry().getPublisherAvatarUrl() != null && !news.getNewsEntry().getPublisherAvatarUrl().isEmpty()) {
            GlideImageLoader.loadRound(mContext, news.getNewsEntry().getPublisherAvatarUrl(), vh.findViewById(R.id.iv_avatar), R.mipmap.ic_launcher_round);
        } else
            vh.findViewById(R.id.iv_avatar).setVisibility(GONE);

        videoPlayer.setAllControlsVisiblity(GONE, GONE, VISIBLE, GONE, VISIBLE, GONE, GONE);
        videoPlayer.tinyBackImageView.setVisibility(GONE);
        videoPlayer.titleTextView.setText("");  // clear title
        videoPlayer.setVideoStateListener(new VideoStateListenerAdapter() {
            private boolean isVideoParsing = false;

            @Override
            public void onStartClick() {
                String videoUrl = "";
                if(news.getParsedVideoUrl() != null)
                    videoUrl = news.getParsedVideoUrl();
                if(!TextUtils.isEmpty(videoUrl)) {
                    videoPlayer.setUp(news.getParsedVideoUrl(), news.getNewsEntry().getTitle(), JzvdStd.SCREEN_NORMAL);
                    videoPlayer.startVideo();
                    return;
                }
                parseVideo();
            }

            private void parseVideo() {
                if(isVideoParsing)
                    return;
                else
                    isVideoParsing = true;
                videoPlayer.setAllControlsVisiblity(GONE, GONE, GONE, VISIBLE, VISIBLE, GONE, GONE);
                vh.findViewById(R.id.ll_duration).setVisibility(View.INVISIBLE);
                vh.findViewById(R.id.ll_title).setVisibility(View.INVISIBLE);
                VideoPathDecoder decoder = new VideoPathDecoder() {
                    @Override
                    public void onSuccess(String url) {
                        Utils.postTaskSafely(()->{
                            isVideoParsing = false;
                            videoPlayer.setUp(url, news.getNewsEntry().getTitle(), JzvdStd.SCREEN_NORMAL);
                            news.setParsedVideoUrl(url);
                            videoPlayer.startVideo();
                        });
                    }

                    @Override
                    public void onDecodeError(String errorMsg) {
                        isVideoParsing = false;
                        videoPlayer.setAllControlsVisiblity(GONE, GONE, VISIBLE, GONE, VISIBLE, GONE, GONE);
                        QMUITipDialog.Builder.makeToast(mContext, QMUITipDialog.Builder.ICON_TYPE_FAIL,
                                mContext.getString(R.string.video_parse_error), Toast.LENGTH_SHORT).show();
                    }
                };
                decoder.decodePath(news.getNewsEntry().getUrl());
            }
        });
    }



    @Override
    public int getItemViewType(int position) {
        return getData().get(position).getType();
    }
}
