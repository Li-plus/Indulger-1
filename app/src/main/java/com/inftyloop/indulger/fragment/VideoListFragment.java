package com.inftyloop.indulger.fragment;

import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inftyloop.indulger.R;
import com.inftyloop.indulger.adapter.BaseRecyclerViewAdapter;
import com.inftyloop.indulger.adapter.VideoListAdapter;
import com.inftyloop.indulger.api.DefaultNewsApiAdapter;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.listener.OnChildAttachStateChangeCallback;
import com.inftyloop.indulger.listener.OnNewsListRefreshListener;
import com.inftyloop.indulger.model.entity.News;
import com.inftyloop.indulger.model.entity.NewsEntry;
import com.inftyloop.indulger.ui.BaseFragment;
import com.inftyloop.indulger.util.ConfigManager;
import com.inftyloop.indulger.viewholder.BaseRecyclerViewHolder;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;


public class VideoListFragment extends BaseFragment implements OnNewsListRefreshListener {
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    BaseRecyclerViewAdapter<News, BaseRecyclerViewHolder> mAdapter;

    private final static String TAG = VideoListFragment.class.getSimpleName();
    private boolean isLoadingInProgress = false;

    DefaultNewsApiAdapter api = new DefaultNewsApiAdapter(this);

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
                        api.obtainToutiaoVideoList(true);
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

        mRecyclerView.addOnChildAttachStateChangeListener(new OnChildAttachStateChangeCallback());
    }

    @Override
    public void onNewsListRefresh(List<News> newsList) {
        isLoadingInProgress = false;
        mAdapter.removeItemImmediately(mAdapter.getData().size() - 1);

        for (News news : newsList) {
            int position = mAdapter.getData().size();
            mAdapter.insertItemImmediately(position, news);
        }

        if (newsList.size() > 0)
            mAdapter.insertItemImmediately(new News(News.LOAD_MORE_FOOTER));
        else
            mAdapter.insertItemImmediately(new News(News.NO_MORE_FOOTER));
    }
}
