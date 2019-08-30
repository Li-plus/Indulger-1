package com.inftyloop.indulger.fragment;

import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inftyloop.indulger.R;
import com.inftyloop.indulger.adapter.FavoriteItemAdapter;
import com.inftyloop.indulger.api.DefaultNewsApiAdapter;
import com.inftyloop.indulger.listener.OnNewsListRefreshListener;
import com.inftyloop.indulger.model.entity.News;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FavoriteFragment extends QMUIFragment implements OnNewsListRefreshListener {
    @BindView(R.id.favorite_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    private FavoriteItemAdapter mAdapter;

    private final static String TAG = FavoriteFragment.class.getSimpleName();
    private boolean isLoadingInProgress = false;
    private DefaultNewsApiAdapter api = new DefaultNewsApiAdapter(this);
    private boolean isLoadingMore = false;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getContext()).inflate(R.layout.favorite, null);
        ButterKnife.bind(this, root);
        mTopBar.setTitle(getString(R.string.favorite_title));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        List<News> data = new ArrayList<>();
        mAdapter = new FavoriteItemAdapter(getActivity(), data);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration divider = new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation());
        divider.setDrawable(getContext().getDrawable(R.drawable.content_divider));
        mRecyclerView.addItemDecoration(divider);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setOnScrollChangeListener((View view, int i, int i1, int i2, int i3) -> {
            if (!mRecyclerView.canScrollVertically(1) && !isLoadingInProgress &&
                    mAdapter.getItemViewType(mAdapter.getItemCount() - 1) != News.NO_MORE_FOOTER) {

                mRecyclerView.post(() -> {
                    isLoadingInProgress = true;
                    api.obtainFavoriteList(isLoadingMore);
                    isLoadingMore = true;
                });
            }
        });

        mAdapter.insertItemImmediately(new News(News.LOAD_MORE_FOOTER));

        return root;
    }

    @Override
    public void onNewsListRefresh(List<News> newsList) {
        isLoadingInProgress = false;
        if (mAdapter.getData().size() > 0 &&
                (mAdapter.getData().get(mAdapter.getData().size() - 1).getType() == News.LOAD_MORE_FOOTER) ||
                (mAdapter.getData().get(mAdapter.getData().size() - 1).getType() == News.NO_MORE_FOOTER)) {
            mAdapter.removeItemImmediately(mAdapter.getData().size() - 1);
        }

        for (News news : newsList) {
            int position = mAdapter.getData().size();
            mAdapter.insertItemImmediately(position, news);
        }

        if (newsList.size() >= 10)
            mAdapter.insertItemImmediately(new News(News.LOAD_MORE_FOOTER));
        else
            mAdapter.insertItemImmediately(new News(News.NO_MORE_FOOTER));
    }
}
