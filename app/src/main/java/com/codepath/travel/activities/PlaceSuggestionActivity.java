package com.codepath.travel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.travel.Constants;
import com.codepath.travel.R;
import com.codepath.travel.adapters.PlaceSuggestionArrayAdapter;
import com.codepath.travel.helper.ImageUtils;
import com.codepath.travel.helper.ItemClickSupport;
import com.codepath.travel.listeners.PlacesCartListener;
import com.codepath.travel.models.SuggestionPlace;
import com.codepath.travel.net.GoogleAsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import cz.msebera.android.httpclient.Header;

public class PlaceSuggestionActivity extends BaseActivity implements PlacesCartListener {

    // Intent ARGS
    public static final String DESTINATION_NAME_ARG = "destination_name";
    public static final String DESTINATION_ID_ARG = "destination_id";
    public static final String DESTINATION_LAT_LONG_ARG = "destination_lat_long";
    public static final String DESTINATION_PHOTO_ARG = "destination_photo";

    // Strings
    @BindString(R.string.toolbar_title_place_suggestion) String toolbarTitle;

    //Views
    @BindView(R.id.btnCart) ImageButton mStoryCart;
    @BindView(R.id.ivBackDrop) ImageView ivBackDrop;
    @BindView(R.id.pbBackDropImageLoading) ProgressBar pbImageLoading;
    @BindView(R.id.rvRestaurantPlaces) RecyclerView mRvRestaurantPlaces;
    @BindView(R.id.rvSightPlaces) RecyclerView mRvSightPlaces;
    @BindView(R.id.tvPlaceCount) TextView mTvPlaceCount;

    //Member variable
    private String mDestination;
    private String mDestinationPhotoRef;
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

        mDestination = getIntent().getStringExtra(DESTINATION_NAME_ARG);
        //Get cover photo url for trip
        getPhotoReferenceByPlaceID(getIntent().getStringExtra(DESTINATION_ID_ARG));
        mLatLng = getIntent().getStringExtra(DESTINATION_LAT_LONG_ARG);
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

        mTvPlaceCount.setText(String.valueOf(mSuggestionPlaces.size()));
        mTvPlaceCount.setVisibility(View.GONE);

        setUpClickListeners();
        showPlacesToEat();
        showPointsOfInterest();

        String photoRef = getIntent().getStringExtra(DESTINATION_PHOTO_ARG);
        String photoUrl = GoogleAsyncHttpClient.getPlacePhotoUrl(photoRef);
        ImageUtils.loadImage(ivBackDrop, photoUrl, R.drawable.ic_photoholder, pbImageLoading);
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
        mTvPlaceCount.setVisibility(View.VISIBLE);
        mTvPlaceCount.setText(String.valueOf(mSuggestionPlaces.size()));

    }

    @Override
    public void removePlace(SuggestionPlace suggestionPlace) {
        mSuggestionPlaces.remove(suggestionPlace);
        mTvPlaceCount.setText(String.valueOf(mSuggestionPlaces.size()));
        if(mSuggestionPlaces.isEmpty()) {
            mTvPlaceCount.setVisibility(View.GONE);
        }
    }

    /* Navigation */
    private void launchCreateStoryActivity() {
        Intent createTrip = new Intent(this, CreateStoryActivity.class);
        createTrip.putExtra(Constants.PLACE_NAME_ARG, mDestination);
        createTrip.putExtra(Constants.PLACE_PHOTO_REF_ARG, mDestinationPhotoRef);
        createTrip.putExtra(Constants.SUGGESTION_PLACES_LIST_ARG,
                Parcels.wrap(mSuggestionPlaces));

        startActivity(createTrip);
        setResult(RESULT_OK);
        finish();
    }

    private void launchPlaceDetailActivity(int position, boolean restaurants) {
        SuggestionPlace suggestionPlace = restaurants ? mRestaurants.get(position) : mSights.get(position);
        Intent placeDetail = new Intent(this, PlaceDetailActivity.class);
        placeDetail.putExtra(Constants.PLACE_ID_ARG, suggestionPlace.getPlaceId());
        placeDetail.putExtra(Constants.PLACE_NAME_ARG, suggestionPlace.getName());
        placeDetail.putExtra(Constants.PLACE_ADDED_ARG, suggestionPlace.isSelected());
        placeDetail.putExtra(Constants.POSITION_ARG, position);
        placeDetail.putExtra(Constants.PLACE_CATEGORY_ARG, restaurants);
        startActivityForResult(placeDetail, Constants.PLACE_DETAIL_REQUEST);
    }

    private void getPhotoReferenceByPlaceID(String placeID) {
        {   //To get photo reference for cover photo
            GoogleAsyncHttpClient.getPlaceDetails(placeID, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        mDestinationPhotoRef = response.getJSONObject("result").getJSONArray("photos").getJSONObject(0).getString("photo_reference");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == Constants.PLACE_DETAIL_REQUEST) {
            boolean restaurants = data.getBooleanExtra(Constants.PLACE_CATEGORY_ARG, true);
            int position = data.getIntExtra(Constants.POSITION_ARG, 0);
            boolean original = restaurants
                    ? mRestaurants.get(position).isSelected()
                    : mSights.get(position).isSelected();
            boolean updated = data.getBooleanExtra(Constants.PLACE_ADDED_ARG, false);
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

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

}
