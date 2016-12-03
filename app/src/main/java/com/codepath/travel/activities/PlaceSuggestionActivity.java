package com.codepath.travel.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.codepath.travel.R;
import com.codepath.travel.adapters.PlacesPagerAdapter;
import com.codepath.travel.helper.ImageUtils;
import com.codepath.travel.listeners.PlacesCartListener;
import com.codepath.travel.models.SuggestionPlace;
import com.codepath.travel.net.GoogleAsyncHttpClient;

import butterknife.BindString;
import butterknife.BindView;

public class PlaceSuggestionActivity extends BaseActivity implements PlacesCartListener {

    // Intent ARGS
    public static final String DESTINATION_NAME_ARG = "destination_name";
    public static final String DESTINATION_ID_ARG = "destination_id";
    public static final String DESTINATION_LAT_LONG_ARG = "destination_lat_long";
    public static final String DESTINATION_PHOTO_ARG = "destination_photo";

    // Strings
    @BindString(R.string.toolbar_title_place_suggestion) String toolbarTitle;

    //Views
    @BindView(R.id.ivBackDrop) ImageView ivBackDrop;
    @BindView(R.id.pbBackDropImageLoading) ProgressBar pbImageLoading;
    @BindView(R.id.tabLayout) TabLayout tabLayout;
    @BindView(R.id.tabViewPager) ViewPager tabViewPager;

    //Member variable
    private String mDestination;
    private String mLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_suggestion);
        initializeCommonViews();

        mDestination = getIntent().getStringExtra(DESTINATION_NAME_ARG);
        mLatLng = getIntent().getStringExtra(DESTINATION_LAT_LONG_ARG);
        setActionBarTitle(String.format(toolbarTitle, mDestination));

        tabViewPager.setAdapter(new PlacesPagerAdapter(getSupportFragmentManager(), this, mLatLng));
        tabLayout.setupWithViewPager(tabViewPager);

        //setUpClickListeners();

        String photoRef = getIntent().getStringExtra(DESTINATION_PHOTO_ARG);
        String photoUrl = GoogleAsyncHttpClient.getPlacePhotoUrl(photoRef);
        ImageUtils.loadImage(ivBackDrop, photoUrl, R.drawable.ic_photoholder, pbImageLoading);
        //getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

//    private void setUpClickListeners() {
//        // cart button -> create story activity
//        mStoryCart.setOnClickListener((View view) -> launchCreateStoryActivity());
//
//        // recycler view items -> place detail activity
//        ItemClickSupport.addTo(this.mRvRestaurantPlaces).setOnItemClickListener(
//                (recyclerView, position, v) -> launchPlaceDetailActivity(position, true)
//        );
//        ItemClickSupport.addTo(this.mRvSightPlaces).setOnItemClickListener(
//                (recyclerView, position, v) -> launchPlaceDetailActivity(position, false)
//        );
//    }

    /* Listeners */
    @Override
    public void addPlace(SuggestionPlace suggestionPlace) {
//        mSuggestionPlaces.add(suggestionPlace);
//        mTvPlaceCount.setVisibility(View.VISIBLE);
//        mTvPlaceCount.setText(String.valueOf(mSuggestionPlaces.size()));

    }

    @Override
    public void removePlace(SuggestionPlace suggestionPlace) {
//        mSuggestionPlaces.remove(suggestionPlace);
//        mTvPlaceCount.setText(String.valueOf(mSuggestionPlaces.size()));
//        if(mSuggestionPlaces.isEmpty()) {
//            mTvPlaceCount.setVisibility(View.GONE);
//        }
    }
//
//    /* Navigation */
//    private void launchCreateStoryActivity() {
//        Intent createTrip = new Intent(this, CreateStoryActivity.class);
//        createTrip.putExtra(Constants.PLACE_NAME_ARG, mDestination);
//        createTrip.putExtra(Constants.PLACE_PHOTO_REF_ARG, mDestinationPhotoRef);
//        createTrip.putExtra(Constants.SUGGESTION_PLACES_LIST_ARG,
//                Parcels.wrap(mSuggestionPlaces));
//
//        startActivity(createTrip);
//        setResult(RESULT_OK);
//        finish();
//    }
//
//    private void launchPlaceDetailActivity(int position, boolean restaurants) {
//        SuggestionPlace suggestionPlace = restaurants ? mRestaurants.get(position) : mSights.get(position);
//        Intent placeDetail = new Intent(this, PlaceDetailActivity.class);
//        placeDetail.putExtra(Constants.PLACE_ID_ARG, suggestionPlace.getPlaceId());
//        placeDetail.putExtra(Constants.PLACE_NAME_ARG, suggestionPlace.getName());
//        placeDetail.putExtra(Constants.PLACE_ADDED_ARG, suggestionPlace.isSelected());
//        placeDetail.putExtra(Constants.POSITION_ARG, position);
//        placeDetail.putExtra(Constants.PLACE_CATEGORY_ARG, restaurants);
//        startActivityForResult(placeDetail, Constants.PLACE_DETAIL_REQUEST);
//    }
//
//    private void getPhotoReferenceByPlaceID(String placeID) {
//        {   //To get photo reference for cover photo
//            GoogleAsyncHttpClient.getPlaceDetails(placeID, new JsonHttpResponseHandler() {
//
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                    try {
//                        mDestinationPhotoRef = response.getJSONObject("result").getJSONArray("photos").getJSONObject(0).getString("photo_reference");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
//                    //Show error snackbar
//                    Log.e("ERROR", t.toString());
//                }
//            });
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK && requestCode == Constants.PLACE_DETAIL_REQUEST) {
//            boolean restaurants = data.getBooleanExtra(Constants.PLACE_CATEGORY_ARG, true);
//            int position = data.getIntExtra(Constants.POSITION_ARG, 0);
//            boolean original = restaurants
//                    ? mRestaurants.get(position).isSelected()
//                    : mSights.get(position).isSelected();
//            boolean updated = data.getBooleanExtra(Constants.PLACE_ADDED_ARG, false);
//            if (original != updated) {
//                if (restaurants) {
//                    mRestaurantsSuggestionArrayAdapter.notifyItemChanged(position);
//                } else {
//                    mSightsSuggestionArrayAdapter.notifyItemChanged(position);
//                }
//            }
//        }
//    }

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
