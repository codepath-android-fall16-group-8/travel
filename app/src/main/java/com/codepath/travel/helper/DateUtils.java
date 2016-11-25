package com.codepath.travel.helper;

import static android.text.format.DateUtils.DAY_IN_MILLIS;
import static android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
import static android.text.format.DateUtils.HOUR_IN_MILLIS;
import static android.text.format.DateUtils.WEEK_IN_MILLIS;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Helper methods for dealing with dates.
 */
public final class DateUtils {
    private static final String DAY_FORMAT = "%d day%s";
    private static final String WEEK_FORMAT = "%d week%s";

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

    public static Calendar todayAtEndOfDay() {
        Calendar today = Calendar.getInstance();
        return dateAtEndOfDay(today);
    }

    /**
     * Returns the given millis duration in terms of weeks or days,
     * whichever is largest. Not i18n friendly.
     *
     * @param millis the duration in millis
     * @return string formatted duration
     */
    public static String getDuration(long millis) {
        if (millis >= WEEK_IN_MILLIS) {
            final int weeks = (int) ((millis + WEEK_IN_MILLIS / 2) / WEEK_IN_MILLIS);
            String plural = weeks > 1 ? "s" : "";
            return String.format(Locale.ENGLISH, WEEK_FORMAT, weeks, plural);
        } else {
            final int days = (int) ((millis + DAY_IN_MILLIS / 2) / DAY_IN_MILLIS);
            String plural = days > 1 ? "s" : "";
            return String.format(Locale.ENGLISH, DAY_FORMAT, days, plural);
        }
    }

    private static Calendar dateAtStartOfDay(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    private static Calendar dateAtEndOfDay(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, day, 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar;
    }
}
