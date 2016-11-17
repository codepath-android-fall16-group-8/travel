package com.codepath.travel.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.travel.GoogleClient;
import com.codepath.travel.R;
import com.codepath.travel.activities.PlaceSuggestionActivity;
import com.codepath.travel.helper.ItemTouchHelperAdapter;
import com.codepath.travel.helper.ItemTouchHelperViewHolder;
import com.codepath.travel.helper.OnStartDragListener;
import com.codepath.travel.models.Media;
import com.codepath.travel.models.ParseModelConstants;
import com.codepath.travel.models.StoryPlace;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by aditikakadebansal on 11/16/16.
 */

/**
 * Created by aditikakadebansal on 11/9/16.
 */
public class PlaceSuggestionArrayAdapter extends RecyclerView.Adapter<PlaceSuggestionArrayAdapter.StoryViewHolder> {

    private List<StoryPlace> mStoryPlaces;
    private Context mContext;
    private AsyncHttpClient mClient;

    public PlaceSuggestionArrayAdapter(Context context, AsyncHttpClient client, List<StoryPlace> storyPlaces) {
        mStoryPlaces = storyPlaces;
        mContext = context;
        mClient = client;
    }

    private Context getContext() {
        return mContext;
    }

    public void clear() {
        mStoryPlaces.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<StoryPlace> storyPlacesList) {
        mStoryPlaces.addAll(storyPlacesList);
        notifyDataSetChanged();
    }

    public void insert(StoryPlace storyPlace, int position) {
        mStoryPlaces.add(position, storyPlace);
    }

    public void remove(int position) {
        mStoryPlaces.remove(position);
    }

    public int getPosition(StoryPlace storyPlace) {
        return mStoryPlaces.indexOf(storyPlace);
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
        StoryPlace storyPlace = mStoryPlaces.get(position);
        holder.populate(storyPlace);
        holder.listeners(storyPlace);

    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return mStoryPlaces.size();
    }



    public class StoryViewHolder extends RecyclerView.ViewHolder {

        // views
        @BindView(R.id.tvSuggestionPlaceName) TextView tvSuggestionPlaceName;
        @BindView(R.id.tvSuggestionPlaceRating) TextView tvSuggestionPlaceRating;
        @BindView(R.id.ivAddSuggestionPlace) ImageView ivAddSuggestionPlace;

        boolean isPlaceSelected = false;

        public StoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void populate(StoryPlace storyPlace) {
            tvSuggestionPlaceName.setText(storyPlace.getName());
            tvSuggestionPlaceRating.setText(String.valueOf(storyPlace.getRating()));
        }

        public void listeners(StoryPlace storyPlace) {
            ivAddSuggestionPlace.setOnClickListener((View view) -> {
                if(isPlaceSelected) {
                    ivAddSuggestionPlace.setImageResource(R.drawable.ic_add);
                    //send updated story place to be added in trip
                    isPlaceSelected = false;
                }else {
                    ivAddSuggestionPlace.setImageResource(R.drawable.com_facebook_button_like_background);
                    isPlaceSelected = true;
                }
            });
        }
    }

    public void populatePlacesToEat(AsyncHttpClient client, String location, String type, String keyword ) {
        {
            RequestParams params = new RequestParams();
            params.put("location", location);
            params.put("type", type);
            params.put("keyword", keyword);
            params.put("key", GoogleClient.GOOGLE_PLACES_SEARCH_API_KEY);
            client.get(GoogleClient.NEARBY_SEARCH_URL, params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try { clear();
                        addAll(StoryPlace.getPlacesFromJSONArray(response.getJSONArray("results")));
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
