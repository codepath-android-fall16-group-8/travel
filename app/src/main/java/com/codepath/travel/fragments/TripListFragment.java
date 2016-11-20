package com.codepath.travel.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.travel.R;
import com.codepath.travel.adapters.TripsAdapter;
import com.codepath.travel.helper.ItemClickSupport;
import com.codepath.travel.models.Trip;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Fragment to display a horizontal scrolling recycler view of trips.
 */
public class TripListFragment extends Fragment {
    private static final String TAG = TripListFragment.class.getSimpleName();
    protected static final String USER_ID_ARG = "userId";
    protected static final String FETCH_USER_ARG = "fetchUser";

    // views
    @BindView(R.id.rvTrips) RecyclerView rvTrips;
    @BindView(R.id.pbLoading) ProgressBar pbLoading;
    @BindView(R.id.tvNoTripsFound) TextView tvNoTripsFound;

    protected Unbinder unbinder;

    protected ArrayList<Trip> mTrips;
    protected TripsAdapter mTripsAdapter;
    protected TripClickListener mListener;
    protected String mUserId;
    protected boolean fetchUser;

    public static TripListFragment newInstance(String userId, boolean fetchUser) {
        TripListFragment fragment = new TripListFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID_ARG, userId);
        args.putBoolean(FETCH_USER_ARG, fetchUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTrips = new ArrayList<>();
        mTripsAdapter = new TripsAdapter(getContext(), mTrips, fetchUser);
        Bundle args = getArguments();
        mUserId = args.getString(USER_ID_ARG);
        fetchUser = args.getBoolean(FETCH_USER_ARG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_trips, parent, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        tvNoTripsFound.setVisibility(View.GONE);
        rvTrips.setHasFixedSize(true);
        rvTrips.setAdapter(mTripsAdapter);
        rvTrips.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));

        ItemClickSupport.addTo(rvTrips).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    Trip trip = mTrips.get(position);
                    mListener.onTripClick(trip.getObjectId(), trip.getTitle());
                }
        );

        populateTrips();
    }

    public void populateTrips() {
        if (mUserId == null) {
            showNoTripsFound();
            return;
        }
        mTrips.clear();
        Trip.getAllTripsForUser(mUserId, fetchUser, (trips, e) -> {
            resetTripAdapter(trips, e);
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof TripClickListener)) {
            throw new RuntimeException("Activity should implement ImagePickerFragmentListener");
        }
        mListener = (TripClickListener) context;
    }

    // When binding a fragment in onCreateView, set the views to null in onDestroyView.
    // ButterKnife returns an Unbinder on the initial binding that has an unbind method to do this automatically.
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void setUser(String userId) {
        mUserId = userId;
        populateTrips();
    }

    protected void showNoTripsFound() {
        pbLoading.setVisibility(View.GONE);
        tvNoTripsFound.setVisibility(View.VISIBLE);
    }

    protected void resetTripAdapter(List<Trip> trips, ParseException e) {
        if (e == null && trips.size() > 0) {
            mTrips.addAll(trips);
            pbLoading.setVisibility(View.GONE);
            mTripsAdapter.notifyDataSetChanged();
        } else {
            showNoTripsFound();
            if (e != null) {
                Log.d(TAG, String.format("Failed to populate all trips: %s", e.getMessage()));
            }
        }
    }
}
