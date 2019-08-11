package com.inftyloop.indulger;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.util.ConfigManager;
import com.inftyloop.indulger.util.LocaleHelper;
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager;
import org.litepal.LitePal;

import java.util.Locale;

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
        QMUISwipeBackActivityManager.init(this);
        LitePal.initialize(this);
    }

    public static void restart() {
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }
}
