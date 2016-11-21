package com.codepath.travel.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.travel.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by aditikakadebansal on 11/18/16.
 * ViewHolder class for user.
 */
public class UserViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.ivProfilePhoto) ImageView ivProfilePhoto;
    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.ivFollowUser) ImageView ivFollowUser;

    public UserViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public ImageView getProfilePhoto() {
        return this.ivProfilePhoto;
    }

    public TextView getTvUsername() {
        return this.tvUsername;
    }

    public ImageView getIvFollowUser() { return this.ivFollowUser; }
}

