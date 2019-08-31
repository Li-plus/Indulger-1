package com.inftyloop.indulger.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inftyloop.indulger.R;
import com.inftyloop.indulger.adapter.BaseRecyclerViewAdapter;
import com.inftyloop.indulger.adapter.NewsListAdapter;
import com.inftyloop.indulger.api.DefaultNewsApiAdapter;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.listener.OnChildAttachStateChangeCallback;
import com.inftyloop.indulger.listener.OnNewsListRefreshListener;
import com.inftyloop.indulger.model.entity.BlockedWords;
import com.inftyloop.indulger.model.entity.News;
import com.inftyloop.indulger.model.entity.RecommendWords;
import com.inftyloop.indulger.ui.BaseFragment;
import com.inftyloop.indulger.viewholder.BaseRecyclerViewHolder;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;


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
    private boolean mIsRecommend = false;
    private boolean mIsRefreshed = false;
    private Random rng = new Random(System.currentTimeMillis());

    public String getChannelCode() {
        return mChannelCode;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.news_list;
    }

    @Override
    public void initView(View rootView) {
        super.initView(rootView);
        Bundle bundle = getArguments();

        if (bundle != null) {
            mChannelCode = bundle.getString(Definition.CHANNEL_CODE);
            mIsRecommend = bundle.getBoolean(Definition.IS_RECOMMEND, false);
        }

        List<News> data = new ArrayList<>();
        mAdapter = new NewsListAdapter(getActivity(), data);
    }

    @Override
    protected void loadData() {
        mRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (!mRecyclerView.canScrollVertically(1) && !isLoadingInProgress) {
                    mInsertFromTop = false;
                    isLoadingInProgress = true;
                    mRefreshLayout.setEnabled(false);
                    mIsRefreshed = false;
                    new Thread(() -> {
                        if (!mIsRecommend)
                            api.obtainNewsList(mChannelCode, "", true);
                        else {
                            try {
                                List<RecommendWords> words = LitePal.where("1").limit(5).order("cnt desc").find(RecommendWords.class);
                                if (words.size() > 0) {
                                    api.obtainNewsList(mChannelCode, words.get(Math.abs(rng.nextInt()) % words.size()).getWord(), true);
                                } else {
                                    api.obtainNewsList(mChannelCode, "", true);
                                }
                            } catch (Exception e) {
                                api.obtainNewsList(mChannelCode, "", true);
                            }
                        }
                    }).start();
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
                if (isLoadingInProgress)
                    return;
                else
                    isLoadingInProgress = true;
                mInsertFromTop = true;
                mIsRefreshed = true;
                new Thread(() -> {
                    if (!mIsRecommend)
                        api.obtainNewsList(mChannelCode, "", false);
                    else {
                        try {
                            List<RecommendWords> words = LitePal.where("1").limit(20).order("cnt desc").find(RecommendWords.class);
                            if (words.size() > 0) {
                                api.obtainNewsList(mChannelCode, words.get(Math.abs(rng.nextInt()) % words.size()).getWord(), false);
                            } else {
                                api.obtainNewsList(mChannelCode, "", false);
                            }
                        } catch (Exception e) {
                            api.obtainNewsList(mChannelCode, "", false);
                        }
                    }
                }).start();
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration divider = new DividerItemDecoration(mRecyclerView.getContext(), RecyclerView.VERTICAL);
        divider.setDrawable(getContext().getDrawable(R.drawable.content_divider));
        mRecyclerView.addItemDecoration(divider);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.addOnChildAttachStateChangeListener(new OnChildAttachStateChangeCallback());
    }

    @Override
    public void onNewsListRefresh(List<News> newsList) {
        isLoadingInProgress = false;
        mRefreshLayout.finishRefresh();
        mRefreshLayout.setEnabled(true);
        if (mIsRefreshed && mIsRecommend && newsList.size() > 0) {
            mAdapter.clearAll();
        }
        if (!mInsertFromTop) {
            mAdapter.removeItemImmediately(mAdapter.getData().size() - 1);
        }
        for (News news : newsList) {
            boolean isBlock = false;
            for (String keyword : news.getNewsEntry().getKeywords()) {
                if (!LitePal.where("word = ?", keyword).find(BlockedWords.class).isEmpty()) {
                    isBlock = true;
                    Log.d(TAG, "blocking news with keyword " + keyword);
                    break;
                }
            }
            if (!isBlock) {
                int position = (mInsertFromTop ? 0 : mAdapter.getData().size());
                mAdapter.insertItemImmediately(position, news);
            }
        }
        if (mInsertFromTop) {
            mNotificationHeader.setText(newsList.size() > 0 ? getString(R.string.news_list_notification, newsList.size()) : getString(R.string.news_list_notification_already_newest));
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
