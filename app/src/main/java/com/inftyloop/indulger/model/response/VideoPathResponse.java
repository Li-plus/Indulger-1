package com.inftyloop.indulger.model.response;

import androidx.annotation.Keep;

import java.util.List;

@Keep
public class VideoPathResponse {
    public String status;
    public List<VideoEntity> video;

    public class VideoEntity{
        public String url;
    }
}
