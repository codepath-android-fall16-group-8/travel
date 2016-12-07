package com.codepath.travel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.codepath.travel.Constants;
import com.codepath.travel.R;
import com.codepath.travel.adapters.SwipeStoryPlaceAdapter;
import com.codepath.travel.fragments.dialog.ConfirmDeleteTripDialogFragment;
import com.codepath.travel.fragments.dialog.DatePickerFragment;
import com.codepath.travel.fragments.dialog.DateRangePickerFragment;
import com.codepath.travel.helper.DateUtils;
import com.codepath.travel.listeners.DatePickerListener;
import com.codepath.travel.listeners.DateRangePickerListener;
import com.codepath.travel.models.parse.StoryPlace;
import com.codepath.travel.models.parse.Trip;
import com.daimajia.swipe.util.Attributes;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.codepath.travel.Constants.PLACE_ID_ARG;
import static com.codepath.travel.Constants.PLACE_NAME_ARG;
import static com.codepath.travel.activities.MediaCollageActivity.IS_OWNER_ARG;
import static com.codepath.travel.activities.MediaCollageActivity.STORY_PLACE_CHECKIN_ARG;
import static com.codepath.travel.activities.MediaCollageActivity.STORY_PLACE_COVER_ARG;
import static com.codepath.travel.activities.MediaCollageActivity.STORY_PLACE_ID_ARG;
import static com.codepath.travel.activities.MediaCollageActivity.STORY_PLACE_NAME_ARG;
import static com.codepath.travel.activities.MediaCollageActivity.STORY_PLACE_RATING_ARG;
import static com.codepath.travel.activities.MediaCollageActivity.USER_ID_ARG;
import static com.codepath.travel.activities.PlaceDetailActivity.IS_STORY_PLACE_ARG;
import static com.codepath.travel.activities.PlaceDetailActivity.LAT_LNG_ARG;
import static com.codepath.travel.helper.DateUtils.formatDateRange;

