package com.codepath.travel.models;

/**
 * Shared constant values for Parse models.
 */
public final class ParseModelConstants {

    // User-specific values
    public static final String USER_CLASS_NAME = "_User";
    public static final String FB_UID_KEY = "fbUid";
    public static final String PROFILE_PIC_URL_KEY = "profilePicUrl";
    public static final String FOLLOWING_RELATION_KEY = "following";
    public static final String FAVORITES_RELATION_KEY = "favorites";

    // Trip-specific values
    public static final String TRIP_CLASS_NAME = "Trip";
    public static final String TITLE_KEY = "title";
    public static final String DESTINATION_PLACE_ID_KEY = "destinationPlaceId";
    public static final String SHARED_RELATION_KEY = "shared";

    // StoryPlace-specific values
    public static final String STORY_PLACE_CLASS_NAME = "StoryPlace";
    public static final String LATITUDE_KEY = "latitude";
    public static final String LONGITUDE_KEY = "longitude";
    public static final String PLACE_TYPES_KEY = "placeTypes";
    public static final String CHECK_IN_TIME_KEY = "checkInTime";
    public static final String RATING_KEY = "rating";
    public static final String ORDER_POSITION_KEY = "orderPosition";

    // SavedPlace-specific values
    public static final String SAVED_PLACE_CLASS_NAME = "SavedPlace";

    // Media-specific values
    public static final String MEDIA_CLASS_NAME = "Media";
    public static final String TYPE_KEY = "type";
    public static final String CAPTION_KEY = "caption";
    public static final String DATA_URL_KEY = "dataUrl";

    // Tag-specific values
    public static final String TAG_CLASS_NAME = "Tag";
    public static final String MEDIA_KEY = "media";

    // Commonly shared values
    public static final String USER_KEY = "user";
    public static final String TRIP_KEY = "trip";
    public static final String STORY_PLACE_KEY = "storyPlace";
    public static final String PLACE_ID_KEY = "placeId";
    public static final String NAME_KEY = "name";
    public static final String COVER_PIC_URL_KEY = "coverPicUrl";
}
