package com.codepath.travel.adapters;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import static com.codepath.travel.R.id.ivCollageIcon;
import static com.codepath.travel.R.id.ivPlacePhoto;
import static com.codepath.travel.R.id.swipe;
import static com.codepath.travel.helper.DateUtils.FUTURE;
import static com.codepath.travel.helper.DateUtils.PAST;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.travel.R;
import com.codepath.travel.helper.DateUtils;
import com.codepath.travel.helper.ImageUtils;
import com.codepath.travel.helper.ItemTouchHelperViewHolder;
import com.codepath.travel.models.parse.Media;
import com.codepath.travel.models.parse.ParseModelConstants;
import com.codepath.travel.models.parse.StoryPlace;
import com.codepath.travel.net.GoogleAsyncHttpClient;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Swipe Recycler View Adapter for a trip's story places.
 */
public class SwipeStoryPlaceAdapter extends RecyclerSwipeAdapter<SwipeStoryPlaceAdapter.StoryPlaceViewHolder> {

    private Context mContext;
    private List<StoryPlace> mStoryPlaces;
    private StoryPlaceListener listener;
    private boolean isOwner;
    private int datesRelation; // PAST, NOW, or FUTURE

    public interface StoryPlaceListener {
        void mediaOnClick(int position);
        void checkinOnClick(int position, Date checkinDate);
        void onStoryPlaceDelete(int position);
        void onStoryPlaceInfo(int position);
    }

    public SwipeStoryPlaceAdapter(Context context, List<StoryPlace> storyPlaces, boolean isOwner,
            int datesRelation) {
        mStoryPlaces = storyPlaces;
        mContext = context;
        listener = (StoryPlaceListener) context;
        this.isOwner = isOwner;
        this.datesRelation = datesRelation;
    }

