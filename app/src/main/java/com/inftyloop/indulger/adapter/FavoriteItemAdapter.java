package com.inftyloop.indulger.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.view.View;

import com.inftyloop.indulger.R;
import com.inftyloop.indulger.model.entity.News;
import com.inftyloop.indulger.viewholder.BaseRecyclerViewHolder;

import java.util.List;


public class FavoriteItemAdapter extends BaseNewsAdapter {

    public FavoriteItemAdapter(Activity context, @NonNull List<News> data) {
        super(context, data);
    }

    @Override
    protected void initCrossIcon(BaseRecyclerViewHolder vh) {
        vh.findViewById(R.id.news_list_clear_icon).setOnClickListener((View view) -> {
            removeItemImmediately(vh.getAdapterPosition());
        });
    }
}
