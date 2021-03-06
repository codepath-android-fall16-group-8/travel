package com.codepath.travel;

import android.app.Application;

import com.codepath.travel.models.parse.Media;
import com.codepath.travel.models.parse.SavedPlace;
import com.codepath.travel.models.parse.StoryPlace;
import com.codepath.travel.models.parse.Tag;
import com.codepath.travel.models.parse.Trip;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.interceptors.ParseLogInterceptor;

/**
 * Created by aditikakadebansal on 11/8/16.
 */

public class ParseApplication extends Application {

    private static final String APP_ID = "traveltrails";
    private static final String SERVER_URL = "http://traveltrails.herokuapp.com/parse";



    @Override
    public void onCreate() {
        super.onCreate();

        // Register parse models
        ParseObject.registerSubclass(Trip.class);
        ParseObject.registerSubclass(StoryPlace.class);
        ParseObject.registerSubclass(SavedPlace.class);
        ParseObject.registerSubclass(Media.class);
        ParseObject.registerSubclass(Tag.class);

        // set applicationId, and server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(APP_ID) // should correspond to APP_ID env variable
                .clientKey(null)  // set explicitly unless clientKey is explicitly configured on Parse server
                .addNetworkInterceptor(new ParseLogInterceptor())
                .server(SERVER_URL).build());

        // ParseFacebookUtils should initialize the Facebook SDK for you
        ParseFacebookUtils.initialize(this);

        // default ACL settings
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }
}