    @Override
    public StoryPlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_storyplace, parent, false);
        return new StoryPlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StoryPlaceViewHolder holder, int position) {
        StoryPlace storyPlace = mStoryPlaces.get(position);
        holder.setupSwipe(isOwner && datesRelation != FUTURE);
        holder.populate(storyPlace);
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return mStoryPlaces.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    // Needed in order to get the correct position of items
    // on drag/dismiss doesn't update the "position" of the recycler view
    private int getRealPosition(StoryPlace storyPlace){
        return mStoryPlaces.indexOf(storyPlace);
    }

    private void onItemDelete(int position) {
        StoryPlace storyPlace = mStoryPlaces.get(position);
        Log.d("onItemDelete", String.format("pos: %d, storyPlace: %s", position, storyPlace.getName()));
        listener.onStoryPlaceDelete(position);
        mStoryPlaces.remove(position);
        notifyItemRemoved(position);
    }

    public class StoryPlaceViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        // main item views
        @BindView(R.id.ivPlacePhoto) ImageView ivPlacePhoto;
        @BindView(R.id.tvPlaceName) TextView tvPlaceName;
        @BindView(R.id.cbCheckin) AppCompatCheckBox cbCheckin;
        @BindView(R.id.tvCheckin) TextView tvCheckin;
        @BindView(R.id.rbUserRating) RatingBar rbUserRating;
        @BindView(R.id.ivCollageIcon) ImageView ivCollageIcon;

        // swipe views
        @BindView(swipe) SwipeLayout swipeLayout;
        @BindView(R.id.bottomLeft) CardView leftMenu;
        @BindView(R.id.ivDelete) ImageView ivDelete;
        @BindView(R.id.bottomRight) CardView rightMenu;
        @BindView(R.id.ivInfo) ImageView ivInfo;
        @BindView(R.id.ivMedia) ImageView ivMedia;

        // strings
        @BindString(R.string.checkin) String checkin;
        @BindString(R.string.forgot_checkin) String forgot_checkin;

        // variables
        private StoryPlace mStoryPlace;

        public StoryPlaceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setupSwipe(boolean enabled) {
            if (enabled) {
                swipeLayout.setSwipeEnabled(true);
                swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
                swipeLayout.setClickToClose(true);
                setupSwipeMenuListeners();
            } else {
                swipeLayout.setSwipeEnabled(false);
            }
        }

        public void populate(StoryPlace storyPlace) {
            mStoryPlace = storyPlace;

            // photo and name
            ImageUtils.loadImage(ivPlacePhoto,
                    GoogleAsyncHttpClient.getPlacePhotoUrl(storyPlace.getPhotoUrl()));
            ivPlacePhoto.setOnClickListener(
                    v -> listener.onStoryPlaceInfo(getRealPosition(storyPlace)));
            tvPlaceName.setText(storyPlace.getName());

            if (isOwner && datesRelation != FUTURE) {
                ivCollageIcon.setVisibility(GONE);
            } else {
                // show collage icon for people to tap on
                ivCollageIcon.setVisibility(VISIBLE);
                ivCollageIcon.setOnClickListener(
                        v -> listener.mediaOnClick(getRealPosition(mStoryPlace)));
            }

            // check-in and user rating
            setupCheckinCheckbox(storyPlace);
        }

        private void setupSwipeMenuListeners() {
            // left menu
            ivDelete.setOnClickListener(v -> onItemDelete(getRealPosition(mStoryPlace)));

            // right menu
            ivInfo.setOnClickListener(v -> listener.onStoryPlaceInfo(getRealPosition(mStoryPlace)));
            ivMedia.setOnClickListener(v -> listener.mediaOnClick(getRealPosition(mStoryPlace)));

            // click listener on surface view to swipe right on tap
            swipeLayout.getSurfaceView().setOnClickListener(v -> {
                for (SwipeLayout layout : getOpenLayouts()) {
                    layout.close(true);
                }
                swipeLayout.open(true, SwipeLayout.DragEdge.Right);
            });

            swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onStartOpen(SwipeLayout layout) {
                }

                @Override
                public void onOpen(SwipeLayout layout) {

                }

                @Override
                public void onStartClose(SwipeLayout layout) {

                }

                @Override
                public void onClose(SwipeLayout layout) {

                }

                @Override
                public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

                }

                @Override
                public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

                }
            });
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
            cbCheckin.setVisibility(GONE);
            tvCheckin.setVisibility(GONE);
            rbUserRating.setVisibility(GONE);
        }

        private void showPastCheckin(StoryPlace storyPlace) {
            cbCheckin.setEnabled(false);
            Date checkIn = storyPlace.getCheckinTime();
            if (checkIn != null) {
                cbCheckin.setChecked(true);
                tvCheckin.setText(DateUtils.formatDate(mContext, checkIn));
                cbCheckin.setVisibility(VISIBLE);
                tvCheckin.setVisibility(VISIBLE);
                float rating = (float) storyPlace.getRating();
                if (rating > 0) {
                    rbUserRating.setRating((float) storyPlace.getRating());
                    rbUserRating.setIsIndicator(true);
                    rbUserRating.setVisibility(VISIBLE);
                } else {
                    rbUserRating.setVisibility(GONE);
                }
            } else {
                cbCheckin.setVisibility(GONE);
                tvCheckin.setVisibility(GONE);
                rbUserRating.setVisibility(GONE);
            }
        }

        private void showCheckin(StoryPlace storyPlace, int drawable, String defaultText) {
            cbCheckin.setEnabled(true);
            cbCheckin.setButtonDrawable(drawable);
            Date checkIn = storyPlace.getCheckinTime();
            cbCheckin.setChecked(checkIn != null);
            tvCheckin.setText(
                    checkIn != null ? DateUtils.formatDate(mContext, checkIn) : defaultText);
            cbCheckin.setVisibility(VISIBLE);
            tvCheckin.setVisibility(VISIBLE);
            tvCheckin.setOnClickListener(v -> {
                if (!tvCheckin.getText().toString().equals(defaultText)) {
                    listener.checkinOnClick(getRealPosition(storyPlace),
                            storyPlace.getCheckinTime());
                }
            });
            if (checkIn != null) {
                rbUserRating.setRating((float) storyPlace.getRating());
                rbUserRating.setIsIndicator(false);
                rbUserRating.setVisibility(VISIBLE);
                rbUserRating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
                    storyPlace.setRating(rating);
                    storyPlace.saveInBackground();
                });
            }
        }

        private void showMyPastCheckin(StoryPlace storyPlace) {
            showCheckin(storyPlace, R.drawable.checkbox_checkin_forgot, forgot_checkin);
            cbCheckin.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!cbCheckin.isChecked()) {
                        listener.checkinOnClick(getRealPosition(storyPlace),
                                storyPlace.getCheckinTime());
                        return true; // this will prevent checkbox from changing state
                    }
                }
                return false;
            });
            cbCheckin.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isChecked) {
                    tvCheckin.setText(forgot_checkin);
                    storyPlace.remove(ParseModelConstants.CHECK_IN_TIME_KEY);
                    storyPlace.saveInBackground();
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
    }
}
