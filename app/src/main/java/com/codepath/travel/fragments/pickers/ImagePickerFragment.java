package com.codepath.travel.fragments.pickers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.codepath.travel.R;
import com.codepath.travel.callbacks.ImageUploadCallback;
import com.codepath.travel.utils.ImageUtils;
import com.parse.ParseException;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;

import static android.app.Activity.RESULT_OK;

/**
 * Created by rpraveen on 11/19/16.
 */

public class ImagePickerFragment extends Fragment {

  private static final int OPEN_GALLERY_CODE = 111;

  // arguments
  private static final String IMAGE_SRC = "image_source";

  // views
  @BindView(R.id.ivImageContainer) ImageView ivImageHolder;
  @BindView(R.id.ivImagePicker) ImageView ivImagePicker;
  @BindView(R.id.pbImageUploading) ProgressBar pbImageUploading;

  // Fragment action listeners
  public interface ImagePickerFragmentListener {
    void onImageUploadSuccess(String tag, String imageURL);
  }

  // member variables
  private ImagePickerFragmentListener mImagePickerListener;

  public static ImagePickerFragment newInstance(String imageSrc) {
    ImagePickerFragment imagePicker = new ImagePickerFragment();
    Bundle args = new Bundle();
    args.putString(IMAGE_SRC, imageSrc);
    imagePicker.setArguments(args);
    return imagePicker;
  }

  @Override
  public void onCreate(Bundle savedInstance) {
    super.onCreate(savedInstance);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstance) {
    View view = inflater.inflate(R.layout.fragment_generic_image_container, parent, false);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstance) {

    loadImage(getArguments().getString(IMAGE_SRC));

    // open gallery or camera here
    // just opening gallery for now
    ivImagePicker.setOnClickListener(v -> {
      launchGalleryActivity();
    });
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (!(context instanceof ImagePickerFragmentListener)) {
      throw new RuntimeException("Activity should implement ImagePickerFragmentListener");
    }
    mImagePickerListener = (ImagePickerFragmentListener) context;
  }

  public void loadImage(String imageSource) {
    // populate the image here
    Glide.with(getActivity())
    .load(imageSource)
    .listener(new RequestListener<String, GlideDrawable>() {
      @Override
      public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
        pbImageUploading.setVisibility(View.GONE);
        return false;
      }

      @Override
      public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
        pbImageUploading.setVisibility(View.GONE);
        return false;
      }
    })
    .into(ivImageHolder);
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == RESULT_OK && requestCode == OPEN_GALLERY_CODE) {
      Uri photoURI = data.getData();
      try {
        Bitmap selectedImage =
          MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoURI);
        uploadImage(selectedImage);
      } catch (Exception e) {
        Log.d("Image Picker error", e.toString());
      }
    }
  }

  private void uploadImage(Bitmap selectedImage) {
    pbImageUploading.setVisibility(View.VISIBLE);
    ImageUtils.compressAndSaveImage(selectedImage, new ImageUploadCallback() {
      @Override
      public void onImageUploadSuccess(String uploadedImageURL) {
        mImagePickerListener.onImageUploadSuccess(getTag(), uploadedImageURL);
      }

      @Override
      public void onImageUploadError(ParseException imageUploadError) {
        Toast.makeText(getActivity(), "Image Upload Failed", Toast.LENGTH_SHORT).show();
        pbImageUploading.setVisibility(View.INVISIBLE);
      }
    });
  }

  @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
  private void launchGalleryActivity() {
    Intent startGallery =
      new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    if (startGallery.resolveActivity(getActivity().getPackageManager()) != null) {
      startActivityForResult(startGallery, OPEN_GALLERY_CODE);
    }
  }
}
