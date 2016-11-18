package com.codepath.travel.fragments;

import static com.codepath.travel.fragments.TripListFragment.FETCH_USER_ARG;
import static com.codepath.travel.fragments.TripListFragment.USER_ID_ARG;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.travel.R;
import com.codepath.travel.helper.DateUtils;
import com.codepath.travel.helper.ImageUtils;
import com.codepath.travel.models.Trip;
import com.codepath.travel.models.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Fragment to display a single trip item.
 */
public class TripItemFragment extends Fragment {
    private static final String TAG = TripItemFragment.class.getSimpleName();

    @BindView(R.id.ivProfilePhoto) ImageView ivProfilePhoto;
    @BindView(R.id.rlBackground) RelativeLayout rlBackground;
    @BindView(R.id.tvTripTitle) TextView tvTripTitle;
    @BindView(R.id.tvTripDates) TextView tvTripDates;
    @BindView(R.id.btnShare) ImageButton btnShare;

    private Unbinder unbinder;
    private Trip mTrip;
    private TripClickListener listener;

    public static TripItemFragment newInstance(String userId, boolean fetchUser) {
        TripItemFragment fragment = new TripItemFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID_ARG, userId);
        args.putBoolean(FETCH_USER_ARG, fetchUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listener = (TripClickListener) getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        View view =  inflater.inflate(R.layout.fragment_trip, parent, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        populateTrip();
        view.setOnClickListener(v -> {
            if (mTrip != null) {
                listener.onTripClick(mTrip.getObjectId(), mTrip.getTitle());
            }

        });
    }

    public void populateTrip() {
        String userId = getArguments().getString(USER_ID_ARG);
        Trip.getCurrentTripForUser(userId, false, (trip, e) -> {
            if (e == null) {
                mTrip = trip;
                if (getArguments().getBoolean(FETCH_USER_ARG)) {
                    ImageUtils.loadImageCircle(ivProfilePhoto, ((User) trip.getUser()).getProfilePicUrl(),
                            R.drawable.com_facebook_profile_picture_blank_portrait);
                } else {
                    ivProfilePhoto.setVisibility(View.GONE);
                }
                ImageUtils.loadBackground(rlBackground, trip.getCoverPicUrl());
                tvTripTitle.setText(trip.getTitle());
                tvTripDates.setText(DateUtils.getDateRangeString(
                        trip.getStartDate(), trip.getEndDate()));
            } else {
                Log.d(TAG, String.format("Failed to find current trip for user %s: %s", userId, e.getMessage()));
            }
        });
    }

    // When binding a fragment in onCreateView, set the views to null in onDestroyView.
    // ButterKnife returns an Unbinder on the initial binding that has an unbind method to do this automatically.
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}