package com.inftyloop.indulger.fragment;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inftyloop.indulger.R;
import com.inftyloop.indulger.adapter.BaseRecyclerViewAdapter;
import com.inftyloop.indulger.adapter.VideoListAdapter;
import com.inftyloop.indulger.api.DefaultNewsApiAdapter;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.listener.OnNewsListRefreshListener;
import com.inftyloop.indulger.model.entity.News;
import com.inftyloop.indulger.model.entity.NewsEntry;
import com.inftyloop.indulger.ui.BaseFragment;
import com.inftyloop.indulger.ui.MyJzVideoPlayer;
import com.inftyloop.indulger.util.ConfigManager;
import com.inftyloop.indulger.viewholder.BaseRecyclerViewHolder;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import cn.jzvd.Jzvd;


public class VideoListFragment extends BaseFragment implements OnNewsListRefreshListener {
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    BaseRecyclerViewAdapter<News, BaseRecyclerViewHolder> mAdapter;

    private final static String TAG = NewsListFragment.class.getSimpleName();
    private boolean isLoadingInProgress = false;

    DefaultNewsApiAdapter api = new DefaultNewsApiAdapter(this);
    String mChannelCode = "video";

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_video_list;
    }

    @Override
    public void initView(View rootView) {
        super.initView(rootView);
        mTopBar.setTitle(getResources().getString(R.string.video_list_title));
        mAdapter = new VideoListAdapter(getActivity(), new ArrayList<>());
    }

    @Override
    protected void loadData() {
        mRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (!mRecyclerView.canScrollVertically(1) && !isLoadingInProgress) {
                    isLoadingInProgress = true;
                    new Thread(() -> {
                        api.obtainNewsList(mChannelCode, "", true);
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration divider = new DividerItemDecoration(mRecyclerView.getContext(), RecyclerView.VERTICAL);
        divider.setDrawable(getContext().getDrawable(R.drawable.content_divider));
        mRecyclerView.addItemDecoration(divider);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

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

    @Override
    public void onNewsListRefresh(List<News> newsList) {
        String[] videoUrls = {
                "http://vfx.mtime.cn/Video/2017/03/31/mp4/170331093811717750.mp4",
                "http://jzvd.nathen.cn/c6e3dc12a1154626b3476d9bf3bd7266/6b56c5f0dc31428083757a45764763b0-5287d2089db37e62345123a1be272f8b.mp4",
                "https://www.w3school.com.cn/example/html5/mov_bbb.mp4",
                "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4",
                "https://www.w3schools.com/html/movie.mp4"
        };

        newsList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            NewsEntry newsEntry = new NewsEntry();
            newsEntry.setTitle("Title");
            newsEntry.setPublisherName("Publisher");
            newsEntry.setVideoUrl(videoUrls[i % videoUrls.length]);
            newsList.add(new News(newsEntry));
        }

        isLoadingInProgress = false;

        mAdapter.removeItemImmediately(mAdapter.getData().size() - 1);

        for (News news : newsList) {
            boolean isBlock = false;
            HashSet<String> blockKeys = (HashSet<String>) ConfigManager.getStringSet(Definition.BLOCKED_KEYS, new HashSet<>());
            for (String keyword : news.getNewsEntry().getKeywords()) {
                if (blockKeys.contains(keyword)) {
                    isBlock = true;
                    Log.d(TAG, "blocking news with keyword " + keyword);
                    break;
                }
            }
            if (!isBlock) {
                int position = mAdapter.getData().size();
                mAdapter.insertItemImmediately(position, news);
            }
        }

        if (newsList.size() > 0)
            mAdapter.insertItemImmediately(new News(News.LOAD_MORE_FOOTER));
        else
            mAdapter.insertItemImmediately(new News(News.NO_MORE_FOOTER));
    }
}
