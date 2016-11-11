package com.codepath.travel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.codepath.travel.Model.StoryPlace;
import com.codepath.travel.R;
import com.codepath.travel.adapters.StoryArrayAdapter;
import com.codepath.travel.adapters.StoryPlaceArrayAdapter;
import com.codepath.travel.helper.OnStartDragListener;
import com.codepath.travel.helper.SimpleItemTouchHelperCallback;

import java.util.ArrayList;

public class CreateStoryActivity extends AppCompatActivity implements OnStartDragListener {

    private RecyclerView rvStoryPlaces;
    private ArrayList<StoryPlace> mStoryPlaces;
    private StoryPlaceArrayAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getApplicationContext().getResources().getString(R.string.toolbar_title_create_story) + " X");
        setSupportActionBar(toolbar);

        rvStoryPlaces = (RecyclerView) findViewById(R.id.rvStoryPlaces);
        mStoryPlaces = StoryPlace.getTestStoryPlacesList(5);
        mAdapter = new StoryPlaceArrayAdapter(getApplicationContext(),this, mStoryPlaces);

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        rvStoryPlaces.setHasFixedSize(true);
        rvStoryPlaces.setAdapter(mAdapter);
        rvStoryPlaces.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvStoryPlaces);
    }

    public void onConfirm(View view) {
        Intent intent = new Intent(this, StoryActivity.class);
        intent.putExtra("storyPlaces", mStoryPlaces);
        startActivity(intent);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
