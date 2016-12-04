package com.codepath.travel.fragments.dialog;

import static com.codepath.travel.models.parse.ParseModelConstants.END_DATE_KEY;
import static com.codepath.travel.models.parse.ParseModelConstants.START_DATE_KEY;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import com.codepath.travel.R;
import com.codepath.travel.helper.DateUtils;
import com.codepath.travel.listeners.DatePickerListener;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Fragment for selecting a single date.
 */
public class DatePickerFragment extends DialogFragment {
    private static final String TAG = DatePickerFragment.class.getSimpleName();
    private static final String DATE_KEY = "date";

    @BindView(R.id.calendarView) CalendarView calendarView;
    @BindView(R.id.tvDate) TextView tvDate;
    @BindView(R.id.btnSave) Button btnSave;
    @BindView(R.id.btnCancel) Button btnCancel;

    private Unbinder unbinder;

    private Calendar date;
    private Calendar startDate;
    private Calendar endDate;
    private DatePickerListener listener;

    public DatePickerFragment() {}

    public static DatePickerFragment newInstance(Date date, Date minDate, Date maxDate) {
        DatePickerFragment fragment = new DatePickerFragment();
        Bundle args = new Bundle();
        if (date != null) {
            args.putLong(DATE_KEY, date.getTime());
        }
        if (minDate != null) {
            args.putLong(START_DATE_KEY, minDate.getTime());
        }
        if (maxDate != null) {
            args.putLong(END_DATE_KEY, maxDate.getTime());
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_picker, parent, false);
        unbinder = ButterKnife.bind(this, view);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Bundle args = getArguments();
        long dateMillis = args.getLong(DATE_KEY, -1);
        long startDateMillis = args.getLong(START_DATE_KEY, -1);
        long endDateMillis = args.getLong(END_DATE_KEY, -1);
        date = DateUtils.todayAtStartOfDay();
        if (dateMillis >= 0) {
            date.setTimeInMillis(dateMillis);
        } else {
            date.setTimeInMillis(startDateMillis);
        }
        if (startDateMillis >= 0 && endDateMillis >= 0) {
            startDate = Calendar.getInstance();
            startDate.setTimeInMillis(startDateMillis);
            endDate = Calendar.getInstance();
            endDate.setTimeInMillis(endDateMillis);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        tvDate.setText(DateUtils.formatDate(getContext(), date.getTime()));
        calendarView.setDate(date.getTimeInMillis());
        calendarView.setMinDate(startDate.getTimeInMillis());
        calendarView.setMaxDate(endDate.getTimeInMillis());
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Log.d(TAG, String.format("Selected: %d-%d-%d", year, month, dayOfMonth));
            date = DateUtils.todayAtStartOfDay();
            date.set(Calendar.YEAR, year);
            date.set(Calendar.MONTH, month);
            date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            tvDate.setText(DateUtils.formatDate(getContext(), date.getTime()));
            calendarView.setDate(date.getTimeInMillis());
        });
        btnSave.setOnClickListener(v -> {
            listener.onDateSet(date);
            dismiss();
        });
        btnCancel.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DatePickerListener) {
            listener = (DatePickerListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement DatePickerListener");
        }
    }

    // When binding a fragment in onCreateView, set the views to null in onDestroyView.
    // ButterKnife returns an Unbinder on the initial binding that has an unbind method to do this automatically.
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
