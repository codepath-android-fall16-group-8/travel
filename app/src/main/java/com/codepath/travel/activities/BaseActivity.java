package com.codepath.travel.activities;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.travel.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rpraveen on 11/13/16.
 */

/**
 * this activity holds views common to all activities
 * processing views, error views, toolbar,
 * helper methods used across activities go here
  */

public abstract class BaseActivity extends AppCompatActivity {

  // views
  @BindView(R.id.toolbar) Toolbar toolbar;

  protected void initializeCommonViews() {
    ButterKnife.bind(this);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  protected void setActionBarTitle(String title) {
    getSupportActionBar().setTitle(title);
  }

  protected void showError(String error) {
    // we could have error views in every activity
    // showing a toast for now
    Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
  }

  /**
   * react to the user tapping the up icon in the action bar
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish(); // up has slightly different behaviour than back button
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

}
