package com.codepath.travel.activities;

import static com.codepath.travel.Constants.IS_STORY_PLACE;
import static com.codepath.travel.Constants.PLACE_ID_ARG;
import static com.codepath.travel.Constants.PLACE_NAME_ARG;
import static com.codepath.travel.helper.DateUtils.formatDateRange;
import static com.codepath.travel.models.parse.User.setCoverPicUrl;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.codepath.travel.Constants;
import com.codepath.travel.R;
import com.codepath.travel.adapters.SwipeStoryPlaceAdapter;
import com.codepath.travel.fragments.dialog.ConfirmDeleteTripDialogFragment;
import com.codepath.travel.fragments.dialog.DateRangePickerFragment;
import com.codepath.travel.fragments.dialog.EditMediaDialogFragment;
import com.codepath.travel.fragments.dialog.DatePickerFragment;
import com.codepath.travel.helper.DateUtils;
import com.codepath.travel.listeners.DatePickerListener;
import com.codepath.travel.listeners.DateRangePickerListener;
import com.codepath.travel.models.parse.Media;
import com.codepath.travel.models.parse.StoryPlace;
import com.codepath.travel.models.parse.Trip;
import com.daimajia.swipe.util.Attributes;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class StoryActivity extends AppCompatActivity implements
        SwipeStoryPlaceAdapter.StoryPlaceListener,
        EditMediaDialogFragment.EditMediaListener,
        ConfirmDeleteTripDialogFragment.DeleteTripListener,
        DateRangePickerListener,
        DatePickerListener {

    private static final String TAG = StoryActivity.class.getSimpleName();

    // views
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tvTripDates) TextView tvTripDates;
    @BindView(R.id.cbShare) AppCompatCheckBox cbShare;
    @BindView(R.id.tvShare) TextView tvShare;
    @BindView(R.id.rvStoryPlaces) RecyclerView rvStoryPlaces;

    // member variables
    private ArrayList<StoryPlace> mStoryPlaces;
    private SwipeStoryPlaceAdapter mAdapter;
    private int mMediaLauncherStoryIndex;
    private String mTripID;
    private Trip mTrip;
    private String mTripTitle;
    private String mPhotoURL;
    private int mCheckinIndex;

    // flags for story place view state
    private boolean isOwner;
    private int datesRelation; // PAST, NOW, or FUTURE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        ButterKnife.bind(this);

        mTripTitle = getIntent().getStringExtra(Constants.TRIP_TITLE_ARG);
        toolbar.setTitle(mTripTitle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        isOwner = getIntent().getBooleanExtra(Constants.IS_OWNER_ARG, false);

        mTripID = getIntent().getStringExtra(Constants.TRIP_ID_ARG);
        Trip.getTripForObjectId(mTripID, (trip, e) -> {
            if (e == null) {
                mTrip = trip;
                Date start = trip.getStartDate();
                Date end = trip.getEndDate();
                datesRelation = DateUtils.todayInRange(start, end);
                if (start != null && end !=null) {
                    tvTripDates.setText(DateUtils.formatDateRange(this, start, end));
                }
                tvTripDates.setOnClickListener(
                        v -> launchDateRangePickerDialog(mTrip.getStartDate(), mTrip.getEndDate()));
                setUpRecyclerView();
                getPlacesInTrip();
                setupSharedCheckbox();
            } else {
                Log.d(TAG, String.format("Failed to get trip for id %s", mTrip));
            }

        });
    }

    private void setupSharedCheckbox() {
        // only display for logged in user
        if (isOwner) {
            tvShare.setVisibility(View.VISIBLE);
            cbShare.setVisibility(View.VISIBLE);
            cbShare.setChecked(mTrip.isShared());
            cbShare.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Log.d(TAG, String.format("Sharing: %s", isChecked));
                mTrip.setShared(isChecked);
                mTrip.saveInBackground();
            });
        } else {
            tvShare.setVisibility(View.GONE);
            cbShare.setVisibility(View.GONE);
        }
    }

    private void setUpRecyclerView() {
        mStoryPlaces = new ArrayList<>();
        mAdapter = new SwipeStoryPlaceAdapter(this, mStoryPlaces, isOwner, datesRelation);
        mAdapter.setMode(Attributes.Mode.Single);
        rvStoryPlaces.setAdapter(mAdapter);
        rvStoryPlaces.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getPlacesInTrip() {
        Trip.getPlacesForTripId(mTripID, (places, se) -> {
            if (se == null) {
                mStoryPlaces.addAll(places);
                mAdapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, String.format("Failed getPlacesInTrip: %s", se.getMessage()));
            }
        });
    }

    /* Camera and Photo helpers */
    private void compressAndSaveImage(Bitmap selectedImage) {

        // scaling down for quick upload - may need a backend service to scale and keep mutiple
        // sizes for the image
        //Bitmap resizedBitmap = BitmapScaler.scaleToFill(selectedImage, 500, 120);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] image = stream.toByteArray();

        ParseFile newImage = new ParseFile(image);
        newImage.saveInBackground((ParseException e) -> {
            if (e != null) {
                Log.d("error", e.toString());
            }
            Media media = new Media(mStoryPlaces.get(mMediaLauncherStoryIndex), Media.Type.PHOTO);
            media.setDataUrl(newImage.getUrl());
            media.saveInBackground((ParseException me) -> {
                if (me != null) {
                    Log.d("error", me.toString());
                } else {
                    mAdapter.notifyItemChanged(mMediaLauncherStoryIndex);
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

    /* Navigation */
    private void launchStoryCollageActivity() {
        Intent intent = new Intent(StoryActivity.this, StoryCollageActivity.class);
        intent.putExtra(Constants.TRIP_ID_ARG, mTripID);
        intent.putExtra(Constants.TRIP_TITLE_ARG, mTripTitle);
        startActivity(intent);
    }

    private void launchPlaceDetailActivity(StoryPlace storyPlace) {
        Intent placeDetail = new Intent(this, PlaceDetailActivity.class);
        placeDetail.putExtra(PLACE_ID_ARG, storyPlace.getPlaceId());
        placeDetail.putExtra(PLACE_NAME_ARG, storyPlace.getName());
        placeDetail.putExtra(IS_STORY_PLACE, true);
        startActivity(placeDetail);
    }

    private void launchMediaDialogFragment(int position, String mediaId,
            String caption, String data) {
        EditMediaDialogFragment fragment = EditMediaDialogFragment.newInstance(position,
                mStoryPlaces.get(position).getName(), mediaId, caption, data, isOwner);
        fragment.show(getSupportFragmentManager(), "editMediaDialogFragment");
    }

    private void launchDatePickerDialog(Date date, Date minDate, Date maxDate) {
        FragmentManager fm = getSupportFragmentManager();
        DatePickerFragment dpf = DatePickerFragment.newInstance(date, minDate, maxDate);
        dpf.show(fm, "DatePickerDialog");
    }

    private void launchDateRangePickerDialog(Date startDate, Date endDate) {
        FragmentManager fm = getSupportFragmentManager();
        DateRangePickerFragment drpf = DateRangePickerFragment.newInstance(startDate, endDate);
        drpf.show(fm, "DateRangePickerDialog");
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    public void launchCameraActivity(int position) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mPhotoURL = "placeMedia" + '_' + timeStamp + ".jpg";
        mMediaLauncherStoryIndex = position;
        Intent startCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startCamera.putExtra(
                MediaStore.EXTRA_OUTPUT, getPhotoFileUri(mPhotoURL)
        );
        if (startCamera.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(startCamera, Constants.START_CAMERA_REQUEST_CODE);
        }
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void launchGalleryActivity(int position) {
        mMediaLauncherStoryIndex = position;
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

    private void showConfirmDeleteDialog() {
        ConfirmDeleteTripDialogFragment fragment =
                ConfirmDeleteTripDialogFragment.newInstance();
        fragment.show(getSupportFragmentManager(), "confirmDeleteTripDialogFragment");
    }

    /* Listeners */
    @Override
    public void cameraOnClick(int position) {
        launchCameraActivity(position);
    }

    @Override
    public void galleryOnClick(int position) {
        launchGalleryActivity(position);
    }

    @Override
    public void noteOnClick(int position) {
        launchMediaDialogFragment(position, null, null, null);
    }

    @Override
    public void onStoryPlaceDelete(int position) {
        Log.d(TAG, String.format("delete story at pos: %d", position));
        StoryPlace storyPlace = mStoryPlaces.get(position);
        storyPlace.deleteWithMedia();
        // TODO: show confirm dialog
    }

    @Override
    public void onStoryPlaceInfo(int position) {
        StoryPlace storyPlace = mStoryPlaces.get(position);
        Log.d(TAG, String.format("show place at pos: %d, %s, %s", position, storyPlace.getName(), storyPlace.getPlaceId()));
        launchPlaceDetailActivity(storyPlace);
    }

    @Override
    public void mediaOnClick(Media media, int mPos, int storyPos) {
        Log.d(TAG, String.format("mediaOnClick, mPos %d, storyPos %d", mPos, storyPos));
        launchMediaDialogFragment(storyPos, media.getObjectId(), media.getCaption(),
                    media.getDataUrl());
    }

    @Override
    public void onSaveCaption(int storyPos, String caption, String mediaId) {
        if (mediaId == null) { // new note
            Media mediaItem = new Media(mStoryPlaces.get(storyPos), Media.Type.TEXT);
            saveMediaNote(mediaItem, caption, storyPos);
        } else { // edited media
            Media.getMediaForObjectId(mediaId, (mediaItem, e) -> {
                if (e == null) {
                    saveMediaNote(mediaItem, caption, storyPos);
                } else {
                    Log.d(TAG, String.format("Get media error: %s", e.toString()));
                }
            });
        }
    }

    private void saveMediaNote(Media mediaItem, String caption, int storyPos) {
        mediaItem.setCaption(caption);
        mediaItem.saveInBackground((ParseException e) -> {
            if (e == null) {
                mAdapter.notifyItemChanged(storyPos);
            } else {
                Log.d(TAG, String.format("Save media error: %s", e.toString()));
            }
        });
    }

    @Override
    public void onDeleteMedia(int storyPos, String mediaId) {
        if (mediaId != null) {
            Media.getMediaForObjectId(mediaId, (mediaItem, e) -> {
                if (e == null) {
                    mediaItem.deleteInBackground(e1 -> mAdapter.notifyItemChanged(storyPos));
                } else {
                    Log.d(TAG, String.format("Get media for delete error: %s", e.toString()));
                }
            });
        }
    }

    @Override
    public void onSetStoryPlaceCoverPhoto(int storyPos, String coverUrl) {
        StoryPlace storyPlace = mStoryPlaces.get(storyPos);
        storyPlace.setPhotoUrl(coverUrl);
        storyPlace.saveInBackground();
    }

    @Override
    public void onSetTripCoverPhoto(String coverUrl) {
        mTrip.setCoverPicUrl(coverUrl);
        mTrip.saveInBackground();
    }

    @Override
    public void onSetUserCoverPhoto(String coverUrl) {
        ParseUser user = ParseUser.getCurrentUser();
        setCoverPicUrl(user, coverUrl);
        user.saveInBackground();
    }

    @Override
    public void onDeleteTrip() {
        Trip.deleteTripForId(mTripID);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void checkinOnClick(int position, Date checkinDate) {
        mCheckinIndex = position;
        launchDatePickerDialog(checkinDate, mTrip.getStartDate(), mTrip.getEndDate());
    }

    @Override
    public void onDateSet(Calendar date) {
        StoryPlace storyPlace = mStoryPlaces.get(mCheckinIndex);
        storyPlace.setCheckinTime(date.getTime());
        storyPlace.saveInBackground(e -> {
            if (e == null) {
                mAdapter.notifyItemChanged(mCheckinIndex);
            }
        });
    }

    @Override
    public void onDateRangeSet(Calendar startDate, Calendar endDate) {
        String formattedDateString = formatDateRange(this, startDate.getTime(), endDate.getTime());
        tvTripDates.setText(formattedDateString);
        mTrip.setStartDate(startDate.getTime());
        mTrip.setEndDate(endDate.getTime());
        mTrip.saveInBackground();
    }

    /* Toolbar */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_story, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!isOwner) {
            // don't show delete menu option unless this trip belongs to the logged in user
            menu.removeItem(R.id.miDelete);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
//        } else if (id == R.id.miMap) {
//            Toast.makeText(this, "TODO: show map!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.miCollage) {
            launchStoryCollageActivity();
        } else if (id == R.id.miDelete) {
            showConfirmDeleteDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
