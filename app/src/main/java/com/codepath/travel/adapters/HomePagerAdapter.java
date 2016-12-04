package com.codepath.travel.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.codepath.travel.R;
import com.codepath.travel.fragments.FollowingTripListFragment;
import com.codepath.travel.fragments.TripListFragment;

/**
 * Pager adapter for the home view.
 */
public class HomePagerAdapter extends FragmentPagerAdapter {

    // member variables
    private Context mContext;
    private String [] tabTitles = { "Trip Feed", "My Trips" };
    private int tabIcons[] = { R.drawable.ic_earth, R.drawable.ic_person };
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
        return tabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        Drawable image = mContext.getResources().getDrawable(tabIcons[position], null);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        // the image color doesn't change when tabs get selected/unselected,
        // so setting it to a neutral less distracting color here
        DrawableCompat.setTint(image, ContextCompat.getColor(mContext, R.color.cardview_dark_background));
        // Replace blank spaces with image icon
        SpannableString sb = new SpannableString("   " + tabTitles[position]);
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
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
