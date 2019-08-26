package com.inftyloop.indulger.listener;

import com.inftyloop.indulger.model.entity.NewsEntry;

public interface OnNewsDetailCallback {
    void onGetNewsDetailSuccess(NewsEntry detail);
    void onError();
}
