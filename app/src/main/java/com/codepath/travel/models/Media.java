package com.codepath.travel.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import static com.codepath.travel.models.ParseModelConstants.CAPTION_KEY;
import static com.codepath.travel.models.ParseModelConstants.DATA_URL_KEY;
import static com.codepath.travel.models.ParseModelConstants.MEDIA_CLASS_NAME;
import static com.codepath.travel.models.ParseModelConstants.STORY_PLACE_KEY;
import static com.codepath.travel.models.ParseModelConstants.TYPE_KEY;

/**
 * Parse model for a media item, which can be text or media.
 */
@ParseClassName(MEDIA_CLASS_NAME)
public class Media extends ParseObject {

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
}
