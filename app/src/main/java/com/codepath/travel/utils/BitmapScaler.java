package com.codepath.travel.utils;

import android.graphics.Bitmap;

/**
 * Created by rpraveen on 11/13/16.
 */

public class BitmapScaler
{
  // scale and keep aspect ratio
  public static Bitmap scaleToFitWidth(Bitmap b, int width)
  {
    float factor = width / (float) b.getWidth();
    return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
  }


  // scale and keep aspect ratio
  public static Bitmap scaleToFitHeight(Bitmap b, int height)
  {
    float factor = height / (float) b.getHeight();
    return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factor), height, true);
  }


  // scale and keep aspect ratio
  public static Bitmap scaleToFill(Bitmap b, int width, int height)
  {
    float factorH = height / (float) b.getWidth();
    float factorW = width / (float) b.getWidth();
    float factorToUse = (factorH > factorW) ? factorW : factorH;
    return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factorToUse),
    (int) (b.getHeight() * factorToUse), true);
  }


  // scale and don't keep aspect ratio
  public static Bitmap strechToFill(Bitmap b, int width, int height)
  {
    float factorH = height / (float) b.getHeight();
    float factorW = width / (float) b.getWidth();
    return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factorW),
    (int) (b.getHeight() * factorH), true);
  }
}