package com.inftyloop.indulger.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.inftyloop.indulger.R;
import com.inftyloop.indulger.adapter.BaseRecyclerViewAdapter;
import com.inftyloop.indulger.adapter.NewsListAdapter;
import com.inftyloop.indulger.adapter.VideoListAdapter;
import com.inftyloop.indulger.api.DefaultNewsApiAdapter;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.listener.OnNewsListRefreshListener;
import com.inftyloop.indulger.model.entity.News;
import com.inftyloop.indulger.model.entity.NewsEntry;
import com.inftyloop.indulger.ui.BaseFragment;
import com.inftyloop.indulger.ui.MyJzVideoPlayer;
import com.inftyloop.indulger.viewholder.BaseRecyclerViewHolder;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.jzvd.Jzvd;


public class NewsListFragment extends BaseFragment implements OnNewsListRefreshListener {
    @BindView(R.id.news_list_refresh_layout)
    QMUIPullRefreshLayout mRefreshLayout;
    @BindView(R.id.news_list_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.notification_header)
    TextView mNotificationHeader;

    BaseRecyclerViewAdapter<News, BaseRecyclerViewHolder> mAdapter;

    private final static String TAG = NewsListFragment.class.getSimpleName();
    private boolean mInsertFromTop = false;
    private boolean isLoadingInProgress = false;

    DefaultNewsApiAdapter api = new DefaultNewsApiAdapter(this);
    private String mChannelCode;

    @Override
    protected int getLayoutId() {
        return R.layout.news_list;
    }

    @Override
    public void initView(View rootView) {
        super.initView(rootView);
        Bundle bundle = getArguments();

        int flag = 0;
        if (bundle != null) {
            mChannelCode = bundle.getString(Definition.CHANNEL_CODE);
            if (bundle.getBoolean(Definition.IS_RECOMMEND, false))
                flag |= 0x01;
            if (bundle.getBoolean(Definition.IS_VIDEO_LIST, false))
                flag |= 0x02;
        }

        List<News> data = new ArrayList<>();
        if (mChannelCode.equals(getString(R.string.channel_code_video))) {
            mAdapter = new VideoListAdapter(getActivity(), data);
        } else {
            mAdapter = new NewsListAdapter(getActivity(), data);
        }
    }

    @Override
    protected void loadData() {
        mRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (!mRecyclerView.canScrollVertically(1) && !isLoadingInProgress) {
                    mInsertFromTop = false;
                    mRecyclerView.post(() -> {
                        isLoadingInProgress = true;
                        api.obtainNewsList(mChannelCode, true);
                    });
                }
            }
        });

        mAdapter.insertItemImmediately(new News(News.LOAD_MORE_FOOTER));
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void initListener() {
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
                    if (!isLoadingInProgress) {
                        isLoadingInProgress = true;
                        api.obtainNewsList(mChannelCode, false);
                        mRefreshLayout.finishRefresh();
                    }
                });
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration divider = new DividerItemDecoration(mRecyclerView.getContext(), RecyclerView.VERTICAL);
        divider.setDrawable(getContext().getDrawable(R.drawable.content_divider));
        mRecyclerView.addItemDecoration(divider);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        if (mChannelCode.equals(getString(R.string.channel_code_video))) {
            mRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                @Override
                public void onChildViewAttachedToWindow(@NonNull View view) {

                }

                @Override
                public void onChildViewDetachedFromWindow(@NonNull View view) {
                    MyJzVideoPlayer detachJzvd = view.findViewById(R.id.video_player);
                    Jzvd curJzvd = Jzvd.CURRENT_JZVD;
                    if (detachJzvd != null && detachJzvd.jzDataSource != null &&
                            curJzvd != null && curJzvd.jzDataSource != null &&
                            detachJzvd.jzDataSource.containsTheUrl(curJzvd.jzDataSource.getCurrentUrl()) &&
                            curJzvd.screen != Jzvd.SCREEN_FULLSCREEN) {
                        Jzvd.releaseAllVideos();
                    }
                }
            });
        }
    }

    @Override
    public void onNewsListRefresh(List<News> newsList) {
        String[] videoUrls = {
                "http://vfx.mtime.cn/Video/2017/03/31/mp4/170331093811717750.mp4",
                "http://jzvd.nathen.cn/c6e3dc12a1154626b3476d9bf3bd7266/6b56c5f0dc31428083757a45764763b0-5287d2089db37e62345123a1be272f8b.mp4",
                "https://www.w3school.com.cn/example/html5/mov_bbb.mp4",
                "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4",
                "https://www.w3schools.com/html/movie.mp4"
        };

        if (mChannelCode.equals("video")) {
            newsList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                NewsEntry newsEntry = new NewsEntry();
                newsEntry.setTitle("Title");
                newsEntry.setPublisherName("Publisher");
                newsEntry.setVideoUrl(videoUrls[i % videoUrls.length]);
                newsList.add(new News(newsEntry));
            }
        }

        isLoadingInProgress = false;
        if (!mInsertFromTop) {
            mAdapter.removeItemImmediately(mAdapter.getData().size() - 1);
        }
        for (News news : newsList) {
            int position = (mInsertFromTop ? 0 : mAdapter.getData().size());
            mAdapter.insertItemImmediately(position, news);
        }
        if (mInsertFromTop) {
            mNotificationHeader.setText(String.format(getString(R.string.news_list_notification), newsList.size()));
            mNotificationHeader.setVisibility(View.VISIBLE);
            mNotificationHeader.postDelayed(() -> {
                mNotificationHeader.setVisibility(View.GONE);
            }, 2000);
            mRecyclerView.scrollToPosition(0);
        } else {
            if (newsList.size() > 0)
                mAdapter.insertItemImmediately(new News(News.LOAD_MORE_FOOTER));
            else
                mAdapter.insertItemImmediately(new News(News.NO_MORE_FOOTER));
        }
    }
}
