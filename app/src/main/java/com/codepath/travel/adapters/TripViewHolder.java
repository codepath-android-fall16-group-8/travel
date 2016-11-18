package com.codepath.travel.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.travel.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ViewHolder class for trip items.
 */
public class TripViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.ivProfilePhoto) ImageView ivProfilePhoto;
    @BindView(R.id.rlBackground) RelativeLayout rlBackground;
    @BindView(R.id.tvTripTitle) TextView tvTripTitle;
    @BindView(R.id.tvTripDates) TextView tvTripDates;
    @BindView(R.id.btnShare) ImageButton btnShare;

    public TripViewHolder(View itemView) {
        super(itemView);
            ButterKnife.bind(this, itemView);
    }

    public ImageView getProfilePhoto() {
        return this.ivProfilePhoto;
    }

    public void setProfilePhoto(ImageView profilePhoto) {
        this.ivProfilePhoto = profilePhoto;
    }

    public RelativeLayout getCoverPhoto() {
        return this.rlBackground;
    }

    public void setCoverPhoto(RelativeLayout coverPhoto) {
        this.rlBackground = coverPhoto;
    }

    public TextView getTripTitle() {
        return this.tvTripTitle;
    }

    public void setTripTitle(TextView tripTitle) {
        this.tvTripTitle = tripTitle;
    }

    public TextView getTripDates() {
        return this.tvTripDates;
    }

    public void setTripDates(TextView tripDates) {
        this.tvTripDates = tripDates;
    }

    public ImageButton getShareButton() {
        return this.btnShare;
    }

    public void setShareButton(ImageButton button) {
        this.btnShare = button;
    }
}

