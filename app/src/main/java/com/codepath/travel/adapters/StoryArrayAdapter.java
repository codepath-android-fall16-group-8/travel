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
 * Adapter for a trip's story places.
 */
public class StoryArrayAdapter extends RecyclerView.Adapter<StoryArrayAdapter.StoryViewHolder> implements ItemTouchHelperAdapter {

    private List<StoryPlace> mStoryPlaces;
    private final OnStartDragListener mDragStartListener;
    private Context mContext;
    private StoryPlaceListener listener;

    public interface StoryPlaceListener {
        void cameraOnClick(int position);
        void galleryOnClick(int position);
        void noteOnClick(int position);
        void reviewOnClick(int position);
        void storyPlaceMoved(int fromPosition, int toPosition);
        void storyPlaceDismissed(int position);
    }

    public StoryArrayAdapter(Context context, OnStartDragListener dragStartListener, List<StoryPlace> storyPlaces) {
        mStoryPlaces = storyPlaces;
        mDragStartListener = dragStartListener;
        mContext = context;
        listener = (StoryPlaceListener) context;
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

        holder.ivNote.setOnClickListener(v -> listener.noteOnClick(getRealPosition(storyPlace)));
        holder.ivReview.setOnClickListener(v -> listener.reviewOnClick(getRealPosition(storyPlace)));
        holder.ivCamera.setOnClickListener(v -> listener.cameraOnClick(getRealPosition(storyPlace)));
        holder.ivGallery.setOnClickListener(v -> listener.galleryOnClick(getRealPosition(storyPlace)));
    }

    @Override
    public void onItemDismiss(int position) {
        StoryPlace storyPlace = mStoryPlaces.get(position);
        Log.d("onItemDismiss", String.format("pos: %d, storyPlace: %s", position, storyPlace.getName()));
        listener.storyPlaceDismissed(position);
        mStoryPlaces.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        StoryPlace storyPlace = mStoryPlaces.get(fromPosition);
        Log.d("onItemMove", String.format("pos: %d - %d, storyPlace: %s", fromPosition, toPosition, storyPlace.getName()));
        listener.storyPlaceMoved(fromPosition, toPosition);
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

    // Needed in order to get the correct position of items
    // on drag/dismiss doesn't update the "position" of the recycler view
    private int getRealPosition(StoryPlace storyPlace){
        return mStoryPlaces.indexOf(storyPlace);
    }

    public class StoryViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        // views
        @BindView(R.id.ivPlacePhoto) ImageView ivPlacePhoto;
        @BindView(R.id.tvPlaceName) TextView tvPlaceName;
        @BindView(R.id.ivReview) ImageView ivReview;
        @BindView(R.id.ivNote) ImageView ivNote;
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
