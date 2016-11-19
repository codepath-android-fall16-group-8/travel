package com.codepath.travel.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import static com.codepath.travel.models.ParseModelConstants.TAG_CLASS_NAME;
import static com.codepath.travel.models.ParseModelConstants.USER_KEY;
import static com.codepath.travel.models.ParseModelConstants.TRIP_KEY;
import static com.codepath.travel.models.ParseModelConstants.STORY_PLACE_KEY;
import static com.codepath.travel.models.ParseModelConstants.MEDIA_KEY;

/**
 * Parse model for a tag.
 */
@ParseClassName(TAG_CLASS_NAME)
public class Tag extends ParseObject {

    public Tag() {
        super();
    }

    public Tag(ParseUser user, Trip trip) {
        super();
        setUser(user);
        setTrip(trip);
    }

    public Tag(ParseUser user, StoryPlace storyPlace) {
        super();
        setUser(user);
        setStoryPlace(storyPlace);
    }

    public Tag(ParseUser user, Media media) {
        super();
        setUser(user);
        setMedia(media);
    }

    public ParseUser getUser()  {
        return getParseUser(USER_KEY);
    }

    public void setUser(ParseUser user) {
        put(USER_KEY, user);
    }

    public Trip getTrip() {
        return (Trip) getParseObject(TRIP_KEY);
    }

    public void setTrip(Trip trip) {
        put(TRIP_KEY, trip);
    }

    public StoryPlace getStoryPlace() {
        return (StoryPlace) getParseObject(STORY_PLACE_KEY);
    }

    public void setStoryPlace(StoryPlace storyPlace) {
        put(STORY_PLACE_KEY, storyPlace);
    }

    public Media getMedia() {
        return (Media) getParseObject(MEDIA_KEY);
    }

    public void setMedia(Media media) {
        put(MEDIA_KEY, media);
    }
}
