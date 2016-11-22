package com.codepath.travel.fragments;

import com.codepath.travel.models.Trip;
import com.codepath.travel.models.User;
import com.parse.ParseUser;

/**
 * Interface for trip item clicks listener.
 */
public interface TripClickListener {
    void onTripClick(String tripId, String tripTitle);
    void onShareClick(Trip trip, boolean share);
    void onProfileClick(ParseUser pUser);
}
