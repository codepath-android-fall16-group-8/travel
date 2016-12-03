package com.codepath.travel;

/**
 * Shared constant values.
 */
public final class Constants {
    /* Request codes */
    public static final int CREATE_STORY_REQUEST = 1;
    public static final int AUTOCOMPLETE_REQUEST = 2;
    public static final int PLACE_DETAIL_REQUEST = 3;
    public static final String APP_TAG = "TravelTrails";
    public static final int START_CAMERA_REQUEST_CODE = 123;
    public static final int PICK_IMAGE_FROM_GALLERY_CODE = 456;

    /* Intent argument keys */
    public static final String LATLNG_ARG = "latlong";
    public static final String SUGGESTION_PLACES_LIST_ARG = "suggestion_places_list";
    public static final String PLACE_ID_ARG = "place_id";
    public static final String PLACE_NAME_ARG = "place_name";
    public static final String PLACE_ADDED_ARG = "place_added";
    public static final String PLACE_CATEGORY_ARG = "place_category";
    public static final String PLACE_PHOTO_REF_ARG = "place_photo_ref";
    public static final String POSITION_ARG = "position";
    public static final String IS_STORY_PLACE = "isStoryPlace";

    /* Story Activity argument keys */
    public static final String TRIP_ID_ARG = "trip_id";
    public static final String TRIP_TITLE_ARG = "trip_title";
    public static final String IS_OWNER_ARG = "is_owner";

}
