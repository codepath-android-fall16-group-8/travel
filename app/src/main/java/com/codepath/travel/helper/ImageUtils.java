package com.codepath.travel.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.codepath.travel.callbacks.ImageUploadCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.io.ByteArrayOutputStream;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Helpers for image loading.
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

    public static void loadImage(ImageView imageView, String imageUrl, int placeholder, ProgressBar processingView) {
        imageView.setImageResource(0);
        if (!TextUtils.isEmpty(imageUrl)) {
            Glide.with(imageView.getContext()).load(imageUrl)
                    .placeholder(placeholder)
                    .listener(new RequestListener<String, GlideDrawable>() {

                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            if (processingView != null) {
                                processingView.setVisibility(View.GONE);
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            if (processingView != null) {
                                processingView.setVisibility(View.GONE);
                            }
                            return false;
                        }
                    })
                    .centerCrop()
                    .bitmapTransform(new RoundedCornersTransformation(imageView.getContext(), 5, 0))
                    .into(imageView);
        }
    }

    public static void loadBackgroundImage(RelativeLayout relativeLayout, String imageUrl, int placeholder, ProgressBar processingView) {
        if (!TextUtils.isEmpty(imageUrl)) {
            Context context = relativeLayout.getContext();
            Glide.with(context).load(imageUrl)
                    .asBitmap()
                    .placeholder(placeholder)
                    .centerCrop()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            if (processingView != null) {
                                processingView.setVisibility(View.GONE);
                            }
                            Drawable drawable = new BitmapDrawable(context.getResources(), resource);
                            relativeLayout.setBackground(drawable);
                        }
                    });
        }
    }

    public static void loadImageCircle(ImageView imageView, String imageUrl, int placeholder) {
        imageView.setImageResource(0);
        if (!TextUtils.isEmpty(imageUrl)) {
            Context context = imageView.getContext();
            Glide.with(context).load(imageUrl)
                    .placeholder(placeholder)
                    .fitCenter()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(imageView);
        }
    }

    public static void loadBackground(RelativeLayout relativeLayout, String imageUrl) {
        if (!TextUtils.isEmpty(imageUrl)) {
            Context context = relativeLayout.getContext();
            Glide.with(context).load(imageUrl).asBitmap().centerCrop().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Drawable drawable = new BitmapDrawable(context.getResources(), resource);
                    relativeLayout.setBackground(drawable);
                }
            });
        }
    }
}