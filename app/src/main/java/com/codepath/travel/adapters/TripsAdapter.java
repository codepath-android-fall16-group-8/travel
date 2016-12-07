package com.codepath.travel.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.codepath.travel.R;
import com.codepath.travel.fragments.TripClickListener;
import com.codepath.travel.helper.DateUtils;
import com.codepath.travel.helper.ImageUtils;
import com.codepath.travel.models.parse.Trip;

import java.util.List;

import static com.codepath.travel.models.parse.User.getProfilePicUrl;

/**
 * Adapter for trips.
 */
public class TripsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int DEFAULT = 0;

    private List<Trip> mTrips;
    private Context mContext;
    private TripClickListener listener;
    private boolean showProfilePhoto;
    private boolean showSharing;

    public TripsAdapter(Context context, List<Trip> trips, boolean showProfilePhoto, boolean showSharing) {
        this.mTrips = trips;
        this.mContext = context;
        this.showProfilePhoto = showProfilePhoto;
        this.showSharing = showSharing;
        this.listener = (TripClickListener) context;
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

        ImageUtils.loadBackgroundImage(viewHolder.getRelativeLayout(),
                trip.getCoverPicUrl(),
                R.drawable.ic_photoholder,
                viewHolder.getProgressBar());

        // profile photo (currently for following tab only)
        ImageView ivProfilePhoto = viewHolder.getProfilePhoto();
        if (showProfilePhoto) {
            ImageUtils.loadImageCircle(ivProfilePhoto, getProfilePicUrl(trip.getUser()),
                    R.drawable.com_facebook_profile_picture_blank_portrait);
            ivProfilePhoto.setOnClickListener(v -> listener.onProfileClick(trip.getUser()));
        } else {
            ivProfilePhoto.setVisibility(View.GONE);
        }
        TextView tvTripTitle = viewHolder.getTripTitle();
        tvTripTitle.setText(trip.getTitle());
        TextView tvTripDates = viewHolder.getTripDates();
        tvTripDates.setText(DateUtils.formatDateRange(mContext, trip.getStartDate(), trip.getEndDate()));
        TextView tvLocation = viewHolder.getTripLocation();
        if(trip.getDestinationPlaceName() != null) {
            tvLocation.setText(trip.getDestinationPlaceName());
        }

        TextView tvShare = viewHolder.getTvShare();
        // sharing toggle button
        ToggleButton toggleBtnShare = viewHolder.getToggleBtnShare();
        if (showSharing) {
            toggleBtnShare.setVisibility(View.VISIBLE);
            tvShare.setVisibility(View.VISIBLE);
            toggleBtnShare.setChecked(trip.isShared());
            toggleBtnShare.setOnCheckedChangeListener((buttonView, isChecked) -> {
                listener.onShareClick(trip, isChecked);
                if(isChecked) {
                    tvShare.setText(R.string.unshare);
                } else {
                    tvShare.setText(R.string.share);
                }
            });

            if(trip.isShared()) {
                tvShare.setText(R.string.unshare);
            } else {
                tvShare.setText(R.string.share);
            }
        } else {
            toggleBtnShare.setVisibility(View.GONE);
            tvShare.setVisibility(View.GONE);
        }
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

