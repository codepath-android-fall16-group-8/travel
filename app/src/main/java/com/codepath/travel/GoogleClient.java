package com.codepath.travel;

import android.content.Context;
import android.util.Log;

import com.codepath.travel.models.StoryPlace;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by aditikakadebansal on 11/15/16.
 */

public class GoogleClient extends AsyncHttpClient  {
    public static String NEARBY_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    public static String GOOGLE_PLACES_SEARCH_API_KEY = "AIzaSyBu7ILXPyx6eFeI70xfYAzp-k2xksqhzfI";

    public static AsyncHttpClient getInstance() {
        return new AsyncHttpClient();
    }
}