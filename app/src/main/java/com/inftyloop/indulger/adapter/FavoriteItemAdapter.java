package com.inftyloop.indulger.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.inftyloop.indulger.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FavoriteItemAdapter extends BaseRecyclerViewAdapter {
    List<Map<String, Object>> mData;

    public FavoriteItemAdapter(List<Map<String, Object>> data) {
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FavoriteItemViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FavoriteItemViewHolder viewHolder = (FavoriteItemViewHolder) holder;
        final Map<String, Object> item = mData.get(position);

        viewHolder.viewDate.setText((String) item.get("date"));
        viewHolder.viewImage.setImageResource((int) item.get("img"));
        viewHolder.viewPress.setText((String) item.get("press"));
        viewHolder.viewTitle.setText((String) item.get("title"));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void remove(int position) {
        Map<String, Object> item = mData.get(position);
        if (mData.contains(item)) {
            mData.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void add(int imageSource, String title, String press, String date) {
        Map<String, Object> map = new HashMap<>();
        map.put("img", imageSource);
        map.put("title", title);
        map.put("press", press);
        map.put("date", date);
        mData.add(map);
        notifyDataSetChanged();
    }

    class FavoriteItemViewHolder extends RecyclerView.ViewHolder {
        ImageView viewImage;
        TextView viewTitle;
        TextView viewDate;
        TextView viewPress;

        FavoriteItemViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_item, parent, false));
            viewTitle = itemView.findViewById(R.id.title);
            viewDate = itemView.findViewById(R.id.date);
            viewImage = itemView.findViewById(R.id.img);
            viewPress = itemView.findViewById(R.id.press);
        }
    }
}