package com.inftyloop.indulger.util;

import android.content.Context;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import butterknife.ButterKnife;
import com.inftyloop.indulger.R;
import butterknife.BindView;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.QMUIWindowInsetLayout;

public abstract class BaseHomeController extends QMUIWindowInsetLayout {
    @BindView(R.id.topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;

    public interface BaseHomeControlListener {
        void startFragment(QMUIFragment fragment);
    }

    private BaseHomeControlListener mListener;
    private int mRecyclerViewSaveStateId = QMUIViewHelper.generateViewId();

    public BaseHomeController(Context ctx) {
        super(ctx);
        LayoutInflater.from(ctx).inflate(R.layout.tabbar_item, this);
        ButterKnife.bind(this);
        mTopBar.setTitle(getTitle());
    }

    protected abstract String getTitle();

    protected void topBarHook() {}

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        int id = mRecyclerView.getId();
        mRecyclerView.setId(mRecyclerViewSaveStateId);
        super.dispatchSaveInstanceState(container);
        mRecyclerView.setId(id);
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        int id = mRecyclerView.getId();
        mRecyclerView.setId(mRecyclerViewSaveStateId);
        super.dispatchRestoreInstanceState(container);
        mRecyclerView.setId(id);
    }

    protected void startFragment(QMUIFragment fragment) {
        if(mListener != null)
            mListener.startFragment(fragment);
    }

    public void setListener(BaseHomeControlListener listener) {
        mListener = listener;
    }
}
