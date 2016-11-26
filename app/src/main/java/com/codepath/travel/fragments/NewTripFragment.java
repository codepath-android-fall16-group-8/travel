package com.codepath.travel.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.travel.Constants;
import com.codepath.travel.R;
import com.codepath.travel.activities.PlaceSuggestionActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

/**
 * Created by aditikakadebansal on 10/26/16.
 */

public class NewTripFragment extends DialogFragment implements PlaceSelectionListener {

    //Class variables
    private static final String TAG = NewTripFragment.class.getSimpleName();
    private static final int CREATE_STORY_REQUEST = 1;
    private static View view;

    //Member variables
    private TextView mTvDestination;
    private Button mBtnNewTrip;
    private String LatLng;

    public NewTripFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static NewTripFragment newInstance() {

        NewTripFragment fragment = new NewTripFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_new_trip, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpData(view);
        setUpClickListeners();
        setUpSearchAutoComplete();
    }

    private void setUpData(View view){
        mTvDestination = (TextView) view.findViewById(R.id.tvDestination);
        mBtnNewTrip = (Button) view.findViewById(R.id.btStartNewTrip);
    }

    private void setUpClickListeners() {
        mBtnNewTrip.setOnClickListener((View view) -> {
            Intent createTrip = new Intent(getActivity(), PlaceSuggestionActivity.class);
            String destination = mTvDestination.getText().toString();
            if(!destination.isEmpty() && !LatLng.isEmpty()) {
                createTrip.putExtra(
                        Constants.DESTINATION_ARG,
                        destination
                );
                createTrip.putExtra(Constants.LATLNG_ARG,
                        LatLng);
                mTvDestination.setText("");
                getActivity().startActivityForResult(createTrip, CREATE_STORY_REQUEST);
            }else {
                Toast.makeText(getActivity(), "Please add a destination", Toast.LENGTH_LONG).show();
            }
            dismiss();
        });
    }

    private void setUpSearchAutoComplete() {
        // Retrieve the PlaceAutocompleteFragment.
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)getActivity()
                .getFragmentManager().findFragmentById(R.id.autocomplete_fragment_new_trip);
        // Register a listener to receive callbacks when a place has been selected or an error has
        // occurred.
        autocompleteFragment.setOnPlaceSelectedListener(this);
    }

    @Override
    public void onPlaceSelected(Place place) {
        Log.i(TAG, "Place Selected: " + place.getName());
        mTvDestination.setText(place.getName());
        LatLng = String.format("%f,%f",place.getLatLng().latitude,place.getLatLng().longitude);
    }

    //Callback invoked when PlaceAutocompleteFragment encounters an error.
    @Override
    public void onError(Status status) {
        Log.e(TAG, "onError: Status = " + status.toString());
    }

    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }

}

