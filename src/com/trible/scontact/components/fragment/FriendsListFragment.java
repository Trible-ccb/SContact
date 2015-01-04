 package com.trible.scontact.components.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.Header;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVQuery.CachePolicy;
import com.avos.avoscloud.FindCallback;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.trible.scontact.R;
import com.trible.scontact.components.activity.CustomSherlockFragmentActivity;
import com.trible.scontact.components.activity.SContactMainActivity;
import com.trible.scontact.components.activity.ViewFriendDetailsActivity;
import com.trible.scontact.components.adpater.FriendsListAdapter;
import com.trible.scontact.components.widgets.ChooseContactActionDialog;
import com.trible.scontact.components.widgets.LoadingDialog;
import com.trible.scontact.controller.impl.LocalFriendsController;
import com.trible.scontact.networks.NetWorkEvent;
import com.trible.scontact.networks.SContactAsyncHttpClient;
import com.trible.scontact.networks.ScontactHttpClient;
import com.trible.scontact.networks.SimpleAsynTask;
import com.trible.scontact.networks.SimpleAsynTask.AsynTaskListner;
import com.trible.scontact.networks.params.AccountParams;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.pojo.GsonHelper;
import com.trible.scontact.pojo.UserGroupRelationInfo;
import com.trible.scontact.pojo.UserRelationInfo;
import com.trible.scontact.utils.Bog;

public class FriendsListFragment extends SherlockFragment 
						implements OnItemLongClickListener,OnItemClickListener{

	FriendsListAdapter mFriendsListAdapter;
//	FriendsListAdapter mFriendsGridAdapter;
	
	ListView mFriendListView;
	GridView mFrindGridView;
	List<AccountInfo> mFriendinfo;
	
	LocalFriendsController mFriendsController;
	
	LoadingDialog mLoadingDialog;
	ChooseContactActionDialog mFriendActionDialog;
	CustomSherlockFragmentActivity mActivity;
	
	boolean isLocalData;
	GroupInfo mFriendBelongToGroup;
	OnFragmentListener mFragmentListener;
	Queue<GroupInfo> mGroupQueueForLoading;
	SimpleAsynTask mLoadingMembersTask;
	
	public FriendsListFragment(){
		mGroupQueueForLoading = new LinkedBlockingQueue<GroupInfo>();
	}
	public static FriendsListFragment getInstance(){
		return new FriendsListFragment();
	}

	public interface OnFragmentListener{
		void onFragmentCreated();
	}
	
	public void setOnFragmentListener(OnFragmentListener l){
		mFragmentListener = l;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@SuppressLint("NewApi")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mFriendsListAdapter = new FriendsListAdapter(getSherlockActivity());
//		mFriendsGridAdapter = new FriendsGridAdapter(getSherlockActivity());
//		mFriendsListAdapter = mFriendsGridAdapter;
//		GridView mFriendListView = mFrindGridView;
		mFriendListView.setAdapter(mFriendsListAdapter);
		mFriendListView.setOnItemClickListener(this);
		mFriendListView.setOnItemLongClickListener(this);
		mFriendsController = new LocalFriendsController(getActivity());
		mActivity = (CustomSherlockFragmentActivity) getActivity();
		mLoadingDialog = new LoadingDialog(mActivity);
		setUpLoadingMembersOfGroups();
		if ( mFragmentListener != null ){
			mFragmentListener.onFragmentCreated();
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_friends_list, null);
		mFriendListView = (ListView) v.findViewById(R.id.users_list_view);
		mFrindGridView = (GridView) v.findViewById(R.id.users_grid_view);
		mFrindGridView.setVisibility(View.GONE);
		return v;
	}
	
	void updateUIBeforeLoadFriend(){
		if ( !isAdded() )return;
		setTitle("0");
		if ( isLocalData ){
			mFriendsListAdapter.mUsePhoto = false;
		} else {
			mFriendsListAdapter.mUsePhoto = true;
		}
		mFriendsListAdapter.showEmptyView(false);
		mFriendsListAdapter.setData(null);
		if ( mLoadingDialog == null )return;
		mLoadingDialog.getDialog().setmDismisslistener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
//				SContactAsyncHttpClient.cancel(mActivity, true);
			}
		});
