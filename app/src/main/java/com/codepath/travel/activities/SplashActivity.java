package com.codepath.travel.activities;

/**
 * Created by aditikakadebansal on 11/15/16.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //View is not setup here since it comes from the theme to save time of layout inflation
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}