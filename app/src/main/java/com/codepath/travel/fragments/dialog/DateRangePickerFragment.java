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
import com.codepath.travel.listeners.DateRangePickerListener;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Fragment for selecting date ranges.
 */
public class DateRangePickerFragment extends DialogFragment {
    private static final String TAG = DateRangePickerFragment.class.getSimpleName();

    @BindView(R.id.calendarView) CalendarView calendarView;
    @BindView(R.id.tvStartDate) TextView tvStartDate;
    @BindView(R.id.tvEndDate) TextView tvEndDate;
    @BindView(R.id.tvDuration) TextView tvDuration;
    @BindView(R.id.btnSave) Button btnSave;
    @BindView(R.id.btnCancel) Button btnCancel;
    private Unbinder unbinder;

    private Calendar startDate;
    private Calendar endDate;
    private DateRangePickerListener listener;
    boolean selectStart;

    public DateRangePickerFragment() {}

    public static DateRangePickerFragment newInstance(Date startDate, Date endDate) {
        DateRangePickerFragment fragment = new DateRangePickerFragment();
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
        View view = inflater.inflate(R.layout.fragment_date_range_picker, parent, false);
        unbinder = ButterKnife.bind(this, view);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Bundle args = getArguments();
        long startDateMillis = args.getLong(START_DATE_KEY, -1);
        long endDateMillis = args.getLong(END_DATE_KEY, -1);
        if (startDateMillis >= 0 && endDateMillis >= 0) {
            startDate = Calendar.getInstance();
            startDate.setTimeInMillis(startDateMillis);
            endDate = Calendar.getInstance();
            endDate.setTimeInMillis(endDateMillis);
        }
        selectStart = true;

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (startDate != null && endDate !=null) {
            tvStartDate.setText(DateUtils.formatDate(getContext(), startDate.getTime()));
            tvEndDate.setText(DateUtils.formatDate(getContext(), endDate.getTime()));
            tvDuration.setText(DateUtils.getDuration(endDate.getTimeInMillis() - startDate.getTimeInMillis()));
        }
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Log.d(TAG, String.format("Selected: %d-%d-%d", year, month, dayOfMonth));
            if (selectStart) {
                startDate = DateUtils.todayAtStartOfDay();
                startDate.set(Calendar.YEAR, year);
                startDate.set(Calendar.MONTH, month);
                startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                tvStartDate.setText(DateUtils.formatDate(getContext(), startDate.getTime()));
                tvEndDate.setText("");
                tvDuration.setText("");
                calendarView.setMinDate(startDate.getTimeInMillis());
                calendarView.setDate(startDate.getTimeInMillis());
                selectStart = false;
                btnSave.setEnabled(false);
            } else {
                endDate = DateUtils.todayAtEndOfDay();
                endDate.set(Calendar.YEAR, year);
                endDate.set(Calendar.MONTH, month);
                endDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                tvEndDate.setText(DateUtils.formatDate(getContext(), endDate.getTime()));
                tvDuration.setText(DateUtils.getDuration(endDate.getTimeInMillis() - startDate.getTimeInMillis()));
                calendarView.setMinDate(0);
                calendarView.setDate(endDate.getTimeInMillis());
                selectStart = true;
                btnSave.setEnabled(true);
            }
        });
        btnSave.setOnClickListener(v -> {
            listener.onDateRangeSet(startDate, endDate);
            dismiss();
        });
        btnCancel.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DateRangePickerListener) {
            listener = (DateRangePickerListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement DateRangePickerListener");
        }
    }

    // When binding a fragment in onCreateView, set the views to null in onDestroyView.
    // ButterKnife returns an Unbinder on the initial binding that has an unbind method to do this automatically.
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
