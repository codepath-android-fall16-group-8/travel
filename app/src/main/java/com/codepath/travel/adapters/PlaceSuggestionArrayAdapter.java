package com.codepath.travel.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.codepath.travel.R;
import com.codepath.travel.adapters.viewholders.SuggestedPlaceViewHolder;
import com.codepath.travel.listeners.PlacesCartListener;
import com.codepath.travel.models.SuggestionPlace;

import java.util.List;

/**
 * RecyclerView adapter for an array of suggested places.
 */
public class PlaceSuggestionArrayAdapter extends RecyclerView.Adapter<SuggestedPlaceViewHolder> {

    private final int DEFAULT = 0;

    private List<SuggestionPlace> mSuggestionPlaces;
    private Context mContext;
    private PlacesCartListener mPlacesCardListener;

    public PlaceSuggestionArrayAdapter(List<SuggestionPlace> suggestionPlaces, Context context) {
        mSuggestionPlaces = suggestionPlaces;
        mContext = context;
        mPlacesCardListener = (PlacesCartListener) mContext;
    }

    @Override
    public SuggestedPlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new SuggestedPlaceViewHolder(inflater.inflate(R.layout.item_suggestion_place,
                parent, false));
    }

    @Override
    public void onBindViewHolder(final SuggestedPlaceViewHolder holder, int position) {
        SuggestionPlace suggestionPlace = mSuggestionPlaces.get(position);
        holder.populate(suggestionPlace);
        holder.listeners(suggestionPlace, mPlacesCardListener);
    }

    @Override
    public int getItemCount() {
        return mSuggestionPlaces.size();
    }

    @Override
    public int getItemViewType(int position) {
        return DEFAULT;
    }
}
