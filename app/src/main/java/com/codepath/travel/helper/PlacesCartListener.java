package com.codepath.travel.helper;

import com.codepath.travel.models.SuggestionPlace;

/**
 * Created by aditikakadebansal on 11/16/16.
 */
public interface PlacesCartListener {

    void addPlace(SuggestionPlace suggestionPlace);

    void removePlace(SuggestionPlace suggestionPlace);
}