public class StoryActivity extends AppCompatActivity implements
        SwipeStoryPlaceAdapter.StoryPlaceListener,
        ConfirmDeleteTripDialogFragment.DeleteTripListener,
        DateRangePickerListener,
        DatePickerListener {

    private static final String TAG = StoryActivity.class.getSimpleName();

    // views
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tvTripDates) TextView tvTripDates;
    @BindView(R.id.toggleBtnShare) ToggleButton toggleBtnShare;
    @BindView(R.id.tvShare) TextView tvShare;
    @BindView(R.id.rvStoryPlaces) RecyclerView rvStoryPlaces;

    // member variables
    private ArrayList<StoryPlace> mStoryPlaces;
    private SwipeStoryPlaceAdapter mAdapter;

    private String mTripID;
    private Trip mTrip;
    private String mTripTitle;
    private int mCheckinIndex;

    // flags for story place view state
    private boolean isOwner;
    private int datesRelation; // PAST, NOW, or FUTURE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        setupWindowAnimations();

        ButterKnife.bind(this);

        mTripTitle = getIntent().getStringExtra(Constants.TRIP_TITLE_ARG);
        toolbar.setTitle(mTripTitle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        isOwner = getIntent().getBooleanExtra(Constants.IS_OWNER_ARG, false);

        mTripID = getIntent().getStringExtra(Constants.TRIP_ID_ARG);
        Trip.getTripForObjectId(mTripID, (trip, e) -> {
            if (e == null) {
                mTrip = trip;
                Date start = trip.getStartDate();
                Date end = trip.getEndDate();
                datesRelation = DateUtils.todayInRange(start, end);
                if (start != null && end !=null) {
                    tvTripDates.setText(DateUtils.formatDateRange(this, start, end));
                }
                tvTripDates.setOnClickListener(
                        v -> launchDateRangePickerDialog(mTrip.getStartDate(), mTrip.getEndDate()));
                setUpRecyclerView();
                getPlacesInTrip();
                setupSharedCheckbox();
            } else {
                Log.d(TAG, String.format("Failed to get trip for id %s", mTrip));
            }

        });
    }

    // should match BaseActivity's setupWindowAnimations() until this class is updated to extend BaseActivity.
    protected void setupWindowAnimations() {
        Fade fadeOut = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.activity_fade_out);
        Slide slideRight = (Slide) TransitionInflater.from(this).inflateTransition(R.transition.activity_slide_right);
        getWindow().setEnterTransition(slideRight); // enter: slide in from right side when being opened
        getWindow().setExitTransition(fadeOut); // exit: fadeOut when opening another activity
        // re-enter: should automatically reverse (fadeIn) when returning from another activity
        // return: should automatically reverse (slideLeft) when closing
        getWindow().setAllowEnterTransitionOverlap(false); // wait for calling activity's exit transition to be done
        getWindow().setAllowReturnTransitionOverlap(false); // wait for called activity's return transition to be done?
    }

    private void setupSharedCheckbox() {
        // only display for logged in user
        if (isOwner) {
            tvShare.setVisibility(View.VISIBLE);
            toggleBtnShare.setVisibility(View.VISIBLE);
            toggleBtnShare.setChecked(mTrip.isShared());
            toggleBtnShare.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Log.d(TAG, String.format("Share: %s", isChecked));
                mTrip.setShared(isChecked);
                if(isChecked) {
                    tvShare.setText(R.string.unshare);
                } else {
                    tvShare.setText(R.string.share);
                }
                mTrip.saveInBackground();
            });
            if(mTrip.isShared()) {
                tvShare.setText(R.string.unshare);
            } else {
                tvShare.setText(R.string.share);
            }
        } else {
            tvShare.setVisibility(View.GONE);
            toggleBtnShare.setVisibility(View.GONE);
        }
    }

    private void setUpRecyclerView() {
        mStoryPlaces = new ArrayList<>();
        mAdapter = new SwipeStoryPlaceAdapter(this, mStoryPlaces, isOwner, datesRelation);
        mAdapter.setMode(Attributes.Mode.Single);
        rvStoryPlaces.setAdapter(mAdapter);
        rvStoryPlaces.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getPlacesInTrip() {
        Trip.getPlacesForTripId(mTripID, (places, se) -> {
            if (se == null) {
                mStoryPlaces.addAll(places);
                mAdapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, String.format("Failed getPlacesInTrip: %s", se.getMessage()));
            }
        });
    }

    /* Navigation */
    private void launchMediaCollageActivity(StoryPlace storyPlace) {
        Intent collage = new Intent(StoryActivity.this, MediaCollageActivity.class);
        collage.putExtra(STORY_PLACE_ID_ARG, storyPlace.getObjectId());
        collage.putExtra(STORY_PLACE_NAME_ARG, storyPlace.getName());
        collage.putExtra(STORY_PLACE_COVER_ARG, storyPlace.getPhotoUrl());
        collage.putExtra(STORY_PLACE_CHECKIN_ARG, DateUtils.formatDate(this, storyPlace.getCheckinTime()));
        collage.putExtra(STORY_PLACE_RATING_ARG, storyPlace.getRating());
        collage.putExtra(USER_ID_ARG, mTrip.getUser().getObjectId());
        collage.putExtra(IS_OWNER_ARG, isOwner);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(StoryActivity.this);
        startActivity(collage, options.toBundle());
    }

    private void launchStoryMapActivity() {
        Intent intent = new Intent(StoryActivity.this, StoryMapViewActivity.class);
        intent.putExtra(StoryMapViewActivity.TRIP_ID_ARG, mTripID);
        intent.putExtra(StoryMapViewActivity.TRIP_TITLE_ARG, mTripTitle);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(StoryActivity.this);
        startActivity(intent, options.toBundle());
    }

    private void launchPlaceDetailActivity(StoryPlace storyPlace) {
        Intent placeDetail = new Intent(this, PlaceDetailActivity.class);
        placeDetail.putExtra(PLACE_ID_ARG, storyPlace.getPlaceId());
        placeDetail.putExtra(PLACE_NAME_ARG, storyPlace.getName());
        placeDetail.putExtra(IS_STORY_PLACE_ARG, true);
        placeDetail.putExtra(LAT_LNG_ARG, new LatLng(storyPlace.getLatitude(), storyPlace.getLongitude()));

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(StoryActivity.this);
        startActivity(placeDetail, options.toBundle());
    }

    private void launchDatePickerDialog(Date date, Date minDate, Date maxDate) {
        FragmentManager fm = getSupportFragmentManager();
        DatePickerFragment dpf = DatePickerFragment.newInstance(date, minDate, maxDate);
        dpf.show(fm, "DatePickerDialog");
    }

    private void launchDateRangePickerDialog(Date startDate, Date endDate) {
        FragmentManager fm = getSupportFragmentManager();
        DateRangePickerFragment drpf = DateRangePickerFragment.newInstance(startDate, endDate);
        drpf.show(fm, "DateRangePickerDialog");
    }

    private void showConfirmDeleteDialog() {
        ConfirmDeleteTripDialogFragment fragment =
                ConfirmDeleteTripDialogFragment.newInstance();
        fragment.show(getSupportFragmentManager(), "confirmDeleteTripDialogFragment");
    }

    /* Listeners */
    @Override
    public void onStoryPlaceDelete(int position) {
        Log.d(TAG, String.format("delete story at pos: %d", position));
        StoryPlace storyPlace = mStoryPlaces.get(position);
        storyPlace.deleteWithMedia();
        // TODO: show confirm dialog
    }

    @Override
    public void onStoryPlaceInfo(int position) {
        StoryPlace storyPlace = mStoryPlaces.get(position);
        Log.d(TAG, String.format("show place at pos: %d, %s, %s", position, storyPlace.getName(), storyPlace.getPlaceId()));
        launchPlaceDetailActivity(storyPlace);
    }

    @Override
    public void mediaOnClick(int position) {
        launchMediaCollageActivity(mStoryPlaces.get(position));
    }

    @Override
    public void onDeleteTrip() {
        Trip.deleteTripForId(mTripID);
        setResult(RESULT_OK);
        finishAfterTransition();
    }

    @Override
    public void checkinOnClick(int position, Date checkinDate) {
        mCheckinIndex = position;
        launchDatePickerDialog(checkinDate, mTrip.getStartDate(), mTrip.getEndDate());
    }

    @Override
    public void onDateSet(Calendar date) {
        StoryPlace storyPlace = mStoryPlaces.get(mCheckinIndex);
        storyPlace.setCheckinTime(date.getTime());
        storyPlace.saveInBackground(e -> {
            if (e == null) {
                mAdapter.notifyItemChanged(mCheckinIndex);
            }
        });
    }

    @Override
    public void onDateRangeSet(Calendar startDate, Calendar endDate) {
        String formattedDateString = formatDateRange(this, startDate.getTime(), endDate.getTime());
        tvTripDates.setText(formattedDateString);
        mTrip.setStartDate(startDate.getTime());
        mTrip.setEndDate(endDate.getTime());
        mTrip.saveInBackground();
    }

    /* Toolbar */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_story, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!isOwner) {
            // don't show delete menu option unless this trip belongs to the logged in user
            menu.removeItem(R.id.miDelete);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            setResult(RESULT_CANCELED); // result_ok is being used for delete trip
            finishAfterTransition();
            return true;
        } else if (id == R.id.miMap) {
            launchStoryMapActivity();
        } else if (id == R.id.miDelete) {
            showConfirmDeleteDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
