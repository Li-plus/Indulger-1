package com.inftyloop.indulger.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.util.TypedValue;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.util.ConfigManager;
import com.inftyloop.indulger.util.LocaleHelper;
import com.inftyloop.indulger.util.ThemeManager;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;

@SuppressWarnings("Duplicates")
public abstract class BaseFragmentActivity extends QMUIFragmentActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.changeThemeNoReload(this, ThemeManager.loadThemeFromConfig());
        super.onCreate(savedInstanceState);
    }
}
