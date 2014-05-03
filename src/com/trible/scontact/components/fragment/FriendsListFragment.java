package com.trible.scontact.components.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

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
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.trible.scontact.R;
import com.trible.scontact.components.activity.CustomSherlockFragmentActivity;
import com.trible.scontact.components.activity.ViewFriendDetailsActivity;
import com.trible.scontact.components.adpater.FriendsListAdapter;
import com.trible.scontact.components.widgets.ChooseFriendActionDialog;
import com.trible.scontact.controller.impl.LocalFriendsController;
import com.trible.scontact.models.Friendinfo;
import com.trible.scontact.networks.NetWorkEvent;
import com.trible.scontact.networks.SContactAsyncHttpClient;
import com.trible.scontact.networks.SimpleAsynTask;
import com.trible.scontact.networks.SimpleAsynTask.AsynTaskListner;
import com.trible.scontact.networks.params.AccountParams;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.pojo.ErrorInfo;
import com.trible.scontact.pojo.GsonHelper;
import com.trible.scontact.utils.Bog;

public class FriendsListFragment extends SherlockFragment 
						implements OnItemLongClickListener,OnItemClickListener{

	FriendsListAdapter mFriendsListAdapter;
	ListView mFriendListView;
	List<AccountInfo> mFriendinfo;
	
	LocalFriendsController mFriendsController;
	
	ChooseFriendActionDialog mFriendActionDialog;
	CustomSherlockFragmentActivity mActivity;
	
	boolean isLocalData;
	
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
		mFriendsController = new LocalFriendsController(getActivity());
		mActivity = (CustomSherlockFragmentActivity) getActivity();
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
		isLocalData = true;
		SimpleAsynTask.doTask2(new AsynTaskListner() {
			
			@Override
			public void onTaskDone(NetWorkEvent event) {
				setTitle("" + mFriendinfo.size() );
				mFriendsListAdapter.setData(mFriendinfo);
			}
			
			@Override
			public void doInBackground() {
				mFriendinfo = mFriendsController.getFriendsListByGroupId(gid);
				if ( mFriendinfo == null ){
					mFriendinfo = new ArrayList<AccountInfo>();
				}
			}
		});
	}
	public void loadRomoteFriendsByGroup(Long gid){
		isLocalData = false;
		SContactAsyncHttpClient.post(AccountParams.getAccountByGroupIdParams(gid),
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
			}
		});
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if( mFriendinfo != null && mFriendinfo.get(position) != null ){
			mActivity.simpleDisplayActivity(ViewFriendDetailsActivity.getInentMyself(
					mFriendinfo.get(position)));
		}

	}
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
//		if ( mFriendinfo != null ){
//			mFriendActionDialog = new ChooseFriendActionDialog(getActivity(),
//					mFriendinfo.get(position));
//			if ( isLocalData ){
//				mFriendActionDialog.setMutilVisible(true, true, true);
//			} else {
//				mFriendActionDialog.setMutilVisible(true, true, false);
//			}
//			mFriendActionDialog.show();
//		}
		return false;
	}
	
	void setTitle(String title){
		mActivity.setTitle(title);
	}
}
