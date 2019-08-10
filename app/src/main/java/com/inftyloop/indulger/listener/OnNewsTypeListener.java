package com.inftyloop.indulger.listener;

/**
 * Listener for recommended channels(news type)
 * @author zx1239856
 */
public interface OnNewsTypeListener {
    void onItemMove(int start, int end);
    void onMoveToMyChannel(int start, int end);
    void onMoveToRecommendedChannel(int start, int end);
}