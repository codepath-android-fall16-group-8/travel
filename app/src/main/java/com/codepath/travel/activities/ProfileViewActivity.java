package com.codepath.travel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.codepath.travel.R;
import com.codepath.travel.adapters.ProfilePagerAdapter;
import com.codepath.travel.callbacks.ParseQueryCallback;
import com.codepath.travel.fragments.TripClickListener;
import com.codepath.travel.fragments.pickers.ImagePickerFragment;
import com.codepath.travel.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.BindView;

/**
 * Created by rpraveen on 11/19/16.
 */

public class ProfileViewActivity
  extends BaseActivity
  implements ImagePickerFragment.ImagePickerFragmentListener, TripClickListener {

  // Intent variables
  public static final String USER_ID = "user_id";

  // Image Picker Fragment Tags
  private static final String COVER_PIC = "cover_pic";
  private static final String PROFILE_PIC = "profile_pic";

  // views
  @BindView(R.id.tvProfileUserName) TextView tvProfileUserName;
  @BindView(R.id.tabLayout) TabLayout tabLayout;
  @BindView(R.id.tabViewPager) ViewPager tabViewPager;

  // member variables
  private ParseUser mUser;
  private ProfilePagerAdapter mProfilePagerAdapter;

  @Override
  public void onCreate(Bundle savedInstance) {
    super.onCreate(savedInstance);
    setContentView(R.layout.activity_user_profile);
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
  public void onTripClick(String tripId, String tripTitle) {
    Intent openStory = new Intent(ProfileViewActivity.this, StoryActivity.class);
    openStory.putExtra(StoryActivity.TRIP_TITLE_ARG, tripTitle);
    openStory.putExtra(StoryActivity.TRIP_ID_ARG, tripId);
    startActivity(openStory);
  }

  // all private methods below

  private void initializeViews(ParseUser user) {
    tvProfileUserName.setText(user.getUsername());
    setActionBarTitle(user.getUsername());

    tabViewPager.setAdapter(
      new ProfilePagerAdapter(
        getSupportFragmentManager(),
        this,
        user.getObjectId()
      )
    );
    tabLayout.setupWithViewPager(tabViewPager);
  }

   private void initializeAllFragments(ParseUser user) {
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

    fragmentTransaction.replace(
      R.id.flCoverPicContainer,
      ImagePickerFragment.newInstance(User.getCoverPicUrl(user)),
      COVER_PIC
    );

     fragmentTransaction.replace(
      R.id.flUserPicContainer,
      ImagePickerFragment.newInstance(User.getProfilePicUrl(user)),
      PROFILE_PIC
     );

    fragmentTransaction.commit();
  }

  private boolean getIsValidUser(String userID) {
    return userID.trim().length() > 0 && (userID != null);
  }
}
