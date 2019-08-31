package com.inftyloop.indulger;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.util.ConfigManager;
import com.inftyloop.indulger.util.LocaleHelper;
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import org.litepal.LitePal;

public class MainApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private static Thread mMainThread;
    private static long mMainThreadId;
    private static Looper mMainLooper;
    private static Handler mHandler;

    public static void setContext(Context ctx) {
        mContext = ctx;
    }

    public static Context getContext() {
        return mContext;
    }

    public static void setMainThread(Thread t) {
        mMainThread = t;
    }

    public static Thread getMainThread() {
        return mMainThread;
    }

    public static void setMainThreadId(long id) {
        mMainThreadId = id;
    }

    public static long getMainThreadId() {
        return mMainThreadId;
    }

    public static void setMainLooper(Looper l) {
        mMainLooper = l;
    }

    public static Looper getMainThreadLooper() {
        return mMainLooper;
    }

    public static void setMainHandler(Handler h) {
        mHandler = h;
    }

    public static Handler getMainHandler() {
        return mHandler;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mMainThread = Thread.currentThread();
        mMainThreadId = android.os.Process.myTid();
        mHandler = new Handler();
        Thread.setDefaultUncaughtExceptionHandler((Thread t, Throwable e) -> {
                Log.e(t.getName(), e.getMessage());
        });
        QMUISwipeBackActivityManager.init(this);
        LitePal.initialize(this);
        // init configs
        if(!ConfigManager.contains(Definition.SETTINGS_APP_THEME)) {
            ConfigManager.putIntNow(Definition.SETTINGS_APP_THEME, 0);
        }
        if(!ConfigManager.contains(Definition.SETTINGS_APP_NIGHT_MODE_FOLLOW_SYS)) {
            ConfigManager.putBooleanNow(Definition.SETTINGS_APP_NIGHT_MODE_FOLLOW_SYS, false);
        }
        if(!ConfigManager.contains(Definition.SETTINGS_APP_NIGHT_MODE_ENABLED)) {
            ConfigManager.putBooleanNow(Definition.SETTINGS_APP_NIGHT_MODE_ENABLED, false);
        }
        // register Weibo SDK
        WbSdk.install(this, new AuthInfo(this, Definition.WEIBO_APP_ID, Definition.WEIBO_REDIRECT_URL, Definition.WEIBO_SCOPE));
    }

    public static void restart() {
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }
}
