package com.codepath.travel.fragments;

import android.os.Bundle;

import com.codepath.travel.models.parse.Trip;

/**
 * Fragment to display a recycler view of followed users' trips.
 */
public class FollowingTripListFragment extends TripListFragment {

    public static FollowingTripListFragment newInstance(String userId) {
        FollowingTripListFragment fragment = new FollowingTripListFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID_ARG, userId);
        args.putBoolean(SHOW_USER_ARG, true);
        args.putBoolean(SHOW_SHARING_ARG, false);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void getTrips() {
        Trip.getFollowingTrips(mUserId, this::updateTrips);
    }

}
