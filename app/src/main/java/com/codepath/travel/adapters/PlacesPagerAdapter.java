package com.codepath.travel.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.travel.fragments.PlacesListFragment;

/**
 * Created by rpraveen on 12/3/16.
 */

public class PlacesPagerAdapter extends FragmentPagerAdapter {

  // member variables
  private Context mContext;
  private String [] mTabs = { "Top Spots", "Dining" };
  private String mDestinationLatLong;

  public PlacesPagerAdapter(FragmentManager fm, Context context, String destinationLatLong) {
    super(fm);
    mContext = context;
    mDestinationLatLong = destinationLatLong;
  }

  @Override
  public Fragment getItem(int position) {
    if (position == 0) {
      return PlacesListFragment.newInstance(mDestinationLatLong, "point_of_interest|establishment|museum|amusement_park|art_gallery|casino|zoo");
    } else {
      return PlacesListFragment.newInstance(mDestinationLatLong, "restaurant|cafe|food");
    }
  }

  @Override
  public CharSequence getPageTitle(int position) {
    // Generate title based on item position
    return mTabs[position];
  }

  @Override
  public int getCount() {
    return mTabs.length;
  }
}
