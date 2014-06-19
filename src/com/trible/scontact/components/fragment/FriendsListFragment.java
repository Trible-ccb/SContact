package com.trible.scontact.components.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.trible.scontact.R;
import com.trible.scontact.components.activity.CustomSherlockFragmentActivity;
import com.trible.scontact.components.activity.SContactMainActivity;
import com.trible.scontact.components.activity.ViewFriendDetailsActivity;
import com.trible.scontact.components.adpater.FriendsGridAdapter;
import com.trible.scontact.components.adpater.FriendsListAdapter;
import com.trible.scontact.components.widgets.ChooseContactActionDialog;
import com.trible.scontact.components.widgets.LoadingDialog;
import com.trible.scontact.controller.impl.LocalFriendsController;
import com.trible.scontact.networks.NetWorkEvent;
import com.trible.scontact.networks.SContactAsyncHttpClient;
import com.trible.scontact.networks.SimpleAsynTask;
import com.trible.scontact.networks.SimpleAsynTask.AsynTaskListner;
import com.trible.scontact.networks.params.AccountParams;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.pojo.GsonHelper;
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
	
	int mLoadFlag = 1;
	boolean isLocalData;
	GroupInfo mFriendBelongToGroup;
	OnFragmentListener mFragmentListener;
	
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
		if ( mFragmentListener != null ){
			mFragmentListener.onFragmentCreated();
		}
		mLoadingDialog = new LoadingDialog(mActivity);
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
		mFriendsListAdapter.setData(null);
		mFriendsListAdapter.showEmptyView(false);
		if ( mLoadingDialog == null )return;
		mLoadingDialog.getDialog().setmDismisslistener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				SContactAsyncHttpClient.cancel(mActivity, true);
			}
		});
//		mLoadingDialog.show();
		if ( mActivity instanceof SContactMainActivity ){
			((SContactMainActivity)mActivity).showTip(getString(R.string.loading));
		}
	}
	void updateUIAfterLoadFriend(){
		if ( !isAdded() )return;
		setTitle(mFriendsListAdapter.getCount() + "");
		mFriendsListAdapter.mEmptyData.mListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshList();
			}
		};
		mFriendsListAdapter.mEmptyData.mBtnText = getString(R.string.re_try);
		mFriendsListAdapter.mEmptyData.mText = getString(R.string.empty_friend_list);
		mFriendsListAdapter.showEmptyView(true);
		mLoadingDialog.getDialog().dismissDialogger();
		if ( mActivity instanceof SContactMainActivity ){
			((SContactMainActivity)mActivity).hideTip();
		}
	}
	public void loadLocalFriendsByGroup(final GroupInfo info){
		mLoadFlag = 1;
		isLocalData = true;
		mFriendBelongToGroup = info;
		updateUIBeforeLoadFriend();
		SimpleAsynTask.doTask2(new AsynTaskListner() {
			
			@Override
			public void onTaskDone(NetWorkEvent event) {
				mFriendsListAdapter.setData(mFriendinfo);
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
	public void loadRomoteFriendsByGroup(GroupInfo info){
		mLoadFlag = 2;
		mFriendBelongToGroup = info;
		isLocalData = false;
		updateUIBeforeLoadFriend();
		SContactAsyncHttpClient.post(AccountParams.getAccountByGroupIdParams(info.getId()),
				null, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				mFriendinfo = GsonHelper.getInfosFromJson(arg2, new AccountInfo().listType());
				mFriendsListAdapter.setData(mFriendinfo);
				if ( mFriendinfo != null ){
				} else {
					Bog.toastErrorInfo(arg2);
				}
			}
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				Bog.toast( R.string.connect_server_err );
			}
			@Override
			public void onFinish() {
				updateUIAfterLoadFriend();
			}
		});
	}
	public void loadRomoteFriendsByUserId(GroupInfo info){
		mLoadFlag = 3;
		mFriendBelongToGroup = info;
		isLocalData = false;
		updateUIBeforeLoadFriend();
		SContactAsyncHttpClient.post(
				AccountParams.getFriendsByUserIdParams(AccountInfo.getInstance().getId()),
				null, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				super.onSuccess(arg0, arg1, arg2);
				mFriendinfo = GsonHelper.getInfosFromJson(arg2, new AccountInfo().listType());
				mFriendsListAdapter.setData(mFriendinfo);
				if ( mFriendinfo != null ){
				} else {
					Bog.toastErrorInfo(arg2);
				}
			}
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				super.onFailure(arg0, arg1, arg2, arg3);
				Bog.toast( R.string.connect_server_err );
			}
			@Override
			public void onFinish() {
				super.onFinish();
				updateUIAfterLoadFriend();
			}
		});
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if( mFriendinfo != null && mFriendinfo.get(position) != null ){
			mActivity.simpleDisplayActivity(
					ViewFriendDetailsActivity.getInentMyself(
					mFriendinfo.get(position),mFriendBelongToGroup));
		}

	}
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		return true;
	}
	
	void setTitle(String size){
		
		String sub = null;
		if ( mFriendBelongToGroup.getCapacity() != null ){
			sub = "(" +size + "/" + mFriendBelongToGroup.getCapacity() +")";
		} else {
			sub = "(" +size +")";
		}
		if (getSherlockActivity() != null )
			getSherlockActivity().getSupportActionBar().setSubtitle(sub);
	}
	public void refreshList(){
		switch (mLoadFlag) {
		case 1:
			loadLocalFriendsByGroup(mFriendBelongToGroup);
			break;
		case 2:
			loadRomoteFriendsByGroup(mFriendBelongToGroup);
			break;
		case 3:
			loadRomoteFriendsByUserId(mFriendBelongToGroup);
			break;
		default:
			break;
		}
	}
}
