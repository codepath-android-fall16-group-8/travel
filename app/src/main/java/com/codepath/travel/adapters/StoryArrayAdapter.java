package com.codepath.travel.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.travel.R;
import com.codepath.travel.activities.StoryActivity;
import com.codepath.travel.helper.ItemTouchHelperAdapter;
import com.codepath.travel.helper.ItemTouchHelperViewHolder;
import com.codepath.travel.helper.OnStartDragListener;
import com.codepath.travel.models.Media;
import com.codepath.travel.models.ParseModelConstants;
import com.codepath.travel.models.StoryPlace;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by aditikakadebansal on 11/9/16.
 */
public class StoryArrayAdapter extends RecyclerView.Adapter<StoryArrayAdapter.StoryViewHolder> implements ItemTouchHelperAdapter {

    private List<StoryPlace> mStoryPlaces;
    private final OnStartDragListener mDragStartListener;
    public Context mContext;

    public StoryArrayAdapter(Context context, OnStartDragListener dragStartListener, List<StoryPlace> storyPlaces) {
        mStoryPlaces = storyPlaces;
        mDragStartListener = dragStartListener;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public StoryArrayAdapter.StoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new StoryViewHolder(
                inflater.inflate(
                        R.layout.item_storyplace,
                        parent,
                        false
                )
        );

    }

    @Override
    public void onBindViewHolder(final StoryArrayAdapter.StoryViewHolder holder, int position) {
        StoryPlace storyPlace = mStoryPlaces.get(position);
        holder.populate(storyPlace);
        // Start a drag whenever the handle view it touched
        holder.ivPlacePhoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });

        // ideal way should be do have an interface and listener for this
        // doing this for a quick check to see if it works
        holder.ivCamera.setOnClickListener((View view) -> {
            ((StoryActivity)mContext).launchCameraActivity(position);
        });

        holder.ivGallery.setOnClickListener((View v) -> {
            ((StoryActivity)mContext).launchGalleryActivity(position);
        });
    }

    @Override
    public void onItemDismiss(int position) {
        mStoryPlaces.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mStoryPlaces, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return mStoryPlaces.size();
    }



    public class StoryViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        // views
        @BindView(R.id.ivPlacePhoto) ImageView ivPlacePhoto;
        @BindView(R.id.tvPlaceName) TextView tvPlaceName;
        @BindView(R.id.ivCamera) ImageView ivCamera;
        @BindView(R.id.ivGallery) ImageView ivGallery;
        @BindView(R.id.rvMediaHolder) RecyclerView rvMediaItems;

        // variables
        private ArrayList<Media> mPlaceMediaItems;
        private MediaItemAdapter mMediaItemAdapter;

        public StoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mPlaceMediaItems = new ArrayList<>();
            mMediaItemAdapter =
                new MediaItemAdapter(StoryArrayAdapter.this.mContext, mPlaceMediaItems);
            rvMediaItems.setAdapter(mMediaItemAdapter);
            rvMediaItems.setLayoutManager(
                new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        }

        public void populate(StoryPlace storyPlace) {
            ivPlacePhoto.setImageResource(0);
            Glide.with(mContext)
                    .load(storyPlace.getCoverPicUrl())
                    .into(ivPlacePhoto);
            tvPlaceName.setText(storyPlace.getName());
            ParseQuery<Media> mediaObjectsQuery = ParseQuery.getQuery("Media");
            mediaObjectsQuery.whereEqualTo(ParseModelConstants.STORY_PLACE_KEY, storyPlace);
            mediaObjectsQuery.findInBackground((List<Media> mediaObjects, ParseException e) -> {
                if (e == null) {
                    mPlaceMediaItems.clear();
                    mPlaceMediaItems.addAll(mediaObjects);
                    mMediaItemAdapter.notifyDataSetChanged();
                } else {
                    Log.d("Media fetch failed", e.toString());
                }
            });
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

}
