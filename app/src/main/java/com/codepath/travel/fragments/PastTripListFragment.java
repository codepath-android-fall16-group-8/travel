package com.codepath.travel.fragments;

import android.os.Bundle;

import com.codepath.travel.models.parse.Trip;

/**
 * Fragment to display a recycler view of past trips.
 */
public class PastTripListFragment extends TripListFragment {

    public static PastTripListFragment newInstance(String userId, boolean showUser) {
        PastTripListFragment fragment = new PastTripListFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID_ARG, userId);
        args.putBoolean(SHOW_USER_ARG, showUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void getTrips() {
        Trip.getPastTripsForUser(mUserId, showUser, this::updateTrips);
    }
}
