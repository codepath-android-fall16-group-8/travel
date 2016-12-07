package com.codepath.travel.activities;

import static com.codepath.travel.helper.ImageUtils.loadImage;
import static com.codepath.travel.models.parse.User.setCoverPicUrl;
import static com.codepath.travel.models.parse.User.setProfilePicUrl;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.travel.Constants;
import com.codepath.travel.R;
import com.codepath.travel.adapters.CollageAdapter;
import com.codepath.travel.callbacks.ParseQueryCallback;
import com.codepath.travel.fragments.dialog.EditMediaDialogFragment;
import com.codepath.travel.helper.ItemClickSupport;
import com.codepath.travel.decoration.SpacesItemDecoration;
import com.codepath.travel.models.parse.Media;
import com.codepath.travel.models.parse.StoryPlace;;
import com.codepath.travel.models.parse.Trip;
import com.codepath.travel.models.parse.User;
import com.codepath.travel.net.GoogleAsyncHttpClient;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * Activity for displaying the media associated with a storyplace.
 */
@RuntimePermissions
public class MediaCollageActivity extends BaseActivity implements
        EditMediaDialogFragment.EditMediaListener {

    private static final String TAG = MediaCollageActivity.class.getSimpleName();
    private static final int GRID_NUM_COLUMNS = 2;
    private static final int GRID_SPACE_SIZE = 5;
    private static final int STORY_COVER = 1;
    private static final int TRIP_COVER = 2;
    private static final int USER_COVER = 3;
    private static final int USER_PROFILE = 4;

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
    @BindView(R.id.tvNoMedia) TextView tvNoMedia;

    // variables
    private String mStoryPlaceId;
    private String mStoryPlaceName;
    private String mUserId;
    private boolean isOwner;
    private ArrayList<Media> mMediaItems;
    private CollageAdapter mCollageAdapter;
    // fetched
    private StoryPlace mStoryPlace;
    private ParseUser mUser;

    // camera/library variables
    private String mPhotoURL;

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
        setupViews(getIntent());
        setUpRecyclerView();

        // fetch storyplace + media
        StoryPlace.getStoryPlaceForObjectId(mStoryPlaceId, (storyPlace, e) -> {
            if (e == null) {
                mStoryPlace = storyPlace;
                Media.getMediaForStoryPlace(mStoryPlace, (mediaItems, e1) -> {
                    if (e1 == null) {
                        if (mediaItems.size() > 0) {
                            tvNoMedia.setVisibility(View.GONE);
                            mMediaItems.addAll(mediaItems);
                            rvMediaItems.setVisibility(View.VISIBLE);
                            mCollageAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.d(TAG, String.format("media fetch failed for storyplace %d: %s",
                                mStoryPlaceId,  e1.toString()));
                    }
                });
            } else {
                Log.d(TAG, String.format("storyplace fetch failed for id %d: %s",
                        mStoryPlaceId,  e.toString()));
            }
        });
    }

    /* View Setup */
    private void setupViews(Intent data) {
        // setup collapsing toolbar
        collapsingToolbar.setTitle(mStoryPlaceName);
        collapsingToolbar.setExpandedTitleColor(Color.WHITE);
        // backdrop
        String coverUrl = data.getStringExtra(STORY_PLACE_COVER_ARG);
        setImage(ivBackdrop, coverUrl);
        // user profile photo
        User.getUserByID(mUserId, new ParseQueryCallback<ParseUser>() {
            @Override
            public void onQuerySuccess(ParseUser data) {
                mUser = data;
                setImage(ivUserPic, User.getProfilePicUrl(data));
            }

            @Override
            public void onQueryError(ParseException e) {
                showError("Error fetching user");
            }
        });

        // show checkin date & rating
        String checkinDate = data.getStringExtra(STORY_PLACE_CHECKIN_ARG);
        if (checkinDate != null && !TextUtils.isEmpty(checkinDate)) {
            tvCheckinDate.setText(checkinDate);
            tvCheckinDate.setVisibility(View.VISIBLE);
        }
        double rating = data.getDoubleExtra(STORY_PLACE_RATING_ARG, 0.0);
        if (rating > 0.0) {
            rbUserRating.setRating((float) rating);
            rbUserRating.setVisibility(View.VISIBLE);
        }
    }

    private void setImage(ImageView imageView, String imageUrl) {
        String url = GoogleAsyncHttpClient.getPlacePhotoUrl(imageUrl);
        loadImage(imageView, url);
    }

    private void setUpRecyclerView() {
        mMediaItems = new ArrayList<>();
        mCollageAdapter = new CollageAdapter(this, mMediaItems);
        this.rvMediaItems.setAdapter(mCollageAdapter);

        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(GRID_NUM_COLUMNS,
                        StaggeredGridLayoutManager.VERTICAL);
        this.rvMediaItems.setLayoutManager(gridLayoutManager);
        ItemClickSupport.addTo(this.rvMediaItems).setOnItemClickListener(
                (recyclerView, position, v) -> onMediaClick(position)
        );
        SpacesItemDecoration decoration = new SpacesItemDecoration(GRID_SPACE_SIZE);
        this.rvMediaItems.addItemDecoration(decoration);
    }

    /* Navigation */
    private void launchMediaDialogFragment(int position) {
        Media mediaItem = null;
        if (position > -1) {
            mediaItem = mMediaItems.get(position);
        }
        EditMediaDialogFragment fragment = EditMediaDialogFragment.newInstance(
                mStoryPlaceName, position, mediaItem, isOwner);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
        fragment.show(getSupportFragmentManager(), "editMediaDialogFragment");
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    public void launchCameraActivity() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mPhotoURL = "placeMedia" + '_' + timeStamp + ".jpg";
        Intent startCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startCamera.putExtra(
                MediaStore.EXTRA_OUTPUT, getPhotoFileUri(mPhotoURL)
        );
        if (startCamera.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(startCamera, Constants.START_CAMERA_REQUEST_CODE);
        }
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void launchGalleryActivity() {
        Intent startGallery =
                new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (startGallery.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(startGallery, Constants.PICK_IMAGE_FROM_GALLERY_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == Constants.START_CAMERA_REQUEST_CODE) {
            // See code above
            Uri takenPhotoUri = getPhotoFileUri(mPhotoURL);
            Bitmap selectedImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
            compressAndSaveImage(selectedImage);
        } else if (resultCode == RESULT_OK && requestCode == Constants.PICK_IMAGE_FROM_GALLERY_CODE) {
            Uri photoURI = data.getData();
            try {
                Bitmap selectedImage =
                        MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
                compressAndSaveImage(selectedImage);
            } catch (Exception e) {
                Log.d("Gallery Image failed", e.toString());
            }
        }
    }

    /* User actions */
    public void onMediaClick(int pos) {
        Log.d(TAG, String.format("onMediaClick at pos %d", pos));
        launchMediaDialogFragment(pos);
    }

    /* Editing Listeners */
    @Override
    public void onSaveCaption(int position, String caption) {
        Media mediaItem = position < 0 ? new Media(mStoryPlace, Media.Type.TEXT) : mMediaItems.get(position);
        mediaItem.setCaption(caption);
        mediaItem.saveInBackground((ParseException e) -> {
            if (e == null) {
                if (position < 0) {
                    mMediaItems.add(mediaItem);
                }
                tvNoMedia.setVisibility(View.GONE);
                rvMediaItems.setVisibility(View.VISIBLE);
                mCollageAdapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, String.format("Save media error for id %s: %s",
                        mediaItem.getObjectId(), e.toString()));
            }
        });
    }

    @Override
    public void onDeleteMedia(int position) {
        Media mediaItem = mMediaItems.get(position);
        mediaItem.deleteInBackground(e -> {
            if (e == null) {
                mMediaItems.remove(mediaItem);
                mCollageAdapter.notifyDataSetChanged();
                if (mMediaItems.size() == 0) {
                    tvNoMedia.setVisibility(View.VISIBLE);
                    rvMediaItems.setVisibility(View.GONE);
                }
            } else {
                Log.d(TAG, String.format("Delete media error for id %s: %s",
                        mediaItem.getObjectId(), e.toString()));
            }
        });
    }

    @Override
    public void onSetPhoto(int position, int type) {
        String photoUrl = mMediaItems.get(position).getDataUrl();
        switch (type) {
            case STORY_COVER:
                setStoryPlaceCoverPhoto(photoUrl);
                break;
            case TRIP_COVER:
                setTripCoverPhoto(photoUrl);
                break;
            case USER_COVER:
                setUserCoverPhoto(photoUrl);
                break;
            case USER_PROFILE:
                setUserProfilePhoto(photoUrl);
                break;
            default:
                Log.e(TAG, String.format("Cannot set photo type for int %s, must be 1,2,3, or 4", type));
        }
    }

    public void setStoryPlaceCoverPhoto(String photoUrl) {
        mStoryPlace.setPhotoUrl(photoUrl);
        mStoryPlace.saveInBackground(e -> {
            if (e == null) {
                // update backdrop in toolbar
                setImage(ivBackdrop, photoUrl);
            } else {
                Log.d(TAG, String.format("Failed to set cover url %s for story %s: %s",
                        photoUrl, mStoryPlaceId, e.toString()));
            }
        });
    }

    public void setTripCoverPhoto(String photoUrl) {
        Trip trip = mStoryPlace.getTrip();
        trip.setCoverPicUrl(photoUrl);
        trip.saveInBackground(e -> {
            if (e != null) {
                Log.d(TAG, String.format("Failed to set cover url %s for trip %s: %s",
                        photoUrl, trip.getObjectId(), e.toString()));
            }
        });
    }

    public void setUserCoverPhoto(String photoUrl) {
        setCoverPicUrl(mUser, photoUrl);
        mUser.saveInBackground(e -> {
            if (e != null) {
                Log.d(TAG, String.format("Failed to set cover url %s for user %s: %s",
                        photoUrl, mUserId, e.toString()));
            }
        });
    }

    public void setUserProfilePhoto(String photoUrl) {
        setProfilePicUrl(mUser, photoUrl);
        mUser.saveInBackground(e -> {
            if (e == null) {
                // update user photo in toolbar
                setImage(ivUserPic, photoUrl);
            } else {
                Log.d(TAG, String.format("Failed to set profile photo url %s for user %s: %s",
                        photoUrl, mUserId, e.toString()));
            }
        });
    }

    /* Toolbar */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_collage, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!isOwner) {
            // don't show editing options unless the owner is viewing
            menu.removeItem(R.id.miCamera);
            menu.removeItem(R.id.miPhotoLibrary);
            menu.removeItem(R.id.miNote);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.miCamera:
                launchCameraActivity();
                return true;
            case R.id.miPhotoLibrary:
                launchGalleryActivity();
                return true;
            case R.id.miNote:
                launchMediaDialogFragment(-1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* Camera and Photo helpers */
    private void compressAndSaveImage(Bitmap selectedImage) {
        // scaling down for quick upload - may need a backend service to scale and keep multiple
        // sizes for the image
        //Bitmap resizedBitmap = BitmapScaler.scaleToFill(selectedImage, 500, 120);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] image = stream.toByteArray();

        ParseFile newImage = new ParseFile(image);
        newImage.saveInBackground((ParseException e) -> {
            if (e != null) {
                Log.d(TAG, e.toString());
            }
            Media media = new Media(mStoryPlace, Media.Type.PHOTO);
            media.setDataUrl(newImage.getUrl());
            media.saveInBackground((ParseException me) -> {
                if (me != null) {
                    Log.d("error", me.toString());
                } else {
                    mMediaItems.add(media);
                    tvNoMedia.setVisibility(View.GONE);
                    rvMediaItems.setVisibility(View.VISIBLE);
                    mCollageAdapter.notifyDataSetChanged();
                }
            });
        });
    }

    // Returns the Uri for a photo stored on disk given the fileName
    public Uri getPhotoFileUri(String fileName) {
        // Only continue if the SD Card is mounted
        if (isExternalStorageAvailable()) {
            // Get safe storage directory for photos
            // Use `getExternalFilesDir` on Context to access package-specific directories.
            // This way, we don't need to request external read/write runtime permissions.
            File mediaStorageDir = new File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES), Constants.APP_TAG);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                Log.d(Constants.APP_TAG, "failed to create directory");
            }

            // Return the file target for the photo based on filename
            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
        }
        return null;
    }

    // Returns true if external storage for photos is available
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }
}
