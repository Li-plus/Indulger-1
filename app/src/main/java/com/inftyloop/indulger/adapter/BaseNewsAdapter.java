package com.inftyloop.indulger.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.fragment.NewsDetailFragment;
import com.inftyloop.indulger.model.entity.News;
import com.inftyloop.indulger.model.entity.NewsEntry;
import com.inftyloop.indulger.util.ConfigManager;
import com.inftyloop.indulger.util.DateUtils;
import com.inftyloop.indulger.viewholder.BaseRecyclerViewHolder;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;
import com.qmuiteam.qmui.util.QMUIResHelper;

import java.util.List;

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
                ((TextView) vh.findViewById(R.id.notification_header_text)).setText(String.format(mContext.getString(R.string.news_list_notification), ConfigManager.getInt("update_news_num", 35)));
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
            currentNewsEntry = item.getNewsEntry();

            NewsDetailFragment fragment = new NewsDetailFragment();
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
        ((TextView) vh.findViewById(R.id.tv_title)).setText(item.getNewsEntry().getTitle());
        ((TextView) vh.findViewById(R.id.tv_author)).setText(item.getNewsEntry().getPublisherName());
        ((TextView) vh.findViewById(R.id.tv_time)).setText(DateUtils.getShortTime(mContext, item.getNewsEntry().getPublishTime()));
        switch (getItemViewType(position)) {
            case News.SINGLE_IMAGE_NEWS:
                Glide.with(vh.getView()).load(item.getNewsEntry().getImageUrls().get(0)).into((ImageView) vh.findViewById(R.id.iv_img));
                break;
            case News.THREE_IMAGES_NEWS:
                Glide.with(vh.getView()).load(item.getNewsEntry().getImageUrls().get(0)).into((ImageView) vh.findViewById(R.id.iv_img1));
                Glide.with(vh.getView()).load(item.getNewsEntry().getImageUrls().get(1)).into((ImageView) vh.findViewById(R.id.iv_img2));
                Glide.with(vh.getView()).load(item.getNewsEntry().getImageUrls().get(2)).into((ImageView) vh.findViewById(R.id.iv_img3));
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
