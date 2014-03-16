package com.trible.scontact.components.fragment;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.trible.scontact.R;
import com.trible.scontact.components.adpater.FriendsListAdapter;
import com.trible.scontact.components.widgets.ChooseFriendActionDialog;
import com.trible.scontact.controller.impl.LocalFriendsController;
import com.trible.scontact.models.Friendinfo;
import com.trible.scontact.networks.NetWorkEvent;
import com.trible.scontact.networks.SimpleAsynTask;
import com.trible.scontact.networks.SimpleAsynTask.AsynTaskListner;
import com.trible.scontact.utils.Bog;

public class FriendsListFragment extends SherlockFragment 
						implements OnItemLongClickListener,OnItemClickListener{

	FriendsListAdapter mFriendsListAdapter;
	ListView mFriendListView;
	List<Friendinfo> mFriendinfo;
	LocalFriendsController mFriendsController;
	
	ChooseFriendActionDialog mFriendActionDialog;
	
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
		mFriendsListAdapter = new FriendsListAdapter(getSherlockActivity());
		mFriendListView.setAdapter(mFriendsListAdapter);
		mFriendListView.setOnItemClickListener(this);
		mFriendListView.setOnItemLongClickListener(this);
		mFriendsListAdapter.setData(Friendinfo.getTestData());
		mFriendsController = new LocalFriendsController(getActivity());
		mFriendActionDialog = new ChooseFriendActionDialog(getActivity(),
				getString(R.string.choose_action));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_friends_list, null);
		mFriendListView = (ListView) v.findViewById(R.id.users_list_view);
		mFriendListView.setItemsCanFocus(true);
		return v;
	}
	
	public void loadLocalFriendsByGroup(final long gid){
		
		SimpleAsynTask.doTask2(new AsynTaskListner() {
			
			@Override
			public void onTaskDone(NetWorkEvent event) {
				Bog.toastDebug("members = " + mFriendinfo.size());
				mFriendsListAdapter.setData(mFriendinfo);
			}
			
			@Override
			public void doInBackground() {
				mFriendinfo = mFriendsController.getFriendsListByGroupId(gid);
			}
		});
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mFriendActionDialog.show();
	}
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		mFriendActionDialog.show();
		return false;
	}
}
