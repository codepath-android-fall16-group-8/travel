package com.codepath.travel.fragments;

import android.os.Bundle;

import com.codepath.travel.models.Trip;

/**
 * Fragment to display a horizontal scrolling recycler view of planned trips.
 */
public class PlannedTripListFragment extends TripListFragment {
    private static final String TAG = PlannedTripListFragment.class.getSimpleName();

    public static PlannedTripListFragment newInstance(String userId, boolean fetchUser) {
        PlannedTripListFragment fragment = new PlannedTripListFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID_ARG, userId);
        args.putBoolean(FETCH_USER_ARG, fetchUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void populateTrips() {
        if (mUserId == null) {
            return;
        }
        mTrips.clear();
        Trip.getPlannedTripsForUser(mUserId, fetchUser, (trips, e) -> {
            resetTripAdapter(trips, e);
        });
    }
}
