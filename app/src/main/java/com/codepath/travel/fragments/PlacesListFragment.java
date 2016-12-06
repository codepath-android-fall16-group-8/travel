package com.codepath.travel.fragments;

import static com.codepath.travel.activities.PlaceDetailActivity.LAT_LNG_ARG;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.codepath.travel.Constants;
import com.codepath.travel.R;
import com.codepath.travel.activities.PlaceDetailActivity;
import com.codepath.travel.activities.PlaceSuggestionActivity;
import com.codepath.travel.activities.StoryActivity;
import com.codepath.travel.adapters.PlaceSuggestionArrayAdapter;
import com.codepath.travel.helper.ItemClickSupport;
import com.codepath.travel.models.SuggestionPlace;
import com.codepath.travel.net.GoogleAsyncHttpClient;
import com.google.android.gms.maps.model.LatLng;
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

  // intent args
  public static final String POSITION_ARG = "position";
  public static final String PLACE_ADDED_ARG = "place_added";

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
    rvPlaces.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    // recycler view items -> place detail activity
    ItemClickSupport.addTo(rvPlaces).setOnItemClickListener(
            (recyclerView, position, v) -> launchPlaceDetailActivity(position)
    );
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

  private void launchPlaceDetailActivity(int position) {
    SuggestionPlace suggestionPlace = mPlaces.get(position);
    Intent placeDetail = new Intent(getContext(), PlaceDetailActivity.class);
    placeDetail.putExtra(PlaceDetailActivity.PLACE_ID_ARG, suggestionPlace.getPlaceId());
    placeDetail.putExtra(PlaceDetailActivity.PLACE_NAME_ARG, suggestionPlace.getName());
    placeDetail.putExtra(PlaceDetailActivity.POSITION_ARG, position);
    placeDetail.putExtra(PLACE_ADDED_ARG, suggestionPlace.isSelected());
    placeDetail.putExtra(LAT_LNG_ARG, new LatLng(suggestionPlace.getLatitude(), suggestionPlace.getLongitude()));

    ActivityOptionsCompat options = android.support.v4.app.ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity());
    startActivityForResult(placeDetail, Constants.PLACE_DETAIL_REQUEST, options.toBundle());
  }
}
