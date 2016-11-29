package com.codepath.travel.adapters;

import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
    @BindView(R.id.tvTripTitle) TextView tvTripTitle;
    @BindView(R.id.tvTripDates) TextView tvTripDates;
    @BindView(R.id.cbShare) AppCompatCheckBox cbShare;
    @BindView(R.id.pbImageLoading) ProgressBar pbImageLoading;
    @BindView(R.id.rlTrip) RelativeLayout rlTrip;

    public TripViewHolder(View itemView) {
        super(itemView);
            ButterKnife.bind(this, itemView);
    }

    public ProgressBar getProgressBar() {
        return pbImageLoading;
    }

    public ImageView getProfilePhoto() {
        return this.ivProfilePhoto;
    }

    public TextView getTripTitle() {
        return this.tvTripTitle;
    }

    public TextView getTripDates() {
        return this.tvTripDates;
    }

    public RelativeLayout getRelativeLayout(){ return this.rlTrip; }
}

