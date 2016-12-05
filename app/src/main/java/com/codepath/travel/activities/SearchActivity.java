package com.codepath.travel.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.travel.R;
import com.codepath.travel.fragments.UserListFragment;
import com.codepath.travel.models.parse.User;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;

/**
 * Search activity for finding friends.
 */
public class SearchActivity extends BaseActivity {

  @BindString(R.string.search_user) String toolbarTitle;
  @BindView(R.id.tvNoUsersFound) TextView tvNoUsersFound;
  @BindView(R.id.pbLoading) ProgressBar pbLoading;

  @Override
  public void onCreate(Bundle savedInstance) {
    super.onCreate(savedInstance);
    setContentView(R.layout.activity_search_users);
    initializeCommonViews();
    setActionBarTitle(toolbarTitle);
    tvNoUsersFound.setVisibility(View.GONE);
    pbLoading.setVisibility(View.GONE);
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.replace(R.id.flUsersContainer, UserListFragment.newInstance(), UserListFragment.TAG);
    fragmentTransaction.commit();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_search, menu);
    MenuItem searchItem = menu.findItem(R.id.action_search);
    final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
    // Expand the search view and request focus
    searchItem.expandActionView();
    searchView.requestFocus();

    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        pbLoading.setVisibility(View.VISIBLE);
        tvNoUsersFound.setVisibility(View.GONE);
        User.findUsersByName(query, (List<ParseUser> objects, ParseException e) -> {
          pbLoading.setVisibility(View.GONE);
          if (e != null) {
            Log.d("Error ", e.toString());
            return;
          }
          if (objects.size() == 0) {
            tvNoUsersFound.setVisibility(View.VISIBLE);
          }
          ((UserListFragment)getSupportFragmentManager().findFragmentByTag(UserListFragment.TAG)).populateUsers(objects);
        });
        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        return false;
      }
    });
    return true;
  }
}
