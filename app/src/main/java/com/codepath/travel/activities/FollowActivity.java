package com.codepath.travel.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.travel.R;
import com.codepath.travel.fragments.UserListFragment;
import com.codepath.travel.models.parse.User;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

import butterknife.BindView;

/**
 * Created by rpraveen on 11/21/16.
 */

// shows followers or following users based on the passed intent
public class FollowActivity extends BaseActivity {

  // Intent arg
  public static final String SHOW_FOLLOWERS = "followers";
  public static final String USER_ID = "userID";

  @BindView(R.id.tvNoUsersFound) TextView tvNoUsersFound;
  @BindView(R.id.pbLoading) ProgressBar pbLoading;

  @Override
  public void onCreate(Bundle savedInstance) {
    super.onCreate(savedInstance);
    setContentView(R.layout.activity_follow);
    initializeCommonViews();
    tvNoUsersFound.setVisibility(View.GONE);
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.replace(R.id.flUsersContainer, UserListFragment.newInstance(), UserListFragment.TAG);
    fragmentTransaction.commit();

    String userID = getIntent().getStringExtra(USER_ID);
    ParseUser user = ParseUser.createWithoutData(ParseUser.class, userID);

    if (getIntent().getBooleanExtra(SHOW_FOLLOWERS, true)) {
      setActionBarTitle("Followers");
      populateFollowers(user);
      return;
    }
    setActionBarTitle("Following");
    populateFollowing(user);
  }

  private void populateFollowers(ParseUser user) {
    User.queryAllFollowers(user, (List<ParseUser> followers, ParseException e) -> {
      pbLoading.setVisibility(View.GONE);
      if (e != null) {
        Log.d("Err fetching followers", e.toString());
        return;
      }
      if (followers.size() == 0) {
        tvNoUsersFound.setVisibility(View.VISIBLE);
      }
      ((UserListFragment)getSupportFragmentManager().findFragmentByTag(UserListFragment.TAG)).populateUsers(followers);
    });
  }

  private void populateFollowing(ParseUser user) {
    User.queryAllFollowing(user, (List<ParseUser> following, ParseException e) -> {
      pbLoading.setVisibility(View.GONE);
      if (e != null) {
        Log.d("Err fetching followers", e.toString());
        return;
      }
      if (following.size() == 0) {
        tvNoUsersFound.setVisibility(View.VISIBLE);
      }
      ((UserListFragment)getSupportFragmentManager().findFragmentByTag(UserListFragment.TAG)).populateUsers(following);
    });
  }

}
