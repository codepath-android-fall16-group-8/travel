package com.codepath.travel.adapters.viewholders;

import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.travel.R;
import com.codepath.travel.helper.ImageUtils;
import com.codepath.travel.listeners.PlacesCartListener;
import com.codepath.travel.models.SuggestionPlace;
import com.codepath.travel.net.GoogleAsyncHttpClient;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * RecyclerView ViewHolder for a suggested place.
 */
public class SuggestedPlaceViewHolder extends RecyclerView.ViewHolder {

    // Views
    @BindView(R.id.tvSuggestionPlaceName) TextView tvSuggestionPlaceName;
    @BindView(R.id.rbPlaceRating) AppCompatRatingBar rbPlaceRating;
    //@BindView(R.id.ivAddSuggestionPlace)  ImageView ivAddSuggestionPlace;
    @BindView(R.id.cbAddPlace) CheckBox cbAddPlace;
    @BindView(R.id.ivSuggestionPlacePhoto) ImageView ivSuggestionPlacePhoto;

    public SuggestedPlaceViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void populate(SuggestionPlace suggestionPlace) {
        tvSuggestionPlaceName.setText(suggestionPlace.getName());
        if (suggestionPlace.getRating() != null) {
            rbPlaceRating.setRating(suggestionPlace.getRating().floatValue());
        }
        if (suggestionPlace.isSelected()) {
            cbAddPlace.setChecked(true);
        } else {
            cbAddPlace.setChecked(false);
        }
        ivSuggestionPlacePhoto.setImageResource(0);

        ImageUtils.loadImage(this.ivSuggestionPlacePhoto,
                GoogleAsyncHttpClient.getPlacePhotoUrl(suggestionPlace.getPhotoUrl()));
    }

    public void listeners(SuggestionPlace suggestionPlace, PlacesCartListener placesCartListener) {
        cbAddPlace.setOnClickListener((View view) -> {
            if (suggestionPlace.isSelected()) {
                suggestionPlace.setSelected(false);
                cbAddPlace.setChecked(false);
                //Remove place id from cart
                placesCartListener.removePlace(suggestionPlace);

            } else {
                suggestionPlace.setSelected(true);
                cbAddPlace.setChecked(true);
                //Add place id to cart
                placesCartListener.addPlace(suggestionPlace);
            }
        });
    }
}