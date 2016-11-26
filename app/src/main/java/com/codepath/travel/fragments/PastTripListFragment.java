package com.codepath.travel.fragments;

import android.os.Bundle;

import com.codepath.travel.models.parse.Trip;

/**
 * Fragment to display a recycler view of past trips.
 */
public class PastTripListFragment extends TripListFragment {

    public static PastTripListFragment newInstance(String userId, boolean fetchUser) {
        PastTripListFragment fragment = new PastTripListFragment();
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
        Trip.getPastTripsForUser(mUserId, fetchUser, (trips, e) -> {
            resetTripAdapter(trips, e);
        });
    }
}
