package com.inftyloop.indulger.util;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class GlideUtils {
    public static void loadNormal(Context ctx, String url, ImageView iv, int resId) {
        RequestOptions options = new RequestOptions();
        options.placeholder(resId);
        Glide.with(ctx).load(url).apply(options).into(iv);
    }

    public static void loadRound(Context ctx, String url, ImageView iv, int resId) {
        RequestOptions options = new RequestOptions();
        options.placeholder(resId).centerCrop().circleCrop();
        Glide.with(ctx).load(url).apply(options).into(iv);
    }
}
