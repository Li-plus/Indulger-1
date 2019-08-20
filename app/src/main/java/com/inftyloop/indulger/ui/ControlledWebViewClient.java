package com.inftyloop.indulger.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ControlledWebViewClient extends WebViewClient {
    public static final int DISABLE_ANY_LINK = 0;
    public static final int HANDLE_LINK_EXTERNALLY = 1;
    public static final int NO_LIMIT = 2;

    private int mMode;

    ControlledWebViewClient(int mode) {
        mMode = mode;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        switch (mMode) {
            case DISABLE_ANY_LINK:
                return true;
            case HANDLE_LINK_EXTERNALLY:
                Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
                view.getContext().startActivity(intent);
                return true;
            case NO_LIMIT:
                return false;
        }
        return true;
    }
}
