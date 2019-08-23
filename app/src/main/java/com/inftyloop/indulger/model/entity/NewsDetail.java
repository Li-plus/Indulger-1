package com.inftyloop.indulger.model.entity;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class NewsDetail extends LitePalSupport{
    public static class MediaUserBean extends LitePalSupport {
        private String avatarUrl;
        private String id;
        private String displayName;

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
    }

    public MediaUserBean getPublisher() {
        return publisher;
    }

    public void setPublisher(MediaUserBean publisher) {
        this.publisher = publisher;
    }

    public long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(long publishTime) {
        this.publishTime = publishTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    private MediaUserBean publisher;
    private long publishTime;
    private String title;
    private String url;
    private String content;
    @Column(nullable = false, unique = true)
    private String uuid;

    public NewsDetail() {
        publisher = new MediaUserBean();
    }
}
