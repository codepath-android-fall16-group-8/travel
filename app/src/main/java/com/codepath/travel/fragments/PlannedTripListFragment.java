package com.codepath.travel.fragments;

import android.os.Bundle;

import com.codepath.travel.models.parse.Trip;

/**
 * Fragment to display a recycler view of planned trips.
 */
public class PlannedTripListFragment extends TripListFragment {
    private static final String TAG = PlannedTripListFragment.class.getSimpleName();

    public static PlannedTripListFragment newInstance(String userId, boolean showUser) {
        PlannedTripListFragment fragment = new PlannedTripListFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID_ARG, userId);
        args.putBoolean(SHOW_USER_ARG, showUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void getTrips() {
        Trip.getPlannedTripsForUser(mUserId, showUser, this::updateTrips);
    }
}
