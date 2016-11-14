package com.codepath.travel.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.travel.R;
import com.codepath.travel.models.Media;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rpraveen on 11/13/16.
 */

public class MediaItemAdapter extends RecyclerView.Adapter<MediaItemAdapter.MediaHolder> {

  private List<Media> mPlaceMediaItems;
  private Context mContext;

  public MediaItemAdapter(Context context, List<Media> placeMediaItems) {
    mPlaceMediaItems = placeMediaItems;
    mContext = context;
  }

  @Override
  public MediaItemAdapter.MediaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(mContext);
    return new MediaHolder(inflater.inflate(R.layout.item_media, parent, false));
  }

  @Override
  public void onBindViewHolder(final MediaItemAdapter.MediaHolder holder, int position) {
    Media mediaItem = mPlaceMediaItems.get(position);
    // populate the image here
    Glide.with(mContext)
      .load(mediaItem.getDataUrl())
      .into(holder.ivPlacePhoto);
  }

  @Override
  public int getItemCount() {
    return mPlaceMediaItems.size();
  }

  class MediaHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.ivPlacePhoto) ImageView ivPlacePhoto;
    public MediaHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
