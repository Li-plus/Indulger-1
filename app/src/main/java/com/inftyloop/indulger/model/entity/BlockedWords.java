package com.inftyloop.indulger.model.entity;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.Locale;

public class BlockedWords extends LitePalSupport {
    @Column(nullable = false, unique = true)
    private String word;

    public BlockedWords(String word) {
        this.word = word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWord() {
        return this.word;
    }
}
