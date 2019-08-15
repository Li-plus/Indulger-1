package com.inftyloop.indulger.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import com.qmuiteam.qmui.arch.QMUIFragment;

public abstract class BaseLazyLoadFragment extends QMUIFragment {
    private static final String TAG = BaseLazyLoadFragment.class.getSimpleName();
    private boolean isFirstEnter = true;
    private boolean isReuse = true;
    private boolean isVisible;
    private View rootView;

    protected void setReuse(boolean reuse) { isReuse = reuse; }

    protected void onFragmentVisibleChange(boolean isVisible) {}

    protected void onFragmentFirstVisible() {}

    protected boolean isFragmentVisible() { return isVisible; }

    private void resetStatus() {
        isFirstEnter = isReuse = true;
        isVisible = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        resetStatus();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(rootView == null)
            return;
        if(isFirstEnter && isVisibleToUser) {
            onFragmentFirstVisible();
            isFirstEnter = false;
        }
        if(isVisible ^ isVisibleToUser) {
            isVisible = isVisibleToUser;
            onFragmentVisibleChange(isVisible);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(rootView == null) {
            rootView = view;
            if(getUserVisibleHint()) {
                if(isFirstEnter) {
                    onFragmentFirstVisible();
                    isFirstEnter = false;
                }
                onFragmentVisibleChange(true);
                isVisible = true;
            }
        }
        super.onViewCreated(isReuse ? rootView : view, savedInstanceState);
    }
}
