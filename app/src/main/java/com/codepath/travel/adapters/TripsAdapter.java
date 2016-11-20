package com.codepath.travel.adapters;

import static com.codepath.travel.models.User.getProfilePicUrl;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.travel.R;
import com.codepath.travel.helper.DateUtils;
import com.codepath.travel.helper.ImageUtils;
import com.codepath.travel.models.Trip;

import java.util.List;

/**
 * Adapter for trips.
 */
public class TripsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int DEFAULT = 0;

    private List<Trip> mTrips;
    private Context mContext;
    private boolean showProfilePhoto;

    public TripsAdapter(Context context, List<Trip> trips, boolean showProfilePhoto) {
        this.mTrips = trips;
        this.mContext = context;
        this.showProfilePhoto = showProfilePhoto;
    }

    private Context getContext() {
        return this.mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View defaultView = inflater.inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(defaultView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        TripViewHolder vh = (TripViewHolder) viewHolder;
        configureViewHolder(vh, position);
    }

    private void configureViewHolder(TripViewHolder viewHolder, int position) {
        Trip trip = this.mTrips.get(position);

        RelativeLayout rlBackground = viewHolder.getCoverPhoto();
        ImageUtils.loadBackground(rlBackground, trip.getCoverPicUrl());
        ImageView ivProfilePhoto = viewHolder.getProfilePhoto();
        if (showProfilePhoto) {
            ImageUtils.loadImageCircle(ivProfilePhoto, getProfilePicUrl(trip.getUser()),
                    R.drawable.com_facebook_profile_picture_blank_portrait);
        } else {
            ivProfilePhoto.setVisibility(View.GONE);
        }
        TextView tvTripTitle = viewHolder.getTripTitle();
        tvTripTitle.setText(trip.getTitle());
        TextView tvTripDates = viewHolder.getTripDates();
        tvTripDates.setText(DateUtils.formatDateRange(mContext, trip.getStartDate(), trip.getEndDate()));
    }

    @Override
    public int getItemCount() {
        return this.mTrips.size();
    }

    @Override
    public int getItemViewType(int position) {
        return DEFAULT;
    }
}

