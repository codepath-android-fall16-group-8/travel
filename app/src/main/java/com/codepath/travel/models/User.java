package com.codepath.travel.models;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import static com.codepath.travel.models.ParseModelConstants.COVER_PIC_URL_KEY;
import static com.codepath.travel.models.ParseModelConstants.FAVORITES_RELATION_KEY;
import static com.codepath.travel.models.ParseModelConstants.FB_UID_KEY;
import static com.codepath.travel.models.ParseModelConstants.FOLLOWING_RELATION_KEY;
import static com.codepath.travel.models.ParseModelConstants.PROFILE_PIC_URL_KEY;
import static com.codepath.travel.models.ParseModelConstants.USER_CLASS_NAME;
import static com.codepath.travel.models.ParseModelConstants.USER_KEY;

/**
 * Parse user model.
 */
@ParseClassName(USER_CLASS_NAME)
public class User extends ParseUser {

    private ParseUser user;

    public User() {
        super();
    }

    public User(String username, String email, String password) {
        super();
        user = new ParseUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
    }

    public int getFbUid() {
        return getInt(FB_UID_KEY);
    }

    public void setFbUid(int fbUid) {
        put(FB_UID_KEY, fbUid);
    }

    public String getProfilePicUrl() {
        return getString(PROFILE_PIC_URL_KEY);
    }

    public void setProfilePicUrl(String profilePicUrl) {
        put(PROFILE_PIC_URL_KEY, profilePicUrl);
    }

    public String getCoverPicUrl() {
        return getString(COVER_PIC_URL_KEY);
    }

    public void setCoverPicUrl(String coverPicUrl) {
        put(COVER_PIC_URL_KEY, coverPicUrl);
    }

    public void queryTrips(FindCallback<Trip> callback) {
        ParseQuery<Trip> query = ParseQuery.getQuery(Trip.class);
        query.whereEqualTo(USER_KEY, user);
        query.findInBackground(callback);
    }

    public ParseRelation<Trip> getFavoriteRelation() {
        return getRelation(FAVORITES_RELATION_KEY);
    }

    public void addFavorite(Trip trip) {
        getFavoriteRelation().add(trip);
        saveInBackground();
    }

    public void removeFavorite(Trip trip) {
        getFavoriteRelation().remove(trip);
        saveInBackground();
    }

    public void queryFavorites(FindCallback<Trip> callback) {
        getFavoriteRelation().getQuery().findInBackground(callback);
    }

    public ParseRelation<User> getFollowingRelation() {
        return getRelation(FOLLOWING_RELATION_KEY);
    }

    public void follow(User user) {
        getFollowingRelation().add(user);
        saveInBackground();
    }

    public void unFollow(User user) {
        getFollowingRelation().remove(user);
        saveInBackground();
    }

    public void queryFollowing(FindCallback<User> callback) {
        getFollowingRelation().getQuery().findInBackground(callback);
    }

    public void queryFollowers(FindCallback<User> callback) {
        ParseQuery<User> query = ParseQuery.getQuery(USER_CLASS_NAME);
        query.whereEqualTo(FOLLOWING_RELATION_KEY, user);
        query.findInBackground(callback);
    }

    // TODO: figure out how to query for all tags of this user (trip/storyPlace/media)

}
