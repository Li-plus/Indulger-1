package com.inftyloop.indulger.model.entity;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class BlockedWords extends LitePalSupport {
    @Column(nullable = false, unique = true)
    String word;
}
