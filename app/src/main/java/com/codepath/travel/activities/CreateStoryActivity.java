package com.codepath.travel.activities;

import android.app.FragmentTransaction;
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

import com.codepath.travel.GoogleAsyncHttpClient;
import com.codepath.travel.R;
import com.codepath.travel.adapters.StoryPlaceArrayAdapter;
import com.codepath.travel.fragments.TripDatesFragment;
import com.codepath.travel.helper.OnStartDragListener;
import com.codepath.travel.helper.SimpleItemTouchHelperCallback;
import com.codepath.travel.models.StoryPlace;
import com.codepath.travel.models.SuggestionPlace;
import com.codepath.travel.models.Trip;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class CreateStoryActivity extends AppCompatActivity implements OnStartDragListener,
        TripDatesFragment.TripDatesListener {

    //Class variables
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private static final String TAG = CreateStoryActivity.class.getSimpleName();

    // intent arguments
    public static final String DESTINATION_ARGS = "destination";
    public static final String SUGGESTION_PLACES_LIST_ARGS = "suggestion_places_list";

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
    private ArrayList<SuggestionPlace> mSelectedSuggestionPlaces;
    private StoryPlaceArrayAdapter mAdapter;
    private Place mNewSelectedPlace;
    private String mDestination;
    private String mPhotoReference;
    private Trip mNewTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story);
        ButterKnife.bind(this);

        mDestination = getIntent().getStringExtra(DESTINATION_ARGS);
        //Get list of selected places from suggestions screen
        mSelectedSuggestionPlaces = getIntent().getParcelableArrayListExtra(SUGGESTION_PLACES_LIST_ARGS);
        toolbar.setTitle(String.format(toolbarTitle, mDestination));
        setSupportActionBar(toolbar);

        setUpRecyclerView();
        setUpClickListeners();
        setUpTrip();
    }

    private void setUpTrip() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.setACL(new ParseACL(currentUser));
        currentUser.saveInBackground();
        mNewTrip = new Trip(currentUser, mDestination);
        mNewTrip.saveInBackground(e -> {
            if (e == null) {
                setupTripDatesFragment();
            } else {
                Log.d(TAG, String.format("Failed to setup trip: %s", e.getMessage()));
            }
        });
    }

    private void setupTripDatesFragment() {
        // note that this is not using support v4 because the datepicker library doesn't support it
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.flContainer,
                TripDatesFragment.newInstance(mNewTrip.getStartDate(), mNewTrip.getEndDate()));
        ft.commit();
        mNewTrip.saveInBackground();
        addSuggestionPlacesToTrip();
    }

    private void addSuggestionPlacesToTrip() {
        for(SuggestionPlace suggestionPlace : mSelectedSuggestionPlaces) {
            StoryPlace storyPlace = new StoryPlace(mNewTrip, suggestionPlace);
            mStoryPlaces.add(storyPlace);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void setUpClickListeners() {
        btAddNewPlace.setOnClickListener((View view) -> {
            String placeOfInterest = etPlaceOfInterest.getText().toString();
            if(!placeOfInterest.isEmpty()) {
                etPlaceOfInterest.setText("");
                addSelectedPlaceInTrip(mNewSelectedPlace, mPhotoReference);
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
            mPhotoReference = "";
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

    private void addSelectedPlaceInTrip(Place place, String photoReference) {
        StoryPlace storyPlace = new StoryPlace(mNewTrip, place);
        storyPlace.setPhotoUrl(photoReference);
        mStoryPlaces.add(storyPlace);
        mAdapter.notifyDataSetChanged();
    }

    private void addPhotoReferenceByPlaceID(String placeID) {
        {   //To get photo reference for a place in create story view
            RequestParams params = new RequestParams();
            params.put("placeid", placeID);
            params.put("key", GoogleAsyncHttpClient.GOOGLE_PLACES_SEARCH_API_KEY);
            GoogleAsyncHttpClient.get(GoogleAsyncHttpClient.PLACE_DETAILS_URL, params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        mPhotoReference = response.getJSONObject("result").getJSONArray("photos").getJSONObject(0).getString("photo_reference");
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
                // Get the user's selected place from the edit text.
                mNewSelectedPlace = PlaceAutocomplete.getPlace(this, data);
                //Get photo reference for it by querying api
                addPhotoReferenceByPlaceID(mNewSelectedPlace.getId());
                Log.i(TAG, "Place Selected: " + mNewSelectedPlace.getName());
                etPlaceOfInterest.setText(mNewSelectedPlace.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e(TAG, "Error: Status = " + status.toString());
            } else if (resultCode == RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }
        }
    }

    /* Listeners */
    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void tripDatesOnSet(Calendar startDate, Calendar endDate) {
        Log.d(TAG, String.format("Dates set: %s - %s", startDate.toString(), endDate.toString()));
        mNewTrip.setStartDate(startDate.getTime());
        mNewTrip.setEndDate(endDate.getTime());
        mNewTrip.saveInBackground();
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