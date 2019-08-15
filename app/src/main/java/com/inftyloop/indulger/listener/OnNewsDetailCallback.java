package com.inftyloop.indulger.listener;

import com.inftyloop.indulger.model.entity.NewsDetail;

public interface OnNewsDetailCallback {
    void onGetNewsDetailSuccess(NewsDetail detail);
    void onError();
}
