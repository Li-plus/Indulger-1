package com.inftyloop.indulger.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.inftyloop.indulger.R;
import com.inftyloop.indulger.activity.MainActivity;
import com.inftyloop.indulger.fragment.NewsDetailFragment;
import com.inftyloop.indulger.model.entity.News;
import com.inftyloop.indulger.viewholder.BaseRecyclerViewHolder;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;
import com.qmuiteam.qmui.util.QMUIResHelper;

import java.util.List;

abstract public class BaseNewsAdapter extends BaseRecyclerViewAdapter<News, BaseRecyclerViewHolder> {
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
            if (item.type != News.SINGLE_IMAGE_NEWS && item.type != News.TEXT_NEWS && item.type != News.THREE_IMAGES_NEWS)
                return;

            ((TextView) vh.findViewById(R.id.tv_title)).setTextColor(QMUIResHelper.getAttrColor(mContext, R.attr.clicked_text_color));

            NewsDetailFragment fragment = new NewsDetailFragment();
            ((QMUIFragmentActivity) mContext).startFragment(fragment);
            Toast.makeText(mContext, "displaying " + getData().get(vh.getAdapterPosition()).title, Toast.LENGTH_SHORT).show();
        });

        return vh;
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

    abstract protected void initCrossIcon(BaseRecyclerViewHolder vh);

    @Override
    public int getItemCount() {
        return getData().size();
    }

    @Override
    public int getItemViewType(int position) {
        return getData().get(position).type;
    }
}
