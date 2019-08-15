package com.inftyloop.indulger.model.entity;

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
    public Integer image1;
    public Integer image2;
    public Integer image3;

    public News(int type) {
        this.type = type;
    }

    public News(int type, String title, String author, String time, Integer image1, Integer image2, Integer image3) {
        this.type = type;
        this.title = title;
        this.author = author;
        this.time = time;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
    }
}
