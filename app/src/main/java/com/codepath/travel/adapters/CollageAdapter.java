package com.codepath.travel.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.travel.R;
import com.codepath.travel.adapters.viewholders.CollageItemViewHolder;
import com.codepath.travel.helper.ImageUtils;
import com.codepath.travel.models.parse.Media;

import java.util.List;

/**
 * Adapter for story place collage items.
 */
public class CollageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Media> mMediaItems;
    private Context mContext;

    private final int DEFAULT = 0;

    public CollageAdapter(Context context, List<Media> mediaItems) {
        this.mMediaItems = mediaItems;
        this.mContext = context;
    }

    private Context getContext() {
        return this.mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            default:
                View defaultView = inflater.inflate(R.layout.item_collage, parent, false);
                viewHolder = new CollageItemViewHolder(defaultView);
                break;
        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            default:
                CollageItemViewHolder vh = (CollageItemViewHolder) viewHolder;
                configureViewHolder(vh, position);
                break;
        }
    }

    private void configureViewHolder(CollageItemViewHolder viewHolder, int position) {
        Media media = this.mMediaItems.get(position);

        ImageView ivPhoto = viewHolder.getPhoto();
        ivPhoto.setImageResource(0);
        String photoUrl = media.getDataUrl();
        if (photoUrl != null && !TextUtils.isEmpty(photoUrl)) {
            ImageUtils.loadImage(ivPhoto, photoUrl);
            ivPhoto.setVisibility(View.VISIBLE);
        } else {
            ivPhoto.setVisibility(View.GONE);
        }
        TextView tvCaption = viewHolder.getCaption();
        String caption = media.getCaption();
        if (caption != null && !TextUtils.isEmpty(caption)) {
            tvCaption.setText(media.getCaption());
            tvCaption.setVisibility(View.VISIBLE);
        } else {
            tvCaption.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return this.mMediaItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return DEFAULT;
    }
}

