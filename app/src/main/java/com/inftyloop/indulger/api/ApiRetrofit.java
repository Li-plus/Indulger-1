package com.inftyloop.indulger.api;

import android.annotation.SuppressLint;
import android.util.Log;
import com.google.gson.GsonBuilder;
import com.inftyloop.indulger.MainApplication;
import com.inftyloop.indulger.util.NetworkUtils;
import okhttp3.*;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ApiRetrofit {
    public static final String TAG = ApiRetrofit.class.getSimpleName();
    private static Map<String, Object> mApiRetrofitMap = new HashMap<>();

    public static final Interceptor ONLINE_INTERCEPTOR = chain -> {
        okhttp3.Response response = chain.proceed(chain.request());
        int maxAge = 60; // read from cache for 60 seconds even if there is internet connection
        return response.newBuilder()
                .header("Cache-Control", "public, max-age=" + maxAge)
                .removeHeader("Pragma")
                .build();
    };

    public static final Interceptor OFFLINE_INTERCEPTOR = chain -> {
        Request request = chain.request();
        if (!NetworkUtils.isNetworkAvailable(MainApplication.getContext())) {
            int maxStale = 60 * 60 * 24 * 30; // Offline cache available for 30 days
            request = request.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .removeHeader("Pragma")
                    .build();
        }
        return chain.proceed(request);
    };

    public static final Interceptor CACHE_INTERCEPTOR = chain -> {
        CacheControl.Builder cacheBuilder = new CacheControl.Builder();
        cacheBuilder.maxAge(0, TimeUnit.SECONDS);
        cacheBuilder.maxStale(365, TimeUnit.DAYS);
        CacheControl cacheControl = cacheBuilder.build();

        Request request = chain.request();
        if (!NetworkUtils.isNetworkAvailable(MainApplication.getContext())) {
            request = request.newBuilder()
                    .cacheControl(cacheControl)
                    .build();
        }
        Response originalResponse = chain.proceed(request);
        if (NetworkUtils.isNetworkAvailable(MainApplication.getContext())) {
            int maxAge = 60; // read from cache
            return originalResponse.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public ,max-age=" + maxAge)
                    .build();
        } else {
            int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
            return originalResponse.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .build();
        }
    };

    public static final Interceptor LOG_INTERCEPTOR = chain -> {
        Request request = chain.request();
        long startTime = System.currentTimeMillis();
        okhttp3.Response response = chain.proceed(chain.request());
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        okhttp3.MediaType mediaType = response.body().contentType();
        String content = response.body().string();
        Log.i(TAG, "----------Request Start----------------");
        Log.i(TAG, "| " + request.toString());
        //Log.i(TAG, "| Response:" + content);
        Log.i(TAG, "----------Request End:" + duration + "ms----------");
        return response.newBuilder()
                .body(okhttp3.ResponseBody.create(mediaType, content))
                .build();
    };

    public static Interceptor TOUTIAO_HEADER_INTERCEPTOR = chain -> {
        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.108 Safari/537.36 2345Explorer/8.0.0.13547");
        builder.addHeader("Cache-Control", "max-age=0");
        builder.addHeader("Upgrade-Insecure-Requests", "1");
        builder.addHeader("X-Requested-With", "XMLHttpRequest");
        builder.addHeader("Cookie", "uuid=\"w:f2e0e469165542f8a3960f67cb354026\"; __tasessionId=4p6q77g6q1479458262778; csrftoken=7de2dd812d513441f85cf8272f015ce5; tt_webid=36385357187");
        return chain.proceed(builder.build());
    };

    public static Interceptor COMMON_HEADER_INTERCEPTOR = chain -> {
        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.108 Safari/537.36 2345Explorer/8.0.0.13547");
        builder.addHeader("Cache-Control", "max-age=0");
        builder.addHeader("Upgrade-Insecure-Requests", "1");
        builder.addHeader("X-Requested-With", "XMLHttpRequest");
        return chain.proceed(builder.build());
    };

    @SuppressWarnings("unchecked")
    public static <T> T getApiInstance(String alias, Class<T> api_class) {
        Object res = mApiRetrofitMap.getOrDefault(alias, null);
        return (T) res;
    }

    @SuppressWarnings("unchecked")
    public static <T> T buildOrGet(String alias, String base_server_url, Class<T> api_class, Interceptor... interceptors) {
        Object res = mApiRetrofitMap.getOrDefault(alias, null);
        if (res != null)
            return (T) res;
        else {
            File httpCacheDirectory = new File(MainApplication.getContext().getCacheDir(), "responses");
            int cacheSize = 10 * 1024 * 1024; // 10 MiB
            Cache cache = new Cache(httpCacheDirectory, cacheSize);
            OkHttpClient.Builder mClientBuilder = new OkHttpClient.Builder();
            for (Interceptor i : interceptors)
                mClientBuilder.addInterceptor(i);
            OkHttpClient mClient = mClientBuilder
                    .cache(cache)
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .build();

            Retrofit mRetrofit = new Retrofit.Builder()
                    .baseUrl(base_server_url)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(mClient)
                    .build();
            T created = mRetrofit.create(api_class);
            mApiRetrofitMap.put(alias, created);
            return created;
        }
    }
}
