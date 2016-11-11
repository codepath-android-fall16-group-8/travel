package com.codepath.travel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.codepath.travel.Model.StoryPlace;
import com.codepath.travel.R;
import com.codepath.travel.adapters.StoryArrayAdapter;
import com.codepath.travel.helper.OnStartDragListener;
import com.codepath.travel.helper.SimpleItemTouchHelperCallback;

import java.util.ArrayList;

public class StoryActivity extends AppCompatActivity implements OnStartDragListener {

    private RecyclerView rvStoryPlaces;
    private ArrayList<StoryPlace> mStoryPlaces;
    private StoryArrayAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getApplicationContext().getResources().getString(R.string.toolbar_title_story) + " X");
        setSupportActionBar(toolbar);

        rvStoryPlaces = (RecyclerView) findViewById(R.id.rvStoryPlaces);

        Intent intent = getIntent();
        mStoryPlaces = intent.getParcelableArrayListExtra("storyPlaces");
        mAdapter = new StoryArrayAdapter(getApplicationContext(),this, mStoryPlaces);
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

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

}
