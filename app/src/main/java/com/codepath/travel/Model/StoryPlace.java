package com.codepath.travel.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by aditikakadebansal on 11/8/16.
 */

public class StoryPlace implements Parcelable {

    private String name;
    private String imageUrl;
    private String address;


    public StoryPlace(String name, String url) {
        this.name = name;
        this.imageUrl = url;
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

    public static ArrayList<StoryPlace> getTestStoryPlacesList(int num) {
        ArrayList<StoryPlace> storyPlaces = new ArrayList<>();

        for (int i = 1; i <= num; i++) {
            storyPlaces.add(new StoryPlace("Place " + i, "http://www.english-heritage.org.uk/content/properties/stonehenge/things-to-do/stonehenge-in-day"));
        }

        return storyPlaces;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.imageUrl);
        dest.writeString(this.address);
    }

    protected StoryPlace(Parcel in) {
        this.name = in.readString();
        this.imageUrl = in.readString();
        this.address = in.readString();
    }

    public static final Parcelable.Creator<StoryPlace> CREATOR = new Parcelable.Creator<StoryPlace>() {
        @Override
        public StoryPlace createFromParcel(Parcel source) {
            return new StoryPlace(source);
        }

        @Override
        public StoryPlace[] newArray(int size) {
            return new StoryPlace[size];
        }
    };
}
