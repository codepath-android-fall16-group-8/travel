package com.codepath.travel.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.codepath.travel.net.GoogleAsyncHttpClient;
import com.codepath.travel.R;
import com.codepath.travel.adapters.viewholders.SuggestedPlaceViewHolder;
import com.codepath.travel.listeners.PlacesCartListener;
import com.codepath.travel.models.SuggestionPlace;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * RecyclerView adapter for an array of suggested places.
 */
public class PlaceSuggestionArrayAdapter extends RecyclerView.Adapter<SuggestedPlaceViewHolder> {

    private List<SuggestionPlace> mSuggestionPlaces;
    PlacesCartListener placesCartListener;
    Context mContext;

    public PlaceSuggestionArrayAdapter(List<SuggestionPlace> suggestionPlaces,
            PlacesCartListener listener, Context context) {
        mSuggestionPlaces = suggestionPlaces;
        placesCartListener = listener;
        mContext = context;
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
        holder.listeners(suggestionPlace, placesCartListener);
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return mSuggestionPlaces.size();
    }

    public void clear() {
        mSuggestionPlaces.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<SuggestionPlace> suggestionPlaceList) {
        mSuggestionPlaces.addAll(suggestionPlaceList);
        notifyDataSetChanged();
    }

    public void populatePlacesNearby(String location, String keyword) {
        GoogleAsyncHttpClient.getNearbyPlaces(location, keyword, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try { clear();
                    addAll(SuggestionPlace.getPlacesFromJSONArray(response.getJSONArray("results")));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                // TODO: Show error snackbar
                Log.e("ERROR", t.toString());
            }
        });
    }

}
