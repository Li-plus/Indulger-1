package com.inftyloop.indulger.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

import com.inftyloop.indulger.R;
import com.inftyloop.indulger.adapter.NewsListAdapter;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.model.entity.News;
import com.qmuiteam.qmui.arch.QMUIFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;


public class NewsListFragment extends QMUIFragment implements BGARefreshLayout.BGARefreshLayoutDelegate {
    @BindView(R.id.news_list_refresh_layout)
    BGARefreshLayout mRefreshLayout;
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

        mRefreshLayout.setDelegate(this);

        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(getActivity(), false);
        // 设置下拉刷新
        TypedValue outValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.app_background_color_dark, outValue, false);
        refreshViewHolder.setRefreshViewBackgroundColorRes(outValue.data);//背景色
        refreshViewHolder.setPullDownRefreshText("drag");//下拉的提示文字
        refreshViewHolder.setReleaseRefreshText("drop");//松开的提示文字
        refreshViewHolder.setRefreshingText("refresh");//刷新中的提示文字

        // 设置下拉刷新和上拉加载更多的风格
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);
        mRefreshLayout.shouldHandleRecyclerViewLoadingMore(mRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        List<News> data = new ArrayList<>();
        mAdapter = new NewsListAdapter(getContext(), data);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation()));

        data.add(new News("text news", "author", "5 minutes ago", null, null, null));
        data.add(new News("single image news", "author", "4 minutes ago", R.mipmap.ic_launcher, null, null));
        data.add(new News("three images news", "author", "4 minutes ago", R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher));

        return root;
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        Timer timer = new Timer();
        // TODO: fetch data from backend, then call the callback in main thread.

        // just a simulation of fetching data.
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                refreshLayout.endRefreshing();
            }
        };
        handler.sendEmptyMessageDelayed(0, 2000);
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }
}
