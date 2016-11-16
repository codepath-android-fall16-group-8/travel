package com.codepath.travel.activities;

import static com.codepath.travel.activities.StoryActivity.TRIP_ID_ARG;
import static com.codepath.travel.activities.StoryActivity.TRIP_TITLE_ARG;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.travel.R;
import com.codepath.travel.adapters.CollageAdapter;
import com.codepath.travel.helper.ItemClickSupport;
import com.codepath.travel.helper.SpacesItemDecoration;
import com.codepath.travel.models.StoryPlace;
import com.codepath.travel.models.Trip;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StoryCollageActivity extends AppCompatActivity {
    private static final int GRID_NUM_COLUMNS = 2;
    private static final int GRID_SPACE_SIZE = 5;

    // strings
    @BindString(R.string.toolbar_title_story_collage) String toolbarTitle;

    // views
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rvStoryPlaces) RecyclerView rvStoryPlaces;

    private String mTripID;
    private ArrayList<StoryPlace> mStoryPlaces;
    private CollageAdapter mCollageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_collage);
        ButterKnife.bind(this);

        toolbar.setTitle(String.format(toolbarTitle, getIntent().getStringExtra(TRIP_TITLE_ARG)));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTripID = getIntent().getStringExtra(TRIP_ID_ARG);

        mStoryPlaces = new ArrayList<>();
        Trip.getPlaces(mTripID, new FindCallback<StoryPlace>() {
            @Override
            public void done(List<StoryPlace> places, ParseException e) {
                if (e == null) {
                    mStoryPlaces.addAll(places);
                    Collections.sort(mStoryPlaces, (p1, p2) -> p2.getRating() - p1.getRating());
                    mCollageAdapter = new CollageAdapter(getApplicationContext(), mStoryPlaces);
                    setUpRecyclerView();
                } else {
                    Log.d("story fetch failed", e.toString());
                }
            }
        });
    }

    private void setUpRecyclerView() {
        this.rvStoryPlaces.setHasFixedSize(true);
        this.rvStoryPlaces.setAdapter(this.mCollageAdapter);

        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(GRID_NUM_COLUMNS,
                        StaggeredGridLayoutManager.VERTICAL);
        this.rvStoryPlaces.setLayoutManager(gridLayoutManager);
        ItemClickSupport.addTo(this.rvStoryPlaces).setOnItemClickListener(
                (recyclerView, position, v) -> seeMore(position)
        );
        SpacesItemDecoration decoration = new SpacesItemDecoration(GRID_SPACE_SIZE);
        this.rvStoryPlaces.addItemDecoration(decoration);
    }

    private void seeMore(int position) {
        StoryPlace storyPlace = this.mStoryPlaces.get(position);
        // Todo: get media items
        Toast.makeText(this, String.format("Todo: See media for place: %s", storyPlace.getName()),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_collage, menu);

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
        } else if (id == R.id.miShare) {
            // Todo: share dialog fragment? followers/following must be implemented first
            Toast.makeText(this, "Todo: share dialog fragment", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}
