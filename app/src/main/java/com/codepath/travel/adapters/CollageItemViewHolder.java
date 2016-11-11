package com.codepath.travel.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.travel.R;

/**
 * ViewHolder class for story place collage items.
 */
public class CollageItemViewHolder extends RecyclerView.ViewHolder {
    private ImageView ivCover;
    private TextView tvName;

    public CollageItemViewHolder(View itemView) {
        super(itemView);
        this.ivCover = (ImageView) itemView.findViewById(R.id.ivCover);
        this.tvName = (TextView) itemView.findViewById(R.id.tvName);
    }

    public ImageView getCover() {
        return this.ivCover;
    }

    public void setCover(ImageView cover) {
        this.ivCover = cover;
    }

    public TextView getName() {
        return this.tvName;
    }

    public void setName(TextView name) {
        this.tvName = name;
    }
}

