package com.codepath.travel.models;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.codepath.travel.models.ParseModelConstants.*;

import android.text.TextUtils;
import android.util.Log;

/**
 * Parse model for a travel story/trip.
 */
@ParseClassName(STORY_PLACE_CLASS_NAME)
public class StoryPlace extends ParseObject {
    private static final String TAG = StoryPlace.class.getSimpleName();

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

    public StoryPlace(JSONObject jsonObject) {
        super();
        try {
        setName(jsonObject.getString("name"));
        setRating(jsonObject.getDouble("rating"));
        }catch (JSONException e) {
            e.printStackTrace();
        }
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
        String coverUrl = getString(COVER_PIC_URL_KEY);
        if (coverUrl == null || TextUtils.isEmpty(coverUrl)) {
            return "http://www.english-heritage.org.uk/content/properties/stonehenge/things-to-do/stonehenge-in-day";
        }
        return coverUrl;
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

    public static ArrayList<StoryPlace> getPlacesFromJSONArray(JSONArray jsonArray) {
        ArrayList<StoryPlace> storyPlaces = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                StoryPlace storyPlace = new StoryPlace(jsonObject);
                storyPlaces.add(storyPlace);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return storyPlaces;
    }

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
