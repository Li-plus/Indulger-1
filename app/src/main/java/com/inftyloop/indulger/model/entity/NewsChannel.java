package com.inftyloop.indulger.model.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

public class NewsChannel implements MultiItemEntity, Serializable {
    public static final int TYPE_MY = 0;
    public static final int TYPE_RECOMMENDED = 1;
    public static final int TYPE_MY_LIST = 2;  // Default
    public static final int TYPE_RECOMMENDED_LIST = 3;

    transient public String title;
    public String channelCode;
    public int itemType;
    public int uniqueID;

    public NewsChannel(String t, String c, int id) { this(TYPE_MY_LIST, t, c, id); }

    public NewsChannel(int type, String t, String c, int id) {
        title = t;
        channelCode = c;
        itemType = type;
        uniqueID = id;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int type) {
        itemType = type;
    }
}
