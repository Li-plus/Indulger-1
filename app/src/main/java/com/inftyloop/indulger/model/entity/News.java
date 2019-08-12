package com.inftyloop.indulger.model.entity;

public class News {
    public String title;
    public String author;
    public String time;
    public Integer image1;
    public Integer image2;
    public Integer image3;

    public News(String title, String author, String time, Integer image1, Integer image2, Integer image3) {
        this.title = title;
        this.author = author;
        this.time = time;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
    }
}
