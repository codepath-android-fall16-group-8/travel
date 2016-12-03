package com.codepath.travel.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.travel.fragments.PastTripListFragment;
import com.codepath.travel.fragments.UpcomingTripListFragment;

/**
 * Pager adapter for displaying trips on a user's profile.
 */
public class ProfilePagerAdapter extends FragmentPagerAdapter {

    // member variables
    private Context mContext;
    private String [] mTabs = {"Past Trips", "Upcoming Trips"};
    private String mUserID;

    public ProfilePagerAdapter(FragmentManager fragmentManager, Context context, String userID) {
        super(fragmentManager);
        mContext = context;
        mUserID = userID;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return PastTripListFragment.newInstance(mUserID, false);
        } else {
            return UpcomingTripListFragment.newInstance(mUserID, false);
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
