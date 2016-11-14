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
import android.widget.Toast;

import com.codepath.travel.R;
import com.codepath.travel.adapters.StoryArrayAdapter;
import com.codepath.travel.helper.OnStartDragListener;
import com.codepath.travel.helper.SimpleItemTouchHelperCallback;
import com.codepath.travel.models.ParseModelConstants;
import com.codepath.travel.models.StoryPlace;
import com.codepath.travel.models.Trip;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoryActivity extends AppCompatActivity implements OnStartDragListener {

    // activity intent args
    public static final String TRIP_ID_ARG = "trip_id";

    // views
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rvStoryPlaces) RecyclerView rvStoryPlaces;

    // member variables
    private ArrayList<StoryPlace> mStoryPlaces;
    private StoryArrayAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;

    private String mTripID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTripID = getIntent().getStringExtra(TRIP_ID_ARG);
        setUpRecyclerView();
        getPlacesInTrip();
    }

    private void getPlacesInTrip() {
        ParseQuery<Trip> tripQuery = ParseQuery.getQuery("Trip");
        tripQuery.whereEqualTo("objectId", mTripID);
        tripQuery.findInBackground((List<Trip> trip, ParseException e) -> {
            if (e == null) {
                ParseQuery<StoryPlace> storyQuery = ParseQuery.getQuery("StoryPlace");
                storyQuery.whereEqualTo(ParseModelConstants.TRIP_KEY, trip.get(0));
                toolbar.setTitle(
                    getApplicationContext().
                    getResources().
                    getString(R.string.toolbar_title_story) + " " + trip.get(0).getTitle());
                storyQuery.findInBackground((List<StoryPlace> places, ParseException se) -> {
                    if (e == null) {
                        mStoryPlaces.addAll(places);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("story fetch failed", e.toString());
                    }
                });
            } else {
                Log.d("trip query error", e.toString());
            }
        });
    }

    private void setUpRecyclerView() {
        mStoryPlaces = new ArrayList<>();
        mAdapter = new StoryArrayAdapter(getApplicationContext(),this, mStoryPlaces);
        rvStoryPlaces.setHasFixedSize(true);
        rvStoryPlaces.setAdapter(mAdapter);
        rvStoryPlaces.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvStoryPlaces);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_story, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.miMap) {
            // TODO: bring up a map view (fragment?)
            Toast.makeText(this, "TODO: show map!", Toast.LENGTH_SHORT);
        } else if (id == R.id.miCollage) {
            launchStoryCollageActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void launchStoryCollageActivity() {
        Intent intent = new Intent(StoryActivity.this, StoryCollageActivity.class);
        intent.putExtra("storyPlaces", Parcels.wrap(mStoryPlaces));
        startActivity(intent);
    }

}
