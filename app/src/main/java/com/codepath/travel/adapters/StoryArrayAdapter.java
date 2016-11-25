package com.codepath.travel.adapters;

import static com.codepath.travel.helper.DateUtils.FUTURE;
import static com.codepath.travel.helper.DateUtils.PAST;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.travel.GoogleAsyncHttpClient;
import com.codepath.travel.R;
import com.codepath.travel.helper.DateUtils;
import com.codepath.travel.helper.ImageUtils;
import com.codepath.travel.helper.ItemTouchHelperAdapter;
import com.codepath.travel.helper.ItemTouchHelperViewHolder;
import com.codepath.travel.helper.OnStartDragListener;
import com.codepath.travel.models.Media;
import com.codepath.travel.models.ParseModelConstants;
import com.codepath.travel.models.StoryPlace;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter for a trip's story places.
 */
public class StoryArrayAdapter extends RecyclerView.Adapter<StoryArrayAdapter.StoryViewHolder>
        implements ItemTouchHelperAdapter {

    private List<StoryPlace> mStoryPlaces;
    private final OnStartDragListener mDragStartListener;
    private Context mContext;
    private StoryPlaceListener listener;

    private boolean isOwner;
    private int datesRelation; // PAST, NOW, or FUTURE

    public interface StoryPlaceListener {
        void cameraOnClick(int position);
        void galleryOnClick(int position);
        void noteOnClick(int position);
        void checkinOnClick(int position, Date checkinDate);
        void storyPlaceMoved(int fromPosition, int toPosition);
        void storyPlaceDismissed(int position);
        void mediaOnClick(Media media, int mPos, int storyPos);
    }

    public StoryArrayAdapter(Context context, OnStartDragListener dragStartListener,
            List<StoryPlace> storyPlaces, boolean isOwner, int datesRelation) {
        mStoryPlaces = storyPlaces;
        mDragStartListener = dragStartListener;
        mContext = context;
        listener = (StoryPlaceListener) context;
        this.isOwner = isOwner;
        this.datesRelation = datesRelation;
    }

    @Override
    public StoryArrayAdapter.StoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new StoryViewHolder(
                inflater.inflate(
                        R.layout.item_storyplace,
                        parent,
                        false
                )
        );

    }

    @Override
    public void onBindViewHolder(final StoryArrayAdapter.StoryViewHolder holder, int position) {
        StoryPlace storyPlace = mStoryPlaces.get(position);
        holder.populate(storyPlace);
        // Start a drag whenever the handle view it touched
        holder.ivPlacePhoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });

        holder.ivNote.setOnClickListener(v -> listener.noteOnClick(getRealPosition(storyPlace)));
        holder.ivCamera.setOnClickListener(v -> listener.cameraOnClick(getRealPosition(storyPlace)));
        holder.ivGallery.setOnClickListener(v -> listener.galleryOnClick(getRealPosition(storyPlace)));
    }

    @Override
    public void onItemDismiss(int position) {
        StoryPlace storyPlace = mStoryPlaces.get(position);
        Log.d("onItemDismiss", String.format("pos: %d, storyPlace: %s", position, storyPlace.getName()));
        listener.storyPlaceDismissed(position);
        mStoryPlaces.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        StoryPlace storyPlace = mStoryPlaces.get(fromPosition);
        Log.d("onItemMove", String.format("pos: %d - %d, storyPlace: %s", fromPosition, toPosition, storyPlace.getName()));
        listener.storyPlaceMoved(fromPosition, toPosition);
        Collections.swap(mStoryPlaces, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return mStoryPlaces.size();
    }

    // Needed in order to get the correct position of items
    // on drag/dismiss doesn't update the "position" of the recycler view
    private int getRealPosition(StoryPlace storyPlace){
        return mStoryPlaces.indexOf(storyPlace);
    }

    public class StoryViewHolder extends RecyclerView.ViewHolder
            implements ItemTouchHelperViewHolder, MediaItemAdapter.MediaItemListener {

        // views
        @BindView(R.id.ivPlacePhoto) ImageView ivPlacePhoto;
        @BindView(R.id.tvPlaceName) TextView tvPlaceName;
        @BindView(R.id.cbCheckin) AppCompatCheckBox cbCheckin;
        @BindView(R.id.tvCheckin) TextView tvCheckin;
        @BindView(R.id.ivNote) ImageView ivNote;
        @BindView(R.id.ivCamera) ImageView ivCamera;
        @BindView(R.id.ivGallery) ImageView ivGallery;
        @BindView(R.id.rvMediaHolder) RecyclerView rvMediaItems;

        @BindString(R.string.checkin) String checkin;
        @BindString(R.string.forgot_checkin) String forgot_checkin;

        // variables
        private ArrayList<Media> mPlaceMediaItems;
        private MediaItemAdapter mMediaItemAdapter;
        private StoryPlace mStoryPlace;

        public StoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mPlaceMediaItems = new ArrayList<>();
            mMediaItemAdapter =
                new MediaItemAdapter(StoryArrayAdapter.this.mContext, mPlaceMediaItems, this);
            rvMediaItems.setAdapter(mMediaItemAdapter);
            rvMediaItems.setLayoutManager(
                new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        }

        public void populate(StoryPlace storyPlace) {
            mStoryPlace = storyPlace;
            ivPlacePhoto.setImageResource(0);
            ImageUtils.loadImage(ivPlacePhoto,
                    GoogleAsyncHttpClient.getPlacePhotoUrl(storyPlace.getPhotoUrl()), R.drawable.ic_photoholder, null);
            tvPlaceName.setText(storyPlace.getName());
            ParseQuery<Media> mediaObjectsQuery = ParseQuery.getQuery(ParseModelConstants.MEDIA_CLASS_NAME);
            mediaObjectsQuery.whereEqualTo(ParseModelConstants.STORY_PLACE_KEY, storyPlace);
            mediaObjectsQuery.findInBackground((List<Media> mediaObjects, ParseException e) -> {
                if (e == null) {
                    mPlaceMediaItems.clear();
                    mPlaceMediaItems.addAll(mediaObjects);
                    mMediaItemAdapter.notifyDataSetChanged();
                } else {
                    Log.d("Media fetch failed", e.toString());
                }
            });
            setupCheckinCheckbox(storyPlace);
        }

        private void setupCheckinCheckbox(StoryPlace storyPlace) {
            // check in
            // dateRelation     isOwner             otherUser
            // FUTURE           hide                hide
            // PAST             show/enabled/forgot show/withDate
            // NOW              show/enabled        hide
            if (datesRelation == FUTURE) {
                hideCheckin();
            } else if (datesRelation == PAST) {
                if (isOwner) {
                    showMyPastCheckin(storyPlace);
                } else {
                    showPastCheckin(storyPlace);
                }
            } else {
                if (isOwner) {
                    showCurrentCheckin(storyPlace);
                } else {
                    hideCheckin();
                }
            }
        }

        private void hideCheckin() {
            cbCheckin.setEnabled(false);
            cbCheckin.setVisibility(View.GONE);
            tvCheckin.setVisibility(View.GONE);
        }

        private void showPastCheckin(StoryPlace storyPlace) {
            cbCheckin.setEnabled(false);
            Date checkIn = storyPlace.getCheckinTime();
            if (checkIn != null) {
                cbCheckin.setChecked(true);
                tvCheckin.setText(DateUtils.formatDate(mContext, checkIn));
                cbCheckin.setVisibility(View.VISIBLE);
                tvCheckin.setVisibility(View.VISIBLE);
            } else {
                cbCheckin.setVisibility(View.GONE);
                tvCheckin.setVisibility(View.GONE);
            }
        }

        private void showCheckin(StoryPlace storyPlace, int drawable, String defaultText) {
            cbCheckin.setEnabled(true);
            cbCheckin.setButtonDrawable(drawable);
            Date checkIn = storyPlace.getCheckinTime();
            cbCheckin.setChecked(checkIn != null);
            tvCheckin.setText(checkIn != null ? DateUtils.formatDate(mContext, checkIn) : defaultText);
            cbCheckin.setVisibility(View.VISIBLE);
            tvCheckin.setVisibility(View.VISIBLE);
            tvCheckin.setOnClickListener(v -> {
                if (!tvCheckin.getText().toString().equals(defaultText)) {
                    listener.checkinOnClick(getRealPosition(storyPlace), storyPlace.getCheckinTime());
                }
            });
        }

        private void showMyPastCheckin(StoryPlace storyPlace) {
            showCheckin(storyPlace, R.drawable.checkbox_checkin_forgot, forgot_checkin);
            cbCheckin.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!cbCheckin.isChecked()) {
                        listener.checkinOnClick(getRealPosition(storyPlace), storyPlace.getCheckinTime());
                        return true; // this will prevent checkbox from changing state
                    }
                }
                return false;
            });
            cbCheckin.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isChecked) {
                    tvCheckin.setText(forgot_checkin);
                }
            });
        }

        private void showCurrentCheckin(StoryPlace storyPlace) {
            showCheckin(storyPlace, R.drawable.checkbox_checkin, checkin);
            cbCheckin.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    Date checkinTime = new Date();
                    tvCheckin.setText(DateUtils.formatDate(mContext, checkinTime));
                    storyPlace.setCheckinTime(checkinTime);
//                    onItemMove(getRealPosition(storyPlace), mStoryPlaces.size() - 1); // move to bottom
                } else {
                    tvCheckin.setText(checkin);
                    storyPlace.remove(ParseModelConstants.CHECK_IN_TIME_KEY);
//                    onItemMove(getRealPosition(storyPlace), 0); // move to top
                }
                storyPlace.saveInBackground();
            });
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }

        @Override
        public void photoOnClick(int mPosition) {
            Media mediaItem = mPlaceMediaItems.get(mPosition);
            Log.d("photoOnClick", mediaItem.getObjectId());
            listener.mediaOnClick(mediaItem, mPosition, getRealPosition(mStoryPlace));
        }

        @Override
        public void noteOnClick(int mPosition) {
            Media mediaItem = mPlaceMediaItems.get(mPosition);
            Log.d("noteOnClick", mediaItem.getCaption());
            listener.mediaOnClick(mediaItem, mPosition, getRealPosition(mStoryPlace));
        }
    }

}
