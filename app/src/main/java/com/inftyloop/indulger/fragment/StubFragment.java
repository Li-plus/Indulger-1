package com.inftyloop.indulger.fragment;

import android.view.LayoutInflater;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.inftyloop.indulger.R;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

public class StubFragment extends QMUIFragment {
    @BindView(R.id.topbar) QMUITopBarLayout mTopBar;
    @Override
    public View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.stub, null);
        ButterKnife.bind(this, root);
        mTopBar.setTitle("Stub Fragment" + this.hashCode());
        return root;
    }

    @Override
    public TransitionConfig onFetchTransitionConfig() {
        return SCALE_TRANSITION_CONFIG;
    }
}

