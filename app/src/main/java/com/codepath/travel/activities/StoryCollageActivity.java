package com.codepath.travel.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.travel.Model.StoryPlace;
import com.codepath.travel.R;
import com.codepath.travel.adapters.CollageAdapter;
import com.codepath.travel.helper.ItemClickSupport;
import com.codepath.travel.helper.SpacesItemDecoration;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;

public class StoryCollageActivity extends AppCompatActivity {
    private static final int GRID_NUM_COLUMNS = 2;
    private static final int GRID_SPACE_SIZE = 5;

    private RecyclerView rvStoryPlaces;
    private ArrayList<StoryPlace> mStoryPlaces;
    private CollageAdapter mCollageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_collage);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getApplicationContext().getResources().getString(R.string.toolbar_title_story_collage) + " X");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.rvStoryPlaces = (RecyclerView) findViewById(R.id.rvStoryPlaces);
        Intent intent = getIntent();
        this.mStoryPlaces = Parcels.unwrap(intent.getParcelableExtra("storyPlaces"));
        Collections.sort(this.mStoryPlaces, (p1, p2) -> p2.getRating() - p1.getRating());
        this.mCollageAdapter = new CollageAdapter(getApplicationContext(), this.mStoryPlaces);
        setUpRecyclerView();
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
