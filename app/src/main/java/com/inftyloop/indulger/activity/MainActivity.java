package com.inftyloop.indulger.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inftyloop.indulger.R;
import com.inftyloop.indulger.fragment.AboutFragment;
import com.inftyloop.indulger.fragment.MainTabBarFragment;
import com.inftyloop.indulger.fragment.WebViewFragment;
import com.inftyloop.indulger.ui.BaseFragmentActivity;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;
import com.qmuiteam.qmui.arch.annotation.DefaultFirstFragment;
import com.qmuiteam.qmui.arch.annotation.FirstFragments;
import com.qmuiteam.qmui.arch.annotation.LatestVisitRecord;

import cn.jzvd.Jzvd;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;

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
public class MainActivity extends BaseFragmentActivity implements WbShareCallback {
    public WbShareHandler shareHandler = new WbShareHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shareHandler.registerApp();
    }

    @Override
    protected int getContextViewId() {
        return R.id.main_activity;
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

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress())
            return;
        Jzvd.releaseAllVideos();
        super.onBackPressed();
    }

    @Override
    public void onWbShareSuccess() {
        QMUITipDialog.Builder.makeToast(this, QMUITipDialog.Builder.ICON_TYPE_NOTHING, getString(R.string.share_success), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWbShareCancel() {
        QMUITipDialog.Builder.makeToast(this, QMUITipDialog.Builder.ICON_TYPE_NOTHING, getString(R.string.share_cancel), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWbShareFail() {
        QMUITipDialog.Builder.makeToast(this, QMUITipDialog.Builder.ICON_TYPE_FAIL, getString(R.string.share_failure), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        shareHandler.doResultIntent(intent, this);
    }
}
