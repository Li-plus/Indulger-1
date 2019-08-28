package com.inftyloop.indulger.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import androidx.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


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


    public static String getColorStringFromAttr(@NonNull Context mContext, int attr_id) {
        TypedValue outValue = new TypedValue();
        mContext.getTheme().resolveAttribute(attr_id, outValue, true);
        String bg_color = Integer.toHexString(mContext.getColor(outValue.resourceId));
        if (bg_color.length() >= 6)
            bg_color = "#" + bg_color.substring(bg_color.length() - 6);
        return bg_color;
    }

    private static InputMethodManager getInputMethodManager(Context context) {
        return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public static void showKeyboard(Context ctx, View target) {
        if (ctx == null || target == null)
            return;
        InputMethodManager imm = getInputMethodManager(ctx);
        imm.showSoftInput(target, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void showKeyboardInDialog(Dialog dialog, EditText target) {
        if (dialog == null || target == null)
            return;
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        target.requestFocus();
    }

    public static void hideKeyboard(Context context, View target) {
        if (context == null || target == null) {
            return;
        }
        InputMethodManager imm = getInputMethodManager(context);
        imm.hideSoftInputFromWindow(target.getWindowToken(), 0);
    }

    public static void hideKeyboard(@NonNull Activity activity) {
        View view = activity.getWindow().getDecorView();
        hideKeyboard(activity, view);
    }
}
