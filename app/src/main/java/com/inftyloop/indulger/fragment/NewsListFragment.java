package com.inftyloop.indulger.fragment;

import android.os.Bundle;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.inftyloop.indulger.R;
import com.inftyloop.indulger.adapter.NewsListAdapter;
import com.inftyloop.indulger.api.Definition;

import com.inftyloop.indulger.model.entity.News;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;

import java.util.ArrayList;
import java.util.List;


public class NewsListFragment extends QMUIFragment {
    @BindView(R.id.news_list_refresh_layout)
    QMUIPullRefreshLayout mRefreshLayout;
    @BindView(R.id.news_list_recycler_view)
    RecyclerView mRecyclerView;

    NewsListAdapter mAdapter;
    private final static String TAG = NewsListFragment.class.getSimpleName();

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getContext()).inflate(R.layout.news_list, null);
        ButterKnife.bind(this, root);

        Bundle bundle = getArguments();
        String name = "";
        int flag = 0;
        if (bundle != null) {
            name = bundle.getString(Definition.CHANNEL_NAME);
            if (bundle.getBoolean(Definition.IS_RECOMMEND, false))
                flag |= 0x01;
            if (bundle.getBoolean(Definition.IS_VIDEO_LIST, false))
                flag |= 0x02;
        }

        mRefreshLayout.setOnPullListener(new QMUIPullRefreshLayout.OnPullListener() {
            @Override
            public void onMoveTarget(int offset) {

            }

            @Override
            public void onMoveRefreshView(int offset) {

            }

            @Override
            public void onRefresh() {
                mRefreshLayout.postDelayed(() -> {
                    mRefreshLayout.finishRefresh();
                    onDataLoaded(true);
                }, 2000);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        List<News> data = new ArrayList<>();
        mAdapter = new NewsListAdapter(getActivity(), data);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration divider = new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation());
        divider.setDrawable(getContext().getDrawable(R.drawable.content_divider));
        mRecyclerView.addItemDecoration(divider);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (!mRecyclerView.canScrollVertically(1)) {
                    mRecyclerView.postDelayed(() -> {
                        onDataLoaded(false);
                    }, 2000);
                }
            }
        });
        onDataLoaded(true);
        return root;
    }

    public void onDataLoaded(boolean fromStart) {
        for (int i = 0; i < 10; i++) {
            double rand = Math.random();
            int position = (fromStart ? 0 : mAdapter.getData().size());
            if (rand < 0.33)
                mAdapter.getData().add(position, new News("text news " + Math.random(), "author", "5 minutes ago", null, null, null));
            else if (rand < 0.66)
                mAdapter.getData().add(position, new News("single image news " + Math.random(), "author", "4 minutes ago", R.mipmap.ic_launcher, null, null));
            else
                mAdapter.getData().add(position, new News("three images news " + Math.random(), "author", "4 minutes ago", R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher));
        }
        int startPosition = (fromStart ? 0 : mAdapter.getData().size() - 10);
        mAdapter.notifyItemRangeInserted(startPosition, 10);
        if (fromStart) {
            mAdapter.getData().add(0, null);
            mAdapter.notifyItemInserted(0);
            mRecyclerView.postDelayed(() -> {
                mAdapter.getData().remove(0);
                mAdapter.notifyItemRemoved(0);
            }, 2000);
            mRecyclerView.scrollToPosition(0);
        }
    }
}
