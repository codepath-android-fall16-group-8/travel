package com.codepath.travel.fragments;

import android.os.Bundle;

import com.codepath.travel.models.parse.Trip;

/**
 * Fragment to display a recycler view of current trips.
 */
public class CurrentTripListFragment extends TripListFragment {

    public static CurrentTripListFragment newInstance(String userId, boolean fetchUser) {
        CurrentTripListFragment fragment = new CurrentTripListFragment();
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
        Trip.getCurrentTripsForUser(mUserId, fetchUser, (trips, e) -> {
            resetTripAdapter(trips, e);
        });
    }
}
