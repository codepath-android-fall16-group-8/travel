package com.codepath.travel.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.travel.Constants;
import com.codepath.travel.R;
import com.codepath.travel.adapters.HomePagerAdapter;
import com.codepath.travel.fragments.TripClickListener;
import com.codepath.travel.helper.ImageUtils;
import com.codepath.travel.models.parse.Trip;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.codepath.travel.models.parse.User.getCoverPicUrl;
import static com.codepath.travel.models.parse.User.getProfilePicUrl;
import static com.codepath.travel.models.parse.User.setCoverPicUrl;
import static com.codepath.travel.models.parse.User.setFbUid;
import static com.codepath.travel.models.parse.User.setProfilePicUrl;
import static com.parse.ParseUser.getCurrentUser;

public class HomeActivity extends AppCompatActivity implements TripClickListener, PlaceSelectionListener {
    //Class variables
    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final int LOGIN_REQUEST = 0;
    private static final int CREATE_STORY_REQUEST = 1;
    private static final int STORY_REQUEST = 2;

    // Views
    @BindView(R.id.drawer_layout) DrawerLayout mDrawer;
    @BindView(R.id.toolbar)  Toolbar toolbar;
    @BindView(R.id.nvView) NavigationView nvDrawer;
    @BindView(R.id.tabLayout) TabLayout tabLayout;
    @BindView(R.id.tabViewPager) ViewPager tabViewPager;
    @BindView(R.id.etAutocomplete) EditText etAutocomplete;

    // Views in Navigation view
    private ActionBarDrawerToggle drawerToggle;
    private ImageView ivProfileImage;
    private TextView tvProfileName;
    private RelativeLayout nvHeader;

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
        setUpSearchAutoComplete();

        // User login
        if (getCurrentUser() != null) { // start with existing user
            startWithCurrentUser();

        } else {
            launchLoginActivity();
        }
    }

    private void setupViews() {
        setSupportActionBar(toolbar);

        nvHeader = (RelativeLayout)nvDrawer.getHeaderView(0);
        this.ivProfileImage = (ImageView) nvHeader.findViewById(R.id.ivProfilePic);
        this.ivProfileImage.setImageResource(0);
        this.tvProfileName = (TextView) nvHeader.findViewById(R.id.tvName);

        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);
    }

    // Get the userId from the cached currentUser object
    private void startWithCurrentUser() {
        ParseUser pUser = getCurrentUser();
        this.tvProfileName.setText(pUser.getUsername());
        ImageUtils.loadImageCircle(this.ivProfileImage, getProfilePicUrl(pUser),
                R.drawable.com_facebook_profile_picture_blank_portrait);
        ImageUtils.loadBackground(nvHeader, getCoverPicUrl(pUser));

        tabViewPager.setAdapter(
            new HomePagerAdapter(
            getSupportFragmentManager(),
            this,
            getCurrentUser().getObjectId()
            )
        );
        tabLayout.setupWithViewPager(tabViewPager);
    }

    private void newFBAccountSetup(ParseUser pUser) {
        Log.d(TAG, String.format("New FB Account setup for: %s",
                pUser.getUsername()));
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                (object, response) -> {
                    try {
                        setFbUid(pUser, object.getInt("id"));
                        pUser.setUsername(object.getString("name"));
                        if (object.has("email")) {
                            pUser.setEmail(object.getString("email"));
                        }
                        if (object.has("picture")) {
                            setProfilePicUrl(pUser, object.getJSONObject("picture").getJSONObject("data").getString("url"));
                        }
                        if (object.has("cover")) {
                            setCoverPicUrl(pUser, object.getJSONObject("cover").getString("source"));
                        }
                        pUser.saveInBackground(e -> {
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

    private void setUpSearchAutoComplete() {
        // Retrieve the PlaceAutocompleteFragment.
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)this
                .getFragmentManager().findFragmentById(R.id.autocomplete_fragment_new_trip);
        // Register a listener to receive callbacks when a place has been selected or an error has
        // occurred.
        autocompleteFragment.setOnPlaceSelectedListener(this);
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
            ParseUser pUser = getCurrentUser();
            // update user fields if this is a new facebook login
            if (pUser.isNew() && ParseFacebookUtils.isLinked(pUser)) {
                newFBAccountSetup(pUser);
            } else {
                startWithCurrentUser();
            }
        } else if (resultCode == RESULT_OK && requestCode == CREATE_STORY_REQUEST) {
            // trip added
            tabViewPager.getAdapter().notifyDataSetChanged();
            etAutocomplete.setText("");
        } else if (resultCode == RESULT_OK && requestCode == STORY_REQUEST) {
            // trip deleted
            tabViewPager.getAdapter().notifyDataSetChanged();
        }
    }

    /* Listeners */
    @Override
    public void onTripClick(String tripId, String tripTitle) {
        launchStoryActivity(tripId, tripTitle);
    }

    @Override
    public void onShareClick(Trip trip, boolean share) {
        boolean isShared = trip.isShared();
        if (!isShared && share) {
            trip.setShared(true);
        } else if (isShared && !share) {
            trip.setShared(false);
        }
        trip.saveInBackground();
    }

    @Override
    public void onProfileClick(ParseUser pUser) {
        Log.d(TAG, String.format("onProfileClick: %s", pUser.getUsername()));
        // TODO: navigate to provile view
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
            case R.id.nav_search:
                showSearchActivity();
                break;
            case R.id.nav_delete_account:
                    deleteAccount();
                break;
            case R.id.nav_profile:
                showUserProfile(ParseUser.getCurrentUser().getObjectId());
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

    // all private methods below
    private void showUserProfile(String userID) {
        Intent intent = new Intent(this, ProfileViewActivity.class);
        intent.putExtra(ProfileViewActivity.USER_ID, userID);
        startActivity(intent);
    }

    private void showSearchActivity() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPlaceSelected(Place place) {
        Log.i(TAG, "Place Selected: " + place.getName());
        String LatLng = String.format("%f,%f",place.getLatLng().latitude,place.getLatLng().longitude);
        Intent createTrip = new Intent(this, PlaceSuggestionActivity.class);
        String destination = place.getName().toString();
        if(!destination.isEmpty() && !LatLng.isEmpty()) {
            createTrip.putExtra(
                    Constants.DESTINATION_ARG,
                    destination
            );
            createTrip.putExtra(Constants.LATLNG_ARG,
                    LatLng);
            startActivityForResult(createTrip, CREATE_STORY_REQUEST);
        }else {
            Toast.makeText(this, "Please add a destination", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onError(Status status) {
        Log.e(TAG, "onError: Status = " + status.toString());
    }
}
