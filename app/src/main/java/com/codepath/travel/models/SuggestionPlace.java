package com.codepath.travel.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Class with limited members as compared to StoryPlace which fetches values from json array
 * and is used only for purpose of passing suggestion places via intent to Create Story view
 */
@Parcel
public class SuggestionPlace {
    private static final String NAME_KEY = "name";
    private static final String PLACE_ID_KEY = "place_id";
    private static final String RATING_KEY = "rating";
    private static final String PHOTOS_KEY = "photos";
    private static final String PHOTO_REF_KEY = "photo_reference";

    // fields must be public
    String name;
    Double rating;
    String placeId;
    String photoUrl;
    boolean selected;

    public SuggestionPlace() {
        // empty constructor needed by the Parceler library
    }

    /**
     * Constructor to get required values from jsonObject
     * @param jsonObject
     */
    public SuggestionPlace(JSONObject place) {
        super();
        try {
            this.name = place.getString(NAME_KEY);
            this.placeId = place.getString(PLACE_ID_KEY);
            if (place.has(RATING_KEY)) {
                this.rating = place.getDouble(RATING_KEY);
            }
            if (place.has(PHOTOS_KEY)) {
                JSONArray photos = place.getJSONArray(PHOTOS_KEY);
                if (photos.length() > 0) {
                    this.photoUrl = photos.getJSONObject(0).getString(PHOTO_REF_KEY);
                }
            }
        } catch (JSONException e) {
            Log.d("Parse place failed", e.toString());
        }
    }

    /**
     * Get Google Places from JSONArray
     * @param jsonArray
     * @return
     */
    public static ArrayList<SuggestionPlace> getPlacesFromJSONArray(JSONArray jsonArray) {
        ArrayList<SuggestionPlace> suggestionPlaces = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                SuggestionPlace place = new SuggestionPlace(jsonObject);
                suggestionPlaces.add(place);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return suggestionPlaces;
    }

    public String getName() {
        return name;
    }

    public Double getRating() {
        return rating;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public boolean isSelected() { return selected; }

    public void setSelected(boolean selected) { this.selected = selected; }
}
