package com.codepath.travel.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.travel.R;
import com.codepath.travel.activities.ProfileViewActivity;
import com.codepath.travel.helper.ImageUtils;
import com.codepath.travel.models.parse.User;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aditikakadebansal on 11/18/16.
 * Adapter for search Users.
 */
public class UsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ParseUser> mUsers;
    private Context mContext;

    public UsersAdapter(Context context, ArrayList<ParseUser> users) {
        this.mUsers = users;
        this.mContext = context;
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
        if (User.getProfilePicUrl(user) != null) {
            ImageUtils.loadImageCircle(
            ivProfilePhoto,
            User.getProfilePicUrl(user),
            R.drawable.com_facebook_profile_picture_blank_portrait
            );
        }

        ivProfilePhoto.setOnClickListener((View v) -> {
            Intent viewProfile = new Intent(mContext, ProfileViewActivity.class);
            viewProfile.putExtra(ProfileViewActivity.USER_ID, user.getObjectId());
            mContext.startActivity(viewProfile);
        });

        TextView tvUsername = viewHolder.getTvUsername();
        tvUsername.setText(user.getUsername());

        ImageView ivFollowUser = viewHolder.getIvFollowUser();

        ivFollowUser.setImageResource(0);
        if(user.hasSameId(ParseUser.getCurrentUser())) {
            //Cannot follow myself
            ivFollowUser.setVisibility(View.GONE);
            return;
        }

        // make query to check following relation
        User.queryIsFollowing(ParseUser.getCurrentUser(), user, (List<ParseUser> objects, ParseException e) -> {
            // successfull query with size > 0 indicates we have a relation
            if (e == null && objects.size() > 0) {
                setFollowRelationImageResource(ivFollowUser, user, R.drawable.ic_person_friend);
                return;
            }
            setFollowRelationImageResource(ivFollowUser, user, R.drawable.ic_person_add);
        });
    }

    private void setFollowRelationImageResource(ImageView imageView, ParseUser user, int resource) {
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(resource);
        if (resource == R.drawable.ic_person_friend) {
            setUnFollowListener(imageView, user);
            return;
        }
        setFollowListener(imageView, user);
    }

    private void setUnFollowListener(ImageView imageView, ParseUser otherUser) {
        imageView.setOnClickListener((View v) -> {
            User.unFollow(ParseUser.getCurrentUser(), otherUser, (ParseException e) -> {
                if (e == null) {
                    setFollowRelationImageResource(imageView, otherUser, R.drawable.ic_person_add);
                }
            });
        });
    }

    private void setFollowListener(ImageView imageView, ParseUser otherUser) {
        imageView.setOnClickListener((View v) -> {
            User.follow(ParseUser.getCurrentUser(), otherUser, (ParseException e) -> {
                if (e == null) {
                    setFollowRelationImageResource(imageView, otherUser, R.drawable.ic_person_friend);
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return this.mUsers.size();
    }
}


