package com.inftyloop.indulger.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.inftyloop.indulger.R;
import com.inftyloop.indulger.model.entity.News;
import com.inftyloop.indulger.viewholder.BaseRecyclerViewHolder;

import java.util.List;


public class FavoriteItemAdapter extends BaseRecyclerViewAdapter<News, BaseRecyclerViewHolder> {

    private Activity mContext;

    public FavoriteItemAdapter(Activity context, @NonNull List<News> data) {
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
            case News.NO_MORE_FOOTER:    // no more
                vh = new BaseRecyclerViewHolder(viewGroup, R.layout.no_more_footer);
                return vh;
            default:
                throw new RuntimeException("bad type");
        }

        // set cross icon gone
        vh.findViewById(R.id.news_list_clear_icon).setOnClickListener((View view) -> {
            removeItemImmediately(vh.getAdapterPosition());
        });

        // init onclick listener
        vh.getView().setOnClickListener((View view) -> {
            if (vh.getAdapterPosition() >= getData().size() || getData().get(vh.getAdapterPosition()) == null)
                return;
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

    @Override
    public int getItemCount() {
        return getData().size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).type;
    }
}
