package com.codepath.travel.models;

import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import static com.codepath.travel.models.ParseModelConstants.FAVORITES_RELATION_KEY;
import static com.codepath.travel.models.ParseModelConstants.FB_UID_KEY;
import static com.codepath.travel.models.ParseModelConstants.FOLLOWING_RELATION_KEY;
import static com.codepath.travel.models.ParseModelConstants.PHOTO_URL;
import static com.codepath.travel.models.ParseModelConstants.PROFILE_PIC_URL_KEY;
import static com.codepath.travel.models.ParseModelConstants.USER_KEY;

import android.text.TextUtils;

/**
 * ParseUser helper methods for extended fields and relations.
 */
public final class User {

    private User() {}

    public static int getFbUid(ParseUser pUser) {
        return pUser.getInt(FB_UID_KEY);
    }

    public static void setFbUid(ParseUser pUser, int fbUid) {
        pUser.put(FB_UID_KEY, fbUid);
    }

    public static String getProfilePicUrl(ParseUser pUser) {
        return pUser.getString(PROFILE_PIC_URL_KEY);
    }

    public static void setProfilePicUrl(ParseUser pUser, String profilePicUrl) {
        pUser.put(PROFILE_PIC_URL_KEY, profilePicUrl);
    }

    public static String getCoverPicUrl(ParseUser pUser) {
        String coverUrl = pUser.getString(PHOTO_URL);
        if (coverUrl == null || TextUtils.isEmpty(coverUrl)) {
            return "http://www.english-heritage.org.uk/content/properties/stonehenge/things-to-do/stonehenge-in-day";
        }
        return coverUrl;
    }

    public static void setCoverPicUrl(ParseUser pUser, String coverPicUrl) {
        pUser.put(PHOTO_URL, coverPicUrl);
    }

    public static void queryTrips(ParseUser pUser, FindCallback<Trip> callback) {
        ParseQuery<Trip> query = ParseQuery.getQuery(Trip.class);
        query.whereEqualTo(USER_KEY, pUser);
        query.findInBackground(callback);
    }

    public static ParseRelation<Trip> getFavoriteRelation(ParseUser pUser) {
        return pUser.getRelation(FAVORITES_RELATION_KEY);
    }

    public static void addFavorite(ParseUser pUser, Trip trip) {
        getFavoriteRelation(pUser).add(trip);
    }

    public static void removeFavorite(ParseUser pUser, Trip trip) {
        getFavoriteRelation(pUser).remove(trip);
    }

    public static void queryFavorites(ParseUser pUser, FindCallback<Trip> callback) {
        getFavoriteRelation(pUser).getQuery().findInBackground(callback);
    }

    public static ParseRelation<ParseUser> getFollowingRelation(ParseUser pUser) {
        return pUser.getRelation(FOLLOWING_RELATION_KEY);
    }

    public static void follow(ParseUser pUser, ParseUser otherUser) {
        getFollowingRelation(pUser).add(otherUser);
    }

    public static void unFollow(ParseUser pUser, ParseUser otherUser) {
        getFollowingRelation(pUser).remove(otherUser);
    }

    public static void queryFollowing(ParseUser pUser, FindCallback<ParseUser> callback) {
        getFollowingRelation(pUser).getQuery().findInBackground(callback);
    }

    public static void queryFollowers(ParseUser pUser, FindCallback<ParseUser> callback) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(FOLLOWING_RELATION_KEY, pUser);
        query.findInBackground(callback);
    }

    // TODO: figure out how to query for all tags of this user (trip/storyPlace/media)

}
