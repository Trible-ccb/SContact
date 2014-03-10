package com.trible.scontact.components.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.trible.scontact.R;
import com.trible.scontact.components.adpater.FriendsListAdapter;
import com.trible.scontact.components.widgets.MultiFunctionListView;
import com.trible.scontact.models.Friendinfo;

public class FriendsListFragment extends SherlockFragment {

	FriendsListAdapter mUsersListAdapter;
	MultiFunctionListView mUserListView;
	
	public static FriendsListFragment getInstance(){
		return new FriendsListFragment();
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mUsersListAdapter = new FriendsListAdapter(getSherlockActivity());
		mUserListView.setAdapter(mUsersListAdapter);
		mUsersListAdapter.setData(Friendinfo.getTestData());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_friends_list, null);
		mUserListView = (MultiFunctionListView) v.findViewById(R.id.users_list_view);
		mUserListView.setMoveMode(MultiFunctionListView.CAN_MOVE_FROM_LEFT_TO＿RIGHT
				| MultiFunctionListView.CAN_MOVE_FROM_RIGHT_TO_LEFT);
		return v;
	}

}
