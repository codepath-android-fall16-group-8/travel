package com.codepath.travel.models;

import static com.codepath.travel.net.GooglePlaceConstants.RATING_KEY;
import static com.facebook.FacebookSdk.getApplicationContext;

import com.codepath.travel.helper.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Date;

/**
 * Model for a google place review.
 */
@Parcel
public class Review {
    private static final String AUTHOR_NAME_KEY = "author_name";
    private static final String TEXT_KEY = "text";
    private static final String RELATIVE_TIME_KEY = "relative_time_description";
    private static final String TIME_KEY = "time";

    // fields must be public
    String author;
    Double rating;
    String text;
    String timestamp;

    public Review() {
        // empty constructor needed by the Parceler library
    }

    public Review(JSONObject jsonObject) {
        super();
        try {
            setAuthor(jsonObject.getString(AUTHOR_NAME_KEY));
            setRating(jsonObject.getDouble(RATING_KEY));
            setText(jsonObject.getString(TEXT_KEY));
            if (jsonObject.has(RELATIVE_TIME_KEY)) {
                setTimestamp(jsonObject.getString(RELATIVE_TIME_KEY));
            } else {
                setTimestamp(DateUtils.formatDate(getApplicationContext(), new Date(jsonObject.getLong(TIME_KEY))));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Review> getReviewsFromJSONArray(JSONArray jsonArray) {
        ArrayList<Review> reviews = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Review review = new Review(jsonObject);
                reviews.add(review);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return reviews;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
