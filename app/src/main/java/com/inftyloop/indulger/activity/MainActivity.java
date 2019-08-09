package com.inftyloop.indulger.activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.fragment.MainTabBarFragment;
import com.inftyloop.indulger.fragment.WebViewFragment;
import com.inftyloop.indulger.fragment.AboutFragment;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;
import com.qmuiteam.qmui.arch.annotation.DefaultFirstFragment;
import com.qmuiteam.qmui.arch.annotation.FirstFragments;
import com.qmuiteam.qmui.arch.annotation.LatestVisitRecord;

import static com.inftyloop.indulger.fragment.WebViewFragment.EXTRA_TITLE;
import static com.inftyloop.indulger.fragment.WebViewFragment.EXTRA_URL;

@FirstFragments(
        value = {
                MainTabBarFragment.class,
                AboutFragment.class,
                WebViewFragment.class
        })
@DefaultFirstFragment(MainTabBarFragment.class)
@LatestVisitRecord
public class MainActivity extends QMUIFragmentActivity {

    @Override
    protected int getContextViewId() {
        return R.id.qmuidemo;
    }


    public static Intent createWebExplorerIntent(Context context, String url, String title) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_URL, url);
        bundle.putString(EXTRA_TITLE, title);
        return of(context, WebViewFragment.class, bundle);
    }

    public static Intent of(@NonNull Context context,
                            @NonNull Class<? extends QMUIFragment> firstFragment) {
        return QMUIFragmentActivity.intentOf(context, MainActivity.class, firstFragment);
    }

    public static Intent of(@NonNull Context context,
                            @NonNull Class<? extends QMUIFragment> firstFragment,
                            @Nullable Bundle fragmentArgs) {
        return QMUIFragmentActivity.intentOf(context, MainActivity.class, firstFragment, fragmentArgs);
    }
}
