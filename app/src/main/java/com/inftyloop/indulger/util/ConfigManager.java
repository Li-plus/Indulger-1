package com.inftyloop.indulger.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.inftyloop.indulger.MainApplication;

import java.util.Set;

/**
 * Persistent storage for configurations
 * key, value, and default value
 * Use `putXXXNow to ensure that the values are written immediately
 * @author zx1239856
 */
public class ConfigManager {
    public static final String CONFIG_FILE_NAME = "config.indulger";

    public static void putBoolean(String key, boolean val) {
        SharedPreferences sp = MainApplication.getContext().getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, val).apply();
    }

    public static void putBooleanNow(String key, boolean val) {
        SharedPreferences sp = MainApplication.getContext().getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, val).commit();
    }

    public static boolean getBoolean(String key, boolean defVal) {
        SharedPreferences sp = MainApplication.getContext().getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defVal);
    }

    public static void putString(String key, String val) {
        SharedPreferences sp = MainApplication.getContext().getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(key, val).apply();
    }

    public static void putStringNow(String key, String val) {
        SharedPreferences sp = MainApplication.getContext().getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(key, val).commit();
    }

    public static String getString(String key, String defVal) {
        SharedPreferences sp = MainApplication.getContext().getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, defVal);
    }

    public static void putInt(String key, int val) {
        SharedPreferences sp = MainApplication.getContext().getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(key, val).apply();
    }

    public static void putIntNow(String key, int val) {
        SharedPreferences sp = MainApplication.getContext().getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(key, val).commit();
    }

    public static int getInt(String key, int defVal) {
        SharedPreferences sp = MainApplication.getContext().getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, defVal);
    }

    public static void putFloat(String key, float val) {
        SharedPreferences sp = MainApplication.getContext().getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putFloat(key, val).apply();
    }

    public static void putFloatNow(String key, float val) {
        SharedPreferences sp = MainApplication.getContext().getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putFloat(key, val).commit();
    }

    public static float getFloat(String key, float defVal) {
        SharedPreferences sp = MainApplication.getContext().getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getFloat(key, defVal);
    }

    public static void putLong(String key, long val) {
        SharedPreferences sp = MainApplication.getContext().getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putLong(key, val).apply();
    }

    public static void putLongNow(String key, long val) {
        SharedPreferences sp = MainApplication.getContext().getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putLong(key, val).commit();
    }

    public static long getLong(String key, long defVal) {
        SharedPreferences sp = MainApplication.getContext().getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getLong(key, defVal);
    }

    public static void putStringSet(String key, Set<String> val) {
        SharedPreferences sp = MainApplication.getContext().getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putStringSet(key, val).apply();
    }

    public static void putStringSetNow(String key, Set<String> val) {
        SharedPreferences sp = MainApplication.getContext().getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putStringSet(key, val).commit();
    }

    public static Set<String> getStringSet(String key, Set<String> defVal) {
        SharedPreferences sp = MainApplication.getContext().getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getStringSet(key, defVal);
    }
}
