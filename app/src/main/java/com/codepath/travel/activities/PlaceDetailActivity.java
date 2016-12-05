package com.codepath.travel.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.travel.R;
import com.codepath.travel.adapters.ReviewsAdapter;
import com.codepath.travel.fragments.PlacesListFragment;
import com.codepath.travel.helper.ImageUtils;
import com.codepath.travel.models.Review;
import com.codepath.travel.net.GoogleAsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import cz.msebera.android.httpclient.Header;

import static com.codepath.travel.R.string.website;
import static com.codepath.travel.net.GooglePlaceConstants.FORMATTED_ADDR_KEY;
import static com.codepath.travel.net.GooglePlaceConstants.FORMATTED_PHONE_KEY;
import static com.codepath.travel.net.GooglePlaceConstants.GOOGLE_URL_KEY;
import static com.codepath.travel.net.GooglePlaceConstants.HOURS_KEY;
import static com.codepath.travel.net.GooglePlaceConstants.INTL_PHONE_KEY;
import static com.codepath.travel.net.GooglePlaceConstants.OPENING_HOURS_KEY;
import static com.codepath.travel.net.GooglePlaceConstants.OPEN_KEY;
import static com.codepath.travel.net.GooglePlaceConstants.PHOTOS_KEY;
import static com.codepath.travel.net.GooglePlaceConstants.PHOTO_REF_KEY;
import static com.codepath.travel.net.GooglePlaceConstants.PRICE_KEY;
import static com.codepath.travel.net.GooglePlaceConstants.RATING_KEY;
import static com.codepath.travel.net.GooglePlaceConstants.RESULT_KEY;
import static com.codepath.travel.net.GooglePlaceConstants.REVIEWS_KEY;
import static com.codepath.travel.net.GooglePlaceConstants.WEBSITE_KEY;

public class PlaceDetailActivity extends BaseActivity {

    // Intent args
    public static final String PLACE_ID_ARG = "place_id";
    public static final String PLACE_NAME_ARG = "place_name";
    public static final String IS_STORY_PLACE_ARG = "is_story_place";
    public static final String POSITION_ARG = "position";
    public static final String LAT_ARG = "latitude";
    public static final String LONG_ARG = "longitude";

    // Strings
    @BindString(R.string.open) String open;
    @BindString(R.string.closed) String closed;
    @BindString(R.string.free) String free;

    // Views
    @BindView(R.id.pbImageLoading) ProgressBar pbImageLoading;
    @BindView(R.id.ivBackDrop) ImageView ivBackDrop;
    @BindView(R.id.tvPlaceName) TextView tvPlaceName;
    @BindView(R.id.cbAddPlace) AppCompatCheckBox cbAddPlace;
    @BindView(R.id.tvRating) TextView tvRating;
    @BindView(R.id.rbRating) RatingBar rbRating;
    @BindView(R.id.tvPriceLevel) TextView tvPriceLevel;

    @BindView(R.id.tvDirections) TextView tvDirections;
    @BindView(R.id.tvCall) TextView tvCall;
    @BindView(R.id.tvWebsite) TextView tvWebsite;

    @BindView(R.id.tvAddress) TextView tvAddress;
    @BindView(R.id.tvPhoneNumber) TextView tvPhoneNumber;
    @BindView(R.id.tvOpenNow) TextView tvOpenNow;
//    @BindView(R.id.tvHours) TextView tvHours;
    @BindView(R.id.tvWebAddress) TextView tvWebAddress;
    @BindView(R.id.tvGoogleUrl) TextView tvGoogleUrl;

    @BindView(R.id.tvReviewsRating) TextView tvReviewsRating;
    @BindView(R.id.rbReviewsRating) RatingBar rbReviewsRating;
    @BindView(R.id.rvReviews) RecyclerView rvReviews;

    // variables
    private boolean isStoryPlace;
    private String placeName;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        initializeCommonViews();

        String placeId = getIntent().getStringExtra(PLACE_ID_ARG);
        placeName = getIntent().getStringExtra(PLACE_NAME_ARG);
        setActionBarTitle(placeName);
        tvPlaceName.setText(placeName);
        isStoryPlace = getIntent().getBooleanExtra(IS_STORY_PLACE_ARG, false);
        if (isStoryPlace) {
            // hide the saved checkbox if launched from the story activity
            cbAddPlace.setVisibility(View.GONE);
        } else {
            // set the saved checkbox state from the suggestion place model
            cbAddPlace.setChecked(getIntent().getBooleanExtra(PlacesListFragment.PLACE_ADDED_ARG, false));
        }

        GoogleAsyncHttpClient.getPlaceDetails(placeId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.has(RESULT_KEY)) {
                        setupViews(response.getJSONObject(RESULT_KEY));
                    } else {
                        Log.e("ERROR", String.format("Null place id from old story place data: %s", placeId));
                        pbImageLoading.setVisibility(View.GONE);
                    }
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

