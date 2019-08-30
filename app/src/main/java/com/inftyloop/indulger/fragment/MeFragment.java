package com.inftyloop.indulger.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.inftyloop.indulger.R;
import com.inftyloop.indulger.api.Definition;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeFragment extends QMUIFragment {
    private static final String TAG = MeFragment.class.getSimpleName();

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.avatar)
    ImageView mAvatar;
    @BindView(R.id.username)
    TextView mUsername;
    @BindView(R.id.me_list)
    QMUIGroupListView mGroupListView;
    @BindView(R.id.btn_login)
    Button mLogin;

    public static boolean isLogin = false;

    @Override
    public View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.me, null);
        ButterKnife.bind(this, root);
        mTopBar.setTitle(getResources().getString(R.string.me_title));
        mUsername.setText(getString(R.string.username_placeholder)); // TODO

        QMUICommonListItemView itemSettings = mGroupListView.createItemView(getString(R.string.me_grouplist_settings));
        itemSettings.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUICommonListItemView itemFavorite = mGroupListView.createItemView(getString(R.string.favorite_title));
        itemFavorite.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUIGroupListView.newSection(getContext())
                .addItemView(itemFavorite, (View v) -> {
                    QMUIFragment fragment = new FavoriteFragment();
                    startFragment(fragment);
                })
                .addItemView(itemSettings, (View v) -> {
                    QMUIFragment fragment = new SettingsFragment();
                    startFragmentForResult(fragment, Definition.REQUEST_CODE_SETTINGS);
                })
                .addTo(mGroupListView);

        mLogin.setOnClickListener((View v) -> {
            QMUIFragment fragment = new LogInFragment();
            startFragmentForResult(fragment, Definition.REQUEST_CODE_LOGIN);
        });
        return root;
    }

    @Override
    public TransitionConfig onFetchTransitionConfig() {
        return SLIDE_TRANSITION_CONFIG;
    }

    @Override
    protected void onFragmentResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        if (requestCode == Definition.REQUEST_CODE_LOGIN) {
            String userName = data.getStringExtra("username");
            mLogin.setVisibility(View.GONE);
            mUsername.setText(userName);
            mUsername.setVisibility(View.VISIBLE);
            mAvatar.setVisibility(View.VISIBLE);
            isLogin = true;
        } else if (requestCode == Definition.REQUEST_CODE_SETTINGS) { // log out
            mLogin.setVisibility(View.VISIBLE);
            mUsername.setVisibility(View.GONE);
            mAvatar.setVisibility(View.GONE);
            isLogin = false;
        }
    }
}
