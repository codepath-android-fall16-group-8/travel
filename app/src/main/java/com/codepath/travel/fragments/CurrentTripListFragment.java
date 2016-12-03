package com.codepath.travel.fragments;

import android.os.Bundle;

import com.codepath.travel.models.parse.Trip;

/**
 * Fragment to display a recycler view of current trips.
 */
public class CurrentTripListFragment extends TripListFragment {

    public static CurrentTripListFragment newInstance(String userId, boolean showUser) {
        CurrentTripListFragment fragment = new CurrentTripListFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID_ARG, userId);
        args.putBoolean(SHOW_USER_ARG, showUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void getTrips() {
        Trip.getCurrentTripsForUser(mUserId, showUser, this::updateTrips);
    }
}
