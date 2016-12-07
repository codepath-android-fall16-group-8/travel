package com.codepath.travel.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.TransitionInflater;
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
import com.codepath.travel.models.parse.User;
import com.codepath.travel.net.GoogleAsyncHttpClient;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.codepath.travel.models.parse.User.getCoverPicUrl;
import static com.codepath.travel.models.parse.User.getProfilePicUrl;
import static com.codepath.travel.models.parse.User.setCoverPicUrl;
import static com.codepath.travel.models.parse.User.setFbUid;
import static com.codepath.travel.models.parse.User.setProfilePicUrl;
import static com.parse.ParseUser.getCurrentUser;

public class HomeActivity extends AppCompatActivity implements TripClickListener, PlaceSelectionListener {
    // Class variables
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
    private RelativeLayout nvHeader;
    private ImageView ivCover;
    private ImageView ivProfileImage;
    private TextView tvProfileName;

    // Member variables
    private HomePagerAdapter mHomePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupWindowAnimations();

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

    private void setupWindowAnimations() {
        Fade fadeOut = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.activity_fade_out);
        Fade fadeIn = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.activity_fade_in);
        getWindow().setExitTransition(fadeOut); // exit: fade out when opening a new activity
        getWindow().setReenterTransition(fadeIn); // re-enter: fade in returning from another activity
        getWindow().setAllowReturnTransitionOverlap(false); // overlap with the callers exit transition when returning?
    }

    private void setupViews() {
        setSupportActionBar(toolbar);

        nvHeader = (RelativeLayout) nvDrawer.getHeaderView(0);
        this.ivCover = (ImageView) nvHeader.findViewById(R.id.ivCover);
        this.ivProfileImage = (ImageView) nvHeader.findViewById(R.id.ivProfilePic);
        this.tvProfileName = (TextView) nvHeader.findViewById(R.id.tvName);

        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);

        // setup tabs
        mHomePagerAdapter = new HomePagerAdapter(getSupportFragmentManager(), this, null);
        tabViewPager.setAdapter(mHomePagerAdapter);
        tabLayout.setupWithViewPager(tabViewPager);
    }

    // Get the userId from the cached currentUser object
    private void startWithCurrentUser() {
        ParseUser pUser = getCurrentUser();
        // update nav drawer views
        ImageUtils.loadImage(ivCover, getCoverPicUrl(pUser), R.drawable.com_facebook_profile_picture_blank_portrait, null);
        ImageUtils.loadImageCircle(this.ivProfileImage, getProfilePicUrl(pUser),
                R.drawable.com_facebook_profile_picture_blank_portrait);
        this.tvProfileName.setText(pUser.getUsername());

        // update tab data using current user
        mHomePagerAdapter.setUser(pUser.getObjectId());
        mHomePagerAdapter.notifyDataSetChanged();
        tabViewPager.setCurrentItem(0); // reset to first tab
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

    private void launchStoryActivity(String tripId, String tripTitle, boolean isOwner) {
        Intent openStory = new Intent(HomeActivity.this, StoryActivity.class);
        openStory.putExtra(Constants.TRIP_TITLE_ARG, tripTitle);
        openStory.putExtra(Constants.TRIP_ID_ARG, tripId);
        openStory.putExtra(Constants.IS_OWNER_ARG, isOwner);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this);
        startActivityForResult(openStory, STORY_REQUEST, options.toBundle());
    }

    private void showUserProfile(ParseUser pUser) {
        Intent viewProfile = new Intent(this, ProfileViewActivity.class);
        viewProfile.putExtra(ProfileViewActivity.USER_ID, pUser.getObjectId());

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this);
        startActivity(viewProfile, options.toBundle());
    }

    private void showSearchActivity() {
        Intent intent = new Intent(this, SearchActivity.class);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this);
        startActivity(intent, options.toBundle());
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
            mHomePagerAdapter.notifyDataSetChanged();
        }
    }

    /* Listeners and other user actions*/
    private void logout() {
        Log.d(TAG, String.format("Logging out for user: %s",
                getCurrentUser().getUsername()));
        ParseUser.logOut();
        mDrawer.closeDrawers();
        launchLoginActivity();
    }

    private void deleteAccount() {
        Log.d(TAG, String.format("Deleting account for user: %s", getCurrentUser().getUsername()));
        User.deleteUserAndData(getCurrentUser());
        launchLoginActivity();
    }

    @Override
    public void onTripClick(String tripId, String tripTitle, boolean isOwner) {
        launchStoryActivity(tripId, tripTitle, isOwner);
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
        showUserProfile(pUser);
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
                showUserProfile(ParseUser.getCurrentUser());
                break;
            default: break;
        }
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

    @Override
    public void onPlaceSelected(Place place) {
        Log.i(TAG, "Place Selected: " + place.getName());
        GoogleAsyncHttpClient.getPlaceDetails(place.getId(), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject result = response.getJSONObject("result");
                    String photoReference = "";
                    if (result.has("photos")) {
                        JSONArray photos = result.getJSONArray("photos");
                        photoReference = photos.getJSONObject(0).getString("photo_reference");
                    }
                    String LatLng = String.format("%f,%f",place.getLatLng().latitude,place.getLatLng().longitude);
                    Intent createTrip = new Intent(HomeActivity.this, PlaceSuggestionActivity.class);
                    String destination = place.getName().toString();
                    String destinationId = place.getId();
                    if(!destination.isEmpty() && !LatLng.isEmpty()) {
                        createTrip.putExtra(PlaceSuggestionActivity.DESTINATION_NAME_ARG, destination);
                        createTrip.putExtra(PlaceSuggestionActivity.DESTINATION_ID_ARG, destinationId);
                        createTrip.putExtra(PlaceSuggestionActivity.DESTINATION_LAT_LONG_ARG, LatLng);
                        createTrip.putExtra(PlaceSuggestionActivity.DESTINATION_PHOTO_ARG, photoReference);
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this);
                        startActivityForResult(createTrip, CREATE_STORY_REQUEST, options.toBundle());
                    } else {
                        Toast.makeText(HomeActivity.this, "Please add a destination", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                // TODO: Show error snackbar
                Log.e("ERROR", t.toString());
            }
        });
    }

    @Override
    public void onError(Status status) {
        Log.e(TAG, "onError: Status = " + status.toString());
    }
}
