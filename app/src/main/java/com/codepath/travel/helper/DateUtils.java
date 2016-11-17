package com.codepath.travel.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Helper methods for dealing with dates.
 */
public final class DateUtils {
    private static final String MONTH_DAY_YEAR_FORMAT = "MMM dd, yyyy";
    private static final SimpleDateFormat FULL_DATE_FORMAT = new SimpleDateFormat(MONTH_DAY_YEAR_FORMAT, Locale.ENGLISH);

    private DateUtils() {}

    /**
     * Get a formatted date string for the given {@link Calendar} date.
     * @param date the Calendar date to convert
     * @return the formatted date string
     */
    public static String getStringFromCalendar(Calendar date) {
        if (date != null) {
            return FULL_DATE_FORMAT.format(date.getTime());
        } else {
            return "";
        }
    }

    /**
     * Get a {@link} Calendar from the given date string. If the given date string is null or empty
     * or otherwise cannot be parsed, the current date is returned by default.
     * @param dateStr yyyy-mm-dd formatted string
     * @return a calendar date
     */
    public static Calendar getCalendarFromString(String dateStr) {
        Calendar date = Calendar.getInstance();
        try {
            date.setTime(FULL_DATE_FORMAT.parse(dateStr));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateAtStartOfDay(date);
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
