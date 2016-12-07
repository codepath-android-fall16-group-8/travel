package com.codepath.travel.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.travel.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ViewHolder class for media collage items.
 */
public class CollageItemViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.ivPhoto) ImageView ivPhoto;
    @BindView(R.id.tvCaption) TextView tvCaption;

    public CollageItemViewHolder(View itemView) {
        super(itemView);
            ButterKnife.bind(this, itemView);
    }

    public ImageView getPhoto() {
        return this.ivPhoto;
    }

    public TextView getCaption() {
        return this.tvCaption;
    }
}

