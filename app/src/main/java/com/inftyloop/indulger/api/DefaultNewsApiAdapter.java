package com.inftyloop.indulger.api;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.inftyloop.indulger.listener.OnNewsListRefreshListener;
import com.inftyloop.indulger.model.entity.News;
import com.inftyloop.indulger.model.entity.NewsEntry;
import com.inftyloop.indulger.model.entity.NewsLoadRecord;
import com.inftyloop.indulger.util.DateUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private NewsLoadRecord getNewsLoadRecord(String channel) {
        List<NewsLoadRecord> res = LitePal.where("channelCode = ?", channel).find(NewsLoadRecord.class);
        if (res.size() == 0)
            return null;
        else
            return res.get(0);
    }

    @Override
    public void obtainNewsList(String channel, boolean isLoadingMore) {
        Date startTime = null, endTime = null;
        NewsLoadRecord record = getNewsLoadRecord(channel);
        if (record == null) {
            // load some initial data
            endTime = new Date();
        } else {
            if (isLoadingMore) {
                endTime = new Date(record.getLastLoadMoreTime());
            } else {
                startTime = new Date(record.getLastUpdatedTime());
                endTime = new Date();
            }
        }
        addSubscription(mApiService.getNewsInfo(NUM_ELEM_PER_PAGE, 1, startTime == null ? "" : DateUtils.formatDateTime(startTime, "yyyy-MM-dd hh:mm:ss"),
                endTime == null ? "" : DateUtils.formatDateTime(endTime, "yyyy-MM-dd hh:mm:ss"), "", CHANNEL_NAME_MAPPER.get(channel)),
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
                        Pattern pat = Pattern.compile("\\[(.*?)\\]");
                        JsonArray data = jsonObject.getAsJsonArray("data");
                        long newest_time = 0;
                        long oldest_time = Long.MAX_VALUE;
                        List<NewsEntry> news_entries = new ArrayList<>();
                        for (JsonElement d : data) {
                            JsonObject dd = d.getAsJsonObject();
                            long pubTime = DateUtils.getTimeStamp(dd.get("publishTime").getAsString(), "yyyy-MM-dd HH:mm:ss");
                            if (pubTime > 0) {
                                if (pubTime > newest_time)
                                    newest_time = pubTime;
                                if (pubTime < oldest_time)
                                    oldest_time = pubTime;
                                NewsEntry entry = new NewsEntry();
                                entry.setPublishTime(pubTime);
                                entry.setTitle(dd.get("title").getAsString());

                                String content = dd.get("content").getAsString();
                                content = "<p>" + content + "</p>";
                                content = content.replaceAll("\\n", "</p><p>");
                                entry.setContent(content);
                                
                                entry.setUrl(dd.get("url").getAsString());
                                entry.setUuid(dd.get("newsID").getAsString());
                                entry.setCategory(channel);
                                entry.setPublisherName(dd.get("publisher").getAsString());
                                String imgStr = dd.get("image").getAsString();
                                if (!TextUtils.isEmpty(imgStr)) {
                                    Matcher matcher = pat.matcher(imgStr);
                                    if (matcher.find()) {
                                        String arr = matcher.group(1);
                                        if (!arr.isEmpty()) {
                                            String[] urls = arr.split("\\s*,\\s*");
                                            for (String url : urls) {
                                                if (!url.trim().isEmpty())
                                                    entry.getImageUrls().add(url);
                                            }
                                        }
                                    }
                                }
                                JsonArray keywords = dd.getAsJsonArray("keywords");
                                int curr = 0;
                                for (JsonElement w : keywords) {
                                    if (curr >= 5) break;
                                    JsonObject ww = w.getAsJsonObject();
                                    entry.getKeywords().add(ww.get("word").getAsString());
                                    ++curr;
                                }
                                entry.save();
                                news_entries.add(entry);
                            }
                        }
                        if (newest_time != 0 && oldest_time != 0) {
                            NewsLoadRecord record = getNewsLoadRecord(channel);
                            if (record == null) {
                                record = new NewsLoadRecord();
                                record.setChannelCode(channel);
                                record.setLastLoadMoreTime(Long.MAX_VALUE);
                                record.setLastUpdatedTime(0);
                                record.setLastLoadMoreTime(oldest_time);
                                record.setLastUpdatedTime(newest_time);
                            } else {
                                if (isLoadingMore) {
                                    record.setLastLoadMoreTime(Math.min(record.getLastLoadMoreTime(), oldest_time));
                                } else {
                                    record.setLastUpdatedTime(newest_time);
                                    record.setLastLoadMoreTime(oldest_time);
                                }
                            }
                            record.save();
                        }
                        List<News> newsList = new ArrayList<>();
                        for (NewsEntry newsEntry : news_entries)
                            newsList.add(new News(newsEntry));
                        mRefreshListener.onNewsListRefresh(newsList);
                        Log.d("hey", news_entries.get(0).getContent());
                    }
                });
    }
}
