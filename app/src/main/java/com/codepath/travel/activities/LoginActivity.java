package com.codepath.travel.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.codepath.travel.R;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login);
    }

    /**
     * Test method to force crash the app. Checkout the Crashlytics dashboard to see your crash!
     */
    public void forceCrash(View view) {
        throw new RuntimeException("This is a crash");
    }

    public void onLogin(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

}
