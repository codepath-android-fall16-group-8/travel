package com.codepath.travel.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Helper methods for dealing with dates.
 */
public final class DateUtils {
    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd", Locale.getDefault());;
    private static final SimpleDateFormat DAY_YEAR_FORMAT = new SimpleDateFormat("dd, yyyy", Locale.getDefault());
    private static final SimpleDateFormat MONTH_DAY_FORMAT = new SimpleDateFormat("MMM dd", Locale.getDefault());
    public static final SimpleDateFormat MONTH_DAY_YEAR_FORMAT = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private static final String RANGE_FORMAT = "%s - %s";

    private DateUtils() {}

    /**
     * Get a formatted date string for the given  date.
     * @param date the date to convert
     * @param dateFormat the date format to use
     * @return the formatted date string
     */
    public static String getStringFromDate(Date date, SimpleDateFormat dateFormat) {
        if (date != null) {
            return dateFormat.format(date);
        } else {
            return "";
        }
    }

    /**
     * Get a formatted date range string for the given start and end dates.
     *
     * @param start the start date
     * @param end the end date
     * @return a formatted date range string
     */
    public static String getDateRangeString(Date start, Date end) {
        if (start == null && end == null) {
            return "";
        }

        Calendar today = todayAtStartOfDay();
        int curYear = today.get(Calendar.YEAR);

        Calendar startCal = calendarFromDate(start);
        int startYear = startCal.get(Calendar.YEAR);

        if (start.compareTo(end) == 0) {
            // Single day:
            // MMM dd                           same day for current year
            // MMM dd, yyyy                     same day for another year
            return getStringFromDate(start, startYear == curYear ? MONTH_DAY_FORMAT : MONTH_DAY_YEAR_FORMAT);
        } else {
            // Range:
            // MMM dd - dd                      same month for cur year
            // MMM dd - MMM dd                  diff month for cur year
            // MMM dd - dd, yyyy                same month for another year
            // MMM dd - MMM dd, yyyy            diff month for another year
            // MMM dd, yyyy - MMM dd, yyyy      diff years
            int startMonth = startCal.get(Calendar.MONTH);
            Calendar endCal = calendarFromDate(end);
            int endYear = endCal.get(Calendar.YEAR);
            int endMonth = endCal.get(Calendar.MONTH);

            String startString;
            String endString;
            if (startYear != endYear) { // start and end have diff years:
                startString = getStringFromDate(start, MONTH_DAY_YEAR_FORMAT);
                endString = getStringFromDate(end, MONTH_DAY_YEAR_FORMAT);
            } else { // start and end have same years
                startString = getStringFromDate(start, MONTH_DAY_FORMAT);
                if (startYear == curYear) { // is current year
                    if (startMonth == endMonth) { // start and end have same month
                        endString = getStringFromDate(end, DAY_FORMAT);
                    } else { // different month
                        endString = getStringFromDate(end, MONTH_DAY_FORMAT);
                    }
                } else { // is another year
                    if (startMonth == endMonth) { // start and end have same month
                        endString = getStringFromDate(end, DAY_YEAR_FORMAT);
                    } else { // different month
                        endString = getStringFromDate(end, MONTH_DAY_YEAR_FORMAT);
                    }
                }
            }
            return String.format(RANGE_FORMAT, startString, endString);
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
