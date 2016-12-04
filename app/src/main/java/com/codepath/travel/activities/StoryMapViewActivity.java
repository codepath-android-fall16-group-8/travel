package com.codepath.travel.activities;

import android.os.Bundle;
import android.widget.Toast;

import com.codepath.travel.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by rpraveen on 12/4/16.
 */

public class StoryMapViewActivity extends BaseActivity {

  // member vars
  private SupportMapFragment mStoryMapFragment;
  private GoogleMap mGoogleMap;

  @Override
  public void onCreate(Bundle savedInstance) {
    super.onCreate(savedInstance);
    setContentView(R.layout.activity_story_map);
    initializeCommonViews();
    initializeViews();
  }

  private void initializeViews() {
    setActionBarTitle("Map View");
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
  }

  protected void loadMap(GoogleMap googleMap) {
    mGoogleMap = googleMap;
    if (mGoogleMap != null) {
      // Map is ready
      Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
    }
  }
 }
