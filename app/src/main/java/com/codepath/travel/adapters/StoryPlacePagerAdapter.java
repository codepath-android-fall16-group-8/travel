package com.codepath.travel.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.travel.fragments.StoryPlaceCardFragment;
import com.codepath.travel.models.parse.StoryPlace;

import java.util.List;

/**
 * Created by rpraveen on 12/4/16.
 */

public class StoryPlacePagerAdapter extends FragmentPagerAdapter {

  private List<StoryPlace> mStoryPlaces;

  public StoryPlacePagerAdapter(FragmentManager fm, List<StoryPlace> storyPlaces) {
    super(fm);
    mStoryPlaces = storyPlaces;
  }


  @Override
  public Fragment getItem(int position) {
    return StoryPlaceCardFragment.newInstance(mStoryPlaces.get(position));
  }

  @Override
  public int getCount() {
    return mStoryPlaces.size();
  }
}
