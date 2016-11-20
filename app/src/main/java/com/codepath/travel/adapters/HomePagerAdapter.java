package com.codepath.travel.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.travel.fragments.PastTripListFragment;
import com.codepath.travel.fragments.PlannedTripListFragment;
import com.codepath.travel.fragments.TripListFragment;

/**
 * Created by rpraveen on 11/19/16.
 */

public class HomePagerAdapter extends FragmentPagerAdapter {

  // member variables
  private Context mContext;
  private String [] mTabs = {"Current Trips", "Past Trips", "Future Trips"};
  private String mUserID;

  public HomePagerAdapter(FragmentManager fragmentManager, Context context, String userID) {
    super(fragmentManager);
    mContext = context;
    mUserID = userID;
  }

  @Override
  public Fragment getItem(int position) {
    if (position == 0) {
      return TripListFragment.newInstance(mUserID, false);
    } else if (position == 1) {
      return PastTripListFragment.newInstance(mUserID, false);
    } else {
      return PlannedTripListFragment.newInstance(mUserID, false);
    }
  }

  @Override
  public int getCount() {
    return mTabs.length;
  }

  @Override
  public CharSequence getPageTitle(int position) {
    // Generate title based on item position
    return mTabs[position];
  }
}
