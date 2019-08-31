package com.inftyloop.indulger.util;

import android.content.Context;
import android.text.format.DateFormat;
import com.inftyloop.indulger.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static final long ONE_MINUTE_MILLIONS = 60 * 1000;
    public static final long ONE_HOUR_MILLIONS = 60 * ONE_MINUTE_MILLIONS;
    public static final long ONE_DAY_MILLIONS = 24 * ONE_HOUR_MILLIONS;

    public static String formatDateTime(Date date, String fmt) {
        return DateFormat.format(fmt, date).toString();
    }

    public static String unitFormat(int i) {
        String retStr;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public static String secToTime(int time) {
        String timeStr;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":"
                        + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String getShortTime(Context ctx, String date) {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date d = sdf.parse(date);
            str = getShortTime(ctx, d.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static long getTimeStamp(String dateStr, String fmt) {
        SimpleDateFormat format = new SimpleDateFormat(fmt);
        try {
            Date date = format.parse(dateStr);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getShortTime(Context ctx, long millis) {

        Date date = new Date(millis);
        Date curDate = new Date();

        String str;

        long durTime = curDate.getTime() - date.getTime();


        int dayStatus = calculateDayStatus(date, new Date());

        if (durTime <= 10 * ONE_MINUTE_MILLIONS) {
            str = ctx.getString(R.string.short_time_just_now);
        } else if (durTime < ONE_HOUR_MILLIONS) {
            long temp = durTime / ONE_MINUTE_MILLIONS;
            if(temp > 1)
                str = temp + ctx.getString(R.string.short_time_minutes_ago);
            else
                str = temp + ctx.getString(R.string.short_time_minute_ago);
        } else if (dayStatus == 0) {
            long temp  = durTime / ONE_HOUR_MILLIONS;
            if(temp > 1)
                str = temp + ctx.getString(R.string.short_time_hours_ago);
            else
                str = temp + ctx.getString(R.string.short_time_hour_ago);
        } else if (dayStatus == -1) {
            str = ctx.getString(R.string.short_time_yesterday) + DateFormat.format("HH:mm", date);
        } else if (isSameYear(date, curDate) && dayStatus < -1) {
            str = DateFormat.format("MM-dd", date).toString();
        } else {
            str = DateFormat.format("yyyy-MM", date).toString();
        }
        return str;
    }

    public static boolean isSameYear(Date targetTime, Date compareTime) {
        Calendar tarCalendar = Calendar.getInstance();
        tarCalendar.setTime(targetTime);
        int tarYear = tarCalendar.get(Calendar.YEAR);

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTime(compareTime);
        int comYear = compareCalendar.get(Calendar.YEAR);

        return tarYear == comYear;
    }

    public static int calculateDayStatus(Date targetTime, Date compareTime) {
        Calendar tarCalendar = Calendar.getInstance();
        tarCalendar.setTime(targetTime);
        int tarDayOfYear = tarCalendar.get(Calendar.DAY_OF_YEAR);

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTime(compareTime);
        int comDayOfYear = compareCalendar.get(Calendar.DAY_OF_YEAR);

        return tarDayOfYear - comDayOfYear;
    }
}
