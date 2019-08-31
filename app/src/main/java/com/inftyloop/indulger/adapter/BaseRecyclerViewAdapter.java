package com.inftyloop.indulger.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

abstract public class BaseRecyclerViewAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected List<T> mData;

    public BaseRecyclerViewAdapter(@NonNull List<T> data) {
        mData = data;
    }

    public List<T> getData() {
        return mData;
    }

    public void insertItemImmediately(int position, T item) {
        mData.add(position, item);
        notifyItemInserted(position);
    }

    public void insertItemImmediately(T item) {
        mData.add(item);
        notifyItemInserted(mData.size() - 1);
    }

    public T removeItemImmediately(int position) {
        T bak = mData.remove(position);
        notifyItemRemoved(position);
        return bak;
    }

    public void clearAll() {
        mData.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
