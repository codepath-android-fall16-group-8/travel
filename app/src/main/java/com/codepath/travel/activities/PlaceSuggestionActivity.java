package com.codepath.travel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.travel.Constants;
import com.codepath.travel.R;
import com.codepath.travel.adapters.PlacesPagerAdapter;
import com.codepath.travel.helper.ImageUtils;
import com.codepath.travel.listeners.PlacesCartListener;
import com.codepath.travel.models.SuggestionPlace;
import com.codepath.travel.net.GoogleAsyncHttpClient;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.codepath.travel.Constants.PLACE_DETAIL_REQUEST;

public class PlaceSuggestionActivity extends BaseActivity implements PlacesCartListener {

    // Intent ARGS
    public static final String DESTINATION_NAME_ARG = "destination_name";
    public static final String DESTINATION_ID_ARG = "destination_id";
    public static final String DESTINATION_LAT_LONG_ARG = "destination_lat_long";
    public static final String DESTINATION_PHOTO_ARG = "destination_photo";

    // Strings
    //@BindString(R.string.toolbar_title_place_suggestion) String toolbarTitle;

    //Views
    @BindView(R.id.ivBackDrop) ImageView ivBackDrop;
    @BindView(R.id.pbBackDropImageLoading) ProgressBar pbImageLoading;
    @BindView(R.id.tabLayout) TabLayout tabLayout;
    @BindView(R.id.tabViewPager) ViewPager tabViewPager;
    @BindView(R.id.tvSavedPlacesCount) TextView tvSavedPlacesCount;
    //@BindView(R.id.btCreateTrip) Button btCreateTrip;
    @BindView(R.id.tvCreateTrip) TextView tvCreateTrip;
    //Member variable
    private String mDestination;
    private String mLatLng;

    private List<SuggestionPlace> mStoryPlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_suggestion);
        initializeCommonViews();

        mDestination = getIntent().getStringExtra(DESTINATION_NAME_ARG);
        mLatLng = getIntent().getStringExtra(DESTINATION_LAT_LONG_ARG);
        setActionBarTitle(mDestination);

        tabViewPager.setAdapter(new PlacesPagerAdapter(getSupportFragmentManager(), this, mLatLng));
        tabLayout.setupWithViewPager(tabViewPager);

        setUpClickListeners();

        String photoRef = getIntent().getStringExtra(DESTINATION_PHOTO_ARG);
        String photoUrl = GoogleAsyncHttpClient.getPlacePhotoUrl(photoRef);
        ImageUtils.loadImage(ivBackDrop, photoUrl, R.drawable.ic_photoholder, pbImageLoading);

        mStoryPlaces = new ArrayList<>();
        setCreateState();
    }

    private void setUpClickListeners() {
        tvCreateTrip.setOnClickListener((View v) -> {
            launchCreateStoryActivity();
        });
    }

    /* Listeners */
    @Override
    public void addPlace(SuggestionPlace suggestionPlace) {
        mStoryPlaces.add(suggestionPlace);
        setCreateState();
    }

    @Override
    public void removePlace(SuggestionPlace suggestionPlace) {
        mStoryPlaces.remove(suggestionPlace);
        setCreateState();
    }
//
    /* Navigation */
    private void launchCreateStoryActivity() {
        Intent createTrip = new Intent(this, CreateStoryActivity.class);
        createTrip.putExtra(Constants.PLACE_NAME_ARG, mDestination);
        createTrip.putExtra(Constants.PLACE_PHOTO_REF_ARG, getIntent().getStringExtra(DESTINATION_PHOTO_ARG));
        createTrip.putExtra(Constants.SUGGESTION_PLACES_LIST_ARG, Parcels.wrap(mStoryPlaces));
        startActivity(createTrip);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == PLACE_DETAIL_REQUEST) {
            // TODO: fix this, when a user leaves the detail view and comes back to this view,
            // the status of the star and the # of saved places should be updated accordingly.
            // required data: which fragment, the position, and the star state
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

    private void setCreateState() {
        tvSavedPlacesCount.setText("" + mStoryPlaces.size());
        if(mStoryPlaces.size() != 0) {
            tvCreateTrip.setTextColor(getResources().getColor(R.color.com_facebook_blue));
            tvCreateTrip.setEnabled(true);
        }else{
            tvCreateTrip.setTextColor(getResources().getColor(R.color.lightGray));
            tvCreateTrip.setEnabled(false);
        }

    }

}
