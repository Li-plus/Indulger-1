package com.inftyloop.indulger.util;

import android.util.Log;
import com.inftyloop.indulger.BuildConfig;
import com.inftyloop.indulger.MainApplication;
import com.inftyloop.indulger.api.ApiRetrofit;
import com.inftyloop.indulger.model.entity.NewsFavEntry;
import okhttp3.*;
import okio.BufferedSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.litepal.LitePal;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URLConnection;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class OkHTTPImageClient {
    public static final String TAG = OkHTTPImageClient.class.getSimpleName();
    private static OkHttpClient mClient;

    private static OkHttpClient getOkHTTPImageClient() {
        File httpCacheDirectory = new File(MainApplication.getContext().getCacheDir(), "images");
        int cacheSize = 500 * 1024 * 1024;
        Cache cache = new Cache(httpCacheDirectory, cacheSize);
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS).readTimeout(5, TimeUnit.SECONDS).cache(cache)
                .addInterceptor(chain -> {
                    Request req = chain.request();
                    HttpUrl url = req.url();
                    if (url.host().equals("127.0.0.1")) {
                        if (BuildConfig.DEBUG) {
                            Log.w(TAG, url.encodedPath() + "#" + url.port());
                        }
                        String path = url.encodedPath().replaceFirst("/", "");
                        String[] info = path.split(":");
                        try {
                            NewsFavEntry entry = LitePal.where("uuid = ?", info[0]).findFirst(NewsFavEntry.class);
                            okhttp3.Response.Builder builder = new okhttp3.Response.Builder();
                            long time = new Date().getTime();
                            builder.code(200).message("OK").protocol(Protocol.HTTP_1_1)
                                    .receivedResponseAtMillis(time).request(req).sentRequestAtMillis(time);
                            long offset = 0;
                            int idx = Integer.parseInt(info[1]);
                            for(int i = 0; i < idx; ++i)
                                offset += entry.getImageSize().get(i);
                            byte[] img = new byte[(int)(long)entry.getImageSize().get(idx)];
                            System.arraycopy(entry.getImageBuffer(), (int)offset, img, 0, (int)(long)entry.getImageSize().get(idx));
                            ByteArrayInputStream in = new ByteArrayInputStream(img);
                            String contentType = URLConnection.guessContentTypeFromStream(in);
                            return builder.body(ResponseBody.create(img, MediaType.get(contentType))).build();
                        } catch (Exception e) {
                            return new okhttp3.Response.Builder().code(404).request(req).protocol(Protocol.HTTP_1_1).message("NOT FOUND").body(ResponseBody.create(
                                    MediaType.get("application/json; charset=utf-8"), "{}")).build();
                        }
                    } else {
                        okhttp3.Response response = chain.proceed(chain.request());
                        return response;
                    }
                }).addInterceptor(ApiRetrofit.OFFLINE_INTERCEPTOR)
                .addInterceptor(ApiRetrofit.ONLINE_INTERCEPTOR);
        return clientBuilder.build();
    }

    public static OkHttpClient getInstance() {
        if (mClient == null)
            mClient = getOkHTTPImageClient();
        return mClient;
    }
}
