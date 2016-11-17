package com.codepath.travel.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.travel.GoogleClient;
import com.codepath.travel.R;
import com.codepath.travel.adapters.PlaceSuggestionArrayAdapter;
import com.codepath.travel.models.StoryPlace;
import com.loopj.android.http.AsyncHttpClient;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaceSuggestionActivity extends AppCompatActivity {

    private static final int CREATE_STORY_REQUEST = 1;

    // intent arguments
    public static final String DESTINATION_ARGS = "destination";
    public static final String LATLNG_ARGS = "latlong";

    //Views
    @BindView(R.id.btnCart) ImageButton mStoryCart;
    @BindView(R.id.rvSuggestionPlaces) RecyclerView mRvSuggestionPlaces;

    //Member variable
    private String mDestination;
    private String mLatLng;

    AsyncHttpClient googleClient;
    private ArrayList<StoryPlace> mStoryPlaces;
    private ArrayList<StoryPlace> mSelectedStoryPlaces;
    private PlaceSuggestionArrayAdapter mPlaceSuggestionArrayAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_suggestion);
        ButterKnife.bind(this);

        googleClient = GoogleClient.getInstance();
        mStoryPlaces = new ArrayList<>();
        mSelectedStoryPlaces = new ArrayList<>();
        mPlaceSuggestionArrayAdapter = new PlaceSuggestionArrayAdapter(this, googleClient, mStoryPlaces);
        mRvSuggestionPlaces.setAdapter(mPlaceSuggestionArrayAdapter);

        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvSuggestionPlaces.setLayoutManager(mLinearLayoutManager);

        mDestination = getIntent().getStringExtra(DESTINATION_ARGS);
        mLatLng = getIntent().getStringExtra(LATLNG_ARGS);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getApplicationContext().getResources().getString(R.string.toolbar_title_place_suggestion) + mDestination);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpClickListeners();
        showPlacesToEat(mLatLng);
    }

    private void setUpClickListeners() {
        mStoryCart.setOnClickListener((View view) -> {
            Intent createTrip = new Intent(this, CreateStoryActivity.class);
            createTrip.putExtra(
                    CreateStoryActivity.DESTINATION_ARGS,
                    mDestination
            );
            //Send array list of story places selected by user
            startActivityForResult(createTrip, CREATE_STORY_REQUEST);
            setResult(RESULT_OK);
            finish();
        });
    }

    private void showPlacesToEat(String latLng) {
        //Displays set of places to eat which are near to a given place
        //Call Google Client api
        mPlaceSuggestionArrayAdapter.populatePlacesToEat(googleClient, mLatLng, "restaurant", "cruise");


    }
}
