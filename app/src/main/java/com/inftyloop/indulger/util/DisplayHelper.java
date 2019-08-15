package com.inftyloop.indulger.util;

import android.app.Activity;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.view.View;


public class DisplayHelper {
    public static Point getScreenSize(@NonNull Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size;
    }

    public static int getScreenWidth(@NonNull Activity activity) {
        return getScreenSize(activity).x;
    }

    public static int getScreenHeight(@NonNull Activity activity) {
        return getScreenSize(activity).y;
    }

    public static int getMeasuredHeight(@NonNull View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return view.getMeasuredHeight();
    }

    public static int getMeasuredWidth(@NonNull View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return view.getMeasuredWidth();
    }

    public static Point getLocationOnScreen(@NonNull View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return new Point(location[0], location[1]);
    }

}
