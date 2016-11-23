package com.codepath.travel.activities;

import static com.codepath.travel.models.User.setCoverPicUrl;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.travel.R;
import com.codepath.travel.adapters.StoryArrayAdapter;
import com.codepath.travel.fragments.TripDatesFragment;
import com.codepath.travel.fragments.dialog.ConfirmDeleteTripDialogFragment;
import com.codepath.travel.fragments.dialog.EditMediaDialogFragment;
import com.codepath.travel.helper.DateUtils;
import com.codepath.travel.helper.OnStartDragListener;
import com.codepath.travel.helper.SimpleItemTouchHelperCallback;
import com.codepath.travel.models.Media;
import com.codepath.travel.models.StoryPlace;
import com.codepath.travel.models.Trip;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class StoryActivity extends AppCompatActivity implements OnStartDragListener,
        StoryArrayAdapter.StoryPlaceListener,
        DatePickerDialog.OnDateSetListener,
        EditMediaDialogFragment.EditMediaListener,
        ConfirmDeleteTripDialogFragment.DeleteTripListener,
        TripDatesFragment.TripDatesListener {

    public final String APP_TAG = "TravelTrails";
    public static final int START_CAMERA_REQUEST_CODE = 123;
    public static final int PICK_IMAGE_FROM_GALLERY_CODE = 456;

    // activity intent args
    public static final String TRIP_ID_ARG = "trip_id";
    public static final String TRIP_TITLE_ARG = "trip_title";

    private static final String TAG = StoryActivity.class.getSimpleName();

    // strings
    @BindString(R.string.toolbar_title_story) String toolbarTitle;

    // views
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.cbShare) AppCompatCheckBox cbShare;
    @BindView(R.id.rvStoryPlaces) RecyclerView rvStoryPlaces;

    // member variables
    private ArrayList<StoryPlace> mStoryPlaces;
    private StoryArrayAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;
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

        mTripTitle = getIntent().getStringExtra(TRIP_TITLE_ARG);
        toolbar.setTitle(String.format(toolbarTitle, mTripTitle));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTripID = getIntent().getStringExtra(TRIP_ID_ARG);
        Trip.getTripForObjectId(mTripID, (trip, e) -> {
            if (e == null) {
                mTrip = trip;
                isOwner = mTrip.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId());
                datesRelation = DateUtils.todayInRange(trip.getStartDate(), trip.getEndDate());
                setupTripDatesFragment();
                setUpRecyclerView();
                getPlacesInTrip();
                setupSharedCheckbox();
            } else {
                Log.d(TAG, String.format("Failed to get trip for id %s", mTrip));
            }

        });
    }

    private void setupTripDatesFragment() {
        // note that this is not using support v4 because the datepicker library doesn't support it
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.flContainer,
                TripDatesFragment.newInstance(mTrip.getStartDate(), mTrip.getEndDate()));
        ft.commit();
    }

    private void setupSharedCheckbox() {
        // only display for logged in user
        if (isOwner) {
            cbShare.setVisibility(View.VISIBLE);
            cbShare.setChecked(mTrip.isShared());
            cbShare.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Log.d(TAG, String.format("Sharing: %s", isChecked));
                mTrip.setShared(isChecked);
                mTrip.saveInBackground();
            });
        } else {
            cbShare.setVisibility(View.GONE);
        }
    }

    private void setUpRecyclerView() {
        mStoryPlaces = new ArrayList<>();
        mAdapter = new StoryArrayAdapter(this, this, mStoryPlaces, isOwner, datesRelation);
        rvStoryPlaces.setHasFixedSize(true);
        rvStoryPlaces.setAdapter(mAdapter);
        rvStoryPlaces.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvStoryPlaces);
    }

    private void getPlacesInTrip() {
        Trip.getPlaces(mTripID, (places, se) -> {
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
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                Log.d(APP_TAG, "failed to create directory");
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
        intent.putExtra(TRIP_ID_ARG, mTripID);
        intent.putExtra(TRIP_TITLE_ARG, mTripTitle);
        startActivity(intent);
    }

    private void launchComposeNoteDialogFragment(int position, String mediaId,
            String caption, String data) {
        EditMediaDialogFragment fragment = EditMediaDialogFragment.newInstance(position,
                mStoryPlaces.get(position).getName(), mediaId, caption, data);
        fragment.show(getSupportFragmentManager(), "editMediaDialogFragment");
    }

    private void launchDatePickerDialog(Date startDate, Date endDate) {
        FragmentManager fm = getFragmentManager();
        Calendar start = DateUtils.calendarFromDate(startDate);
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                StoryActivity.this,
                start.get(Calendar.YEAR),
                start.get(Calendar.MONTH),
                start.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setMinDate(start);
        dpd.setMaxDate(DateUtils.calendarFromDate(endDate));
        dpd.setOnCancelListener(dialogInterface -> mAdapter.notifyItemChanged(mCheckinIndex));
        dpd.show(fm, "CheckinDatePickerDialog");
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
            startActivityForResult(startCamera, START_CAMERA_REQUEST_CODE);
        }
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void launchGalleryActivity(int position) {
        mMediaLauncherStoryIndex = position;
        Intent startGallery =
                new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (startGallery.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(startGallery, PICK_IMAGE_FROM_GALLERY_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == START_CAMERA_REQUEST_CODE) {
            // See code above
            Uri takenPhotoUri = getPhotoFileUri(mPhotoURL);
            Bitmap selectedImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
            compressAndSaveImage(selectedImage);
        } else if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_FROM_GALLERY_CODE) {
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
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        Log.d("onStartDrag", "dragging...");
        mItemTouchHelper.startDrag(viewHolder);
    }

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
        launchComposeNoteDialogFragment(position, null, null, null);
    }

    @Override
    public void storyPlaceMoved(int fromPosition, int toPosition) {
        Log.d(TAG, String.format("story moved fromPos: %d, toPos: %d", fromPosition, toPosition));
        // TODO: save order in Parse
    }

    @Override
    public void storyPlaceDismissed(int position) {
        Log.d(TAG, String.format("dismissed story at pos: %d", position));
        StoryPlace storyPlace = mStoryPlaces.get(position);
        storyPlace.deleteWithMedia();
    }

    @Override
    public void mediaOnClick(Media media, int mPos, int storyPos) {
        Log.d(TAG, String.format("mediaOnClick, mPos %d, storyPos %d", mPos, storyPos));
        launchComposeNoteDialogFragment(storyPos, media.getObjectId(), media.getCaption(),
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
    public void onDelete(int storyPos, String mediaId) {
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
        Trip.deleteTrip(mTripID);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void tripDatesOnSet(Calendar startDate, Calendar endDate) {
        Log.d(TAG, String.format("Dates set: %s - %s", startDate.toString(), endDate.toString()));
        Trip.getTripForObjectId(mTripID, (trip, e) -> {
            trip.setStartDate(startDate.getTime());
            trip.setEndDate(endDate.getTime());
            trip.saveInBackground();
        });
    }

    @Override
    public void checkinOnClick(int position) {
        mCheckinIndex = position;
        launchDatePickerDialog(mTrip.getStartDate(), mTrip.getEndDate());
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, monthOfYear);
        date.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        StoryPlace storyPlace = mStoryPlaces.get(mCheckinIndex);
        storyPlace.setCheckinTime(date.getTime());
        storyPlace.saveInBackground(e -> {
            if (e == null) {
                mAdapter.notifyItemChanged(mCheckinIndex); // TODO: look into animate reorder
            }
        });
    }

    /* Toolbar */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_story, menu);

        return super.onCreateOptionsMenu(menu);
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
        } else if (id == R.id.miMap) {
            Toast.makeText(this, "TODO: show map!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.miCollage) {
            launchStoryCollageActivity();
        } else if (id == R.id.miDelete) {
            showConfirmDeleteDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
