package com.codepath.travel.fragments;

import android.os.Bundle;
import android.util.Log;

import com.codepath.travel.models.Trip;

/**
 * Fragment to display a horizontal scrolling recycler view of past trips.
 */
public class PastTripListFragment extends TripListFragment {
    private static final String TAG = PastTripListFragment.class.getSimpleName();

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
        mTrips.clear();
        String userId = getArguments().getString(USER_ID_ARG);

        Trip.getPastTripsForUser(userId, fetchUser, (trips, e) -> {
            if (e == null) {
                mTrips.addAll(trips);
                mTripsAdapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, String.format("Failed to populate past trips: %s", e.getMessage()));
            }
        });
    }
}
