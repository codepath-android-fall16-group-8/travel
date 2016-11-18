package com.codepath.travel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import com.codepath.travel.R;
import com.codepath.travel.adapters.PlaceSuggestionArrayAdapter;
import com.codepath.travel.helper.PlacesCartListener;
import com.codepath.travel.models.SuggestionPlace;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaceSuggestionActivity extends AppCompatActivity implements PlacesCartListener {

    private static final int CREATE_STORY_REQUEST = 1;

    // intent arguments
    public static final String DESTINATION_ARGS = "destination";
    public static final String LATLNG_ARGS = "latlong";

    //Views
    @BindView(R.id.btnCart) ImageButton mStoryCart;
    @BindView(R.id.rvRestaurantPlaces) RecyclerView mRvRestaurantPlaces;
    @BindView(R.id.rvSightPlaces) RecyclerView mRvSightPlaces;


    //Member variable
    private String mDestination;
    private String mLatLng;

    private ArrayList<SuggestionPlace> mRestaurants;
    private ArrayList<SuggestionPlace> mSights;
    private ArrayList<SuggestionPlace> mSuggestionPlaces;
    private PlaceSuggestionArrayAdapter mRestaurantsSuggestionArrayAdapter;
    private PlaceSuggestionArrayAdapter mSightsSuggestionArrayAdapter;

    private LinearLayoutManager mRestaurantLinearLayoutManager;
    private LinearLayoutManager mSightLinearLayoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_suggestion);
        ButterKnife.bind(this);
        mSuggestionPlaces = new ArrayList<>();

        mRestaurants = new ArrayList<>();
        mRestaurantsSuggestionArrayAdapter = new PlaceSuggestionArrayAdapter(mRestaurants, this, getApplicationContext());
        mRvRestaurantPlaces.setAdapter(mRestaurantsSuggestionArrayAdapter);
        mRestaurantLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvRestaurantPlaces.setLayoutManager(mRestaurantLinearLayoutManager);

        mSights = new ArrayList<>();
        mSightsSuggestionArrayAdapter = new PlaceSuggestionArrayAdapter(mSights, this, getApplicationContext());
        mRvSightPlaces.setAdapter(mSightsSuggestionArrayAdapter);
        mSightLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvSightPlaces.setLayoutManager(mSightLinearLayoutManager);

        mDestination = getIntent().getStringExtra(DESTINATION_ARGS);
        mLatLng = getIntent().getStringExtra(LATLNG_ARGS);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getApplicationContext().getResources().getString(R.string.toolbar_title_place_suggestion) + mDestination);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpClickListeners();
        showPlacesToEat();
        showPointsOfInterest();
    }

    private void setUpClickListeners() {
        mStoryCart.setOnClickListener((View view) -> {
            Intent createTrip = new Intent(this, CreateStoryActivity.class);
            createTrip.putExtra(
                    CreateStoryActivity.DESTINATION_ARGS,
                    mDestination
            );
            createTrip.putParcelableArrayListExtra(CreateStoryActivity.SUGGESTION_PLACES_LIST_ARGS, mSuggestionPlaces);

            startActivityForResult(createTrip, CREATE_STORY_REQUEST);
            setResult(RESULT_OK);
            finish();
        });
    }

    private void showPlacesToEat() {
        //Get set of places to eat near to a given place
        //Call Google AsyncHttpClient api
         mRestaurantsSuggestionArrayAdapter.populatePlacesNearby(mLatLng, "restaurant|cafe|food");
    }

    private void showPointsOfInterest() {
        //Get set of sights to see near to a given place
        mSightsSuggestionArrayAdapter.populatePlacesNearby(mLatLng, "point_of_interest|establishment|museum|amusement_park|art_gallery|casino|zoo");
    }

    @Override
    public void addPlace(SuggestionPlace suggestionPlace) {
        mSuggestionPlaces.add(suggestionPlace);

    }

    @Override
    public void removePlace(SuggestionPlace suggestionPlace) {
        mSuggestionPlaces.remove(suggestionPlace);
    }

}
