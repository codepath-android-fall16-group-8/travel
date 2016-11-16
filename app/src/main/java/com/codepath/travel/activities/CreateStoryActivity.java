package com.codepath.travel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.travel.R;
import com.codepath.travel.adapters.StoryPlaceArrayAdapter;
import com.codepath.travel.helper.OnStartDragListener;
import com.codepath.travel.helper.SimpleItemTouchHelperCallback;
import com.codepath.travel.models.StoryPlace;
import com.codepath.travel.models.Trip;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateStoryActivity extends AppCompatActivity implements OnStartDragListener {
    //Class variables
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private static final String TAG = CreateStoryActivity.class.getSimpleName();

    // intent arguments
    public static final String DESTINATION_ARGS = "destination";

    // strings
    @BindString(R.string.toolbar_title_create_story) String toolbarTitle;

    // Views
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rvStoryPlaces) RecyclerView rvStoryPlaces;
    @BindView(R.id.btAddNewPlace) Button btAddNewPlace;
    @BindView(R.id.btCreateTrip) Button btCreateMyTrip;
    @BindView(R.id.etPlacesOfInterest) EditText etPlaceOfInterest;

    // Listeners
    private ItemTouchHelper mItemTouchHelper;

    // member variables
    private ArrayList<StoryPlace> mStoryPlaces;
    private StoryPlaceArrayAdapter mAdapter;
    private String mDestination;
    private Trip mNewTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story);
        ButterKnife.bind(this);

        mDestination = getIntent().getStringExtra(DESTINATION_ARGS);
        toolbar.setTitle(String.format(toolbarTitle, mDestination));
        setSupportActionBar(toolbar);

        setUpTrip();
        setUpRecyclerView();
        setUpClickListeners();
    }

    private void setUpTrip() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.setACL(new ParseACL(currentUser));
        currentUser.saveInBackground();
        mNewTrip = new Trip(currentUser, mDestination);
        mNewTrip.saveInBackground();
    }

    private void setUpClickListeners() {
        btAddNewPlace.setOnClickListener((View view) -> {
            String placeOfInterest = etPlaceOfInterest.getText().toString();
            if(!placeOfInterest.isEmpty()) {
                etPlaceOfInterest.setText("");
                StoryPlace storyPlace = new StoryPlace(mNewTrip, placeOfInterest);
                mStoryPlaces.add(storyPlace);
                mAdapter.notifyDataSetChanged();
            }else {
                Toast.makeText(CreateStoryActivity.this, "Please add a place of interest", Toast.LENGTH_LONG).show();
            }
        });

        btCreateMyTrip.setOnClickListener((View v) -> {
            StoryPlace.saveAllInBackground(mStoryPlaces, (ParseException e) -> {
                if (e == null) {
                    Toast.makeText(CreateStoryActivity.this, "Trip saved", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Log.d("createStory", String.format("Failed: %s", e.getMessage()));
                }
            });
        });

        etPlaceOfInterest.setOnClickListener((View view) -> {
            openAutocompleteActivity();
        });
    }

    private void setUpRecyclerView() {
        mStoryPlaces = new ArrayList<>();
        mAdapter = new StoryPlaceArrayAdapter(getApplicationContext(),this, mStoryPlaces);
        rvStoryPlaces.setHasFixedSize(true);
        rvStoryPlaces.setAdapter(mAdapter);
        rvStoryPlaces.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvStoryPlaces);
    }

    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Log.e(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place Selected: " + place.getName());
                etPlaceOfInterest.setText(place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e(TAG, "Error: Status = " + status.toString());
            } else if (resultCode == RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    /* Toolbar */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_story, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.miDelete) {
            mNewTrip.deleteInBackground();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
