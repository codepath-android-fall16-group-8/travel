package com.codepath.travel.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.codepath.travel.R;
import com.codepath.travel.adapters.viewholders.ReviewViewHolder;
import com.codepath.travel.models.Review;

import java.util.List;

/**
 * Adapter for reviews recycler view
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewViewHolder> {

    private List<Review> mReviews;
    Context mContext;

    public ReviewsAdapter(Context context, List<Review> reviews) {
        mContext = context;
        mReviews = reviews;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ReviewViewHolder(inflater.inflate(R.layout.item_review,
                parent, false));
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        Review review = mReviews.get(position);
        holder.populate(review);
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }
}
