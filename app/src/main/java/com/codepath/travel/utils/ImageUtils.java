package com.codepath.travel.utils;

import android.graphics.Bitmap;

import com.codepath.travel.callbacks.ImageUploadCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.io.ByteArrayOutputStream;

/**
 * Created by rpraveen on 11/19/16.
 */

public class ImageUtils {

  // saves file and calls back to
  public static void compressAndSaveImage(Bitmap bitMapImage, ImageUploadCallback callback) {
    // scaling down for quick upload - may need a backend service to scale and keep mutiple
    // sizes for the image
    //Bitmap resizedBitmap = BitmapScaler.scaleToFill(selectedImage, 500, 120);
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    bitMapImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
    byte[] image = stream.toByteArray();

    ParseFile newImage = new ParseFile(image);
    newImage.saveInBackground((ParseException e) -> {
      if (e != null) {
        callback.onImageUploadError(e);
        return;
      }
      callback.onImageUploadSuccess(newImage.getUrl());
    });
  }
}
