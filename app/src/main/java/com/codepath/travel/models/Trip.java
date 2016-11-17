package com.codepath.travel.models;

import com.google.android.gms.location.places.Place;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import static com.codepath.travel.models.ParseModelConstants.*;

import android.util.Log;

/**
 * Parse model for a travel story/trip.
 */
@ParseClassName(TRIP_CLASS_NAME)
public class Trip extends ParseObject {
    private static final String TAG = Media.class.getSimpleName();

    public Trip() {
        super();
    }

    // for use until we use API places
    public Trip(ParseUser user, String title) {
        super();
        setUser(user);
        setTitle(title);
    }

    public Trip(ParseUser user, String title, Place destination) {
        super();
        setUser(user);
        setTitle(title);
        setDestinationPlaceId(destination.getId());
    }

    public ParseUser getUser()  {
        return getParseUser(USER_KEY);
    }

    public void setUser(ParseUser user) {
        put(USER_KEY, user);
    }

    public String getTitle() {
        return getString(TITLE_KEY);
    }

    public void setTitle(String title) {
        put(TITLE_KEY, title);
    }

    public String getDestinationPlaceId() {
        return getString(DESTINATION_PLACE_ID_KEY);
    }

    public void setDestinationPlaceId(String destinationPlaceId) {
        put(DESTINATION_PLACE_ID_KEY, destinationPlaceId);
    }

    public String getCoverPicUrl() {
        return getString(COVER_PIC_URL_KEY);
    }

    public void setCoverPicUrl(String coverPicUrl) {
        put(COVER_PIC_URL_KEY, coverPicUrl);
    }

    public ParseRelation<User> getSharedRelation() {
        return getRelation(SHARED_RELATION_KEY);
    }

    public void shareWith(User user) {
        getSharedRelation().add(user);
        saveInBackground();
    }

    public void unShareWith(User user) {
        getSharedRelation().remove(user);
        saveInBackground();
    }

    public void queryFavorites(FindCallback<User> callback) {
        getSharedRelation().getQuery().findInBackground(callback);
    }

    public String toString() {
        return getTitle();
    }

    // TODO: figure out how to query for all tags inside this trip (including storyPlace and media tags)

    /**
     * Get the Trip object for the given object id
     *
     * @param objectId the object id for the Trip object to find
     */
    public static void getTripForObjectId(String objectId, GetCallback<Trip> callback) {
        Log.d(TAG, String.format("Querying Parse for Trip with objectId: %s", objectId));
        ParseQuery<Trip> tripQuery = ParseQuery.getQuery(TRIP_CLASS_NAME);
        tripQuery.whereEqualTo(OBJECT_ID_KEY, objectId);
        tripQuery.getFirstInBackground(callback);
    }

    /**
     * Find story places that belong to the given trip id and call the given callback.
     *
     * @param tripId the Parse object id of the trip to find
     * @param callback the callback function to call
     */
    public static void getPlaces(String tripId, FindCallback<StoryPlace> callback) {
        ParseQuery<StoryPlace> storyQuery = ParseQuery.getQuery(STORY_PLACE_CLASS_NAME);
        storyQuery.whereEqualTo(TRIP_KEY, ParseObject.createWithoutData(Trip.class, tripId));
        storyQuery.findInBackground(callback);
    }

    /**
     * Delete the trip associated with the given trip id and all of its related data (story places,
     * media).
     *
     * @param tripId the trip id of the trip to delete
     */
    public static void deleteTrip(String tripId) {
        Log.d("deleteTrip", String.format("Deleting trip with id: %s", tripId));
        getTripForObjectId(tripId, (trip, e) -> {
            if (e == null) {
                getPlaces(tripId, (storyPlaces, e1) -> {
                    if (e1 == null) {
                        for (StoryPlace storyPlace : storyPlaces) {
                            storyPlace.deleteWithMedia();
                        }
                        trip.deleteInBackground(e11 -> {
                            if (e11 != null) {
                                Log.d(TAG, String.format("Failed to delete trip: %s",
                                        e11.getMessage()));
                            }
                        });
                    } else {
                        Log.d(TAG, String.format("Failed to find story places for deleteTrip: %s",
                                e1.getMessage()));
                    }
                });
            } else {
                Log.d(TAG, String.format("Failed to find trip for deleteTrip: %s", e.getMessage()));
            }
        });
    }
}