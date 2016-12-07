package com.codepath.travel.models.parse;

import android.text.TextUtils;
import android.util.Log;

import com.codepath.travel.callbacks.ParseQueryCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import static com.codepath.travel.models.parse.ParseModelConstants.FAVORITES_RELATION_KEY;
import static com.codepath.travel.models.parse.ParseModelConstants.FB_UID_KEY;
import static com.codepath.travel.models.parse.ParseModelConstants.FOLLOWING_RELATION_KEY;
import static com.codepath.travel.models.parse.ParseModelConstants.KEY_USERNAME;
import static com.codepath.travel.models.parse.ParseModelConstants.OBJECT_ID_KEY;
import static com.codepath.travel.models.parse.ParseModelConstants.PHOTO_URL;
import static com.codepath.travel.models.parse.ParseModelConstants.PROFILE_PIC_URL_KEY;
import static com.codepath.travel.models.parse.ParseModelConstants.USER_KEY;

/**
 * ParseUser helper methods for extended fields and relations.
 */
public final class User {
    private static final String TAG = User.class.getSimpleName();

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
            return "http://webvision.med.utah.edu/wp-content/uploads/2012/06/50-percent-gray.jpg";
        }
        return coverUrl;
    }

    public static void setCoverPicUrl(ParseUser pUser, String coverPicUrl) {
        pUser.put(PHOTO_URL, coverPicUrl);
    }

    /* Photo Saving */
    public static void saveCoverPicURL(ParseUser pUser, String coverPicURL, ParseQueryCallback<ParseUser> callback) {
        pUser.put(PHOTO_URL, coverPicURL);
        pUser.saveInBackground((ParseException e) -> {
            if (e != null) {
                callback.onQueryError(e);
                return;
            }
            callback.onQuerySuccess(pUser);
        });
    }

    public static void saveProfilePicURL(ParseUser pUser, String profilePicURL, ParseQueryCallback<ParseUser> callback) {
        pUser.put(PROFILE_PIC_URL_KEY, profilePicURL);
        pUser.saveInBackground((ParseException e) -> {
            if (e != null) {
                callback.onQueryError(e);
                return;
            }
            callback.onQuerySuccess(pUser);
        });
    }

    public static void queryTrips(ParseUser pUser, FindCallback<Trip> callback) {
        ParseQuery<Trip> query = ParseQuery.getQuery(Trip.class);
        query.whereEqualTo(USER_KEY, pUser);
        query.findInBackground(callback);
    }

    /* User Lookup */
    public static void findUsersByName(String searchTerm, FindCallback<ParseUser> callback) {
        ParseQuery<ParseUser> userQuery = ParseQuery.getQuery(ParseUser.class);
        userQuery.whereMatches(KEY_USERNAME, "^.*"+searchTerm+".*$", "i");
        userQuery.findInBackground(callback);
    }

    public static void getUserByID(String userId, ParseQueryCallback<ParseUser> callback) {
        ParseQuery<ParseUser> userQuery = ParseQuery.getQuery(ParseUser.class);
        userQuery.whereEqualTo(OBJECT_ID_KEY, userId);
        userQuery.findInBackground((List<ParseUser> objects, ParseException e) -> {
            if (e != null || objects.size() == 0) {
                callback.onQueryError(e);
                return;
            }
            callback.onQuerySuccess(objects.get(0));
        });
    }

    /* Following */
    public static ParseRelation<ParseUser> getFollowingRelation(ParseUser pUser) {
        return pUser.getRelation(FOLLOWING_RELATION_KEY);
    }

    public static void queryIsFollowing(
            ParseUser pCurrentUser,
            ParseUser pCheckUser,
            FindCallback<ParseUser> callback
    ) {
        ParseQuery followingRelationQuery = getFollowingRelation(pCurrentUser).getQuery();
        followingRelationQuery.whereEqualTo(OBJECT_ID_KEY, pCheckUser.getObjectId());
        followingRelationQuery.findInBackground(callback);
    }

    public static void follow(ParseUser pUser, ParseUser otherUser, SaveCallback callback) {
        getFollowingRelation(pUser).add(otherUser);
        pUser.saveInBackground(callback);
    }

    public static void unFollow(ParseUser pUser, ParseUser otherUser, SaveCallback callback) {
        getFollowingRelation(pUser).remove(otherUser);
        pUser.saveInBackground(callback);
    }

    public static void queryAllFollowers(ParseUser pUser, FindCallback<ParseUser> callback) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(FOLLOWING_RELATION_KEY, pUser);
        query.findInBackground(callback);
    }

    public static void queryAllFollowing(ParseUser pUser, FindCallback<ParseUser> callback) {
        ParseQuery followingRelationQuery = getFollowingRelation(pUser).getQuery();
        followingRelationQuery.findInBackground(callback);
    }

    /* Favorites */
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

    /* Account Deletion */
    public static void deleteUserAndData(ParseUser pUser) {
        // delete trips
        ParseQuery<Trip> query = ParseQuery.getQuery(Trip.class);
        query.whereEqualTo(USER_KEY, pUser);
        query.findInBackground((List<Trip> trips, ParseException e) -> {
            if (e == null) {
                for (Trip trip : trips) {
                    Trip.deleteTrip(trip);
                }

                // unfollow
                queryAllFollowing(pUser, (followedUsers, e1) -> {
                    if (e1 == null) {
                        for (ParseUser followedUser : followedUsers) {
                            getFollowingRelation(pUser).remove(followedUser);
                        }
                        pUser.saveInBackground();

                        // remove followers
                        queryAllFollowers(pUser, (followers, e2) -> {
                            if (e2 == null) {
                                for (ParseUser follower : followers) {
                                    unFollow(follower, pUser, null);
                                }

                                pUser.deleteInBackground(e3 -> {
                                    if (e3 == null) {
                                        Log.d(TAG, "Delete account successful");
                                        ParseUser.logOut();
                                    } else {
                                        Log.d(TAG, String.format("Delete account failed for %s, %s",
                                                pUser.getObjectId(), e3.getMessage()));
                                    }
                                });
                            } else {
                                Log.d(TAG, String.format("Failed to find users to unfollow for user %s: %s",
                                        pUser.getObjectId(), e2.getMessage()));
                            }
                        });
                    } else {
                        Log.d(TAG, String.format("Failed to find users to unfollow for user %s: %s",
                                pUser.getObjectId(), e1.getMessage()));
                    }
                });
            } else {
                Log.d(TAG, String.format("Failed to find trips to delete for user %s: %s",
                        pUser.getObjectId(), e.getMessage()));
            }
        });
    }
}
