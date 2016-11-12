package com.codepath.travel.Model;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by aditikakadebansal on 11/8/16.
 */
@Parcel
public class StoryPlace {

    private String name;
    private String imageUrl;
    private String address;
    private int rating;

    public StoryPlace() {}

    public StoryPlace(String name, String url, int rating) {
        this.name = name;
        this.imageUrl = url;
        this.rating = rating;
    }
    /*
    public StoryPlace(Place place) {

    }
    */

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getRating() {
        return rating;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public static ArrayList<StoryPlace> getTestStoryPlacesList(int num) {
        ArrayList<StoryPlace> storyPlaces = new ArrayList<>();

        for (int i = 1; i <= num; i++) {
            storyPlaces.add(new StoryPlace("Place " + i, "http://www.english-heritage.org.uk/content/properties/stonehenge/things-to-do/stonehenge-in-day", i));
        }

        return storyPlaces;
    }
}
