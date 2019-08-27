package com.inftyloop.indulger.util;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import com.inftyloop.indulger.activity.ImageViewPagerActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShowPicJSBridge {
    private static final String TAG = ShowPicJSBridge.class.getSimpleName();
    private Context mContext;
    private List<String> mUrls;

    public ShowPicJSBridge(Context ctx, List<String> imgUrls) {
        mContext = ctx;
        mUrls = imgUrls;
    }

    @JavascriptInterface
    public void openImg(String url) {
        Intent intent = new Intent(mContext, ImageViewPagerActivity.class);
        intent.putExtra(ImageViewPagerActivity.POSITION, mUrls.indexOf(url));
        intent.putStringArrayListExtra(ImageViewPagerActivity.IMG_URLS, (ArrayList<String>)mUrls);
        mContext.startActivity(intent);
    }
}
