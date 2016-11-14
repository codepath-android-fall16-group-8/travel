package com.codepath.travel.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.travel.R;
import com.codepath.travel.models.StoryPlace;

import java.util.List;

/**
 * Adapter for story place collage items.
 */
public class CollageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<StoryPlace> mStoryPlaces;
    private Context mContext;

    private final int DEFAULT = 0;

    public CollageAdapter(Context context, List<StoryPlace> storyPlaces) {
        this.mStoryPlaces = storyPlaces;
        this.mContext = context;
    }

    private Context getContext() {
        return this.mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            default:
                View defaultView = inflater.inflate(R.layout.item_collage, parent, false);
                viewHolder = new CollageItemViewHolder(defaultView);
                break;
        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            default:
                CollageItemViewHolder vh = (CollageItemViewHolder) viewHolder;
                configureViewHolder(vh, position);
                break;
        }
    }

    private void configureViewHolder(CollageItemViewHolder viewHolder, int position) {
        StoryPlace storyPlace = this.mStoryPlaces.get(position);

        ImageView ivCoverPhoto = viewHolder.getCoverPhoto();
        ivCoverPhoto.setImageResource(0);
        String coverUrl = storyPlace.getCoverPicUrl();
        if (!TextUtils.isEmpty(coverUrl)) {
            Glide.with(getContext()).load(coverUrl)
                    .placeholder(android.R.drawable.ic_menu_slideshow)
                    .centerCrop()
                    .into(ivCoverPhoto);
        }
        TextView tvPlaceName = viewHolder.getPlaceName();
        tvPlaceName.setText(storyPlace.getName());
    }

    @Override
    public int getItemCount() {
        return this.mStoryPlaces.size();
    }

    @Override
    public int getItemViewType(int position) {
        return DEFAULT;
    }
}

