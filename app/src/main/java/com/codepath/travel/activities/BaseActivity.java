package com.codepath.travel.activities;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.travel.R;
import com.codepath.travel.fragments.dialog.ErrorMessageDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Base activity holding common logic and views
 */
public abstract class BaseActivity extends AppCompatActivity {

    // views
    @BindView(R.id.toolbar) Toolbar toolbar;

    protected void initializeCommonViews() {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    protected void showError(String error) {
        // we could have error views in every activity
        // showing a toast for now
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    protected void showErrorDialog(String error) {
        ErrorMessageDialogFragment fragment = ErrorMessageDialogFragment.newInstance(error);
        fragment.show(getSupportFragmentManager(), "errorMessageDialogFragment");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * react to the user tapping the up icon in the action bar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* Activity Transitions */
    protected void setupWindowAnimationsEnterRight() {
        // IF MAKING CHANGES HERE, PLEASE UPDATE StoryActivity!!!
        Fade fadeOut = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.activity_fade_out);
        Slide slideRight = (Slide) TransitionInflater.from(this).inflateTransition(R.transition.activity_slide_right);
        getWindow().setEnterTransition(slideRight); // enter: slide in from right side when being opened
        getWindow().setExitTransition(fadeOut); // exit: fadeOut when opening another activity
        // re-enter: should automatically reverse (fadeIn) when returning from another activity
        // return: should automatically reverse (slideLeft) when closing
        getWindow().setAllowEnterTransitionOverlap(false); // wait for calling activity's exit transition to be done
        getWindow().setAllowReturnTransitionOverlap(false); // wait for called activity's return transition to be done?
    }

    // for detail views: place detail, collage
    protected void setupWindowAnimationsEnterBottom() {
        Slide slideUp = (Slide) TransitionInflater.from(this).inflateTransition(R.transition.activity_slide_up);
        getWindow().setEnterTransition(slideUp); // slide up from bottom when being opened
        // return: should automatically reverse (slide down) when closing
        getWindow().setAllowEnterTransitionOverlap(false); // wait for calling activity's exit transition to be done
    }

}
