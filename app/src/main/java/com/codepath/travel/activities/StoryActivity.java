package com.codepath.travel.activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.travel.R;
import com.codepath.travel.adapters.StoryArrayAdapter;
import com.codepath.travel.fragment.dialog.ComposeNoteDialogFragment;
import com.codepath.travel.helper.OnStartDragListener;
import com.codepath.travel.helper.SimpleItemTouchHelperCallback;
import com.codepath.travel.models.Media;
import com.codepath.travel.models.StoryPlace;
import com.codepath.travel.models.Trip;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class StoryActivity extends AppCompatActivity implements OnStartDragListener,
        StoryArrayAdapter.StoryPlaceMediaClickListener,
        ComposeNoteDialogFragment.ComposeNoteListener {

    public final String APP_TAG = "TravelTrails";
    public static final int START_CAMERA_REQUEST_CODE = 123;
    public static final int PICK_IMAGE_FROM_GALLERY_CODE = 456;

    // activity intent args
    public static final String TRIP_POS_ARG = "trip_pos";
    public static final String TRIP_ID_ARG = "trip_id";
    public static final String TRIP_TITLE_ARG = "trip_title";

    // views
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rvStoryPlaces) RecyclerView rvStoryPlaces;

    // member variables
    private ArrayList<StoryPlace> mStoryPlaces;
    private StoryArrayAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;

    private int mMediaLauncherStoryIndex;
    private String mTripID;
    private String mPhotoURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String tripTitle = getIntent().getStringExtra(TRIP_TITLE_ARG);
        toolbar.setTitle(
                getApplicationContext().getResources().
                        getString(R.string.toolbar_title_story) + " " + tripTitle);

        mTripID = getIntent().getStringExtra(TRIP_ID_ARG);
        setUpRecyclerView();
        getPlacesInTrip();
    }

    private void getPlacesInTrip() {
        Trip.getPlaces(mTripID, new FindCallback<StoryPlace>() {
                @Override
                public void done(List<StoryPlace> places, ParseException se) {
                    if (se == null) {
                        mStoryPlaces.addAll(places);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("getPlacesInTrip", String.format("Failed: %s", se.getMessage()));
                    }
                }
            });
    }

    private void setUpRecyclerView() {
        mStoryPlaces = new ArrayList<>();
        mAdapter = new StoryArrayAdapter(this, this, mStoryPlaces);
        rvStoryPlaces.setHasFixedSize(true);
        rvStoryPlaces.setAdapter(mAdapter);
        rvStoryPlaces.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvStoryPlaces);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
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
                }
                mAdapter.notifyDataSetChanged();
            });
        });
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mMediaLauncherStoryIndex = position;
        Intent startGallery =
            new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (startGallery.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(startGallery, PICK_IMAGE_FROM_GALLERY_CODE);
        }
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

    private void launchComposeNoteDialogFragment(int position) {
        ComposeNoteDialogFragment fragment = ComposeNoteDialogFragment.newInstance(position,
                mStoryPlaces.get(position).getName());
        fragment.show(getSupportFragmentManager(), "composeNoteDialogFragment");
    }

    /* Navigation */
    private void launchStoryCollageActivity() {
        Intent intent = new Intent(StoryActivity.this, StoryCollageActivity.class);
        intent.putExtra(TRIP_ID_ARG, mTripID);
        startActivity(intent);
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
        launchComposeNoteDialogFragment(position);
    }

    @Override
    public void reviewOnClick(int position) {
        Toast.makeText(this, "review! " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onComposeSave(int position, String noteText) {
        Media media = new Media(mStoryPlaces.get(position), Media.Type.TEXT);
        media.setCaption(noteText);
        media.saveInBackground((ParseException e) -> {
            if (e != null) {
                Log.d("error", e.toString());
            }
            mAdapter.notifyDataSetChanged();
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
            // TODO: add confirm dialog
            Trip.deleteTrip(mTripID);
            Intent data = new Intent();
            data.putExtra(TRIP_POS_ARG, getIntent().getIntExtra(TRIP_POS_ARG, -1));
            setResult(RESULT_OK, data);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
