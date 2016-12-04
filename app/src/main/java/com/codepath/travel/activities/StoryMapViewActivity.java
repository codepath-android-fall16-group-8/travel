package com.codepath.travel.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.codepath.travel.R;
import com.codepath.travel.adapters.StoryPlaceArrayAdapter;
import com.codepath.travel.models.parse.StoryPlace;
import com.codepath.travel.models.parse.Trip;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by rpraveen on 12/4/16.
 */

public class StoryMapViewActivity extends BaseActivity {

  private static final String TAG = StoryActivity.class.getSimpleName();

  // intent args
  public static final String TRIP_TITLE_ARG = "trip_title";
  public static final String TRIP_ID_ARG = "trip_id";

  // views
  @BindView(R.id.rvStoryPlaces) RecyclerView rvStoryPlaces;

  // member vars
  private SupportMapFragment mStoryMapFragment;
  private GoogleMap mGoogleMap;
  private String mTripID;
  private String mTripTitle;
  private Trip mTrip;
  private List<StoryPlace> mStoryPlaces;
  private StoryPlaceArrayAdapter mStoryPlacesAdapter;

  @Override
  public void onCreate(Bundle savedInstance) {
    super.onCreate(savedInstance);
    setContentView(R.layout.activity_story_map);
    initializeCommonViews();
    mStoryMapFragment =
    ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapStoryView));
    if (mStoryMapFragment != null) {
      mStoryMapFragment.getMapAsync(new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap map) {
          loadMap(map);
        }
      });
    } else {
      Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
    }
    mTripID = getIntent().getStringExtra(TRIP_ID_ARG);
    mTripTitle = getIntent().getStringExtra(TRIP_TITLE_ARG);
  }

  private void initializeViews() {
    setActionBarTitle(mTripTitle);
    Trip.getTripForObjectId(mTripID, (trip, e) -> {
      if (e == null) {
        mTrip = trip;
        setUpRecyclerView();
        getPlacesInTrip();
      } else {
        Log.d(TAG, String.format("Failed to get trip for id %s", mTrip));
      }

    });
  }

  private void setUpRecyclerView() {
    mStoryPlaces = new ArrayList<>();
    mStoryPlacesAdapter = new StoryPlaceArrayAdapter(this, null, mStoryPlaces);
    rvStoryPlaces.setHasFixedSize(true);
    rvStoryPlaces.setAdapter(mStoryPlacesAdapter);
    rvStoryPlaces.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
  }

  private void getPlacesInTrip() {
    Trip.getPlacesForTripId(mTripID, (places, se) -> {
      if (se == null) {
        mStoryPlaces.addAll(places);
        mStoryPlacesAdapter.notifyDataSetChanged();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (StoryPlace place : places) {
          double latitude = place.getLatitude();
          double longitude = place.getLongitude();
          Marker marker =
            mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
          builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
        mGoogleMap.animateCamera(cu);
      } else {
        Log.d(TAG, String.format("Failed getPlacesInTrip: %s", se.getMessage()));
      }
    });
  }

  protected void loadMap(GoogleMap googleMap) {
    mGoogleMap = googleMap;
    if (mGoogleMap != null) {
      // Map is ready
      initializeViews();
    } else {
      Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
    }
  }
 }
