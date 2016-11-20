package com.codepath.travel;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by aditikakadebansal on 11/15/16.
 */

public class GoogleAsyncHttpClient {

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static String NEARBY_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    public static String PLACE_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400";
    public static String PLACE_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json?";
    public static String GOOGLE_PLACES_SEARCH_API_KEY = "AIzaSyBu7ILXPyx6eFeI70xfYAzp-k2xksqhzfI";

    public static AsyncHttpClient getInstance() {
        return client;
    }

    public static String getPlacePhotoUrl(String reference) {
        return GoogleAsyncHttpClient.PLACE_PHOTO_URL
                + "&photoreference=" + reference
                + "&key=" + GoogleAsyncHttpClient.GOOGLE_PLACES_SEARCH_API_KEY;
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }
}
