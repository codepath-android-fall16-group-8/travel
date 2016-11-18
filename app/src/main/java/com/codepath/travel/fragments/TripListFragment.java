package com.codepath.travel.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.travel.R;
import com.codepath.travel.adapters.TripsAdapter;
import com.codepath.travel.helper.ItemClickSupport;
import com.codepath.travel.models.Trip;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Fragment to display a horizontal scrolling recycler view of trips.
 */
public class TripListFragment extends Fragment {
    private static final String TAG = TripListFragment.class.getSimpleName();
    protected static final String USER_ID_ARG = "userId";
    protected static final String FETCH_USER_ARG = "fetchUser";

    @BindView(R.id.rvTrips) RecyclerView rvTrips;

    protected Unbinder unbinder;

    protected ArrayList<Trip> mTrips;
    protected TripsAdapter mTripsAdapter;
    protected TripClickListener listener;
    protected boolean fetchUser;

    public static TripListFragment newInstance(String userId, boolean fetchUser) {
        TripListFragment fragment = new TripListFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID_ARG, userId);
        args.putBoolean(FETCH_USER_ARG, fetchUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTrips = new ArrayList<>();
        mTripsAdapter = new TripsAdapter(getContext(), mTrips, fetchUser);
        listener = (TripClickListener) getContext();
        fetchUser = getArguments().getBoolean(FETCH_USER_ARG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_trips, parent, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        rvTrips.setHasFixedSize(true);
        rvTrips.setAdapter(mTripsAdapter);
        rvTrips.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        ItemClickSupport.addTo(rvTrips).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    Trip trip = mTrips.get(position);
                    listener.onTripClick(trip.getObjectId(), trip.getTitle());
                }
        );

        populateTrips();
    }

    public void populateTrips() {
        mTrips.clear();
        String userId = getArguments().getString(USER_ID_ARG);

        Trip.getAllTripsForUser(userId, fetchUser, (trips, e) -> {
            if (e == null) {
                mTrips.addAll(trips);
                mTripsAdapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, String.format("Failed to populate all trips: %s", e.getMessage()));
            }
        });
    }

    // When binding a fragment in onCreateView, set the views to null in onDestroyView.
    // ButterKnife returns an Unbinder on the initial binding that has an unbind method to do this automatically.
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
