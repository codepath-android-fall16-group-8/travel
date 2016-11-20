package com.codepath.travel.helper;

import static android.text.format.DateUtils.FORMAT_ABBREV_MONTH;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Helper methods for dealing with dates.
 */
public final class DateUtils {
    private DateUtils() {}

    /**
     * Get a formatted date string for the given  date.
     *
     * @param date the date to convert
     * @return the formatted date string
     */
    public static String formatDate(Context context, Date date) {
        if (date == null) {
            return "";
        }

        return android.text.format.DateUtils.formatDateTime(context, date.getTime(), FORMAT_ABBREV_MONTH);
    }

    /**
     * Get a formatted date range string for the given start and end dates.
     *
     * @param start the start date
     * @param end the end date
     * @return a formatted date range string
     */
    public static String formatDateRange(Context context, Date start, Date end) {
        if (start == null && end == null) {
            return "";
        }

        return android.text.format.DateUtils.formatDateRange(
                context, start.getTime(), end.getTime(), FORMAT_ABBREV_MONTH);
    }

    public static final int PAST = -1;
    public static final int NOW = 0;
    public static final int FUTURE = 1;

    public static int todayInRange(Date start, Date end) {
        if (start == null && end == null) {
            return FUTURE;
        }

        Calendar today = todayAtStartOfDay();
        Calendar startCal = dateAtStartOfDay(calendarFromDate(start));
        Calendar endCal = dateAtStartOfDay(calendarFromDate(end));
        if (today.after(endCal)) {
            return PAST;
        } else if (today.before(startCal)) {
            return FUTURE;
        } else {
            return NOW;
        }
    }

    public static Calendar calendarFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        return calendar;
    }

    public static Calendar todayAtStartOfDay() {
        Calendar today = Calendar.getInstance();
        return dateAtStartOfDay(today);
    }

    private static Calendar dateAtStartOfDay(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }
}
