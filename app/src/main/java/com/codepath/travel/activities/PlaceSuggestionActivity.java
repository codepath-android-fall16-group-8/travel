package com.codepath.travel.activities;

import static com.codepath.travel.Constants.CREATE_STORY_REQUEST;
import static com.codepath.travel.Constants.PLACE_ADDED_ARG;
import static com.codepath.travel.Constants.PLACE_CATEGORY_ARG;
import static com.codepath.travel.Constants.PLACE_DETAIL_REQUEST;
import static com.codepath.travel.Constants.PLACE_ID_ARG;
import static com.codepath.travel.Constants.PLACE_NAME_ARG;
import static com.codepath.travel.Constants.POSITION_ARG;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.codepath.travel.Constants;
import com.codepath.travel.R;
import com.codepath.travel.adapters.PlaceSuggestionArrayAdapter;
import com.codepath.travel.helper.ItemClickSupport;
import com.codepath.travel.listeners.PlacesCartListener;
import com.codepath.travel.models.SuggestionPlace;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;

public class PlaceSuggestionActivity extends BaseActivity implements PlacesCartListener {

    // Strings
    @BindString(R.string.toolbar_title_place_suggestion) String toolbarTitle;

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
        initializeCommonViews();

        mDestination = getIntent().getStringExtra(Constants.DESTINATION_ARG);
        mLatLng = getIntent().getStringExtra(Constants.LATLNG_ARG);
        setActionBarTitle(String.format(toolbarTitle, mDestination));

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

        setUpClickListeners();
        showPlacesToEat();
        showPointsOfInterest();
    }

    private void setUpClickListeners() {
        // cart button -> create story activity
        mStoryCart.setOnClickListener((View view) -> launchCreateStoryActivity());

        // recycler view items -> place detail activity
        ItemClickSupport.addTo(this.mRvRestaurantPlaces).setOnItemClickListener(
                (recyclerView, position, v) -> launchPlaceDetailActivity(position, true)
        );
        ItemClickSupport.addTo(this.mRvSightPlaces).setOnItemClickListener(
                (recyclerView, position, v) -> launchPlaceDetailActivity(position, false)
        );
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

    /* Listeners */
    @Override
    public void addPlace(SuggestionPlace suggestionPlace) {
        mSuggestionPlaces.add(suggestionPlace);

    }

    @Override
    public void removePlace(SuggestionPlace suggestionPlace) {
        mSuggestionPlaces.remove(suggestionPlace);
    }

    /* Navigation */
    private void launchCreateStoryActivity() {
        Intent createTrip = new Intent(this, CreateStoryActivity.class);
        createTrip.putExtra(Constants.DESTINATION_ARG, mDestination);
        createTrip.putExtra(Constants.SUGGESTION_PLACES_LIST_ARG,
                Parcels.wrap(mSuggestionPlaces));

        startActivity(createTrip);
        setResult(RESULT_OK);
        finish();
    }

    private void launchPlaceDetailActivity(int position, boolean restaurants) {
        SuggestionPlace suggestionPlace = restaurants ? mRestaurants.get(position) : mSights.get(position);
        Intent placeDetail = new Intent(this, PlaceDetailActivity.class);
        placeDetail.putExtra(PLACE_ID_ARG, suggestionPlace.getPlaceId());
        placeDetail.putExtra(PLACE_NAME_ARG, suggestionPlace.getName());
        placeDetail.putExtra(PLACE_ADDED_ARG, suggestionPlace.isSelected());
        placeDetail.putExtra(POSITION_ARG, position);
        placeDetail.putExtra(PLACE_CATEGORY_ARG, restaurants);
        startActivityForResult(placeDetail, PLACE_DETAIL_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == PLACE_DETAIL_REQUEST) {
            boolean restaurants = data.getBooleanExtra(PLACE_CATEGORY_ARG, true);
            int position = data.getIntExtra(POSITION_ARG, 0);
            boolean original = restaurants
                    ? mRestaurants.get(position).isSelected()
                    : mSights.get(position).isSelected();
            boolean updated = data.getBooleanExtra(PLACE_ADDED_ARG, false);
            if (original != updated) {
                if (restaurants) {
                    mRestaurantsSuggestionArrayAdapter.notifyItemChanged(position);
                } else {
                    mSightsSuggestionArrayAdapter.notifyItemChanged(position);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // if called from home activity
                setResult(RESULT_OK);
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
