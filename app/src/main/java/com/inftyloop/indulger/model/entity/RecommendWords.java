package com.inftyloop.indulger.model.entity;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.Locale;

public class RecommendWords extends LitePalSupport {
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    @Column(unique=true, nullable=false)
    String word;
    @Column(nullable=false)
    int cnt;
}
