package com.inftyloop.indulger.fragment;

import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.inftyloop.indulger.R;
import com.inftyloop.indulger.adapter.FavoriteItemAdapter;

import com.inftyloop.indulger.model.entity.News;
import com.inftyloop.indulger.util.DisplayHelper;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.util.ArrayList;
import java.util.List;


public class FavoriteFragment extends QMUIFragment {
    @BindView(R.id.favorite_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    FavoriteItemAdapter mAdapter;

    private final static String TAG = FavoriteFragment.class.getSimpleName();

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getContext()).inflate(R.layout.favorite, null);
        ButterKnife.bind(this, root);
        mTopBar.setTitle(getString(R.string.favorite_title));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        List<News> data = new ArrayList<>();
        mAdapter = new FavoriteItemAdapter(getActivity(), data);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration divider = new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation());
        divider.setDrawable(getContext().getDrawable(R.drawable.content_divider));
        mRecyclerView.addItemDecoration(divider);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setOnScrollChangeListener((View view, int i, int i1, int i2, int i3) -> {
            if (!view.canScrollVertically(1)) {
                view.postDelayed(() -> {
                    onDataLoaded();
                }, 2000);
            }
        });

        mAdapter.insertItemImmediately(new News(News.LOAD_MORE_FOOTER));
        onDataLoaded();

        return root;
    }

    public boolean canLoadMore() {
        return mAdapter.getData().size() < 20;
    }

    public void onDataLoaded() {
        if (!canLoadMore())
            return;
        mAdapter.removeItemImmediately(mAdapter.getData().size() - 1);
        int loadSize = 10;
        for (int i = 0; i < loadSize; i++) {
            double rand = Math.random();
            int position = mAdapter.getData().size();
            if (rand < 0.33)
                mAdapter.insertItemImmediately(position, new News(News.TEXT_NEWS, "text news " + Math.random(), "author", "5 minutes ago", null, null, null));
            else if (rand < 0.66)
                mAdapter.insertItemImmediately(position, new News(News.SINGLE_IMAGE_NEWS, "single image news " + Math.random(), "author", "4 minutes ago", R.mipmap.ic_launcher, null, null));
            else
                mAdapter.insertItemImmediately(position, new News(News.THREE_IMAGES_NEWS, "three images news " + Math.random(), "author", "4 minutes ago", R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher));
        }
        if (canLoadMore()) {
            mAdapter.insertItemImmediately(new News(News.LOAD_MORE_FOOTER));
        } else {
            mAdapter.insertItemImmediately(new News(News.NO_MORE_FOOTER));
        }
    }
}
