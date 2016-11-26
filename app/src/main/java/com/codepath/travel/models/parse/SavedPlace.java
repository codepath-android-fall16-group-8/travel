package com.codepath.travel.models.parse;

import com.google.android.gms.location.places.Place;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import static com.codepath.travel.models.parse.ParseModelConstants.PLACE_ID_KEY;
import static com.codepath.travel.models.parse.ParseModelConstants.SAVED_PLACE_CLASS_NAME;
import static com.codepath.travel.models.parse.ParseModelConstants.TRIP_KEY;

/**
 * Parse model for a saved place.
 */
@ParseClassName(SAVED_PLACE_CLASS_NAME)
public class SavedPlace extends ParseObject {

    public SavedPlace() {
        super();
    }

    public SavedPlace(Trip trip, Place place) {
        super();
        setTrip(trip);
        setPlaceId(place.getId());
    }

    public Trip getTrip() {
        return (Trip) getParseObject(TRIP_KEY);
    }

    public void setTrip(Trip trip) {
        put(TRIP_KEY, trip);
    }

    public String getPlaceId() {
        return getString(PLACE_ID_KEY);
    }

    public void setPlaceId(String placeId) {
        put(PLACE_ID_KEY, placeId);
    }
}
