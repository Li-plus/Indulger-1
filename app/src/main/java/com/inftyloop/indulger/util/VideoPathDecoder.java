package com.inftyloop.indulger.util;

import android.text.TextUtils;
import android.util.Base64;
import com.inftyloop.indulger.api.ApiRetrofit;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.api.ToutiaoApiService;
import com.inftyloop.indulger.model.entity.Video;
import com.inftyloop.indulger.model.entity.VideoModel;
import com.inftyloop.indulger.model.response.ResultResponse;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

public abstract class VideoPathDecoder {
    private ToutiaoApiService api = ApiRetrofit.buildOrGet("toutiao_video", Definition.TOUTIAO_BASE_SERVER_URL, ToutiaoApiService.class,
            ApiRetrofit.TOUTIAO_HEADER_INTERCEPTOR, ApiRetrofit.LOG_INTERCEPTOR);

    public static final String TAG = VideoPathDecoder.class.getSimpleName();

    public void decodePath(String srcUrl) {
        api.getVideoHtml(srcUrl)
                .flatMap(new Func1<String, Observable<ResultResponse<VideoModel>>>() {
                    @Override
                    public Observable<ResultResponse<VideoModel>> call(String response) {
                        Pattern pattern = Pattern.compile("\"video_id\":(.+)");
                        Matcher matcher = pattern.matcher(response);
                        if (matcher.find()) {
                            String videoId = matcher.group(1);
                            if (TextUtils.isEmpty(videoId) || videoId.split(",") == null) {
                                return null;
                            }
                            videoId = videoId.split(",")[0].replace("\"","");
                            //1. /video/urls/v/1/toutiao/mp4/{videoid}?r={Math.random()} -> crc32
                            String r = getRandom();
                            CRC32 crc32 = new CRC32();
                            String s = String.format(Definition.URL_VIDEO, videoId, r);
                            crc32.update(s.getBytes());
                            String crcString = crc32.getValue() + "";
                            //2. http://i.snssdk.com/video/urls/v/1/toutiao/mp4/{videoid}?r={Math.random()}&s={crc32}
                            String url = Definition.HOST_VIDEO + s + "&s=" + crcString;
                            return api.getVideoData(url);
                        }
                        return null;
                    }
                })
                .map(new Func1<ResultResponse<VideoModel>, Video>() {
                    @Override
                    public Video call(ResultResponse<VideoModel> videoModelResultResponse) {
                        VideoModel.VideoListBean data = videoModelResultResponse.data.video_list;

                        if (data.video_3 != null) {
                            return updateVideo(data.video_3);
                        }
                        if (data.video_2 != null) {
                            return updateVideo(data.video_2);
                        }
                        if (data.video_1 != null) {
                            return updateVideo(data.video_1);
                        }
                        return null;
                    }

                    private String getRealPath(String base64) {
                        return new String(Base64.decode(base64.getBytes(), Base64.DEFAULT));
                    }

                    private Video updateVideo(Video video) {
                        //base64解码
                        video.main_url = getRealPath(video.main_url);
                        return video;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Video>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        onDecodeError("Failed to decode video");
                    }

                    @Override
                    public void onNext(Video video) {
                        onSuccess(video.main_url);
                    }
                });
    }

    private String getRandom() {
        Random random = new Random();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            result.append(random.nextInt(10));
        }
        return result.toString();
    }

    public abstract void onSuccess(String url);
    public abstract void onDecodeError(String errorMsg);
}
