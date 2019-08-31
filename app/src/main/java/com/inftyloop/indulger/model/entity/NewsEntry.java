package com.inftyloop.indulger.model.entity;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

public class NewsEntry extends LitePalSupport {
    public NewsEntry() {
        super();
    }

    public NewsEntry(NewsFavEntry entry) {
        super();
        publisherName = entry.getPublisherName();
        publisherAvatarUrl = entry.getPublisherAvatarUrl();
        publishTime = entry.getPublishTime();
        title = entry.getTitle();
        url = entry.getUrl();
        content = entry.getContent();
        category = entry.getCategory();
        imageUrls = entry.getImgUrls();
        keywords = entry.getKeywords();
        videoUrl = entry.getVideoUrl();
        uuid = entry.getUuid();
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

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getPublisherAvatarUrl() {
        return publisherAvatarUrl;
    }

    public void setPublisherAvatarUrl(String publisherAvatarUrl) {
        this.publisherAvatarUrl = publisherAvatarUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getVideoUrl() {
        return this.videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    private String publisherName;
    private String publisherAvatarUrl;
    private long publishTime;
    private String title;
    private String url;
    private String content;
    private String category;
    private List<String> imageUrls = new ArrayList<>();
    private List<String> keywords = new ArrayList<>();
    private String videoUrl;
    @Column(nullable = false, unique = true)
    private String uuid;
}
