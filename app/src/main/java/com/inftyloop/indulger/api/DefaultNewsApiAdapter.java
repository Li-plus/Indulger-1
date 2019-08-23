package com.inftyloop.indulger.api;

import android.util.Log;
import com.google.gson.JsonObject;
import com.inftyloop.indulger.util.DateUtils;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.Subscriber;

import java.util.*;

interface DefaultNewsApiService {
    String BASE_URL = "https://api2.newsminer.net/";
    String GET_NEWS_INFO = "svc/news/queryNewsList";

    @GET(GET_NEWS_INFO)
    Observable<JsonObject> getNewsInfo(@Query("size") int size, @Query("page") int page, @Query("startDate") String startDate,
                                       @Query("endDate") String endDate, @Query("words") String words,
                                       @Query("categories") String categories);
}

public class DefaultNewsApiAdapter extends BaseNewsApiAdapter {
    public final static String TAG = DefaultNewsApiAdapter.class.getSimpleName();

    private DefaultNewsApiService mApiService = null;
    private Date lastRefreshedTime = null;
    private Date lastLoadedMoreTime = null;
    private int curPage = -1;
    private int numElements = 0;
    private final int NUM_ELEM_PER_PAGE = 35;
    private static final Map<String, String> CHANNEL_NAME_MAPPER = Collections.unmodifiableMap(new HashMap<String, String>()
    {{
        put("news_society","社会");
        put("news_entertainment","娱乐");
        put("news_tech","科技");
        put("news_auto","汽车");
        put("news_sports","体育");
        put("news_finance","财经");
        put("news_health","健康");
        put("news_military","军事");
        put("news_education","教育");
        put("news_culture","文化");
    }});

    public DefaultNewsApiAdapter() {
        mApiService = ApiRetrofit.buildOrGet("THUDefault", DefaultNewsApiService.BASE_URL, DefaultNewsApiService.class, ApiRetrofit.CACHE_INTERCEPTOR,
                ApiRetrofit.LOG_INTERCEPTOR, ApiRetrofit.COMMON_HEADER_INTERCEPTOR);
    }

    @Override
    public void updateNewsList(String channel) {
        // TODO - save last refreshed timestamp to settings, when clearing cache, delete it
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, -1);
        addSubscription(mApiService.getNewsInfo(NUM_ELEM_PER_PAGE, 1, DateUtils.formatDateTime(lastRefreshedTime == null ? c.getTime() : lastRefreshedTime, "yyyy-MM-dd hh:mm:ss"),
                DateUtils.formatDateTime(new Date(), "yyyy-MM-dd hh:mm:ss"), "", CHANNEL_NAME_MAPPER.get(channel)),
                new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                        // TODO - notify view
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        if(lastRefreshedTime == null) { // initial state
                            lastLoadedMoreTime = c.getTime();
                            curPage = 1;
                        }
                        lastRefreshedTime = new Date();
                        numElements = jsonObject.get("total").getAsInt();
                        Log.i(TAG, "success");
                        // TODO - save this time
                        // TODO - notify view
                    }
                });
    }

    @Override
    public void loadMoreNewsList(String channel) {
        if(lastRefreshedTime == null) {
            updateNewsList(channel);
            return;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(lastLoadedMoreTime);
        // if the whole page is loaded, just push back time for one day
        boolean pageChanged = false;
        if(curPage * NUM_ELEM_PER_PAGE >= numElements) {
            c.add(Calendar.DATE, -1);
            pageChanged = true;
        }
        addSubscription(mApiService.getNewsInfo(NUM_ELEM_PER_PAGE, pageChanged ? 1 : curPage + 1, DateUtils.formatDateTime(c.getTime(), "yyyy-MM-dd hh:mm:ss"), DateUtils.formatDateTime(lastLoadedMoreTime, "yyyy-MM-dd hh:mm:ss"), "", CHANNEL_NAME_MAPPER.get(channel)),
                new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                        // TODO - notify view
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        curPage = (c.getTime().compareTo(lastLoadedMoreTime) != 0) ? 1 : curPage + 1;
                        lastLoadedMoreTime = c.getTime();
                        Log.i(TAG, "success");
                        // TODO - save this time
                        // TODO - notify view
                    }
                });
    }

    @Override
    public void loadDetailedNews(String news_id) {
        // TODO - obtain from database
    }

    @Override
    public void updateRecommendedList() {}

    @Override
    public void loadMoreRecommendedList() {}
}
