package com.codepath.travel.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by aditikakadebansal on 11/18/16.
 */
//Class with limited members as compared to StoryPlace which fetches values from json array
// and is used only for purpose of passing suggestion places via intent to Create Story view
public class SuggestionPlace implements Parcelable {

    private String name;
    private Double rating;
    private String placeId;
    private String photoUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean selected;

    public boolean isSelected() { return selected; }

    public void setSelected(boolean selected) { this.selected = selected; }

    /**
     * Constructor to get required values from jsonObject
     * @param jsonObject
     */
    public SuggestionPlace(JSONObject jsonObject) {
        super();
        try {
            setName(jsonObject.getString("name"));
            setPlaceId(jsonObject.getString("place_id"));
            setRating(jsonObject.getDouble("rating"));
            setPhotoUrl(jsonObject.getJSONArray("photos").getJSONObject(0)!=null
                    ? jsonObject.getJSONArray("photos").getJSONObject(0).getString("photo_reference")
                    : null);
        }catch (JSONException e) {
            e.printStackTrace();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeValue(this.rating);
        dest.writeString(this.placeId);
        dest.writeString(this.photoUrl);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
    }

    protected SuggestionPlace(Parcel in) {
        this.name = in.readString();
        this.rating = (Double) in.readValue(Double.class.getClassLoader());
        this.placeId = in.readString();
        this.photoUrl = in.readString();
        this.selected = in.readByte() != 0;
    }

    public static final Parcelable.Creator<SuggestionPlace> CREATOR = new Parcelable.Creator<SuggestionPlace>() {
        @Override
        public SuggestionPlace createFromParcel(Parcel source) {
            return new SuggestionPlace(source);
        }

        @Override
        public SuggestionPlace[] newArray(int size) {
            return new SuggestionPlace[size];
        }
    };
}
