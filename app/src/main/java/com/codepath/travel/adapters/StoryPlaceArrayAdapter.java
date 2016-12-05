package com.codepath.travel.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.travel.net.GoogleAsyncHttpClient;
import com.codepath.travel.R;
import com.codepath.travel.helper.ImageUtils;
import com.codepath.travel.helper.ItemTouchHelperAdapter;
import com.codepath.travel.helper.ItemTouchHelperViewHolder;
import com.codepath.travel.helper.OnStartDragListener;
import com.codepath.travel.models.parse.StoryPlace;

import java.util.Collections;
import java.util.List;

/**
 * Adapter for story places being added to a new trip.
 */
public class StoryPlaceArrayAdapter extends RecyclerView.Adapter<StoryPlaceArrayAdapter.StoryPlaceViewHolder> implements ItemTouchHelperAdapter {

    private List<StoryPlace> mStoryPlaces;
    private final OnStartDragListener mDragStartListener;
    public Context mContext;

    public StoryPlaceArrayAdapter(Context context, OnStartDragListener dragStartListener, List<StoryPlace> storyPlaces) {
        mStoryPlaces = storyPlaces;
        mDragStartListener = dragStartListener;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public StoryPlaceArrayAdapter.StoryPlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new StoryPlaceViewHolder(
                        inflater.inflate(
                                R.layout.item_place,
                                parent,
                                false
                        )
                );

    }

    @Override
    public void onBindViewHolder(final StoryPlaceArrayAdapter.StoryPlaceViewHolder holder, int position) {
        StoryPlace destination = mStoryPlaces.get(position);
        holder.populate(destination);
        // Start a drag whenever the handle view it touched
        holder.ivPlacePhoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    if (mDragStartListener != null) {
                        mDragStartListener.onStartDrag(holder);
                    }
                }
                return false;
            }
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



    public class StoryPlaceViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        public ImageView ivPlacePhoto;
        public TextView tvPlaceName;

        public StoryPlaceViewHolder(View itemView) {
            super(itemView);
            ivPlacePhoto = (ImageView) itemView.findViewById(R.id.ivPlacePhoto);
            tvPlaceName = (TextView) itemView.findViewById(R.id.tvPlaceName);
        }

        public void populate(StoryPlace storyPlace) {
            ivPlacePhoto.setImageResource(0);
            ImageUtils.loadImage(
                ivPlacePhoto,
                GoogleAsyncHttpClient.getPlacePhotoUrl(storyPlace.getPhotoUrl()),
                R.drawable.ic_photoholder,
                null
            );
            tvPlaceName.setText(storyPlace.getName());
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
