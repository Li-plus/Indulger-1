package com.inftyloop.indulger.util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import org.greenrobot.eventbus.EventBus;

public abstract class BaseFragment<T extends BasePresenter> extends BaseLazyLoadFragment {
    protected T mPresenter;
    private View rootView;
    protected Activity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
    }

    @Override
    protected View onCreateView() {
        if(rootView == null) {
            rootView = LayoutInflater.from(getActivity()).inflate(getLayoutId(), null);
            ButterKnife.bind(this, rootView);
            initView(rootView);
            initData();
            initListener();
        } else {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if(parent != null)
                parent.removeView(rootView);
        }
        return rootView;
    }

    public View getStateViewRoot() { return rootView; }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    public void initView(View rootView) {}

    public void initData() {}

    public void initListener() {}

    @Override
    protected void onFragmentFirstVisible() {
        loadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }
        rootView = null;
    }

    protected abstract T createPresenter();

    protected abstract int getLayoutId();

    protected abstract void loadData();

    // event bus stuffs
    public boolean isEventBusRegistered(Object subscribe) {
        return EventBus.getDefault().isRegistered(subscribe);
    }

    public void registerEventBus(Object subscribe) {
        if(!isEventBusRegistered(subscribe))
            EventBus.getDefault().register(subscribe);
    }

    public void unregisterEventBus(Object subscribe) {
        if(isEventBusRegistered(subscribe))
            EventBus.getDefault().unregister(subscribe);
    }
}
