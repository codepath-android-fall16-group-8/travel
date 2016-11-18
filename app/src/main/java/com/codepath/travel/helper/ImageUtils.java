package com.codepath.travel.helper;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Helpers for image loading.
 */
public class ImageUtils {
    public static void loadImage(ImageView imageView, String imageUrl, int placeholder) {
        imageView.setImageResource(0);
        if (!TextUtils.isEmpty(imageUrl)) {
            Glide.with(imageView.getContext()).load(imageUrl)
                    .placeholder(placeholder)
                    .centerCrop()
                    .bitmapTransform(new RoundedCornersTransformation(imageView.getContext(), 5, 0))
                    .into(imageView);
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