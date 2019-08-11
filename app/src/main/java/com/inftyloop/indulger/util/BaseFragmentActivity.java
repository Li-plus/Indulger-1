package com.inftyloop.indulger.util;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.api.Definition;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;

public abstract class BaseFragmentActivity extends QMUIFragmentActivity {
    protected int curStyleResId;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        super.onCreate(savedInstanceState);
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
}
