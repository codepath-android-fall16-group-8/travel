package com.codepath.travel;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

import io.fabric.sdk.android.Fabric;

public class HomeActivity extends AppCompatActivity {
    static final String TAG = HomeActivity.class.getSimpleName();

    private TextView tvHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // fabric crash reporting
        Fabric.with(this, new Crashlytics());

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
        ParseUser user = ParseUser.getCurrentUser();
        Toast.makeText(HomeActivity.this, "Using user: " + user.getUsername(), Toast.LENGTH_SHORT).show();
        this.tvHello.setText(String.format("Hello, %s!", ParseUser.getCurrentUser().getUsername()));
    }

    private static final int LOGIN_REQUEST_CODE = 0;
    private void launchLoginActivity() {
        ParseLoginBuilder builder = new ParseLoginBuilder(HomeActivity.this);
        startActivityForResult(builder.build(), LOGIN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == LOGIN_REQUEST_CODE) {
            ParseUser user = ParseUser.getCurrentUser();
            Toast.makeText(this, String.format("Logged in: %s", user.getUsername()), Toast.LENGTH_SHORT).show();
            this.tvHello.setText(String.format("Hello, %s!", user.getUsername()));
        }
    }
}
