package com.codepath.travel.models.parse;

import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import static com.codepath.travel.models.parse.ParseModelConstants.*;

import android.util.Log;

/**
 * Parse model for a media item, which can be text or media.
 */
@ParseClassName(MEDIA_CLASS_NAME)
public class Media extends ParseObject {
    private static final String TAG = Media.class.getSimpleName();

    public enum Type {
        TEXT, REVIEW, PHOTO, VIDEO
    }

    public Media() {
        super();
    }

    public Media(StoryPlace storyPlace, Type type) {
        super();
        setStoryPlace(storyPlace);
        setType(type);
    }

    public StoryPlace getStoryPlace() {
        return (StoryPlace) getParseObject(STORY_PLACE_KEY);
    }

    public void setStoryPlace(StoryPlace storyPlace) {
        put(STORY_PLACE_KEY, storyPlace);
    }

    public Type getType() {
        return Type.values()[getInt(TYPE_KEY)];
    }

    public void setType(Type type) {
        put(TYPE_KEY, type.ordinal());
    }

    public String getCaption() {
        return getString(CAPTION_KEY);
    }

    public void setCaption(String caption) {
        put(CAPTION_KEY, caption);
    }

    public String getDataUrl() {
        return getString(DATA_URL_KEY);
    }

    public void setDataUrl(String dataUrl) {
        put(DATA_URL_KEY, dataUrl);
    }

    /**
     * Get the Media object for the given object id
     *
     * @param objectId the object id for the Media object to find
     */
    public static void getMediaForObjectId(String objectId, GetCallback<Media> callback) {
        Log.d(TAG, String.format("Querying Parse for Media with objectId: %s", objectId));
        ParseQuery<Media> mediaQuery = ParseQuery.getQuery(MEDIA_CLASS_NAME);
        mediaQuery.whereEqualTo(OBJECT_ID_KEY, objectId);
        mediaQuery.getFirstInBackground(callback);
    }
}
