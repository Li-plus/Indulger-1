package com.inftyloop.indulger.listener;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.TypedValue;
import android.view.View;

import com.inftyloop.indulger.R;
import com.inftyloop.indulger.adapter.BaseRecyclerViewAdapter;

public class SwipeCallback extends ItemTouchHelper.SimpleCallback {
    // we want to cache these and not allocate anything repeatedly in the onChildDraw method
    Drawable background;
    Drawable xMark;
    int xMarkMargin;
    RecyclerView mRecyclerView;

    public SwipeCallback(Context context, RecyclerView recyclerView) {
        super(0, ItemTouchHelper.LEFT);
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.app_background_color, outValue, false);
        background = context.getResources().getDrawable(R.drawable.swipe_clear_background, context.getTheme());

        xMark = ContextCompat.getDrawable(context, R.drawable.ic_clear_24dp);
        xMark.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        xMarkMargin = (int) context.getResources().getDimension(R.dimen.ic_clear_margin);
        mRecyclerView = recyclerView;
    }

    // not important, we don't want drag & drop
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
        int swipedPosition = viewHolder.getAdapterPosition();
        BaseRecyclerViewAdapter adapter = (BaseRecyclerViewAdapter) mRecyclerView.getAdapter();
        adapter.remove(swipedPosition);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;

        // not sure why, but this method get's called for viewholder that are already swiped away
        if (viewHolder.getAdapterPosition() == -1) {
            // not interested in those
            return;
        }

        // draw red background
        background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        background.draw(c);

        // draw x mark
        int itemHeight = itemView.getBottom() - itemView.getTop();
        int intrinsicWidth = xMark.getIntrinsicWidth();
        int intrinsicHeight = xMark.getIntrinsicWidth();

        int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
        int xMarkRight = itemView.getRight() - xMarkMargin;
        int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int xMarkBottom = xMarkTop + intrinsicHeight;
        xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

        xMark.draw(c);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}