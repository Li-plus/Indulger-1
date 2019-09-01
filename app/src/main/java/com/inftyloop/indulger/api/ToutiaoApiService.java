package com.inftyloop.indulger.api;

import com.inftyloop.indulger.model.entity.VideoModel;
import com.inftyloop.indulger.model.response.NewsResponse;
import com.inftyloop.indulger.model.response.ResultResponse;
import com.inftyloop.indulger.model.response.VideoPathResponse;
import retrofit2.http.*;
import rx.Observable;

public interface ToutiaoApiService {
    String GET_ARTICLE_LIST = "api/news/feed/v62/?iid=5034850950&device_id=6096495334&refer=1&count=20&aid=13";

    @GET(GET_ARTICLE_LIST)
    Observable<NewsResponse> getNewsList(@Query("category") String category, @Query("max_behot_time") long maxBeHotTime, @Query("min_behot_time") long minBeHotTime);

    @GET
    Observable<String> getVideoHtml(@Url String url);

    @GET
    Observable<ResultResponse<VideoModel>> getVideoData(@Url String url);

    @Headers({
            "Content-Type:application/x-www-form-urlencoded; charset=UTF-8",
            "Cookie:PHPSESSIID=334267171504; _ga=GA1.2.646236375.1499951727; _gid=GA1.2.951962968.1507171739; Hm_lvt_e0a6a4397bcb500e807c5228d70253c8=1507174305;Hm_lpvt_e0a6a4397bcb500e807c5228d70253c8=1507174305; _gat=1",
            "Origin:http://toutiao.iiilab.com"
    })

    @POST("https://www.parsevideo.com/api.php")
    Observable<VideoPathResponse> parseVideo(@Query("url") String url, @Query("hash")String hash);
}