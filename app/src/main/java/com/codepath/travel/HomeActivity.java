package com.codepath.travel;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.codepath.travel.models.User;
import com.crashlytics.android.Crashlytics;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.parse.ParseFacebookUtils;

import io.fabric.sdk.android.Fabric;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final int LOGIN_REQUEST = 0;

    private TextView tvHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // fabric crash reporting
//        Fabric.with(this, new Crashlytics());

        // facebook integration
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        this.tvHello = (TextView) findViewById(R.id.hello);

        // User login
        if (ParseUser.getCurrentUser() != null) { // start with existing user
            startWithCurrentUser();
        } else {
            launchLoginActivity();
        }
    }

    // Get the userId from the cached currentUser object
    private void startWithCurrentUser() {
        User user = (User) ParseUser.getCurrentUser();
        this.tvHello.setText(String.format("Hello, %s!", ParseUser.getCurrentUser().getUsername()));
    }

    private void launchLoginActivity() {
        ParseLoginBuilder builder = new ParseLoginBuilder(HomeActivity.this);
        startActivityForResult(builder.build(), LOGIN_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == LOGIN_REQUEST) {
            ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
            User user = (User) ParseUser.getCurrentUser();
            // update user fields if this is a new facebook login
            if (user.isNew() && ParseFacebookUtils.isLinked(user)) {
                newAccountSetup(user);
            }
            this.tvHello.setText(String.format("Hello, %s!", user.getUsername()));
        }
    }

    private void newAccountSetup(User user) {
        // TODO: fetch fb data like username and profile pic urls if possible
    }

    public void onLogout(View view) {
        Log.d(TAG, String.format("Logging out for user: %s",
                ParseUser.getCurrentUser().getUsername()));
        ParseUser.logOut();
        launchLoginActivity();
    }

    public void onDeleteAccount(View view) {
        Log.d(TAG, String.format("Deleting account for user: %s",
                ParseUser.getCurrentUser().getUsername()));
        ParseUser.getCurrentUser().deleteEventually();
        launchLoginActivity();
    }
}
