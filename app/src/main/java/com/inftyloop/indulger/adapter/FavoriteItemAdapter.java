package com.inftyloop.indulger.adapter;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;

import com.inftyloop.indulger.R;
import com.inftyloop.indulger.model.entity.News;
import com.inftyloop.indulger.model.entity.NewsEntry;
import com.inftyloop.indulger.viewholder.BaseRecyclerViewHolder;

import java.util.List;


public class FavoriteItemAdapter extends BaseNewsAdapter {

    public FavoriteItemAdapter(Activity context, @NonNull List<News> data) {
        super(context, data);
    }

    @Override
    protected void initCrossIcon(BaseRecyclerViewHolder vh) {
        vh.findViewById(R.id.news_list_clear_icon).setOnClickListener((View view) -> {
            int position = vh.getAdapterPosition();
            NewsEntry newsEntry = getData().get(position).getNewsEntry();
            newsEntry.setToDefault("isFavorite");
            newsEntry.updateAll("uuid = ?", newsEntry.getUuid());
            removeItemImmediately(position);
        });
    }
}
