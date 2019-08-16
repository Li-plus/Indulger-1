package com.inftyloop.indulger.fragment;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inftyloop.indulger.R;
import com.inftyloop.indulger.adapter.FavoriteItemAdapter;
import com.inftyloop.indulger.model.entity.News;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchResultFragment extends QMUIFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.search_result_recycler_view)
    RecyclerView mRecyclerView;

    FavoriteItemAdapter mAdapter;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.search_result, null);
        ButterKnife.bind(this, root);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(25, 25, 25, 25);
        View searchBar = getLayoutInflater().inflate(R.layout.searchbar_home, null);
        searchBar.setOnClickListener((View v) -> {
            popBackStack();
        });
        ((TextView) searchBar.findViewById(R.id.txt_search)).setText(HomeSearchFragment.keyword);
        mTopBar.addRightView(searchBar, R.id.topbar_search, lp);

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

    private boolean canLoadMore() {
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

    @Override
    public TransitionConfig onFetchTransitionConfig() {
        return FADE_IN_SLIDE_OUT_TRANSITION_CONFIG;
    }
}
