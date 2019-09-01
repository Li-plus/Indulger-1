package com.inftyloop.indulger.adapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.Toast;
import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.inftyloop.indulger.BuildConfig;
import com.inftyloop.indulger.MainApplication;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.activity.MainActivity;
import com.inftyloop.indulger.listener.VideoStateListenerAdapter;
import com.inftyloop.indulger.model.entity.News;
import com.inftyloop.indulger.ui.MyJzVideoPlayer;
import com.inftyloop.indulger.util.*;
import com.inftyloop.indulger.viewholder.BaseRecyclerViewHolder;

import java.util.List;

import cn.jzvd.JzvdStd;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.inftyloop.indulger.util.ShareUtils;
import org.w3c.dom.Text;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.inftyloop.indulger.MainApplication.getContext;


public class VideoListAdapter extends BaseRecyclerViewAdapter<News, BaseRecyclerViewHolder> {
    public static final String TAG = VideoListAdapter.class.getSimpleName();

    private Context mContext;
    private ShareUtils shareUtils;

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

        shareUtils = new ShareUtils();
        shareUtils.regToWX(mContext);

        MyJzVideoPlayer videoPlayer = vh.findViewById(R.id.video_player);
        final Bitmap[] mMainImage = new Bitmap[1];
        if(news.getVideoThumbUrl() != null) {
            GlideApp.with(mContext)
                    .load(news.getVideoThumbUrl())
                    .placeholder(R.color.color_d8d8d8)
                    .into(videoPlayer.thumbImageView);
            GlideApp.with(mContext)
                    .asBitmap()
                    .load(news.getVideoThumbUrl())
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            mMainImage[0] = resource;
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {}
                    });
        }

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
        vh.findViewById(R.id.iv_share).setOnClickListener((v)->{
            final int TAG_SHARE_WECHAT_FRIEND = 0;
            final int TAG_SHARE_WECHAT_MOMENT = 1;
            final int TAG_SHARE_WEIBO = 2;
            QMUIBottomSheet.BottomGridSheetBuilder builder = new QMUIBottomSheet.BottomGridSheetBuilder(mContext);
            builder.addItem(R.mipmap.icon_more_operation_share_friend, mContext.getString(R.string.share_wechat_friend), TAG_SHARE_WECHAT_FRIEND, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                    .addItem(R.mipmap.icon_more_operation_share_moment, mContext.getString(R.string.share_wechat_moment), TAG_SHARE_WECHAT_MOMENT, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                    .addItem(R.mipmap.icon_more_operation_share_weibo, mContext.getString(R.string.share_weibo), TAG_SHARE_WEIBO, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                    .setOnSheetItemClickListener((QMUIBottomSheet dialog, View itemView) -> {
                        dialog.dismiss();
                        int tag = (int) itemView.getTag();
                        switch (tag) {
                            case TAG_SHARE_WECHAT_FRIEND:
                                if (shareUtils.getIwxapi() != null) {
                                    if (!shareUtils.getIwxapi().isWXAppInstalled()) {
                                        QMUITipDialog.Builder.makeToast(getContext(), QMUITipDialog.Builder.ICON_TYPE_NOTHING, mContext.getString(R.string.share_wechat_not_installed), Toast.LENGTH_SHORT).show();
                                    } else {
                                        shareUtils.shareToWeChatFriends(news.getNewsEntry().getUrl(), news.getNewsEntry().getTitle(), "", mMainImage[0]);
                                    }
                                }
                                break;
                            case TAG_SHARE_WECHAT_MOMENT:
                                if (shareUtils.getIwxapi() != null) {
                                    if (!shareUtils.getIwxapi().isWXAppInstalled()) {
                                        QMUITipDialog.Builder.makeToast(getContext(), QMUITipDialog.Builder.ICON_TYPE_NOTHING, mContext.getString(R.string.share_wechat_not_installed), Toast.LENGTH_SHORT).show();
                                    } else {
                                        shareUtils.shareToWeChatMoments(news.getNewsEntry().getUrl(), news.getNewsEntry().getTitle(), "", mMainImage[0]);
                                    }
                                }
                                break;
                            case TAG_SHARE_WEIBO:
                                if(mContext instanceof MainActivity) {
                                    MainActivity ac = (MainActivity)mContext;
                                    StringBuilder bs = new StringBuilder();
                                    bs.append(news.getNewsEntry().getTitle());
                                    bs.append("\n");
                                    bs.append(mContext.getString(R.string.original_article_link, news.getNewsEntry().getUrl()));
                                    ShareUtils.shareToWeibo(ac.shareHandler, bs.toString(), mMainImage[0]);
                                }
                                break;
                        }
                    }).build().show();
        });

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
                String url = news.getNewsEntry().getVideoUrl();
                Log.e(TAG, url);
                if(url.endsWith(".mp4")) {
                    isVideoParsing = false;
                    Log.e(TAG, "Can directly play, not parsing");
                    videoPlayer.setUp(url, news.getNewsEntry().getTitle(), JzvdStd.SCREEN_NORMAL);
                    news.setParsedVideoUrl(url);
                    videoPlayer.startVideo();
                    return;
                }
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
                        Log.e(TAG, url);
                        videoPlayer.setAllControlsVisiblity(GONE, GONE, VISIBLE, GONE, VISIBLE, GONE, GONE);
                        QMUITipDialog.Builder.makeToast(mContext, QMUITipDialog.Builder.ICON_TYPE_FAIL,
                                mContext.getString(R.string.video_parse_error), Toast.LENGTH_SHORT).show();
                    }
                };
                decoder.decodePath(url);
            }
        });
    }



    @Override
    public int getItemViewType(int position) {
        return getData().get(position).getType();
    }
}
