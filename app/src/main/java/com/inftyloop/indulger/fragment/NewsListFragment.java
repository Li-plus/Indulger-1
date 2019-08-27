package com.inftyloop.indulger.fragment;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.inftyloop.indulger.R;
import com.inftyloop.indulger.adapter.NewsListAdapter;
import com.inftyloop.indulger.api.DefaultNewsApiAdapter;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.listener.OnNewsListRefreshListener;
import com.inftyloop.indulger.model.entity.News;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class NewsListFragment extends QMUIFragment implements OnNewsListRefreshListener {
    @BindView(R.id.news_list_refresh_layout)
    QMUIPullRefreshLayout mRefreshLayout;
    @BindView(R.id.news_list_recycler_view)
    RecyclerView mRecyclerView;

    NewsListAdapter mAdapter;
    private final static String TAG = NewsListFragment.class.getSimpleName();
    private boolean mInsertFromTop = false;

    DefaultNewsApiAdapter api = new DefaultNewsApiAdapter(this);
    String mChannelCode;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getContext()).inflate(R.layout.news_list, null);
        ButterKnife.bind(this, root);
        Bundle bundle = getArguments();

        int flag = 0;
        if (bundle != null) {
            mChannelCode = bundle.getString(Definition.CHANNEL_CODE);
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
                mInsertFromTop = true;
                mRefreshLayout.post(() -> {
                    api.obtainNewsList(mChannelCode, false);
                    mRefreshLayout.finishRefresh();
                });
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
                    mInsertFromTop = false;
                    mRecyclerView.post(() -> {
                        api.obtainNewsList(mChannelCode, true);
                    });
                }
            }
        });

        mAdapter.insertItemImmediately(new News(News.LOAD_MORE_FOOTER));
        return root;
    }

    public void onDataLoaded(boolean fromStart) {
//        if (fromStart) {
//            if (mAdapter.getData().size() > 0 && mAdapter.getData().get(0).getType() == News.NOTIFICATION_HEADER) {
//                mAdapter.removeItemImmediately(0);
//            }
//        } else {
//            if (mAdapter.getData().size() > 0)
//                mAdapter.removeItemImmediately(mAdapter.getData().size() - 1);
//        }
//
//        for (int i = 0; i < 10; i++) {
//            double rand = Math.random();
//            int position = (fromStart ? 0 : mAdapter.getData().size());
////            if (rand < 0.33)
////            mAdapter.insertItemImmediately(position, new News("text news " + Math.random(), "author", "5 minutes ago"));
////            else if (rand < 0.66)
////                mAdapter.insertItemImmediately(position, new News("single image news " + Math.random(), "author", "4 minutes ago", R.mipmap.ic_launcher));
////            else
////                mAdapter.insertItemImmediately(position, new News("three images news " + Math.random(), "author", "4 minutes ago", R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher));
//        }
//        if (fromStart) {
//            mAdapter.insertItemImmediately(0, new News(News.NOTIFICATION_HEADER));
//            mRecyclerView.postDelayed(() -> {
//                if (mAdapter.getData().size() > 0 && mAdapter.getData().get(0).type == News.NOTIFICATION_HEADER)
//                    mAdapter.removeItemImmediately(0);
//            }, 2000);
//            mRecyclerView.scrollToPosition(0);
//        } else {
//            mAdapter.insertItemImmediately(new News(News.LOAD_MORE_FOOTER));
//        }
    }

    @Override
    public void onNewsListRefresh(List<News> newsList) {
        if (mInsertFromTop) {
            if (mAdapter.getData().size() > 0 && mAdapter.getData().get(0).getType() == News.NOTIFICATION_HEADER) {
                mAdapter.removeItemImmediately(0);
            }
        } else {
            mAdapter.removeItemImmediately(mAdapter.getData().size() - 1);
        }
        for (News news : newsList) {
            int position = (mInsertFromTop ? 0 : mAdapter.getData().size());
            mAdapter.insertItemImmediately(position, news);
        }
        if (mInsertFromTop) {
            mAdapter.insertItemImmediately(0, new News(News.NOTIFICATION_HEADER));
            mRecyclerView.postDelayed(() -> {
                if (mAdapter.getData().size() > 0 && mAdapter.getData().get(0).getType() == News.NOTIFICATION_HEADER)
                    mAdapter.removeItemImmediately(0);
            }, 2000);
            mRecyclerView.scrollToPosition(0);
        } else {
            mAdapter.insertItemImmediately(new News(News.LOAD_MORE_FOOTER));
        }
    }
}
