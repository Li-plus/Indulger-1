package com.inftyloop.indulger.util;

import com.inftyloop.indulger.MainApplication;
import com.inftyloop.indulger.api.ApiRetrofit;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class OkHTTPImageClient {
    private static OkHttpClient mClient;

    private static OkHttpClient getOkHTTPImageClient() {
        File httpCacheDirectory = new File(MainApplication.getContext().getCacheDir(), "images");
        int cacheSize = 500 * 1024 * 1024;
        Cache cache = new Cache(httpCacheDirectory, cacheSize);
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS).readTimeout(5, TimeUnit.SECONDS).cache(cache).addInterceptor(ApiRetrofit.OFFLINE_INTERCEPTOR)
                .addInterceptor(ApiRetrofit.ONLINE_INTERCEPTOR);
        return clientBuilder.build();
    }

    public static OkHttpClient getInstance() {
        if(mClient == null)
            mClient = getOkHTTPImageClient();
        return mClient;
    }
}
