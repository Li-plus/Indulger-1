package com.inftyloop.indulger.model.entity;

public class NewsDetail {
    public static class MediaUserBean {
        public String avatarUrl;
        public String id;
        public String displayName;
    }

    public MediaUserBean publisher;
    public int publishTime;
    public String title;
    public String url;
    public String content;

    public NewsDetail() {
        publisher = new MediaUserBean();
    }
}
