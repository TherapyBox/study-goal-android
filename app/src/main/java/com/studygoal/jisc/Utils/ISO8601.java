package com.studygoal.jisc.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public final class ISO8601 {
    private static final String TAG = ISO8601.class.getSimpleName();

    /**
     * Transform Calendar to ISO 8601 string.
     */
    public static String fromCalendar(final Calendar calendar) {
        Date date = calendar.getTime();
        String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(date);
        return formatted.substring(0, 22) + ":" + formatted.substring(22);
    }

    public static String fromTime(final long time) {
        Date date = new Date();
        date.setTime(time);
        String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(date);
        return formatted.substring(0, 22) + ":" + formatted.substring(22);
    }

    public static String fromDate(final long time) {
        Date date = new Date();
        date.setTime(time);
        String formatted = new SimpleDateFormat("yyyy-MM-dd").format(date);
        return formatted;
    }

    /**
     * Get current date and time formatted as ISO 8601 string.
     */
    public static String now() {
        return fromCalendar(GregorianCalendar.getInstance());
    }

    /**
     * Transform ISO 8601 string to Calendar.
     */
    public static Calendar toCalendar(final String iso8601string) {
        Calendar calendar = GregorianCalendar.getInstance();
        String s = iso8601string.replace("Z", "+00:00");

        try {
            s = s.substring(0, 22) + s.substring(23);  // to get rid of the ":"

            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(s);
            calendar.setTime(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return calendar;
    }

    public static long toTime(final String iso8601string) {
        Calendar calendar = GregorianCalendar.getInstance();

        try {
            if (iso8601string != null) {
                String s = iso8601string.replace("Z", "+00:00");
                s = s.substring(0, 22) + s.substring(23);  // to get rid of the ":"
                Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(s);
                calendar.setTime(date);
            }
        } catch (Exception e) {
            //TLog.e(TAG, e);
        }

        return calendar.getTimeInMillis();
    }

    public static long toDate(final String iso8601string) {
        Calendar calendar = GregorianCalendar.getInstance();

        try {
            if (iso8601string != null) {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(iso8601string);
                calendar.setTime(date);
            }
        } catch (Exception e) {
            //TLog.e(TAG, e);
        }

        return calendar.getTimeInMillis();
    }
}