package com.codepath.travel.models.parse;

import android.text.TextUtils;
import android.util.Log;

import com.codepath.travel.models.SuggestionPlace;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.List;

import static com.codepath.travel.models.parse.ParseModelConstants.CHECK_IN_TIME_KEY;
import static com.codepath.travel.models.parse.ParseModelConstants.LATITUDE_KEY;
import static com.codepath.travel.models.parse.ParseModelConstants.LONGITUDE_KEY;
import static com.codepath.travel.models.parse.ParseModelConstants.MEDIA_CLASS_NAME;
import static com.codepath.travel.models.parse.ParseModelConstants.NAME_KEY;
import static com.codepath.travel.models.parse.ParseModelConstants.OBJECT_ID_KEY;
import static com.codepath.travel.models.parse.ParseModelConstants.ORDER_POSITION_KEY;
import static com.codepath.travel.models.parse.ParseModelConstants.PHOTO_URL;
import static com.codepath.travel.models.parse.ParseModelConstants.PLACE_ID_KEY;
import static com.codepath.travel.models.parse.ParseModelConstants.PLACE_TYPES_KEY;
import static com.codepath.travel.models.parse.ParseModelConstants.RATING_KEY;
import static com.codepath.travel.models.parse.ParseModelConstants.STORY_PLACE_CLASS_NAME;
import static com.codepath.travel.models.parse.ParseModelConstants.STORY_PLACE_KEY;
import static com.codepath.travel.models.parse.ParseModelConstants.TRIP_KEY;
import static com.codepath.travel.models.parse.ParseModelConstants.UPDATED_AT_KEY;

/**
 * Parse model for a travel story/trip.
 */
@ParseClassName(STORY_PLACE_CLASS_NAME)
public class StoryPlace extends ParseObject {
    private static final String TAG = StoryPlace.class.getSimpleName();

    public StoryPlace() {
        super();
    }

    public StoryPlace(Trip trip, Place place, String photoUrl) {
        super();
        setTrip(trip);
        setPlaceId(place.getId());
        setName(place.getName().toString());
        setPhotoUrl(photoUrl);
        LatLng latlng = place.getLatLng();
        setLatitude(latlng.latitude);
        setLongitude(latlng.longitude);
        setPlaceTypes(place.getPlaceTypes());
    }

    public StoryPlace(Trip trip, SuggestionPlace suggestionPlace) {
        setTrip(trip);
        setPlaceId(suggestionPlace.getPlaceId());
        setName(suggestionPlace.getName());
        if (suggestionPlace.getPhotoUrl() != null) {
            setPhotoUrl(suggestionPlace.getPhotoUrl());
        }
        setLatitude(suggestionPlace.getLatitude());
        setLongitude(suggestionPlace.getLongitude());
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

    public double getRating() {
        return getDouble(RATING_KEY);
    }

    public void setRating(double rating) {
        put(RATING_KEY, rating);
    }

    public int getOrderPosition() {
        return getInt(ORDER_POSITION_KEY);
    }

    public void setOrderPosition(int orderPosition) { put(ORDER_POSITION_KEY, orderPosition);}

    public String getPhotoUrl() {
        String photoUrl = getString(PHOTO_URL);
        if (photoUrl == null || TextUtils.isEmpty(photoUrl)) {
            return "http://webvision.med.utah.edu/wp-content/uploads/2012/06/50-percent-gray.jpg";
        }
        return photoUrl;
    }

    public void setPhotoUrl(String url) { put(PHOTO_URL, url); }

    /**
     * Get the StoryPlace object for the given object id
     *
     * @param objectId the object id for the StoryPlace object to find
     */
    public static void getStoryPlaceForObjectId(String objectId, GetCallback<StoryPlace> callback) {
        Log.d(TAG, String.format("Querying Parse for StoryPlace with objectId: %s", objectId));
        ParseQuery<StoryPlace> placeQuery = ParseQuery.getQuery(STORY_PLACE_CLASS_NAME);
        placeQuery.whereEqualTo(OBJECT_ID_KEY, objectId);
        placeQuery.getFirstInBackground(callback);
    }


    /**
     * Find media items that belong to the given story place id and call the given callback.
     *
     * @param storyPlaceId the story place id
     * @param callback the callback function to call
     */
    public static void getMediaForStoryPlaceId(String storyPlaceId, FindCallback<Media> callback) {
        ParseQuery<Media> mediaQuery = ParseQuery.getQuery(MEDIA_CLASS_NAME);
        mediaQuery.whereEqualTo(STORY_PLACE_KEY, ParseObject.createWithoutData(StoryPlace.class, storyPlaceId));
        mediaQuery.findInBackground(callback);
    }

    /**
     * Delete the story place and its associated media items.
     */
    public void deleteWithMedia() {
        Log.d(TAG, String.format("Deleting story place: %s", getName()));
        ParseQuery<Media> mediaQuery = ParseQuery.getQuery(MEDIA_CLASS_NAME);
        mediaQuery.whereEqualTo(STORY_PLACE_KEY, this);
        mediaQuery.findInBackground((mediaItems, e) -> {
            if (e == null) {
                ParseObject.deleteAllInBackground(mediaItems, e1 -> {
                    if (e1 == null) {
                        deleteInBackground(e2 -> {
                            if (e2 != null) {
                                Log.d(TAG, String.format("Failed to delete story place: %s",
                                        e2.getMessage()));
                            }
                        });
                    } else {
                        Log.d(TAG, String.format("Failed to delete all media for story place: %s",
                                e1.getMessage()));
                    }
                });
            } else {
                Log.d(TAG, String.format("Failed to find related media for story place delete: %s",
                        e.getMessage()));
            }
        });
    }
}
