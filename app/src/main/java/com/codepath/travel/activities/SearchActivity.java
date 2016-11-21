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
import com.codepath.travel.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by rpraveen on 11/20/16.
 */

public class SearchActivity extends BaseActivity {

  @BindView(R.id.tvNoUsersFound) TextView tvNoUsersFound;
  @BindView(R.id.pbSearching) ProgressBar pbSearching;

  @Override
  public void onCreate(Bundle savedInstance) {
    super.onCreate(savedInstance);
    setContentView(R.layout.activity_search_users);
    initializeCommonViews();
    tvNoUsersFound.setVisibility(View.GONE);
    pbSearching.setVisibility(View.GONE);
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
        pbSearching.setVisibility(View.VISIBLE);
        tvNoUsersFound.setVisibility(View.GONE);
        User.findUsersByName(query, (List<ParseUser> objects, ParseException e) -> {
          pbSearching.setVisibility(View.GONE);
          ArrayList<ParseUser> results = new ArrayList<>();
          if (e != null) {
            Log.d("Error ", e.toString());
            return;
          }
          if (objects.size() == 0) {
            tvNoUsersFound.setVisibility(View.VISIBLE);
          }
          results.addAll(objects);
          ((UserListFragment)getSupportFragmentManager().findFragmentByTag(UserListFragment.TAG)).populateUsers(results);
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
