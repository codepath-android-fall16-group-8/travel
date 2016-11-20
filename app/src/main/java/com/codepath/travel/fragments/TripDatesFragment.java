package com.codepath.travel.fragments;

import static com.codepath.travel.models.ParseModelConstants.END_DATE_KEY;
import static com.codepath.travel.models.ParseModelConstants.START_DATE_KEY;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.codepath.travel.R;
import com.codepath.travel.helper.DateUtils;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Fragment for selecting a trip's start and end dates
 */
public class TripDatesFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = TripDatesFragment.class.getSimpleName();
    private static final int REQUEST_CODE_DATE_PICKER = 42;

    @BindView(R.id.tvTripDates) TextView tvTripDates;
    private Unbinder unbinder;

    private Calendar startDate;
    private Calendar endDate;
    private TripDatesListener listener;

    public interface TripDatesListener {
        void tripDatesOnSet(Calendar startDate, Calendar endDate);
    }

    public TripDatesFragment() {}

    public static TripDatesFragment newInstance(Date startDate, Date endDate) {
        TripDatesFragment fragment = new TripDatesFragment();
        Bundle args = new Bundle();
        if (startDate != null && endDate != null) {
            args.putLong(START_DATE_KEY, startDate.getTime());
            args.putLong(END_DATE_KEY, endDate.getTime());
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_dates, parent, false);
        unbinder = ButterKnife.bind(this, view);
        listener = (TripDatesListener) getActivity();

        // check for existing trip dates
        Bundle args = getArguments();
        long startDateMillis = args.getLong(START_DATE_KEY, -1);
        long endDateMillis = args.getLong(END_DATE_KEY, -1);
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
        tvTripDates.setOnClickListener(v -> launchDatePicker());
        if (startDate != null && endDate != null) {
            setTripDatesText();
        }
    }

    private void launchDatePicker() {
        FragmentManager fm = getFragmentManager();
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setTargetFragment(TripDatesFragment.this, REQUEST_CODE_DATE_PICKER);
        dpd.show(fm, "DateRangePickerDialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth,
            int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        startDate = Calendar.getInstance();
        startDate.set(Calendar.YEAR, year);
        startDate.set(Calendar.MONTH, monthOfYear);
        startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        endDate = Calendar.getInstance();
        endDate.set(Calendar.YEAR, yearEnd);
        endDate.set(Calendar.MONTH, monthOfYearEnd);
        endDate.set(Calendar.DAY_OF_MONTH, dayOfMonthEnd);

        setTripDatesText();
        listener.tripDatesOnSet(startDate, endDate);
    }

    private void setTripDatesText() {
        String formattedDateString = DateUtils.formatDateRange(getContext(), startDate.getTime(), endDate.getTime());
        tvTripDates.setText(formattedDateString);
        Log.d(TAG, String.format("Set trip dates string: %s", formattedDateString));
    }

    // When binding a fragment in onCreateView, set the views to null in onDestroyView.
    // ButterKnife returns an Unbinder on the initial binding that has an unbind method to do this automatically.
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
