package com.codepath.travel.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.codepath.travel.R;
import com.codepath.travel.adapters.PlaceSuggestionArrayAdapter;
import com.codepath.travel.models.SuggestionPlace;
import com.codepath.travel.net.GoogleAsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;

/**
 * Created by rpraveen on 12/3/16.
 */

public class PlacesListFragment extends Fragment {
  private static final String TAG = PlacesListFragment.class.getSimpleName();

  // args
  protected static final String DESTINATION_LAT_LONG_ARG = "destination_lat_long";
  protected static final String DESTINATION_PLACE_CATEGORY_ARG = "destination_place_category";

  @BindView(R.id.rvPlaces) RecyclerView rvPlaces;
  @BindView(R.id.pbLoading) ProgressBar pbLoading;

  protected Unbinder unbinder;

  // member vars
  protected ArrayList<SuggestionPlace> mPlaces;
  protected PlaceSuggestionArrayAdapter mPlacesSuggestionAdapter;
  protected String mDestinationLatLong;
  protected String mPlaceCategory;

  public static PlacesListFragment newInstance(String destinationLatLong, String placeCategory) {
    PlacesListFragment fragment = new PlacesListFragment();
    Bundle args = new Bundle();
    args.putString(DESTINATION_LAT_LONG_ARG, destinationLatLong);
    args.putString(DESTINATION_PLACE_CATEGORY_ARG, placeCategory);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle args = getArguments();
    mDestinationLatLong = args.getString(DESTINATION_LAT_LONG_ARG);
    mPlaceCategory = args.getString(DESTINATION_PLACE_CATEGORY_ARG);
    mPlaces = new ArrayList<>();
    mPlacesSuggestionAdapter = new PlaceSuggestionArrayAdapter(mPlaces, getActivity());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    View view =  inflater.inflate(R.layout.fragment_suggested_places, parent, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    rvPlaces.setHasFixedSize(true);
    rvPlaces.setAdapter(mPlacesSuggestionAdapter);
    rvPlaces.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    populatePlaces();
  }

  protected void populatePlaces() {
    GoogleAsyncHttpClient.getNearbyPlaces(mDestinationLatLong, mPlaceCategory, new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        try {
          mPlaces.addAll(SuggestionPlace.getPlacesFromJSONArray(response.getJSONArray("results")));
          mPlacesSuggestionAdapter.notifyDataSetChanged();
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
