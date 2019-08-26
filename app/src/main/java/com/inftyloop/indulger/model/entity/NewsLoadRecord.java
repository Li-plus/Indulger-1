package com.inftyloop.indulger.model.entity;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class NewsLoadRecord extends LitePalSupport {
    private long lastUpdatedTime;
    private long lastLoadMoreTime;
    @Column(nullable = false, unique = true)
    private String channelCode;

    public long getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(long lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public long getLastLoadMoreTime() {
        return lastLoadMoreTime;
    }

    public void setLastLoadMoreTime(long lastLoadMoreTime) {
        this.lastLoadMoreTime = lastLoadMoreTime;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }
}
