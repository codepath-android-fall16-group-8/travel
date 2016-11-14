package com.codepath.travel.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.travel.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ViewHolder class for story place collage items.
 */
public class CollageItemViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.ivCoverPhoto) ImageView ivCoverPhoto;
    @BindView(R.id.tvPlaceName) TextView tvPlaceName;

    public CollageItemViewHolder(View itemView) {
        super(itemView);
            ButterKnife.bind(this, itemView);
    }

    public ImageView getCoverPhoto() {
        return this.ivCoverPhoto;
    }

    public void setCoverPhoto(ImageView coverPhoto) {
        this.ivCoverPhoto = coverPhoto;
    }

    public TextView getPlaceName() {
        return this.tvPlaceName;
    }

    public void setPlaceName(TextView placeName) {
        this.tvPlaceName = placeName;
    }
}

