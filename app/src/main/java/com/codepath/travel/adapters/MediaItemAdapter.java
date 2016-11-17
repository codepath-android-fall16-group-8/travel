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
    private MediaItemListener mListener;

    public interface MediaItemListener {
        void photoOnClick(int position);
        void noteOnClick(int position);
    }

    public MediaItemAdapter(Context context, List<Media> placeMediaItems, MediaItemListener listener) {
        mPlaceMediaItems = placeMediaItems;
        mContext = context;
        mListener = listener;
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
        Media mediaItem = mPlaceMediaItems.get(position);

        switch (viewHolder.getItemViewType()) {
            case PHOTO:
                MediaHolder photoVh = (MediaHolder) viewHolder;
                configurePhotoViewHolder(photoVh, mediaItem);
                break;
            default:
                TextMediaViewHolder defaultVh = (TextMediaViewHolder) viewHolder;
                configureTextViewHolder(defaultVh, mediaItem);
                break;
        }
    }

    private void configurePhotoViewHolder(MediaHolder holder, Media mediaItem) {
        // populate the image here
        Glide.with(mContext)
                .load(mediaItem.getDataUrl())
                .into(holder.ivPlacePhoto);
        holder.ivPlacePhoto.setOnClickListener(v -> {
            mListener.photoOnClick(getRealPosition(mediaItem));
        });
    }

    private void configureTextViewHolder(TextMediaViewHolder holder, Media mediaItem) {
        holder.tvNoteMedia.setText(mediaItem.getCaption());
        holder.tvNoteMedia.setOnClickListener(v -> {
            mListener.noteOnClick(getRealPosition(mediaItem));
        });
    }

    @Override
    public int getItemCount() {
        return mPlaceMediaItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mPlaceMediaItems.get(position).getType().ordinal();
    }

    // Needed in order to get the correct position of items
    private int getRealPosition(Media media){
        return mPlaceMediaItems.indexOf(media);
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
