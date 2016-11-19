package com.codepath.travel.activities;

import static com.parse.ParseUser.getCurrentUser;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.travel.R;
import com.codepath.travel.fragments.NewTripFragment;
import com.codepath.travel.fragments.PastTripListFragment;
import com.codepath.travel.fragments.PlannedTripListFragment;
import com.codepath.travel.fragments.TripClickListener;
import com.codepath.travel.fragments.TripItemFragment;
import com.codepath.travel.helper.ImageUtils;
import com.codepath.travel.models.User;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsLogger;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements TripClickListener {
    //Class variables
    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final int LOGIN_REQUEST = 0;
    private static final int CREATE_STORY_REQUEST = 1;
    private static final int STORY_REQUEST = 2;

    // Views
    @BindView(R.id.drawer_layout) DrawerLayout mDrawer;
    @BindView(R.id.toolbar)  Toolbar toolbar;
    @BindView(R.id.nvView) NavigationView nvDrawer;

    private ActionBarDrawerToggle drawerToggle;
    private FloatingActionButton mFab;

    // Fragments
    private PastTripListFragment pastTripsFragment;
    private PlannedTripListFragment plannedTripsFragment;
    private TripItemFragment currentTripFragment;

    // Views in Navigation view
    private ImageView ivProfileImage;
    private TextView tvProfileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        // Facebook integration
        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        AppEventsLogger.activateApp(getApplication());

        setupViews();
        setupDrawerContent(nvDrawer);
        setUpClickListeners();

        // User login
        if (getCurrentUser() != null) { // start with existing user
            startWithCurrentUser();

        } else {
            launchLoginActivity();
        }

        if (savedInstanceState == null) {
            setupTripFragments();
        } else {
            FragmentManager fm = getSupportFragmentManager();
            plannedTripsFragment = (PlannedTripListFragment) fm.findFragmentByTag("plannedTripsFragment");
            currentTripFragment = (TripItemFragment) fm.findFragmentByTag("currentTripFragment");
            pastTripsFragment = (PastTripListFragment) fm.findFragmentByTag("pastTripsFragment");
        }
    }

    private void setupViews() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        View nvHeader = nvDrawer.getHeaderView(0);
        this.ivProfileImage = (ImageView) nvHeader.findViewById(R.id.ivProfilePic);
        this.ivProfileImage.setImageResource(0);

        this.tvProfileName = (TextView) nvHeader.findViewById(R.id.tvName);

        this.mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);

        mFab = (FloatingActionButton) findViewById(R.id.fab_new_trip);
    }

    private void setUpClickListeners() {
        mFab.setOnClickListener(view -> {
            launchNewTripFragment();
        });
    }

    private void setupTripFragments() {
        String userId = null;
        if (getCurrentUser() != null) {
            userId = getCurrentUser().getObjectId();
        }
        plannedTripsFragment = PlannedTripListFragment.newInstance(userId, false);
        currentTripFragment = TripItemFragment.newInstance(userId, false);
        pastTripsFragment = PastTripListFragment.newInstance(userId, false);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flPlannedContainer, plannedTripsFragment, "plannedTripsFragment");
        ft.replace(R.id.flCurrentContainer, currentTripFragment, "currentTripFragment");
        ft.replace(R.id.flPastContainer, pastTripsFragment, "pastTripsFragment");
        ft.commit();
    }

    private void refreshMyTrips() {
        plannedTripsFragment.populateTrips();
        currentTripFragment.populateTrip();
        pastTripsFragment.populateTrips();
    }

    private void setTripFragmentsUser(String userId) {
        plannedTripsFragment.setUser(userId);
        currentTripFragment.setUser(userId);
        pastTripsFragment.setUser(userId);
    }

    // Get the userId from the cached currentUser object
    private void startWithCurrentUser() {
        User user = (User) getCurrentUser();
        this.tvProfileName.setText(user.getUsername());
        ImageUtils.loadImageCircle(this.ivProfileImage, user.getProfilePicUrl(),
                R.drawable.com_facebook_profile_picture_blank_portrait);
    }

    private void newFBAccountSetup(final User user) {
        Log.d(TAG, String.format("New FB Account setup for: %s",
                user.getUsername()));
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                (object, response) -> {
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
                        user.saveInBackground(e -> {
                            if (e == null) {
                                startWithCurrentUser();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture,cover");
        request.setParameters(parameters);
        request.executeAsync();
    }

    /* Navigation */
    private void launchLoginActivity() {
        ParseLoginBuilder builder = new ParseLoginBuilder(HomeActivity.this);
        startActivityForResult(builder.build(), LOGIN_REQUEST);
    }

    private void launchStoryActivity(String tripId, String tripTitle) {
        Intent openStory = new Intent(HomeActivity.this, StoryActivity.class);
        openStory.putExtra(StoryActivity.TRIP_TITLE_ARG, tripTitle);
        openStory.putExtra(StoryActivity.TRIP_ID_ARG, tripId);
        startActivityForResult(openStory, STORY_REQUEST);
    }

    private void launchNewTripFragment() {
        FragmentManager fm = getSupportFragmentManager();
        NewTripFragment newTripDialog = NewTripFragment.newInstance();
        newTripDialog.show(fm, "New Trip");
    }

    private void logout() {
        Log.d(TAG, String.format("Logging out for user: %s",
                getCurrentUser().getUsername()));
        ParseUser.logOut();
        launchLoginActivity();
    }

    private void deleteAccount() {
        Log.d(TAG, String.format("Deleting account for user: %s",
                getCurrentUser().getUsername()));
        // TODO: delete user's data
        getCurrentUser().deleteEventually();
        ParseUser.logOut();
        launchLoginActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == LOGIN_REQUEST) {
            ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
            User user = (User) getCurrentUser();
            // update user fields if this is a new facebook login
            if (user.isNew() && ParseFacebookUtils.isLinked(user)) {
                newFBAccountSetup(user);
            } else {
                setTripFragmentsUser(user.getObjectId());
                startWithCurrentUser();
            }
        } else if (resultCode == RESULT_OK && requestCode == CREATE_STORY_REQUEST) {
            // trip added
            refreshMyTrips();
        } else if (resultCode == RESULT_OK && requestCode == STORY_REQUEST) {
            // trip deleted
            refreshMyTrips();
        }
    }

    /* Listeners */
    @Override
    public void onTripClick(String tripId, String tripTitle) {
        launchStoryActivity(tripId, tripTitle);
    }

    /* Navigation Drawer */
    // Initialize the navigation drawer listener
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    selectDrawerItem(menuItem);
                    return true;
                });
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
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

    /* Toolbar */
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
}
