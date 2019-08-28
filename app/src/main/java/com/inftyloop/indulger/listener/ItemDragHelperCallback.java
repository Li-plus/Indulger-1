package com.inftyloop.indulger.listener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;

public class ItemDragHelperCallback extends ItemTouchHelper.Callback {
    private OnNewsTypeDragListener onNewsTypeDragListener;

    public ItemDragHelperCallback(OnNewsTypeDragListener l) { onNewsTypeDragListener = l; }

    public void setOnNewsTypeDragListener(OnNewsTypeDragListener l) { onNewsTypeDragListener = l; }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        RecyclerView.LayoutManager man = recyclerView.getLayoutManager();
        int flags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        if(man instanceof GridLayoutManager || man instanceof StaggeredGridLayoutManager)
            flags |= (ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        return makeMovementFlags(flags, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        if(viewHolder.getItemViewType() != viewHolder1.getItemViewType())
            return false;
        if(onNewsTypeDragListener != null)
            onNewsTypeDragListener.onItemMove(viewHolder.getAdapterPosition(), viewHolder1.getAdapterPosition());
        return true;
    }

    @Override
    public boolean isLongPressDragEnabled() { return false; }

    @Override
    public boolean isItemViewSwipeEnabled() { return false; }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {}
}
