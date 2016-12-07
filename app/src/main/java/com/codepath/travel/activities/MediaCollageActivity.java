package com.codepath.travel.activities;

import static android.R.attr.data;

import static com.codepath.travel.helper.ImageUtils.loadImage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.travel.R;
import com.codepath.travel.adapters.CollageAdapter;
import com.codepath.travel.callbacks.ParseQueryCallback;
import com.codepath.travel.helper.ItemClickSupport;
import com.codepath.travel.decoration.SpacesItemDecoration;
import com.codepath.travel.models.parse.Media;
import com.codepath.travel.models.parse.StoryPlace;;
import com.codepath.travel.models.parse.User;
import com.codepath.travel.net.GoogleAsyncHttpClient;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Activity for displaying the media associated with a storyplace.
 */
public class MediaCollageActivity extends BaseActivity {
    private static final int GRID_NUM_COLUMNS = 2;
    private static final int GRID_SPACE_SIZE = 5;

    // intent args
    public static final String STORY_PLACE_ID_ARG = "story_place_id";
    public static final String STORY_PLACE_NAME_ARG = "story_place_name";
    public static final String STORY_PLACE_COVER_ARG = "story_place_cover_url";
    public static final String STORY_PLACE_CHECKIN_ARG = "story_place_checkin";
    public static final String STORY_PLACE_RATING_ARG = "story_place_rating";
    public static final String USER_ID_ARG = "user_id";
    public static final String IS_OWNER_ARG = "is_owner";

    // views
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.ivBackDrop) ImageView ivBackdrop;
    @BindView(R.id.ivUserPic) ImageView ivUserPic;
    @BindView(R.id.tvCheckinDate) TextView tvCheckinDate;
    @BindView(R.id.rbUserRating) RatingBar rbUserRating;
    @BindView(R.id.rvMediaItems) RecyclerView rvMediaItems;

    // variables
    private String mStoryPlaceId;
    private String mStoryPlaceName;
    private String mUserId;
    private boolean isOwner;
    private ArrayList<Media> mMediaItems;
    private CollageAdapter mCollageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_collage);
        setupWindowAnimationsEnterBottom();
        initializeCommonViews();
        mStoryPlaceName = getIntent().getStringExtra(STORY_PLACE_NAME_ARG);
        setActionBarTitle(mStoryPlaceName);

        mStoryPlaceId = getIntent().getStringExtra(STORY_PLACE_ID_ARG);
        mUserId = getIntent().getStringExtra(USER_ID_ARG);
        isOwner = getIntent().getBooleanExtra(IS_OWNER_ARG, false);

        mMediaItems = new ArrayList<>();
        StoryPlace.getMediaForStoryPlaceId(mStoryPlaceId, (mediaItems, e) -> {
            if (e == null) {
                mMediaItems.addAll(mediaItems);
                mCollageAdapter = new CollageAdapter(this, mMediaItems);
                setUpRecyclerView();
            } else {
                Log.d("story fetch failed", e.toString());
            }
        });
    }

    private void setupViews(Intent data) {
        // setup collapsing toolbar
        collapsingToolbar.setTitle(mStoryPlaceName);
        collapsingToolbar.setExpandedTitleColor(Color.WHITE);
        // backdrop
        String coverUrl = data.getStringExtra(STORY_PLACE_COVER_ARG);
        String realCoverUrl = GoogleAsyncHttpClient.getPlacePhotoUrl(coverUrl);
        loadImage(ivBackdrop, realCoverUrl);
        // user profile photo
        User.getUserByID(mUserId, new ParseQueryCallback<ParseUser>() {
            @Override
            public void onQuerySuccess(ParseUser data) {
                loadImage(ivUserPic, User.getCoverPicUrl(data));
            }

            @Override
            public void onQueryError(ParseException e) {
                showError("Error fetching user");
            }
        });

        // show checkin date & rating
        String checkinDate = data.getStringExtra(STORY_PLACE_CHECKIN_ARG);
        if (checkinDate != null) {
            tvCheckinDate.setText(checkinDate);
            double rating = data.getDoubleExtra(STORY_PLACE_RATING_ARG, 0.0);
            rbUserRating.setRating((float) rating);
            tvCheckinDate.setVisibility(View.VISIBLE);
            rbUserRating.setVisibility(View.VISIBLE);
        }
    }

    private void setUpRecyclerView() {
        this.rvMediaItems.setAdapter(this.mCollageAdapter);

        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(GRID_NUM_COLUMNS,
                        StaggeredGridLayoutManager.VERTICAL);
        this.rvMediaItems.setLayoutManager(gridLayoutManager);
        ItemClickSupport.addTo(this.rvMediaItems).setOnItemClickListener(
                (recyclerView, position, v) -> seeMore(position)
        );
        SpacesItemDecoration decoration = new SpacesItemDecoration(GRID_SPACE_SIZE);
        this.rvMediaItems.addItemDecoration(decoration);
    }

    private void seeMore(int position) {
        Media media = this.mMediaItems.get(position);
        // Todo:
        Toast.makeText(this, String.format("Todo: %s, %s, %s", media.getType(), media.getCaption(), media.getDataUrl()),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_collage, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
