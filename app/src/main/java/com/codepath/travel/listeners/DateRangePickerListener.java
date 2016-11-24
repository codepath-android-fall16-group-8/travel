package com.codepath.travel.listeners;

import java.util.Calendar;

/**
 * Listener interface for date range pickers.
 */
public interface DateRangePickerListener {
    void onDateRangeSet(Calendar startDate, Calendar endDate);
}
