package com.inftyloop.indulger.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.api.Definition;

public class ThemeManager {
    private static int mCurStyleResId;
    private static boolean mEnableNightMode;

    public static int loadThemeFromConfig() {
        // get theme from config, if not set, default to 0
        return ConfigManager.getInt(Definition.SETTINGS_APP_THEME, 0);
    }

    public static int getCurStyleFromContext(Context ctx) {
        int curStyleResId = 0;
        TypedValue outValue = new TypedValue();
        try{
            ctx.getTheme().resolveAttribute(R.attr.style_name, outValue, true);
            if (outValue.string.equals(ctx.getString(R.string.day_theme_name)))
                curStyleResId = R.style.AppTheme;
            else if (outValue.string.equals(ctx.getString(R.string.night_theme_name)))
                curStyleResId = R.style.NightTheme;
            else if (outValue.string.equals(ctx.getString(R.string.toutiao_theme_name)))
                curStyleResId = R.style.ToutiaoTheme;
            else if (outValue.string.equals(ctx.getString(R.string.jiujing_theme_name)))
                curStyleResId = R.style.JiujingTheme;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return curStyleResId;
    }

    public static int getCurStyleResId(Context ctx) {
        mCurStyleResId = getCurStyleFromContext(ctx);
        return mCurStyleResId;
    }

    public static boolean isSystemNightModeEnabled(Context ctx) {
        int nightModeFlags = ctx.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    public static boolean shouldUseNightMode(Context ctx) {
        boolean isAuto = ConfigManager.getBoolean(Definition.SETTINGS_APP_NIGHT_MODE_FOLLOW_SYS, false);
        if(isAuto)
            return isSystemNightModeEnabled(ctx);
        else
            return ConfigManager.getBoolean(Definition.SETTINGS_APP_NIGHT_MODE_ENABLED, false);
    }

    /**
     *
     * @param ctx Context
     * @param selection Theme number, note this is not resource id, and we use -1 to denote night mode
     * @return
     */
    public static boolean changeThemeNoReload(Context ctx, int selection) {
        getCurStyleResId(ctx);
        boolean reload = false;
        if(shouldUseNightMode(ctx)) {
            if(mCurStyleResId != R.style.NightTheme) {
                ctx.setTheme(R.style.NightTheme);
                reload = true;
                mCurStyleResId = R.style.NightTheme;
            }
            return reload;
        }
        // we ignore selection if night mode is on
        switch (selection) {
            case 0:
                // default
                if (mCurStyleResId != R.style.AppTheme) {
                    ctx.setTheme(R.style.AppTheme);
                    reload = true;
                    mCurStyleResId = R.style.AppTheme;
                }
                break;
            case 1:
                if (mCurStyleResId != R.style.ToutiaoTheme) {
                    ctx.setTheme(R.style.ToutiaoTheme);
                    reload = true;
                    mCurStyleResId = R.style.ToutiaoTheme;
                }
                break;
            case 2:
                if (mCurStyleResId != R.style.JiujingTheme) {
                    ctx.setTheme(R.style.JiujingTheme);
                    reload = true;
                    mCurStyleResId = R.style.JiujingTheme;
                }
        }
        return reload;
    }

    public static void changeTheme(Context ctx, int selection) {
        if(ctx == null)
            return;
        boolean reload = changeThemeNoReload(ctx, selection);
        if (reload && ctx instanceof Activity) {
            ((Activity) ctx).recreate();
        }
    }
}
