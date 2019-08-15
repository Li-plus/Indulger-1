package com.inftyloop.indulger.util;

import android.content.Context;
import android.util.TypedValue;
import com.inftyloop.indulger.R;

public class DisplayUtils {
    public static String getColorStringFromAttr(Context mContext, int attr_id) {
        TypedValue outValue = new TypedValue();
        mContext.getTheme().resolveAttribute(attr_id, outValue, true);
        String bg_color = Integer.toHexString(mContext.getColor(outValue.resourceId));
        if(bg_color.length() >= 6)
            bg_color = "#" + bg_color.substring(bg_color.length() - 6);
        return bg_color;
    }
}
