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
import com.codepath.travel.models.parse.Trip;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Fragment to display a recycler view of trips.
 */
public class TripListFragment extends Fragment {
    private static final String TAG = TripListFragment.class.getSimpleName();
    protected static final String USER_ID_ARG = "userId";
    protected static final String SHOW_USER_ARG = "showUser";
    protected static final String SHOW_SHARING_ARG = "showSharing";

    // views
    @BindView(R.id.rvTrips) RecyclerView rvTrips;
    @BindView(R.id.pbLoading) ProgressBar pbLoading;
    @BindView(R.id.tvNoTripsFound) TextView tvNoTripsFound;

    protected Unbinder unbinder;

    protected ArrayList<Trip> mTrips;
    protected TripsAdapter mTripsAdapter;
    protected TripClickListener mListener;
    protected String mUserId;
    protected boolean showUser;
    protected boolean showSharing;

    public static TripListFragment newInstance(String userId, boolean showUser,
            boolean showSharing) {
        TripListFragment fragment = new TripListFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID_ARG, userId);
        args.putBoolean(SHOW_USER_ARG, showUser);
        args.putBoolean(SHOW_SHARING_ARG, showSharing);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mUserId = args.getString(USER_ID_ARG);
        showUser = args.getBoolean(SHOW_USER_ARG, false);
        showSharing = args.getBoolean(SHOW_SHARING_ARG, false);

        mTrips = new ArrayList<>();
        mTripsAdapter = new TripsAdapter(getContext(), mTrips, showUser, showSharing);
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
                    boolean isOwner = false;
                    String curUserId = ParseUser.getCurrentUser().getObjectId();
                    if (trip.getUser() != null) {
                        isOwner = trip.getUser().getObjectId().equals(curUserId);
                    } else {
                        isOwner = mUserId.equals(curUserId);
                    }
                    mListener.onTripClick(trip.getObjectId(), trip.getTitle(), isOwner);
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
        getTrips();
    }

    protected void getTrips() {
        Trip.getAllTripsForUser(mUserId, showUser, this::updateTrips);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof TripClickListener)) {
            throw new RuntimeException("Activity should implement TripClickListener");
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
    }

    protected void showNoTripsFound() {
        pbLoading.setVisibility(View.GONE);
        tvNoTripsFound.setVisibility(View.VISIBLE);
    }

    protected void updateTrips(List<Trip> trips, ParseException e) {
        if (e == null && trips.size() > 0) {
            mTrips.addAll(trips);
            pbLoading.setVisibility(View.GONE);
            mTripsAdapter.notifyDataSetChanged();
        } else {
            showNoTripsFound();
            if (e != null) {
                Log.d(TAG, String.format("Failed to populate trips: %s", e.getMessage()));
            }
        }
    }
}
