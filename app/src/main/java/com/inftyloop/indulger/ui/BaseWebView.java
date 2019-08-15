package com.inftyloop.indulger.ui;

import com.inftyloop.indulger.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebSettings;

import com.qmuiteam.qmui.BuildConfig;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIPackageHelper;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.widget.webview.QMUIWebView;

public class BaseWebView extends QMUIWebView {

    public BaseWebView(Context context) {
        this(context, null);
    }

    public BaseWebView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
    }

    public BaseWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected void init(Context context) {
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setTextZoom(100);

        String screen = QMUIDisplayHelper.getScreenWidth(context) + "x" + QMUIDisplayHelper.getScreenHeight(context);
        String userAgent = "QMUIDemo/" + QMUIPackageHelper.getAppVersion(context)
                + " (Android; " + Build.VERSION.SDK_INT
                + "; Screen/" + screen + "; Scale/" + QMUIDisplayHelper.getDensity(context) + ")";
        String agent = getSettings().getUserAgentString();
        if (agent == null || !agent.contains(userAgent)) {
            getSettings().setUserAgentString(agent + " " + userAgent);
        }

        if (BuildConfig.DEBUG) {
            setWebContentsDebuggingEnabled(true);
        }
    }

    public void exec(final String jsCode) {
        evaluateJavascript(jsCode, null);
    }

    @Override
    protected int getExtraInsetTop(float density) {
        return (int) (QMUIResHelper.getAttrDimen(getContext(), R.attr.qmui_topbar_height) / density);
    }
}
