package com.codepath.travel.models.parse;

import com.google.android.gms.location.places.Place;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import static com.codepath.travel.models.parse.ParseModelConstants.*;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

/**
 * Parse model for a travel story/trip.
 */
@ParseClassName(TRIP_CLASS_NAME)
public class Trip extends ParseObject {
    private static final String TAG = Trip.class.getSimpleName();

    public Trip() {
        super();
    }

    // for use until we use API places
    public Trip(ParseUser user, String destinationName) {
        super();
        setDefaultACL(user);
        setUser(user);
        setDestinationPlaceName(destinationName);
    }

    public Trip(ParseUser user, String title, Place destination) {
        super();
        setDefaultACL(user);
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

    public String getDestinationPlaceName() {
        return getString(DESTINATION_PLACE_NAME_KEY);
    }

    public void setDestinationPlaceName(String destinationPlaceName) {
        put(DESTINATION_PLACE_NAME_KEY, destinationPlaceName);
    }

    public String getCoverPicUrl() {
        String coverUrl = getString(PHOTO_URL);
        if (coverUrl == null || TextUtils.isEmpty(coverUrl)) {
            return "http://webvision.med.utah.edu/wp-content/uploads/2012/06/50-percent-gray.jpg";
        }
        return coverUrl;
    }

    public void setCoverPicUrl(String coverPicUrl) {
        put(PHOTO_URL, coverPicUrl);
    }

    public Date getStartDate() {
        return getDate(START_DATE_KEY);
    }

    public void setStartDate(Date startDate) {
        put(START_DATE_KEY, startDate);
    }

    public Date getEndDate() {
        return getDate(END_DATE_KEY);
    }

    public void setEndDate(Date endDate) {
        put(END_DATE_KEY, endDate);
    }

    public boolean isShared() {
        ParseACL acl = getACL();
        if (acl == null) { // for existing data, with no ACL set
            // default ACL to private read
            setACLPrivateRead();
            saveInBackground();
            return true;
        } else {
            return getACL().getPublicReadAccess();
        }
    }

    public void setShared(boolean share) {
        if (share) {
            setACLPublicRead();
        } else {
            setACLPrivateRead();
        }
    }

    // TODO: share with specific users
    // TODO: favorites
    // TODO: tags

    /* PRIVATE METHODS */
    private void setDefaultACL(ParseUser user) {
        ParseACL defaultACL = new ParseACL(user); // owner r/w
        defaultACL.setPublicReadAccess(false); // not readable publicly
        setACL(defaultACL);
    }

    private void setACLPrivateRead() {
        Log.d(TAG, String.format("Setting private read: %s", getTitle()));
        ParseACL acl = getACL();
        if (acl == null) {
            acl = new ParseACL(getUser());
        }
        acl.setPublicReadAccess(false);
        setACL(acl);
    }

    private void setACLPublicRead() {
        Log.d(TAG, String.format("Setting public read: %s", getTitle()));
        ParseACL acl = getACL();
        if (acl == null) {
            acl = new ParseACL(getUser());
        }
        acl.setPublicReadAccess(true);
        setACL(acl);
    }

    /* STATIC METHODS */
    /**
     * Get the Trip object for the given object id
     *
     * @param objectId the object id for the Trip object to find
     * @param callback the callback function to call
     */
    public static void getTripForObjectId(String objectId, GetCallback<Trip> callback) {
        Log.d(TAG, String.format("Querying Parse for Trip with objectId: %s", objectId));
        ParseQuery<Trip> tripQuery = ParseQuery.getQuery(TRIP_CLASS_NAME);
        tripQuery.whereEqualTo(OBJECT_ID_KEY, objectId);
        tripQuery.include(USER_KEY);
        tripQuery.getFirstInBackground(callback);
    }

    /**
     * Find all trips for a user ordered by descending start date.
     *
     * @param userId the user object id
     * @param includeUser flag to include the user object in the result
     * @param callback the callback function to call
     */
    public static void getAllTripsForUser(String userId, boolean includeUser,
                                          FindCallback<Trip> callback) {
        ParseQuery<Trip> tripQuery = ParseQuery.getQuery(TRIP_CLASS_NAME);
        tripQuery.whereEqualTo(USER_KEY, ParseObject.createWithoutData(ParseUser.class, userId));
        tripQuery.addDescendingOrder(START_DATE_KEY);
        if (includeUser) {
            tripQuery.include(USER_KEY);
        }
        tripQuery.findInBackground(callback);
    }

    /**
     * Find all past trips for a user, ordered by descending start date.
     *
     * @param userId the user object id
     * @param includeUser flag to include the user object in the result
     * @param callback the callback function to call
     */
    public static void getPastTripsForUser(String userId, boolean includeUser, FindCallback<Trip> callback) {
        ParseQuery<Trip> tripQuery = ParseQuery.getQuery(TRIP_CLASS_NAME);
        tripQuery.whereEqualTo(USER_KEY, ParseObject.createWithoutData(ParseUser.class, userId));
        tripQuery.whereLessThan(END_DATE_KEY, new Date());
        tripQuery.addDescendingOrder(START_DATE_KEY);
        if (includeUser) {
            tripQuery.include(USER_KEY);
        }
        tripQuery.findInBackground(callback);
    }

    /**
     * Find all upcoming trips for a user ordered by ascending start date.
     * Upcoming trips encompass trips that are current or in the future.
     *
     * @param userId the user object id
     * @param includeUser flag to include the user object in the results
     * @param callback the callback function to call
     */
    public static void getUpcomingTripsForUser(String userId, boolean includeUser,
                                               FindCallback<Trip> callback) {
        ParseQuery<Trip> upcomingTrips = ParseQuery.getQuery(TRIP_CLASS_NAME);
        upcomingTrips.whereGreaterThanOrEqualTo(END_DATE_KEY, new Date()); // ends now or today

        // TODO: can remove after data cleanup since all trips will have dates
        ParseQuery<Trip> datelessTrips = ParseQuery.getQuery(TRIP_CLASS_NAME);
        datelessTrips.whereDoesNotExist(START_DATE_KEY); // has no start date

        ArrayList<ParseQuery<Trip>> queries = new ArrayList<>();
        queries.add(upcomingTrips);
        queries.add(datelessTrips);

        ParseQuery<Trip> tripQuery = ParseQuery.or(queries);
        tripQuery.whereEqualTo(USER_KEY, ParseObject.createWithoutData(ParseUser.class, userId));
        tripQuery.addAscendingOrder(START_DATE_KEY);
        if (includeUser) {
            tripQuery.include(USER_KEY);
        }
        tripQuery.findInBackground(callback);
    }

    /**
     * Find all planned trips for a user ordered by ascending start date.
     * Planned trips encompass trips with no dates and trips with future dates. Trips with no dates
     * will get sorted to the beginning of the results.
     *
     * @param userId the user object id
     * @param includeUser flag to include the user object in the results
     * @param callback the callback function to call
     */
    public static void getPlannedTripsForUser(String userId, boolean includeUser, FindCallback<Trip> callback) {
        ParseQuery<Trip> futureTrips = ParseQuery.getQuery(TRIP_CLASS_NAME);
        futureTrips.whereGreaterThan(START_DATE_KEY, new Date()); // starts after today

        // TODO: can remove after data cleanup since all trips will have dates
        ParseQuery<Trip> datelessTrips = ParseQuery.getQuery(TRIP_CLASS_NAME);
        datelessTrips.whereDoesNotExist(START_DATE_KEY); // has no start date

        ArrayList<ParseQuery<Trip>> queries = new ArrayList<>();
        queries.add(futureTrips);
        queries.add(datelessTrips);

        ParseQuery<Trip> tripQuery = ParseQuery.or(queries);
        tripQuery.whereEqualTo(USER_KEY, ParseObject.createWithoutData(ParseUser.class, userId));
        tripQuery.addAscendingOrder(START_DATE_KEY);
        if (includeUser) {
            tripQuery.include(USER_KEY);
        }
        tripQuery.findInBackground(callback);
    }

    /**
     * Find all trips shared by the given user's followed users.
     *
     * @param userId the user object id
     * @param callback the callback function to call
     */
    public static void getFollowingTrips(String userId, FindCallback<Trip> callback) {
        ParseQuery<ParseUser> followingQuery = User.getFollowingRelation(
                ParseObject.createWithoutData(ParseUser.class, userId)).getQuery();

        ParseQuery<Trip> tripQuery = ParseQuery.getQuery(TRIP_CLASS_NAME);
        tripQuery.whereMatchesQuery(USER_KEY, followingQuery);
        tripQuery.addDescendingOrder(START_DATE_KEY);
        tripQuery.include(USER_KEY);
        tripQuery.findInBackground(callback);
    }

    /**
     * Find current trip for a user, i.e. whose start and end dates contain today's date.
     *
     * @param userId the user object id
     * @param includeUser flag to include the user object in the result
     * @param callback the callback function to call
     */
    public static void getCurrentTripsForUser(String userId, boolean includeUser, FindCallback<Trip> callback) {
        ParseQuery<Trip> tripQuery = ParseQuery.getQuery(TRIP_CLASS_NAME);
        tripQuery.whereEqualTo(USER_KEY, ParseObject.createWithoutData(ParseUser.class, userId));
        Date today = new Date();
        tripQuery.whereLessThanOrEqualTo(START_DATE_KEY, today);
        tripQuery.whereGreaterThanOrEqualTo(END_DATE_KEY, today);
        if (includeUser) {
            tripQuery.include(USER_KEY);
        }
        tripQuery.findInBackground(callback);
    }

    /**
     * Find story places that belong to the given trip id and call the given callback.
     *
     * @param tripId the Parse object id of the trip to find
     * @param callback the callback function to call
     */
    public static void getPlacesForTripId(String tripId, FindCallback<StoryPlace> callback) {
        ParseQuery<StoryPlace> storyQuery = ParseQuery.getQuery(STORY_PLACE_CLASS_NAME);
        storyQuery.whereEqualTo(TRIP_KEY, ParseObject.createWithoutData(Trip.class, tripId));
        storyQuery.addAscendingOrder(CHECK_IN_TIME_KEY);
        storyQuery.findInBackground(callback);
    }

    /**
     * Find story places that belong to the given trip and call the given callback.
     *
     * @param trip the trip to find
     * @param callback the callback function to call
     */
    public static void getPlaces(Trip trip, FindCallback<StoryPlace> callback) {
        ParseQuery<StoryPlace> storyQuery = ParseQuery.getQuery(STORY_PLACE_CLASS_NAME);
        storyQuery.whereEqualTo(TRIP_KEY, trip);
        storyQuery.addAscendingOrder(CHECK_IN_TIME_KEY);
        storyQuery.findInBackground(callback);
    }

    /**
     * Delete the trip associated with the given trip id and all of its related data (story places,
     * media).
     *
     * @param tripId the trip id of the trip to delete
     */
    public static void deleteTripForId(String tripId) {
        getTripForObjectId(tripId, (trip, e) -> {
            if (e == null) {
                deleteTrip(trip);
            } else {
                Log.d(TAG, String.format("Failed to find trip for deleteTripForId: %s",
                        e.getMessage()));
            }
        });
    }

    /**
     * Delete the given trip id and all of its related data (story places, media).
     *
     * @param trip the trip to delete
     */
    public static void deleteTrip(Trip trip) {
        Log.d(TAG, String.format("Deleting trip (%s)", trip.getObjectId()));
        getPlaces(trip, (storyPlaces, e) -> {
            if (e == null) {
                for (StoryPlace storyPlace : storyPlaces) {
                    storyPlace.deleteWithMedia();
                }
                trip.deleteInBackground(e1 -> {
                    if (e1 != null) {
                        Log.d(TAG, String.format("Failed to delete trip: %s", e1.getMessage()));
                    }
                });
            } else {
                Log.d(TAG, String.format("Failed to find story places for deleteTrip: %s",
                        e.getMessage()));
            }
        });
    }
}