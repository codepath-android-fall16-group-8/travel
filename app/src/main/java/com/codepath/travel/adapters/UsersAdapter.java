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
import com.codepath.travel.helper.ImageUtils;
import com.codepath.travel.models.User;
import com.parse.ParseUser;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.parse.ParseUser.getCurrentUser;

/**
 * Created by aditikakadebansal on 11/18/16.
 * Adapter for search Users.
 */
public class UsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int DEFAULT = 0;

    private ArrayList<ParseUser> mUsers;
    private ArrayList<ParseUser> mFollowingList;
    private Context mContext;
    private boolean showProfilePhoto;

    public UsersAdapter(Context context, ArrayList<ParseUser> users, ArrayList<ParseUser> mFollowingList,  boolean showProfilePhoto) {
        this.mUsers = users;
        this.mFollowingList = mFollowingList;
        this.mContext = context;
        this.showProfilePhoto = showProfilePhoto;
    }

    private Context getContext() {
        return this.mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View defaultView = inflater.inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(defaultView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        UserViewHolder vh = (UserViewHolder) viewHolder;
        configureViewHolder(vh, position);
    }

    private void configureViewHolder(UserViewHolder viewHolder, int position) {
        ParseUser user = this.mUsers.get(position);

        ImageView ivProfilePhoto = viewHolder.getProfilePhoto();
        if (showProfilePhoto) {
            ImageUtils.loadImageCircle(ivProfilePhoto, User.getProfilePicUrl(user),
                    R.drawable.com_facebook_profile_picture_blank_portrait);
        } else {
            ivProfilePhoto.setVisibility(View.GONE);
        }
        TextView tvUsername = viewHolder.getTvUsername();
        tvUsername.setText(user.getUsername());

        ImageView ivFollowUser = viewHolder.getIvFollowUser();

        ivFollowUser.setImageResource(0);
        if(user.hasSameId(ParseUser.getCurrentUser())) {
            //Cannot follow myself
            ivFollowUser.setVisibility(View.GONE);
        }else {
            if (isFollowingUser(user, mFollowingList)) {
                setFollowRelationImageResource(ivFollowUser, View.VISIBLE, R.drawable.ic_person_friend);
            } else {
                setFollowRelationImageResource(ivFollowUser, View.VISIBLE, R.drawable.ic_person_add);
            }
        }

        ivFollowUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isFollowingUser(user, mFollowingList)) {
                    setFollowRelationImageResource(ivFollowUser, View.VISIBLE, R.drawable.ic_person_add);
                    User.unFollow(ParseUser.getCurrentUser(), user);
                    mFollowingList.remove(user);
                }else {
                    setFollowRelationImageResource(ivFollowUser, View.VISIBLE, R.drawable.ic_person_friend);
                    User.follow(ParseUser.getCurrentUser(), user);
                    mFollowingList.add(user);
                }
            }
        });
    }

    private void setFollowRelationImageResource(ImageView imageView, int visibillity, int resource) {
        imageView.setVisibility(visibillity);
        imageView.setImageResource(resource);
    }

    private boolean isFollowingUser(ParseUser user, ArrayList<ParseUser> list) {
        //Check if userID is present in mfollowinglist
        for(ParseUser followingUser : mFollowingList) {
            if(user.hasSameId(followingUser)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return this.mUsers.size();
    }

    @Override
    public int getItemViewType(int position) {
        return DEFAULT;
    }

}


