package com.codepath.travel.callbacks;

import com.parse.ParseException;

/**
 * Created by rpraveen on 11/19/16.
 */

public interface ImageUploadCallback {
  void onImageUploadSuccess(String uploadedImageURL);
  void onImageUploadError(ParseException imageUploadError);
}
