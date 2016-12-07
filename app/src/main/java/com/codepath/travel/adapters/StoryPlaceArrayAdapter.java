package com.codepath.travel.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.travel.R;
import com.codepath.travel.helper.ImageUtils;
import com.codepath.travel.helper.ItemTouchHelperViewHolder;
import com.codepath.travel.models.parse.StoryPlace;
import com.codepath.travel.net.GoogleAsyncHttpClient;

import java.util.Date;
import java.util.List;

/**
 * Adapter for story places being added to a new trip.
 */
public class StoryPlaceArrayAdapter extends RecyclerView.Adapter<StoryPlaceArrayAdapter.StoryPlaceViewHolder> {

    private List<StoryPlace> mStoryPlaces;
    public Context mContext;

    public StoryPlaceArrayAdapter(Context context, List<StoryPlace> storyPlaces) {
        mStoryPlaces = storyPlaces;
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
        holder.ivPlaceDelete.setOnClickListener(v -> {
            mStoryPlaces.remove(destination);
            notifyDataSetChanged();
        });
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
        public ImageView ivPlaceDelete;
        public TextView tvPlaceName;

        public StoryPlaceViewHolder(View itemView) {
            super(itemView);
            ivPlacePhoto = (ImageView) itemView.findViewById(R.id.ivPlacePhoto);
            tvPlaceName = (TextView) itemView.findViewById(R.id.tvPlaceName);
            ivPlaceDelete = (ImageView) itemView.findViewById(R.id.ivDelete);
        }

        public void populate(StoryPlace storyPlace) {
            ImageUtils.loadImage(
                ivPlacePhoto,
                GoogleAsyncHttpClient.getPlacePhotoUrl(storyPlace.getPhotoUrl()));
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
