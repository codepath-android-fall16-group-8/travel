package com.codepath.travel.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.travel.R;
import com.codepath.travel.models.Media;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter for media items.
 */
public class MediaItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TEXT = 0;
    private final int REVIEW = 1;
    private final int PHOTO = 2;
    private final int VIDEO = 3;

    private List<Media> mPlaceMediaItems;
    private Context mContext;

    public MediaItemAdapter(Context context, List<Media> placeMediaItems) {
        mPlaceMediaItems = placeMediaItems;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case PHOTO:
                View photoView = inflater.inflate(R.layout.item_media, parent, false);
                viewHolder = new MediaHolder(photoView);
                break;
            default:
                View defaultView = inflater.inflate(R.layout.item_media_text, parent, false);
                viewHolder = new TextMediaViewHolder(defaultView);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case PHOTO:
                MediaHolder photoVh = (MediaHolder) viewHolder;
                configurePhotoViewHolder(photoVh, position);
                break;
            default:
                TextMediaViewHolder defaultVh = (TextMediaViewHolder) viewHolder;
                configureTextViewHolder(defaultVh, position);
                break;
        }
    }

    private void configurePhotoViewHolder(MediaHolder holder, int position) {
        Media mediaItem = mPlaceMediaItems.get(position);
        // populate the image here
        Glide.with(mContext)
                .load(mediaItem.getDataUrl())
                .into(holder.ivPlacePhoto);
    }

    private void configureTextViewHolder(TextMediaViewHolder holder, int position) {
        Media mediaItem = mPlaceMediaItems.get(position);
        holder.tvNoteMedia.setText(mediaItem.getCaption());
    }

    @Override
    public int getItemCount() {
        return mPlaceMediaItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mPlaceMediaItems.get(position).getType().ordinal();
    }

    class MediaHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivPlacePhoto) ImageView ivPlacePhoto;
        public MediaHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class TextMediaViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvNoteMedia) TextView tvNoteMedia;
        public TextMediaViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
