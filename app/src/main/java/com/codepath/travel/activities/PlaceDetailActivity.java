package com.codepath.travel.activities;

import android.content.Intent;
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

    // Strings
    @BindString(R.string.open) String open;
    @BindString(R.string.closed) String closed;
    @BindString(R.string.free) String free;

    // Views
    @BindView(R.id.cbAddPlace) AppCompatCheckBox cbAddPlace;
    @BindView(R.id.pbImageLoading) ProgressBar pbImageLoading;
    @BindView(R.id.ivBackDrop) ImageView ivBackDrop;
    @BindView(R.id.tvPhoneNumber) TextView tvPhoneNumber;
    @BindView(R.id.tvAddress) TextView tvAddress;
    @BindView(R.id.tvOpenNow) TextView tvOpenNow;
    @BindView(R.id.tvHours) TextView tvHours;
    @BindView(R.id.tvWebsite) TextView tvWebsite;
    @BindView(R.id.tvGoogleUrl) TextView tvGoogleUrl;
    @BindView(R.id.tvPriceLevel) TextView tvPriceLevel;
    @BindView(R.id.ratingBar) RatingBar ratingBar;
    @BindView(R.id.rvReviews) RecyclerView rvReviews;

    // variables
    boolean isStoryPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        initializeCommonViews();

        String placeId = getIntent().getStringExtra(PLACE_ID_ARG);
        String placeName = getIntent().getStringExtra(PLACE_NAME_ARG);
        setActionBarTitle(placeName);

        isStoryPlace = getIntent().getBooleanExtra(IS_STORY_PLACE_ARG, false);
        if (isStoryPlace) {
            cbAddPlace.setVisibility(View.GONE);
        } else {
            //cbAddPlace.setChecked(getIntent().getBooleanExtra(PLACE_ADDED_ARG, false));
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

        // address
        if (data.has(FORMATTED_ADDR_KEY)) {
            tvAddress.setText(data.getString(FORMATTED_ADDR_KEY));
        }

        // phone number
        if (data.has(INTL_PHONE_KEY)) {
            tvPhoneNumber.setText(data.getString(INTL_PHONE_KEY));
        } else if (data.has(FORMATTED_PHONE_KEY)) {
            tvPhoneNumber.setText(data.getString(FORMATTED_PHONE_KEY));
        }

        // open now, hours
        if (data.has(OPENING_HOURS_KEY)) {
            JSONObject openingHours = data.getJSONObject(OPENING_HOURS_KEY);
            boolean openNow = openingHours.getBoolean(OPEN_KEY);
            tvOpenNow.setText(openNow ? open : closed);
            JSONArray rawHours = openingHours.getJSONArray(HOURS_KEY);
            ArrayList<String> hoursList = new ArrayList<>();
            for (int i = 0; i < rawHours.length(); i++) {
                hoursList.add(rawHours.getString(i));
            }
            tvHours.setText(TextUtils.join("\n", hoursList));
        }

        // website
        if (data.has(WEBSITE_KEY)) {
            tvWebsite.setText(data.getString(WEBSITE_KEY));
        } else {
            tvWebsite.setVisibility(View.GONE);
        }

        // google url
        if (data.has(GOOGLE_URL_KEY)) {
            tvGoogleUrl.setText(data.getString(GOOGLE_URL_KEY));
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

        // rating
        if (data.has(RATING_KEY)) {
            ratingBar.setRating(data.getLong(RATING_KEY));
        }

        // reviews
        if (data.has(REVIEWS_KEY)) {
            ArrayList<Review> reviews =
                    Review.getReviewsFromJSONArray(data.getJSONArray(REVIEWS_KEY));
            ReviewsAdapter adapter = new ReviewsAdapter(PlaceDetailActivity.this, reviews);
            rvReviews.setAdapter(adapter);
            rvReviews.setLayoutManager(new LinearLayoutManager(PlaceDetailActivity.this));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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
