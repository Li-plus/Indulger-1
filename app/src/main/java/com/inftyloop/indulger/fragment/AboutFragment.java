package com.inftyloop.indulger.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.inftyloop.indulger.R;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.util.QMUIPackageHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import static com.inftyloop.indulger.fragment.WebViewFragment.EXTRA_URL;
import static com.inftyloop.indulger.fragment.WebViewFragment.EXTRA_TITLE;

public class AboutFragment extends QMUIFragment {
    @BindView(R.id.topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.version) TextView mVersionTextView;
    @BindView(R.id.about_list) QMUIGroupListView mAboutGroupListView;
    @BindView(R.id.copyright) TextView mCopyrightTextView;

    @Override
    public View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.about, null);
        ButterKnife.bind(this, root);
        mTopBar.setTitle(getResources().getString(R.string.about_title));
        mTopBar.addLeftBackImageButton().setOnClickListener((View v) -> {
                popBackStack();
        });
        mVersionTextView.setText(QMUIPackageHelper.getAppVersion(getContext()));
        mCopyrightTextView.setText(getResources().getString(R.string.copyright));
        QMUIGroupListView.newSection(getContext())
                .addItemView(mAboutGroupListView.createItemView(getResources().getString(R.string.about_github)), v -> {
                        String url = getResources().getString(R.string.github_url);
                        Bundle bundle = new Bundle();
                        bundle.putString(EXTRA_URL, url);
                        bundle.putString(EXTRA_TITLE, getResources().getString(R.string.about_github));
                        QMUIFragment fragment = new WebViewFragment();
                        fragment.setArguments(bundle);
                        startFragment(fragment);
                    }).addTo(mAboutGroupListView);
        return root;
    }

    @Override
    public TransitionConfig onFetchTransitionConfig() {
        return SLIDE_TRANSITION_CONFIG;
    }
}
