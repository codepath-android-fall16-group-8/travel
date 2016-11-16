package com.codepath.travel.models;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.List;

import static com.codepath.travel.models.ParseModelConstants.*;

import android.util.Log;

/**
 * Parse model for a travel story/trip.
 */
@ParseClassName(STORY_PLACE_CLASS_NAME)
public class StoryPlace extends ParseObject {

    public StoryPlace() {
        super();
    }

    // for testing, DELETE ME after we have API places
    public StoryPlace(Trip trip, String name) {
        super();
        setTrip(trip);
        setName(name);
    }

    public StoryPlace(Trip trip, Place place) {
        super();
        setTrip(trip);
        setPlaceId(place.getId());
        setName(place.getName().toString());
//        setCoverPicUrl();
        LatLng latlng = place.getLatLng();
        setLatitude(latlng.latitude);
        setLongitude(latlng.longitude);
        setPlaceTypes(place.getPlaceTypes());
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

    public String getName() {
        return getString(NAME_KEY);
    }

    public void setName(String name) {
        put(NAME_KEY, name);
    }

    public String getCoverPicUrl() {
        return "http://www.english-heritage.org.uk/content/properties/stonehenge/things-to-do/stonehenge-in-day";
        //return getString(COVER_PIC_URL_KEY);
    }

    public void setCoverPicUrl(String coverPicUrl) {
        put(COVER_PIC_URL_KEY, coverPicUrl);
    }

    public double getLatitude() {
        return getDouble(LATITUDE_KEY);
    }

    public void setLatitude(double latitude) {
        put(LATITUDE_KEY, latitude);
    }

    public double getLongitude() {
        return getDouble(LONGITUDE_KEY);
    }

    public void setLongitude(double longitude) {
        put(LONGITUDE_KEY, longitude);
    }

    public List<Integer> getPlaceTypes() {
        return getList(PLACE_TYPES_KEY);
    }

    public void setPlaceTypes(List<Integer> placeTypes) {
        put(PLACE_TYPES_KEY, placeTypes);
    }

    public Date getCheckinTime() {
        return getDate(CHECK_IN_TIME_KEY);
    }

    public void setCheckinTime(Date checkInTime) {
        put(CHECK_IN_TIME_KEY, checkInTime);
    }

    public int getRating() {
        return getInt(RATING_KEY);
    }

    public void setRating(int rating) {
        put(RATING_KEY, rating);
    }

    public int getOrderPosition() {
        return getInt(ORDER_POSITION_KEY);
    }

    public void setOrderPosition(int orderPosition) {
        put(ORDER_POSITION_KEY, orderPosition);
    }

    /**
     * Delete the story place and its associated media items.
     */
    public void deleteWithMedia() {
        Log.d("deleteStoryPlace", String.format("Deleting story place: %s", getName()));
        ParseQuery<Media> mediaQuery = ParseQuery.getQuery(MEDIA_CLASS_NAME);
        mediaQuery.whereEqualTo(STORY_PLACE_KEY, this);
        mediaQuery.findInBackground(new FindCallback<Media>() {
                @Override
                public void done(List<Media> mediaItems, ParseException e) {
                    if (e == null) {
                        ParseObject.deleteAllInBackground(mediaItems, new DeleteCallback() {
                                @Override
                                public void done(ParseException e1) {
                                    if (e1 == null) {
                                        deleteInBackground(new DeleteCallback() {
                                                @Override
                                                public void done(ParseException e2) {
                                                    if (e2 != null) {
                                                        Log.d("deleteStoryPlace", String.format("Failed to delete story place: %s", e2.getMessage()));
                                                    }
                                                }
                                        });
                                    } else {
                                        Log.d("deleteStoryPlace", String.format("Failed to delete all media: %s", e1.getMessage()));
                                    }
                                }
                        });
                    } else {
                        Log.d("deleteStoryPlace", String.format("Failed to find related media: %s", e.getMessage()));
                    }
                }
        });
    }
}