    private void setupViews(JSONObject data) throws JSONException {
        // place photo
        if (data.has(PHOTOS_KEY)) {
            String photoRef = data.getJSONArray(PHOTOS_KEY)
                    .getJSONObject(0).getString(PHOTO_REF_KEY);
            String photoUrl = GoogleAsyncHttpClient.getPlacePhotoUrl(photoRef);
            ImageUtils.loadImage(ivBackDrop, photoUrl, R.drawable.ic_photoholder,
            pbImageLoading);
        }

        // rating
        if (data.has(RATING_KEY)) {
            float rating = data.getLong(RATING_KEY);
            tvRating.setText(String.valueOf(rating));
            tvReviewsRating.setText(String.valueOf(rating));
            rbRating.setRating(rating);
            rbReviewsRating.setRating(rating);
        }

        // price level
        if (data.has(PRICE_KEY)) {
            int priceLevel = data.getInt(PRICE_KEY);
            if (priceLevel == 0) {
                tvPriceLevel.setText(free);
            } else {
                tvPriceLevel.setText(new String(new char[priceLevel]).replace("\0", "$"));
            }
        } else {
            tvPriceLevel.setVisibility(View.GONE);
        }

        // address
        if (data.has(FORMATTED_ADDR_KEY)) {
            String address = data.getString(FORMATTED_ADDR_KEY);
            tvAddress.setText(address);
            // open up maps with directions
            tvAddress.setOnClickListener(v -> onAddressClick(address));
            tvDirections.setOnClickListener(v -> onDirectionsClick(address));
        }

        // phone number
        if (data.has(INTL_PHONE_KEY)) {
            setupPhoneNumber(data.getString(INTL_PHONE_KEY));
        } else if (data.has(FORMATTED_PHONE_KEY)) {
            setupPhoneNumber(data.getString(FORMATTED_PHONE_KEY));
        }

        // website
        if (data.has(WEBSITE_KEY)) {
            // use website if it is available, otherwise default to google url below
            String website = data.getString(WEBSITE_KEY);
            tvWebAddress.setText(website);
            // open up web browser
            tvWebAddress.setOnClickListener(v -> onWebAddressClick(website));
            tvWebsite.setOnClickListener(v -> onWebAddressClick(website));
        } else {
            tvWebsite.setVisibility(View.GONE);
            // google url
            if (data.has(GOOGLE_URL_KEY)) {
                // google url is basically just map view with the location coordinates.
                tvGoogleUrl.setVisibility(View.VISIBLE);
                String googleUrl = data.getString(GOOGLE_URL_KEY);
                tvGoogleUrl.setText(website);
                // open up maps
                tvGoogleUrl.setOnClickListener(v -> onGoogleUrlClick(googleUrl));
                tvWebsite.setOnClickListener(v -> onGoogleUrlClick(googleUrl));
            }
        }

        // open now
        if (data.has(OPENING_HOURS_KEY)) {
            JSONObject openingHours = data.getJSONObject(OPENING_HOURS_KEY);
            boolean openNow = openingHours.getBoolean(OPEN_KEY);
            tvOpenNow.setText(openNow ? open : closed);

            // hours
            JSONArray rawHours = openingHours.getJSONArray(HOURS_KEY);
            ArrayList<String> hoursList = new ArrayList<>();
            for (int i = 0; i < rawHours.length(); i++) {
                hoursList.add(rawHours.getString(i));
            }
            String hours = TextUtils.join("\n", hoursList);
//            tvHours.setText(hours);
            tvOpenNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(PlaceDetailActivity.this, hours, Toast.LENGTH_LONG).show();
                }
            });
        }

        // reviews
        if (data.has(REVIEWS_KEY)) {
            ArrayList<Review> reviews =
                    Review.getReviewsFromJSONArray(data.getJSONArray(REVIEWS_KEY));
            ReviewsAdapter adapter = new ReviewsAdapter(PlaceDetailActivity.this, reviews);
            rvReviews.setAdapter(adapter);
            rvReviews.setLayoutManager(new LinearLayoutManager(PlaceDetailActivity.this));
        } else {
            rvReviews.setVisibility(View.GONE);
        }
    }

    private void setupPhoneNumber(String phone) {
        tvPhoneNumber.setVisibility(View.VISIBLE);
        tvCall.setVisibility(View.VISIBLE);
        tvPhoneNumber.setText(phone);

        // open phone intent
        tvPhoneNumber.setOnClickListener(v -> onPhoneClick(phone));
        tvCall.setOnClickListener(v -> onPhoneClick(phone));
    }

    /* Navigation */
    private void onDirectionsClick(String address) {
        String data = String.format("google.navigation:q=%s", address);
        launchMapsIntent(data);
    }

    private void onAddressClick(String address) {
        String data = String.format("geo:0,0?q=%s(%s)", address, placeName);
        launchMapsIntent(data);
    }

    private void onPhoneClick(String phoneNumber) {
        String uri = String.format("tel:%s", phoneNumber);
        Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
        phoneIntent.setData(Uri.parse(uri));
        if (phoneIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(phoneIntent);
        }
    }

    private void onWebAddressClick(String webAddress) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webAddress));
        if (browserIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(browserIntent);
        }
    }

    private void onGoogleUrlClick(String googleUrl) {
        Toast.makeText(PlaceDetailActivity.this, "GoogleURL: " + googleUrl, Toast.LENGTH_SHORT).show();
        launchMapsIntent(googleUrl);
    }

    private void launchMapsIntent(String data) {
        Intent mapsIntent = new Intent();
        mapsIntent.setAction(Intent.ACTION_VIEW);
        mapsIntent.setPackage("com.google.android.apps.maps");
        mapsIntent.setData(Uri.parse(data));
        if (mapsIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapsIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // if the activity was launched from the suggestion view, return the "saved" status
                // so we can reflect it on the related suggestion place card
                if (!isStoryPlace) {
                    Intent update = new Intent();
                    update.putExtra(PlacesListFragment.POSITION_ARG, getIntent().getIntExtra(POSITION_ARG, 0));
                    update.putExtra(PlacesListFragment.PLACE_ADDED_ARG, cbAddPlace.isChecked());
                    setResult(RESULT_OK, update);
                }
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