//		mLoadingDialog.show();
		if ( mActivity instanceof SContactMainActivity ){
			((SContactMainActivity)mActivity).showTip(getString(R.string.loading));
		}
	}
	void updateUIAfterLoadFriend(){
		if ( !isAdded() )return;
		mFriendsListAdapter.setData(mFriendinfo);
		setTitle(mFriendsListAdapter.getCount() + "");
		mFriendsListAdapter.showEmptyView(true);
		mLoadingDialog.getDialog().dismissDialogger();
		if ( mActivity instanceof SContactMainActivity ){
			((SContactMainActivity)mActivity).hideTip();
		}
	}
	public void loadLocalFriendsByGroup(final GroupInfo info){
		isLocalData = true;
		mFriendBelongToGroup = info;
		updateUIBeforeLoadFriend();
		SimpleAsynTask.doTask2(new AsynTaskListner() {
			@Override
			public void onTaskDone(NetWorkEvent event) {
				
				mFriendsListAdapter.mEmptyData.mListener = new OnClickListener() {
					@Override
					public void onClick(View v) {
						refreshList();
					}
				};
				mFriendsListAdapter.mEmptyData.mBtnText = getString(R.string.re_try);
				mFriendsListAdapter.mEmptyData.mText = getString(R.string.load_err);
				if ( SContactMainActivity.GROUP_OF_LOCAL_FRIEND.
						equals(mFriendBelongToGroup.getId()) )
				updateUIAfterLoadFriend();
			}
			
			@Override
			public void doInBackground() {
				mFriendinfo = mFriendsController.getPhoneFriendsList();
				if ( mFriendinfo == null ){
					mFriendinfo = new ArrayList<AccountInfo>();
				}
			}
		});
	}
	//like a background service for refresh friend list
	private void setUpLoadingMembersOfGroupsFromAVOS(){
		while ( !mGroupQueueForLoading.isEmpty() ){
			final GroupInfo g = mGroupQueueForLoading.poll();
			if ( g != null 
					&& !SContactMainActivity.GROUP_OF_LOCAL_FRIEND.equals(g.getId())){
				if ( SContactMainActivity.GROUP_ID_OF_FRIEND.equals(g.getId()) ){
					//加载用户好友列表
					AVQuery<UserRelationInfo> myfriends = AVQuery.getQuery(UserRelationInfo.class);
					myfriends.include(UserRelationInfo.FieldName.contacts);
					myfriends.include(UserRelationInfo.FieldName.follower);
					myfriends.include(UserRelationInfo.FieldName.user);
					myfriends.setCachePolicy(CachePolicy.NETWORK_ELSE_CACHE);
					myfriends.whereEqualTo(UserRelationInfo.FieldName.follower, AccountInfo.getInstance());
					myfriends.findInBackground(new FindCallback<UserRelationInfo>() {
						@Override
						public void done(List<UserRelationInfo> arg0, AVException arg1) {
							List<AccountInfo> info = new ArrayList<AccountInfo>();
							if ( arg1 == null && arg0 != null ){
								for ( UserRelationInfo f : arg0 ){
									AccountInfo user = f.getUser();
									user.setContactsList(f.getContacts());
									info.add(user);
								}
//								g.saveMembersToSpf(info);
								if( g.getId().equals(mFriendBelongToGroup.getId()) ){
									mFriendinfo = info;
									updateUIAfterLoadFriend();
								}
							} else {
								Bog.e(arg1.getMessage());
							}
						}
					});
				} else {
					//加载某个群内的好友
					AVQuery<UserGroupRelationInfo> myfriends = AVQuery.getQuery(UserGroupRelationInfo.class);
					myfriends.include(UserGroupRelationInfo.FieldName.contacts);
					myfriends.include(UserGroupRelationInfo.FieldName.user);
					myfriends.whereEqualTo(UserGroupRelationInfo.FieldName.group, g);
					myfriends.setCachePolicy(CachePolicy.NETWORK_ELSE_CACHE);
					myfriends.findInBackground(new FindCallback<UserGroupRelationInfo>() {
						@Override
						public void done(List<UserGroupRelationInfo> arg0, AVException arg1) {
							List<AccountInfo> info = new ArrayList<AccountInfo>();
							if ( arg1 == null && arg0 != null ){
								for ( UserGroupRelationInfo f : arg0 ){
									AccountInfo user = f.getUser();
									user.setContactsList(f.getContacts());
									info.add(user);
								}
//								g.saveMembersToSpf(info);
								if( g.getId().equals(mFriendBelongToGroup.getId()) ){
									mFriendinfo = info;
									updateUIAfterLoadFriend();
								}
							} else {
								Bog.e(arg1.getMessage());
							}
						}
					});
				}
			}
		}
	}
	
	private void setUpLoadingMembersOfGroups(){
		setUpLoadingMembersOfGroupsFromAVOS();
	}
	public void addGroupForLoad(GroupInfo g){
		mGroupQueueForLoading.add(g);
		if ( mLoadingMembersTask == null ){
			setUpLoadingMembersOfGroups();
		}
		
	}
	public void loadRomoteAccountsByGroup(final GroupInfo info){
		mFriendBelongToGroup = info;
		isLocalData = false;
		updateUIBeforeLoadFriend();
		if ( mFriendBelongToGroup != null ){
			if ( SContactMainActivity.GROUP_ID_OF_FRIEND.equals(mFriendBelongToGroup.getId()) ){
				mFriendsListAdapter.mEmptyData.mListener = null;
				mFriendsListAdapter.mEmptyData.mText = getString(R.string.empty_friend_list);
			} else {
				mFriendsListAdapter.mEmptyData.mListener = new OnClickListener() {
					@Override
					public void onClick(View v) {
						refreshList();
					}
				};
				mFriendsListAdapter.mEmptyData.mBtnText = getString(R.string.re_try);
				mFriendsListAdapter.mEmptyData.mText = getString(R.string.load_err);
			}
			//load data from cache first
			addGroupForLoad(info);
			SimpleAsynTask.doTask2(new AsynTaskListner() {
				
				@Override
				public void onTaskDone(NetWorkEvent event) {
					updateUIAfterLoadFriend();
				}
				@Override
				public void doInBackground() {
					mFriendinfo = mFriendBelongToGroup.getMembersFromSpf();
				}
			});
			
			
		}
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		AccountInfo info = (AccountInfo) mFriendsListAdapter.getItem(position);
		if( info != null ){
			mActivity.simpleDisplayActivity(
					ViewFriendDetailsActivity.getInentMyself(
							info,mFriendBelongToGroup));
		}

	}
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		return true;
	}
	@Override
	public void onDestroy() {
		if ( mLoadingMembersTask != null ){
			mLoadingMembersTask.cancelTask();
			mLoadingMembersTask = null;
		}
		super.onDestroy();
	}
	void setTitle(String size){
		String sub = null;
		if ( mFriendBelongToGroup.getCapacity() != null 
				&& mFriendBelongToGroup.getCapacity() != 0 ){
			sub = "(" +size + "/" + mFriendBelongToGroup.getCapacity() +")";
		} else {
			sub = "(" +size + "/" + size + ")";
		}
		if (getSherlockActivity() != null )
			getSherlockActivity().getSupportActionBar().setSubtitle(sub);
	}
	public void refreshList(){
		if ( mFriendBelongToGroup != null ){
			if ( SContactMainActivity.GROUP_OF_LOCAL_FRIEND
					.equals(mFriendBelongToGroup.getId() )){
				loadLocalFriendsByGroup(mFriendBelongToGroup);
			} else {
				loadRomoteAccountsByGroup(mFriendBelongToGroup);
			}
		}
	}
}
