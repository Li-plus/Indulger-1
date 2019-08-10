package com.inftyloop.indulger.fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.inftyloop.indulger.R;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

public class MeFragment extends QMUIFragment {
    private static final String TAG = MeFragment.class.getSimpleName();

    @BindView(R.id.topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.avatar) ImageView mAvatar;
    @BindView(R.id.username) TextView mUsername;
    @BindView(R.id.me_list) QMUIGroupListView mGroupListView;

    @Override
    public View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.me, null);
        ButterKnife.bind(this, root);
        mTopBar.setTitle(getResources().getString(R.string.me_title));
        mUsername.setText("Username Placeholder"); // TODO
        QMUIGroupListView.newSection(getContext())
                .addItemView(mGroupListView.createItemView(getResources().getString(R.string.me_grouplist_settings)), v -> {
                        QMUIFragment fragment = new SettingsFragment();
                        startFragment(fragment);
                    }).addTo(mGroupListView);
        return root;
    }

    @Override
    public TransitionConfig onFetchTransitionConfig() {
        return SLIDE_TRANSITION_CONFIG;
    }
}
