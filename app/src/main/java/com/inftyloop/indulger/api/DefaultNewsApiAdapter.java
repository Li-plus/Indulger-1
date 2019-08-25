package com.inftyloop.indulger.api;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.inftyloop.indulger.listener.OnNewsListRefreshListener;
import com.inftyloop.indulger.model.entity.News;
import com.inftyloop.indulger.util.DateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.Subscriber;

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

    private OnNewsListRefreshListener mRefreshListener;

    private DefaultNewsApiService mApiService = null;
    private Date lastRefreshedTime = null;
    private Date lastLoadedMoreTime = null;
    private int curPage = 0;
    private int numElements = 0;
    private final int NUM_ELEM_PER_PAGE = 35;
    private static final Map<String, String> CHANNEL_NAME_MAPPER = Collections.unmodifiableMap(new HashMap<String, String>() {{
        put("news_society", "社会");
        put("news_entertainment", "娱乐");
        put("news_tech", "科技");
        put("news_auto", "汽车");
        put("news_sports", "体育");
        put("news_finance", "财经");
        put("news_health", "健康");
        put("news_military", "军事");
        put("news_education", "教育");
        put("news_culture", "文化");
    }});

    public DefaultNewsApiAdapter(OnNewsListRefreshListener refreshListener) {
        mApiService = ApiRetrofit.buildOrGet("THUDefault", DefaultNewsApiService.BASE_URL, DefaultNewsApiService.class, ApiRetrofit.CACHE_INTERCEPTOR,
                ApiRetrofit.LOG_INTERCEPTOR, ApiRetrofit.COMMON_HEADER_INTERCEPTOR);
        mRefreshListener = refreshListener;
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
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                        // TODO - notify view
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        if (lastRefreshedTime == null) { // initial state
                            lastLoadedMoreTime = c.getTime();
                            curPage = 1;
                        }
                        lastRefreshedTime = new Date();
                        numElements = jsonObject.get("total").getAsInt();
                        Log.i(TAG, "successfully get " + numElements + " pieces of channel " + channel);
                        // TODO - save this time
                        // TODO - notify view
                    }
                });
    }

    @Override
    public void loadMoreNewsList(String channel) {
        Calendar c = Calendar.getInstance();
        if (lastLoadedMoreTime == null) {
            lastLoadedMoreTime = new Date();
            c.add(Calendar.DATE, -1);
            lastLoadedMoreTime = c.getTime();
        } else {
            c.setTime(lastLoadedMoreTime);
        }

        // if the whole page is loaded, just push back time for one day
        boolean pageChanged = false;
        if (curPage * NUM_ELEM_PER_PAGE >= numElements) {
            c.add(Calendar.DATE, -1);
            pageChanged = true;
        }
        addSubscription(mApiService.getNewsInfo(NUM_ELEM_PER_PAGE, pageChanged ? 1 : curPage + 1, DateUtils.formatDateTime(c.getTime(), "yyyy-MM-dd hh:mm:ss"), DateUtils.formatDateTime(new Date(), "yyyy-MM-dd hh:mm:ss"), "", CHANNEL_NAME_MAPPER.get(channel)),
                new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                        // TODO - notify view
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        curPage = (c.getTime().compareTo(lastLoadedMoreTime) != 0) ? 1 : curPage + 1;
                        lastLoadedMoreTime = c.getTime();
                        Log.i(TAG, "successfully get " + jsonObject.get("total").getAsString() + " pieces from " + channel);
                        // TODO - save this time
                        JsonArray data = jsonObject.getAsJsonArray("data");
                        Log.i(TAG, data.toString());
                        List<News> newsToDisplay = new ArrayList<>();
                        for (JsonElement jsonNewsElement : data) {
                            JsonObject jsonNews = jsonNewsElement.getAsJsonObject();
                            String title = jsonNews.get("title").getAsString();
                            String publisher = jsonNews.get("publisher").getAsString();
                            String publishTime = jsonNews.get("publishTime").getAsString();
                            String imageListStr = jsonNews.get("image").getAsString();
                            imageListStr = imageListStr.replaceAll("\\[|\\]", "");
                            List<String> imageList = new ArrayList<>(Arrays.asList(imageListStr.split("\\s*,\\s*")));
                            imageList.removeIf(Predicate.isEqual(""));
                            News news = new News(title, publisher, publishTime);
                            if (imageList.size() >= 3) {
                                news.type = News.THREE_IMAGES_NEWS;
                                news.image1 = imageList.get(0);
                                news.image2 = imageList.get(1);
                                news.image3 = imageList.get(2);
                            } else if (imageList.size() >= 1) {
                                news.type = News.SINGLE_IMAGE_NEWS;
                                news.image1 = imageList.get(0);
                            }
                            newsToDisplay.add(news);
                        }
                        mRefreshListener.onNewsListRefresh(newsToDisplay);
                    }
                });
    }

    @Override
    public void loadDetailedNews(String news_id) {
        // TODO - obtain from database
    }

    @Override
    public void updateRecommendedList() {
    }

    @Override
    public void loadMoreRecommendedList() {
    }
}
