package com.codepath.travel.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.travel.fragments.FollowingTripListFragment;
import com.codepath.travel.fragments.TripListFragment;

/**
 * Pager adapter for the home view.
 */
public class HomePagerAdapter extends FragmentPagerAdapter {

    // member variables
    private Context mContext;
    private String [] mTabs = { "Trip Feed", "My Trips" };
    private String mUserID;
    private FollowingTripListFragment followingFragment;
    private TripListFragment myTripsFragment;

    public HomePagerAdapter(FragmentManager fragmentManager, Context context, String userID) {
        super(fragmentManager);
        mContext = context;
        mUserID = userID;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            if (followingFragment == null) {
                followingFragment = FollowingTripListFragment.newInstance(mUserID);
            }
            return followingFragment;
        } else {
            if (myTripsFragment == null) {
                myTripsFragment = TripListFragment.newInstance(mUserID, false, true);
            }
            return myTripsFragment;
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

    public int getItemPosition(Object object){
        return POSITION_NONE;
    }

    public void setUser(String userId) {
        mUserID = userId;
        if (followingFragment != null) {
            followingFragment.setUser(userId);
        }
        if (myTripsFragment != null) {
            myTripsFragment.setUser(userId);
        }
    }
}
