package com.codepath.travel.fragments;

import android.os.Bundle;

import com.codepath.travel.models.parse.Trip;

/**
 * Fragment to display a recycler view of upcoming trips.
 */
public class UpcomingTripListFragment extends TripListFragment {
    private static final String TAG = UpcomingTripListFragment.class.getSimpleName();

    public static UpcomingTripListFragment newInstance(String userId, boolean showUser) {
        UpcomingTripListFragment fragment = new UpcomingTripListFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID_ARG, userId);
        args.putBoolean(SHOW_USER_ARG, showUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void getTrips() {
        Trip.getUpcomingTripsForUser(mUserId, showUser, this::updateTrips);
    }
}
