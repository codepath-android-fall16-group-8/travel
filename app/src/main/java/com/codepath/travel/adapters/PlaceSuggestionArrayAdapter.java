package com.codepath.travel.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.travel.GoogleAsyncHttpClient;
import com.codepath.travel.R;
import com.codepath.travel.helper.PlacesCartListener;
import com.codepath.travel.models.StoryPlace;
import com.codepath.travel.models.SuggestionPlace;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.CropCircleTransformation;


/**
 * Created by aditikakadebansal on 11/9/16.
 */
public class PlaceSuggestionArrayAdapter extends RecyclerView.Adapter<PlaceSuggestionArrayAdapter.StoryViewHolder> {

    private List<SuggestionPlace> mSuggestionPlaces;
    PlacesCartListener placesCartListener;
    Context mContext;

    public PlaceSuggestionArrayAdapter(List<SuggestionPlace> suggestionPlaces,PlacesCartListener listener, Context context) {
        mSuggestionPlaces = suggestionPlaces;
        placesCartListener = listener;
        mContext = context;
    }

    public void clear() {
        mSuggestionPlaces.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<SuggestionPlace> suggestionPlaceList) {
        mSuggestionPlaces.addAll(suggestionPlaceList);
        notifyDataSetChanged();
    }

    @Override
    public PlaceSuggestionArrayAdapter.StoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new StoryViewHolder(
                inflater.inflate(
                        R.layout.item_suggestion_place,
                        parent,
                        false
                )
        );

    }

    @Override
    public void onBindViewHolder(final PlaceSuggestionArrayAdapter.StoryViewHolder holder, int position) {
        SuggestionPlace suggestionPlace = mSuggestionPlaces.get(position);
        holder.populate(suggestionPlace);
        holder.listeners(suggestionPlace);

    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return mSuggestionPlaces.size();
    }

    public class StoryViewHolder extends RecyclerView.ViewHolder {

        // views
        @BindView(R.id.tvSuggestionPlaceName) TextView tvSuggestionPlaceName;
        @BindView(R.id.tvSuggestionPlaceRating) TextView tvSuggestionPlaceRating;
        @BindView(R.id.ivAddSuggestionPlace) ImageView ivAddSuggestionPlace;
        @BindView(R.id.ivSuggestionPlacePhoto) ImageView ivSuggestionPlacePhoto;

        public StoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void populate(SuggestionPlace suggestionPlace) {
            tvSuggestionPlaceName.setText(suggestionPlace.getName());
            tvSuggestionPlaceRating.setText(String.valueOf(suggestionPlace.getRating()));
            if(suggestionPlace.isSelected()) {
                ivAddSuggestionPlace.setImageResource(R.drawable.ic_tick);
            }else {
                ivAddSuggestionPlace.setImageResource(R.drawable.ic_add);
            }
            ivSuggestionPlacePhoto.setImageResource(0);
            Glide.with(mContext).load(GoogleAsyncHttpClient.PLACE_PHOTO_URL
                    + "&photoreference=" + suggestionPlace.getThumbnail()
                    + "&key=" + GoogleAsyncHttpClient.GOOGLE_PLACES_SEARCH_API_KEY)
                    .placeholder(R.drawable.ic_photoholder)
                    .into(this.ivSuggestionPlacePhoto);
        }

        public void listeners(SuggestionPlace suggestionPlace) {
            ivAddSuggestionPlace.setOnClickListener((View view) -> {
                if(suggestionPlace.isSelected()) {
                    ivAddSuggestionPlace.setImageResource(R.drawable.ic_add);
                    suggestionPlace.setSelected(false);
                    //Remove place id from cart
                    placesCartListener.removePlace(suggestionPlace);

                }else {
                    ivAddSuggestionPlace.setImageResource(R.drawable.ic_tick);
                    suggestionPlace.setSelected(true);
                    //Add place id to cart
                    placesCartListener.addPlace(suggestionPlace);
                }
            });
        }
    }

    public void populatePlacesNearby(String location, String keyword) {
        {
            RequestParams params = new RequestParams();
            params.put("location", location);
            params.put("keyword", keyword);
            params.put("key", GoogleAsyncHttpClient.GOOGLE_PLACES_SEARCH_API_KEY);
            GoogleAsyncHttpClient.get(GoogleAsyncHttpClient.NEARBY_SEARCH_URL, params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try { clear();
                        //addAll(StoryPlace.getPlacesFromJSONArray(response.getJSONArray("results")));
                        addAll(SuggestionPlace.getPlacesFromJSONArray(response.getJSONArray("results")));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                    //Show error snackbar
                    Log.e("ERROR", t.toString());
                }
            });
        }
    }

}
