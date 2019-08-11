package com.inftyloop.indulger.fragment;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.inftyloop.indulger.R;
import com.inftyloop.indulger.adapter.FavoriteItemAdapter;
import com.inftyloop.indulger.listener.SwipeAnimationDecoration;
import com.inftyloop.indulger.listener.SwipeCallback;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.util.ArrayList;
import java.util.Map;

public class FavoriteFragment extends QMUIFragment {
    private final static String TAG = FavoriteFragment.class.getSimpleName();

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    RecyclerView mRecyclerView;
    ArrayList<Map<String, Object>> mData = new ArrayList<>();
    FavoriteItemAdapter mAdapter;

    @Override
    public View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.favorite, null);
        ButterKnife.bind(this, root);
        mTopBar.setTitle(getString(R.string.favorite_title));
        mTopBar.addRightTextButton("add fav", 0).setOnClickListener((View view) -> {
            Toast.makeText(getActivity(), "add", Toast.LENGTH_SHORT).show();
            mAdapter.add(R.mipmap.ic_launcher, "news " + Math.random(), "plus-Li", "9102-08-08");
        });

        mAdapter = new FavoriteItemAdapter(mData);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = root.findViewById(R.id.favorite_item);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation()));
        new ItemTouchHelper(new SwipeCallback(getContext(), mRecyclerView)).attachToRecyclerView(mRecyclerView);
        mRecyclerView.addItemDecoration(new SwipeAnimationDecoration(getContext()));

        mAdapter.add(R.mipmap.ic_launcher, "Java Summer Semester", "Li-plus", "2019-08-08");

        return root;
    }

    @Override
    public TransitionConfig onFetchTransitionConfig() {
        return SLIDE_TRANSITION_CONFIG;
    }
}
