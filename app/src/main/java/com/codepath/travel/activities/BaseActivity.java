package com.codepath.travel.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.codepath.travel.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rpraveen on 11/13/16.
 */

public abstract class BaseActivity extends AppCompatActivity {

  // views
  @BindView(R.id.toolbar) Toolbar toolbar;

  @Override
  protected void onCreate(Bundle savedInstance) {
    super.onCreate(savedInstance);
    ButterKnife.bind(this);
    setSupportActionBar(toolbar);
  }

}
