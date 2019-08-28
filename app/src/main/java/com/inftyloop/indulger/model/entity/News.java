package com.inftyloop.indulger.model.entity;

import java.util.List;

public class News {
    public static final int NO_MORE_FOOTER = -4;
    public static final int LOAD_MORE_FOOTER = -1;
    public static final int TEXT_NEWS = 0;
    public static final int SINGLE_IMAGE_NEWS = 1;
    public static final int THREE_IMAGES_NEWS = 2;
    public static final int VIDEO_NEWS = 3;

    private int mType;
    private boolean mIsRead = false;
    private NewsEntry mEntry;

    public News(int type) {
        this.mType = type;
    }

    public News(NewsEntry newsEntry) {
        this.mEntry = newsEntry;
        List<String> imageUrls = newsEntry.getImageUrls();
        if (newsEntry.getVideoUrl() != null) {
            this.mType = VIDEO_NEWS;
        } else if (imageUrls.size() >= 3) {
            this.mType = THREE_IMAGES_NEWS;
        } else if (imageUrls.size() >= 1) {
            this.mType = SINGLE_IMAGE_NEWS;
        }
    }

    public NewsEntry getNewsEntry() {
        return mEntry;
    }

    public int getType() {
        return mType;
    }

    public boolean getIsRead() {
        return mIsRead;
    }

    public void setIsRead(boolean isRead) {
        mIsRead = isRead;
    }
}
