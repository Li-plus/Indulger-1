package com.inftyloop.indulger.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.util.BaseFragment;
import com.inftyloop.indulger.util.BasePresenter;

public class NewsListFragment extends BaseFragment {
    //TODO
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.stub;
    }

    @Override
    protected void loadData() {}
}
