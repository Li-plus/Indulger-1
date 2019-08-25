package com.inftyloop.indulger.model.entity;

import android.support.annotation.NonNull;

public class News {
    public static final int NO_MORE_FOOTER = -4;
    public static final int NOTIFICATION_HEADER = -2;
    public static final int LOAD_MORE_FOOTER = -1;
    public static final int TEXT_NEWS = 0;
    public static final int SINGLE_IMAGE_NEWS = 1;
    public static final int THREE_IMAGES_NEWS = 2;

    public int type;
    public String title;
    public String author;
    public String time;
    public String image1;
    public String image2;
    public String image3;
    public boolean isRead = false;

    public News(int type) {
        this.type = type;
    }

    public News(@NonNull String title, @NonNull String author, @NonNull String time) {
        this(TEXT_NEWS, title, author, time, null, null, null);
    }

    public News(@NonNull String title, @NonNull String author, @NonNull String time, @NonNull String image1) {
        this(SINGLE_IMAGE_NEWS, title, author, time, image1, null, null);
    }

    public News(@NonNull String title, @NonNull String author, @NonNull String time, @NonNull String image1, @NonNull String image2, @NonNull String image3) {
        this(THREE_IMAGES_NEWS, title, author, time, image1, image2, image3);
    }

    private News(int type, String title, String author, String time, String image1, String image2, String image3) {
        this.type = type;
        this.title = title;
        this.author = author;
        this.time = time;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
    }
}
