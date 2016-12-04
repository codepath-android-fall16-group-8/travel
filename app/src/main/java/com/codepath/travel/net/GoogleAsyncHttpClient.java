package com.codepath.travel.net;

import android.text.TextUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * HTTP client for Google Places API
 */
public class GoogleAsyncHttpClient {

    private static AsyncHttpClient client = new AsyncHttpClient();

    private static String NEARBY_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static String PLACE_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400";
    public static String PLACE_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json?";
//    public static String GOOGLE_PLACES_SEARCH_API_KEY = "AIzaSyCo5UwvCcWOxwQ1N7vq1G0mfZiab8BGRp4";
//    public static String GOOGLE_PLACES_SEARCH_API_KEY = "AIzaSyBu7ILXPyx6eFeI70xfYAzp-k2xksqhzfI";
    public static String GOOGLE_PLACES_SEARCH_API_KEY = "AIzaSyDkD4R2rlb7BRRwDOm3E1G4iRGGRbtBbe8"; // huyen's 2nd key

    public static AsyncHttpClient getInstance() {
        return client;
    }

    public static String getPlacePhotoUrl(String reference) {
        if (reference == null || TextUtils.isEmpty(reference) || reference.contains("http")) {
            // null, empty or not a google photo reference
            return reference;
        } else {
            return GoogleAsyncHttpClient.PLACE_PHOTO_URL
                    + "&photoreference=" + reference
                    + "&key=" + GoogleAsyncHttpClient.GOOGLE_PLACES_SEARCH_API_KEY;
        }
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }

    public static void getNearbyPlaces(String location, String keyword, JsonHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("location", location);
        params.put("keyword", keyword);
        params.put("rankby", "prominence");
        params.put("key", GoogleAsyncHttpClient.GOOGLE_PLACES_SEARCH_API_KEY);
        GoogleAsyncHttpClient.get(GoogleAsyncHttpClient.NEARBY_SEARCH_URL, params, handler);
    }

    public static void getPlaceDetails(String placeId, JsonHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("placeid", placeId);
        params.put("key", GoogleAsyncHttpClient.GOOGLE_PLACES_SEARCH_API_KEY);
        GoogleAsyncHttpClient.get(GoogleAsyncHttpClient.PLACE_DETAILS_URL, params, handler);
    }
}
