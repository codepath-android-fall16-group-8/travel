package com.codepath.travel.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.travel.R;
import com.codepath.travel.adapters.UsersAdapter;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rpraveen on 11/20/16.
 */

/**
 * this looks pretty much the same as trip list fragment
 * should probably consider refactoring to make this more generic to hold any recycler view list
 * with generic id
  */

public class UserListFragment extends Fragment {
  public static final String TAG = UserListFragment.class.getName();

  // views
  @BindView(R.id.rvUsers) RecyclerView rvUsers;

  // member variables
  private UsersAdapter mUsersAdapter;
  private ArrayList<ParseUser> mUsers;

  public static UserListFragment newInstance() {
    return new UserListFragment();
  }

  @Override
  public void onCreate(Bundle savedInstance) {
    super.onCreate(savedInstance);
    mUsers = new ArrayList<>();
    mUsersAdapter = new UsersAdapter(getActivity(), mUsers);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstance) {
    View view = inflater.inflate(R.layout.fragment_user_list, parent, false);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    rvUsers.setHasFixedSize(true);
    rvUsers.setLayoutManager(new LinearLayoutManager(
      getActivity(),
      LinearLayoutManager.VERTICAL,
      false)
    );
    rvUsers.setAdapter(mUsersAdapter);
  }

  public void populateUsers(List<ParseUser> parseUsers) {
    mUsers.clear();
    mUsers.addAll(parseUsers);
    mUsersAdapter.notifyDataSetChanged();
  }
}
