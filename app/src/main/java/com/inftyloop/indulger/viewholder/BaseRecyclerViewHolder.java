package com.inftyloop.indulger.viewholder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class BaseRecyclerViewHolder extends RecyclerView.ViewHolder {
    public BaseRecyclerViewHolder(@NonNull ViewGroup parent, int layoutResId) {
        super(LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false));
    }

    public <T extends View> T findViewById(int resId) {
        return itemView.findViewById(resId);
    }

    public View getView() {
        return itemView;
    }
}
