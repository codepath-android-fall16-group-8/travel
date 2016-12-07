package com.codepath.travel.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.travel.Constants;
import com.codepath.travel.R;
import com.codepath.travel.adapters.ProfilePagerAdapter;
import com.codepath.travel.callbacks.ParseQueryCallback;
import com.codepath.travel.fragments.TripClickListener;
import com.codepath.travel.fragments.pickers.ImagePickerFragment;
import com.codepath.travel.models.parse.Trip;
import com.codepath.travel.models.parse.User;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

import butterknife.BindView;

/**
 * Activity for displaying a user's profile.
 */
public class ProfileViewActivity
    extends BaseActivity
    implements ImagePickerFragment.ImagePickerFragmentListener, TripClickListener {

    private static final String TAG = ProfileViewActivity.class.getSimpleName();

    // Intent variables
    public static final String USER_ID = "user_id";

    // Image Picker Fragment Tags
    private static final String COVER_PIC = "cover_pic";
    private static final String PROFILE_PIC = "profile_pic";

    // views
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.tabLayout) TabLayout tabLayout;
    @BindView(R.id.tabViewPager) ViewPager tabViewPager;
    @BindView(R.id.ivFollowUser) ImageView ivFollowUser;
    @BindView(R.id.tvFollowersCount) TextView tvFollowersCount;
    @BindView(R.id.tvFollowingCount) TextView tvFollowingCount;

    // member variables
    private ParseUser mUser;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_user_profile);
        setupWindowAnimationsEnterRight();
        initializeCommonViews();
        String userID = getIntent().getStringExtra(USER_ID);

        // prefer early return over if/else blocks
        if (!getIsValidUser(userID)) {
            showError("invalid user id passed");
            return;
        }

        // get the user and load all fragments
        User.getUserByID(userID, new ParseQueryCallback<ParseUser>() {
            @Override
            public void onQuerySuccess(ParseUser data) {
                mUser = data;
                initializeViews(data);
                initializeAllFragments(data);
            }

            @Override
            public void onQueryError(ParseException e) {
                showError("error fetching user");
            }
        });
    }

    @Override
    public void onImageUploadSuccess(final String tag, final String imageURL) {
        ImagePickerFragment imagePicker =
            (ImagePickerFragment) (getSupportFragmentManager().findFragmentByTag(tag));
        switch (tag) {
            case COVER_PIC:
                User.saveCoverPicURL(mUser, imageURL, new ParseQueryCallback<ParseUser>() {
                    @Override
                    public void onQuerySuccess(ParseUser data) {
                        // load the image in the fragment
                        imagePicker.loadImage(imageURL);
                    }

                    @Override
                    public void onQueryError(ParseException e) {
                        // TODO hook this to the fragment
                        showError("Cover Pic Failed");
                    }
                });
                break;
            case PROFILE_PIC:
                User.saveProfilePicURL(mUser, imageURL, new ParseQueryCallback<ParseUser>() {
                    @Override
                    public void onQuerySuccess(ParseUser data) {
                        // load the image in the fragment
                        imagePicker.loadImage(imageURL);
                    }

                    @Override
                    public void onQueryError(ParseException e) {
                        // TODO hook this to the fragment
                        showError("Profile Pic Failed");
                    }
                });
                break;
        }
    }

    @Override
    public void onTripClick(String tripId, String tripTitle, boolean isOwner) {
        Intent openStory = new Intent(ProfileViewActivity.this, StoryActivity.class);
        openStory.putExtra(Constants.TRIP_TITLE_ARG, tripTitle);
        openStory.putExtra(Constants.TRIP_ID_ARG, tripId);
        openStory.putExtra(Constants.IS_OWNER_ARG, isOwner);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(ProfileViewActivity.this);
        startActivity(openStory, options.toBundle());
    }

    @Override
    public void onShareClick(Trip trip, boolean share) {
        boolean isShared = trip.isShared();
        if (!isShared && share) {
            trip.setShared(true);
        } else if (isShared && !share) {
            trip.setShared(false);
        }
        trip.saveInBackground();
    }

    @Override
    public void onProfileClick(ParseUser pUser) {
        // do nothing
    }

    // all private methods below

    private void initializeViews(ParseUser user) {

        setActionBarTitle(user.getUsername());
        collapsingToolbar.setTitle(user.getUsername());
        collapsingToolbar.setExpandedTitleColor(Color.WHITE);
        tabViewPager.setAdapter(
            new ProfilePagerAdapter(
                getSupportFragmentManager(),
                this,
                user.getObjectId()
            )
        );
        tabLayout.setupWithViewPager(tabViewPager);

        initFollowers(user);
        initFollowing(user);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(ProfileViewActivity.this);
        tvFollowersCount.setOnClickListener((View v) -> {
            Intent showFollowers = new Intent(ProfileViewActivity.this, FollowActivity.class);
            showFollowers.putExtra(FollowActivity.SHOW_FOLLOWERS, true);
            showFollowers.putExtra(FollowActivity.USER_ID, user.getObjectId());
            startActivity(showFollowers, options.toBundle());
        });

        tvFollowingCount.setOnClickListener((View v) -> {
            Intent showFollowers = new Intent(ProfileViewActivity.this, FollowActivity.class);
            showFollowers.putExtra(FollowActivity.SHOW_FOLLOWERS, false);
            showFollowers.putExtra(FollowActivity.USER_ID, user.getObjectId());
            startActivity(showFollowers, options.toBundle());
        });

        // user can't follow himself
        if (ParseUser.getCurrentUser().getObjectId().equals(user.getObjectId())) {
            return;
        }

        ivFollowUser.setVisibility(View.VISIBLE);

        // make query to check following relation
        User.queryIsFollowing(ParseUser.getCurrentUser(), user, (List<ParseUser> objects, ParseException e) -> {
            // successfull query with size > 0 indicates we have a relation
            if (e == null && objects.size() > 0) {
                setFollowRelationImageResource(ivFollowUser, user, R.drawable.ic_person_friend);
                return;
            }
            setFollowRelationImageResource(ivFollowUser, user, R.drawable.ic_person_add);
        });
    }

     private void initializeAllFragments(ParseUser user) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(
            R.id.flCoverPicContainer,
            ImagePickerFragment.newInstance(User.getCoverPicUrl(user), user.getObjectId()),
            COVER_PIC
        );

         fragmentTransaction.replace(
            R.id.flUserPicContainer,
            ImagePickerFragment.newInstance(User.getProfilePicUrl(user), user.getObjectId()),
            PROFILE_PIC
         );

        fragmentTransaction.commit();
    }

    private boolean getIsValidUser(String userID) {
        return userID.trim().length() > 0 && (userID != null);
    }

    private void initFollowers(ParseUser user) {
        User.queryAllFollowers(user, (List<ParseUser> followers, ParseException e) -> {
            if (e == null) {
                tvFollowersCount.setText(followers.size()+"");
            }
        });
    }

    private void initFollowing(ParseUser user) {
        User.queryAllFollowing(user, (List<ParseUser> following, ParseException e) -> {
            if (e == null) {
                tvFollowingCount.setText(following.size()+"");
            }
        });

    }

    // this stuff is the same as user adapter follow
    // fragments within a fragment does not seem like a good idea
    // need to figure out how to share this between user adapter and profile
    private void setFollowRelationImageResource(ImageView imageView, ParseUser user, int resource) {
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(resource);
        if (resource == R.drawable.ic_person_friend) {
            setUnFollowListener(imageView, user);
            return;
        }
        setFollowListener(imageView, user);
    }

    private void setUnFollowListener(ImageView imageView, ParseUser otherUser) {
        imageView.setOnClickListener((View v) -> {
            User.unFollow(ParseUser.getCurrentUser(), otherUser, (ParseException e) -> {
                if (e == null) {
                    setFollowRelationImageResource(imageView, otherUser, R.drawable.ic_person_add);
                    initFollowers(otherUser);
                }
            });
        });
    }

    private void setFollowListener(ImageView imageView, ParseUser otherUser) {
        imageView.setOnClickListener((View v) -> {
            User.follow(ParseUser.getCurrentUser(), otherUser, (ParseException e) -> {
                if (e == null) {
                    setFollowRelationImageResource(imageView, otherUser, R.drawable.ic_person_friend);
                    initFollowers(otherUser);
                }
            });
        });
    }
}
