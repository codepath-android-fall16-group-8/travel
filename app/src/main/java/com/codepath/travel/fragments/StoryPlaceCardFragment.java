package com.codepath.travel.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.travel.R;
import com.codepath.travel.helper.DateUtils;
import com.codepath.travel.helper.ImageUtils;
import com.codepath.travel.listeners.StoryPlaceClickListener;
import com.codepath.travel.models.parse.StoryPlace;
import com.codepath.travel.net.GoogleAsyncHttpClient;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by rpraveen on 12/4/16.
 */
public class StoryPlaceCardFragment extends Fragment {

    // ARGS
    private static final String PLACE_POSITION_ARG = "place_position";
    private static final String PLACE_TITLE_ARG = "place_title";
    private static final String PLACE_IMAGE_ARG = "place_image";
    private static final String PLACE_CHECKIN_ARG = "place_checkin";
    private static final String PLACE_RATING_ARG = "place_rating";

    @BindView(R.id.ivPlacePhoto) ImageView ivPlacePhoto;
    @BindView(R.id.tvPlaceTitle) TextView tvPlaceTitle;
    @BindView(R.id.tvCheckin) TextView tvCheckin;
    @BindView(R.id.rbUserRating) RatingBar rbUserRating;
    @BindView(R.id.ivCollageIcon) ImageView ivCollageIcon;
    private Unbinder unbinder;

    // variables
    private StoryPlaceClickListener mListener;

    public static Fragment newInstance(StoryPlace storyPlace, int position) {
        StoryPlaceCardFragment fragment = new StoryPlaceCardFragment();
        Bundle args = new Bundle();
        args.putInt(PLACE_POSITION_ARG, position);
        args.putString(PLACE_IMAGE_ARG, storyPlace.getPhotoUrl());
        args.putString(PLACE_TITLE_ARG, storyPlace.getName());
        if (storyPlace.getCheckinTime() != null) {
            args.putLong(PLACE_CHECKIN_ARG, storyPlace.getCheckinTime().getTime());
        }
        args.putDouble(PLACE_RATING_ARG, storyPlace.getRating());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_story_place, container, false);
        unbinder = ButterKnife.bind(this, view);

        Bundle args = getArguments();
        int position = args.getInt(PLACE_POSITION_ARG);
        tvPlaceTitle.setText(args.getString(PLACE_TITLE_ARG));
        String photoURL = args.getString(PLACE_IMAGE_ARG);
        ImageUtils.loadImage(ivPlacePhoto, GoogleAsyncHttpClient.getPlacePhotoUrl(photoURL));
        long checkinMillis = args.getLong(PLACE_CHECKIN_ARG, -1);
        if (checkinMillis == -1) {
            tvCheckin.setVisibility(View.GONE);
            rbUserRating.setVisibility(View.GONE);
        } else {
            tvCheckin.setText(DateUtils.formatDate(getActivity(), new Date(checkinMillis)));
            rbUserRating.setRating((float) args.getDouble(PLACE_RATING_ARG, 0.0));
        }

        // click handlers
        ivPlacePhoto.setOnClickListener(v -> mListener.onImageClick(position));
        ivCollageIcon.setOnClickListener(v -> mListener.onStoryPlaceClick(position));

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof StoryPlaceClickListener)) {
            throw new RuntimeException("Activity should implement StoryPlaceClickListener");
        }
        mListener = (StoryPlaceClickListener) context;
    }

    // When binding a fragment in onCreateView, set the views to null in onDestroyView.
    // ButterKnife returns an Unbinder on the initial binding that has an unbind method to do this automatically.
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
