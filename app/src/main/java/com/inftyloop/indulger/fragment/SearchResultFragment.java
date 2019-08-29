package com.inftyloop.indulger.fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inftyloop.indulger.R;
import com.inftyloop.indulger.adapter.NewsListAdapter;
import com.inftyloop.indulger.api.DefaultNewsApiAdapter;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.listener.OnNewsListRefreshListener;
import com.inftyloop.indulger.model.entity.News;
import com.inftyloop.indulger.util.ConfigManager;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchResultFragment extends QMUIFragment implements OnNewsListRefreshListener {
    public static final String TAG = SearchResultFragment.class.getSimpleName();

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.search_result_recycler_view)
    RecyclerView mRecyclerView;

    private NewsListAdapter mAdapter;
    private DefaultNewsApiAdapter api = new DefaultNewsApiAdapter(this);
    private boolean isLoadingInProgress = false;
    private boolean isLoadingMore = false;

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

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        List<News> data = new ArrayList<>();
        mAdapter = new NewsListAdapter(getActivity(), data);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration divider = new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation());
        divider.setDrawable(getContext().getDrawable(R.drawable.content_divider));
        mRecyclerView.addItemDecoration(divider);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setOnScrollChangeListener((View view, int i, int i1, int i2, int i3) -> {
            if (!mRecyclerView.canScrollVertically(1) && !isLoadingInProgress) {
                mRecyclerView.post(() -> {
                    isLoadingInProgress = true;
                    api.obtainSearchResult(HomeSearchFragment.keyword, isLoadingMore);
                    isLoadingMore = true;
                });
            }
        });

        mAdapter.insertItemImmediately(new News(News.LOAD_MORE_FOOTER));

        return root;
    }

    @Override
    public TransitionConfig onFetchTransitionConfig() {
        return FADE_IN_SLIDE_OUT_TRANSITION_CONFIG;
    }

    @Override
    public void onNewsListRefresh(List<News> newsList) {
        isLoadingInProgress = false;
        mAdapter.removeItemImmediately(mAdapter.getData().size() - 1);

        HashSet<String> blockKeys = (HashSet<String>) ConfigManager.getStringSet(Definition.BLOCK_KEYS, new HashSet<>());
        for (News news : newsList) {
            boolean isBlock = false;
            for (String keyword : news.getNewsEntry().getKeywords()) {
                if (blockKeys.contains(keyword)) {
                    isBlock = true;
                    Log.d(TAG, "blocked news with keyword " + keyword);
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
