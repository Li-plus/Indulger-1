package com.inftyloop.indulger.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import butterknife.ButterKnife;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.listener.PermissionListener;
import com.qmuiteam.qmui.arch.QMUIActivity;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public abstract class BaseActivity extends QMUIActivity {
    protected int curStyleResId;
    protected abstract int getLayoutId();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    private void setCustomTheme(int selection) {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (selection) {
            case 0:
                // default
                if (curStyleResId != R.style.AppTheme) {
                    setTheme(R.style.AppTheme);
                    curStyleResId = R.style.AppTheme;
                }
                break;
            case 1:
                // auto
                if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                    if (curStyleResId != R.style.NightTheme) {
                        setTheme(R.style.NightTheme);
                        curStyleResId = R.style.NightTheme;
                    }
                } else {
                    if (curStyleResId != R.style.AppTheme) {
                        setTheme(R.style.AppTheme);
                        curStyleResId = R.style.AppTheme;
                    }
                }
                break;
            case 2:
                // night
                if (curStyleResId != R.style.NightTheme) {
                    setTheme(R.style.NightTheme);
                    curStyleResId = R.style.NightTheme;
                }
                break;
        }
    }

    public int getCurStyleResId() {
        return curStyleResId;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // get theme from config, if not set, default to 0
        int theme_checked_idx = ConfigManager.getInt(Definition.SETTINGS_APP_THEME, -1);
        if (theme_checked_idx < 0) {
            theme_checked_idx = 0;
            ConfigManager.putInt(Definition.SETTINGS_APP_THEME, theme_checked_idx);
        }
        // get current theme
        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.style_name, outValue, true);
        if (outValue.string.equals(getString(R.string.day_theme_name)))
            curStyleResId = R.style.AppTheme;
        else if (outValue.string.equals(getString(R.string.night_theme_name)))
            curStyleResId = R.style.NightTheme;
        setCustomTheme(theme_checked_idx);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
    }

    private PermissionListener mPermissionListener;

    public void requestRuntimePermission(String[] permissions, PermissionListener permissionListener) {
        mPermissionListener = permissionListener;
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), 1);
        } else {
            permissionListener.onGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0) {
            List<String> deniedPermissions = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                int grantResult = grantResults[i];
                String permission = permissions[i];
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permission);
                }
            }
            if (deniedPermissions.isEmpty()) {
                mPermissionListener.onGranted();
            } else {
                mPermissionListener.onDenied(deniedPermissions);
            }
        }
    }
}
