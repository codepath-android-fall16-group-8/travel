package com.codepath.travel.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.travel.R;
import com.codepath.travel.helper.ImageUtils;
import com.codepath.travel.models.parse.StoryPlace;
import com.codepath.travel.net.GoogleAsyncHttpClient;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rpraveen on 12/4/16.
 */

public class StoryPlaceCardFragment extends Fragment {

  // ARGS
  private static final String PLACE_TITLE_ARG = "place_title";
  private static final String PLACE_IMAGE_ARG = "place_image";

  @BindView(R.id.ivPlacePhoto) ImageView ivPlacePhoto;
  @BindView(R.id.tvPlaceTitle) TextView tvPlaceTitle;

  public static Fragment newInstance(StoryPlace storyPlace) {
    StoryPlaceCardFragment fragment = new StoryPlaceCardFragment();
    Bundle args = new Bundle();
    args.putString(PLACE_IMAGE_ARG, storyPlace.getPhotoUrl());
    args.putString(PLACE_TITLE_ARG, storyPlace.getName());
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
    View view = inflater.inflate(R.layout.fragment_story_place, container, false);
    ButterKnife.bind(this, view);
    tvPlaceTitle.setText(getArguments().getString(PLACE_TITLE_ARG));
    String photoURL = getArguments().getString(PLACE_IMAGE_ARG);
    ImageUtils.loadImage(ivPlacePhoto, GoogleAsyncHttpClient.getPlacePhotoUrl(photoURL));
    return view;
  }
}
