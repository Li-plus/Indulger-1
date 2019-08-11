package com.inftyloop.indulger.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.LocaleList;
import com.inftyloop.indulger.api.Definition;

import java.util.Locale;

public class LocaleHelper {
    public static Context setLocale(Context base) {
        int app_lang = ConfigManager.getInt(Definition.SETTINGS_APP_LANG, -1);
        if (app_lang < 0) {
            app_lang = 0;
            ConfigManager.putIntNow(Definition.SETTINGS_APP_LANG, app_lang);
        }
        if(app_lang != 0) {
            String lang = "";
            switch (app_lang) {
                case 1:
                    lang = "zh";
                    break;
                case 2:
                    lang = "en";
                    break;
            }
            return updateResources(base, lang);
        } else return base;
    }

    private static Context updateResources(Context ctx, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        LocaleList localeList = new LocaleList(locale);
        LocaleList.setDefault(localeList);
        Resources res = ctx.getResources();
        Configuration conf = res.getConfiguration();
        conf.setLocale(locale);
        conf.setLocales(localeList);
        return ctx.createConfigurationContext(conf);
    }
}
