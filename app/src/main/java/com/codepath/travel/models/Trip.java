package com.codepath.travel.models;

import com.google.android.gms.location.places.Place;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import static com.codepath.travel.models.ParseModelConstants.COVER_PIC_URL_KEY;
import static com.codepath.travel.models.ParseModelConstants.DESTINATION_PLACE_ID_KEY;
import static com.codepath.travel.models.ParseModelConstants.SHARED_RELATION_KEY;
import static com.codepath.travel.models.ParseModelConstants.STORY_PLACE_CLASS_NAME;
import static com.codepath.travel.models.ParseModelConstants.TITLE_KEY;
import static com.codepath.travel.models.ParseModelConstants.TRIP_CLASS_NAME;
import static com.codepath.travel.models.ParseModelConstants.USER_KEY;

/**
 * Parse model for a travel story/trip.
 */
@ParseClassName(TRIP_CLASS_NAME)
public class Trip extends ParseObject {

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

    public static void getPlaces(String tripId, FindCallback callback) {
        ParseQuery<StoryPlace> storyQuery = ParseQuery.getQuery(STORY_PLACE_CLASS_NAME);
        storyQuery.whereEqualTo(ParseModelConstants.TRIP_KEY,
                ParseObject.createWithoutData(Trip.class, tripId));
        storyQuery.findInBackground(callback);
    }
}