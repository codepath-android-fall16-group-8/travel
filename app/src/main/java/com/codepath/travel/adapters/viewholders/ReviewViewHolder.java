package com.codepath.travel.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.travel.R;
import com.codepath.travel.models.Review;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * RecyclerView ViewHolder for a suggested place.
 */
public class ReviewViewHolder extends RecyclerView.ViewHolder {

    // Views
    @BindView(R.id.tvAuthor) TextView tvAuthor;
    @BindView(R.id.rbRating) RatingBar rbRating;
    @BindView(R.id.tvText) TextView tvText;
    @BindView(R.id.tvTime) TextView tvTime;

    public ReviewViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void populate(Review review) {
        tvAuthor.setText(review.getAuthor());
        rbRating.setRating(review.getRating());
        tvTime.setText(review.getTimestamp());

        String text = review.getText();
        if (!TextUtils.isEmpty(text)) {
            tvText.setVisibility(View.VISIBLE);
            tvText.setText(text);
        } else {
            tvText.setVisibility(View.GONE);
        }
    }
}
