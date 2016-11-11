package com.codepath.travel.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.travel.R;
import com.codepath.travel.models.User;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.parse.ParseFacebookUtils;

import org.json.JSONException;
import org.json.JSONObject;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final int LOGIN_REQUEST = 0;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private TextView tvName;
    private ImageView ivProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Facebook integration
        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        AppEventsLogger.activateApp(getApplication());

        setupViews();
        setupDrawerContent(nvDrawer);

        // User login
        if (ParseUser.getCurrentUser() != null) { // start with existing user
            startWithCurrentUser();
        } else {
            launchLoginActivity();
        }
    }

    private void setupViews() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        View nvHeader = nvDrawer.getHeaderView(0);
        this.ivProfile = (ImageView) nvHeader.findViewById(R.id.ivProfilePic);
        this.ivProfile.setImageResource(0);
        this.tvName = (TextView) nvHeader.findViewById(R.id.tvName);

        this.mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);
    }

    // Get the userId from the cached currentUser object
    private void startWithCurrentUser() {
        User user = (User) ParseUser.getCurrentUser();
        this.tvName.setText(user.getUsername());
        Glide.with(this).load(user.getProfilePicUrl())
                .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
                .fitCenter()
                .bitmapTransform(new CropCircleTransformation(this))
                .into(this.ivProfile);
    }

    private void newFBAccountSetup(final User user) {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            user.setFbUid(object.getInt("id"));
                            user.setUsername(object.getString("name"));
                            if (object.has("email")) {
                                user.setEmail(object.getString("email"));
                            }
                            if (object.has("picture")) {
                                user.setProfilePicUrl(object.getJSONObject("picture").getJSONObject("data").getString("url"));
                            }
                            if (object.has("cover")) {
                                user.setCoverPicUrl(object.getJSONObject("cover").getString("source"));
                            }
                            user.saveInBackground();
                            startWithCurrentUser();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture,cover");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void launchLoginActivity() {
        ParseLoginBuilder builder = new ParseLoginBuilder(HomeActivity.this);
        startActivityForResult(builder.build(), LOGIN_REQUEST);
    }

    private void logout() {
        Log.d(TAG, String.format("Logging out for user: %s",
                ParseUser.getCurrentUser().getUsername()));
        ParseUser.logOut();
        launchLoginActivity();
    }

    private void deleteAccount() {
        Log.d(TAG, String.format("Deleting account for user: %s",
                ParseUser.getCurrentUser().getUsername()));
        ParseUser.getCurrentUser().deleteEventually();
        ParseUser.logOut();
        launchLoginActivity();
    }

    // Initialize the navigation drawer listener
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == LOGIN_REQUEST) {
            ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
            User user = (User) ParseUser.getCurrentUser();
            // update user fields if this is a new facebook login
            if (user.isNew() && ParseFacebookUtils.isLinked(user)) {
                newFBAccountSetup(user);
            }
            startWithCurrentUser();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onDestination(View view) {
        Intent intent = new Intent(this, CreateStoryActivity.class);
        startActivity(intent);
    }

    // Assign action to perform for different navigation drawer item
    public void selectDrawerItem(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.nav_logout:
                    logout();
                break;
            case R.id.nav_delete_account:
                    deleteAccount();
                break;
            default: break;
        }

        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

}
