package com.codepath.travel.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.codepath.travel.R;
import com.codepath.travel.adapters.UsersAdapter;
import com.codepath.travel.models.User;
import com.parse.ParseUser;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.parse.ParseUser.getCurrentUser;

/**
 * Created by aditikakadebansal on 11/18/16.
 *
 * Dialog Fragment to search for users registered with the app.
 */
public class SearchUserFragment extends DialogFragment {
    private static final String TAG = SearchUserFragment.class.getSimpleName();
    protected static final String USER_ID_ARG = "userId";
    protected static final String FETCH_USER_ARG = "fetchUser";

    @BindView(R.id.rvUsers) RecyclerView rvUsers;
    @BindView(R.id.searchView) SearchView searchView;
    @BindString(R.string.search_user) String searchUser;

    protected Unbinder unbinder;

    protected ArrayList<ParseUser> mUsers;
    protected ArrayList<ParseUser> mFollowingList;
    protected UsersAdapter mUsersAdapter;
    protected String mUserId;
    protected boolean fetchUser;

    public static SearchUserFragment newInstance(String userId) {
        SearchUserFragment fragment = new SearchUserFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID_ARG, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUsers = new ArrayList<>();
        mFollowingList = new ArrayList<>();
        mUsersAdapter = new UsersAdapter(getContext(), mUsers, mFollowingList, true);
        Bundle args = getArguments();
        mUserId = args.getString(USER_ID_ARG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_search_user, parent, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        rvUsers.setHasFixedSize(true);
        rvUsers.setAdapter(mUsersAdapter);
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        searchView.setQueryHint(searchUser);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                populateUsersForSearchedName(query);
                return false;
            }

        });
    }

    public void populateUsersForSearchedName(String name) {
        mUsers.clear();
        User.queryUsers(name, (parseUsers, e) -> {
            if (e == null) {
                for(ParseUser parseUser : parseUsers) {
                    mUsers.add(parseUser);
                }
                mUsersAdapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, String.format("Failed to populate all users: %s", e.getMessage()));
            }
        });
        mFollowingList.clear();
        User.queryFollowing(ParseUser.getCurrentUser(),(users, e) -> {
            if (e == null) {
                mFollowingList.addAll(users);
                mUsersAdapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, String.format("Failed to populate all following users: %s", e.getMessage()));
            }
        });

    }

    public void setUser(String userId) {
        mUserId = userId;
    }

    // When binding a fragment in onCreateView, set the views to null in onDestroyView.
    // ButterKnife returns an Unbinder on the initial binding that has an unbind method to do this automatically.
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
