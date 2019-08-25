package com.inftyloop.indulger.listener;

import com.inftyloop.indulger.model.entity.News;

import java.util.List;

public interface OnNewsListRefreshListener {
    void onNewsListRefresh(List<News> newsList);
}
