package com.codepath.travel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.parse.ParseException;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateStoryActivity extends AppCompatActivity implements OnStartDragListener {

    // intent arguments
    public static final String DESTINATION_ARGS = "destination";

    // Views
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

        mDestination = getIntent().getStringExtra(DESTINATION_ARGS);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getApplicationContext().getResources().getString(R.string.toolbar_title_create_story) + mDestination);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        setUpTrip();
        setUpRecyclerView();
        setUpClickListeners();
    }

    private void setUpTrip() {
        mNewTrip = new Trip();
        mNewTrip.setTitle(mDestination);
        mNewTrip.setUser(ParseUser.getCurrentUser());
    }

    private void setUpClickListeners() {
        btAddNewPlace.setOnClickListener((View view) -> {
            String placeOfInterest = etPlaceOfInterest.getText().toString();
            etPlaceOfInterest.setText("");
            StoryPlace storyPlace = new StoryPlace();
            storyPlace.setTrip(mNewTrip);
            storyPlace.setName(placeOfInterest);
            mStoryPlaces.add(storyPlace);
            mAdapter.notifyDataSetChanged();
        });

        btCreateMyTrip.setOnClickListener((View v) -> {
            StoryPlace.saveAllInBackground(mStoryPlaces, (ParseException e) -> {
                if (e == null) {
                    Toast.makeText(CreateStoryActivity.this, "Trip saved", Toast.LENGTH_LONG);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(CreateStoryActivity.this, "Error saving", Toast.LENGTH_SHORT);
                }
            });
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

    public void onConfirm(View view) {
        Intent intent = new Intent(this, StoryActivity.class);
        intent.putExtra("storyPlaces", Parcels.wrap(mStoryPlaces));
        startActivity(intent);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
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
        }

        return super.onOptionsItemSelected(item);
    }
}
