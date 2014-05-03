package com.trible.scontact.components.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBarDrawerToggle;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.trible.scontact.R;
import com.trible.scontact.components.adpater.GroupsListAdapter;
import com.trible.scontact.components.fragment.FriendsListFragment;
import com.trible.scontact.components.widgets.ChooseGroupActionDialog;
import com.trible.scontact.components.widgets.ChooseGroupActionDialog.OnGroupActionListener;
import com.trible.scontact.components.widgets.LoadingDialog;
import com.trible.scontact.components.widgets.TabLayoutHelper;
import com.trible.scontact.controller.IGroupControl;
import com.trible.scontact.controller.impl.LocalGroupControlller;
import com.trible.scontact.controller.impl.RemoteGroupControlller;
import com.trible.scontact.managers.PrefManager;
import com.trible.scontact.networks.NetWorkEvent;
import com.trible.scontact.networks.SContactAsyncHttpClient;
import com.trible.scontact.networks.SimpleAsynTask;
import com.trible.scontact.networks.SimpleAsynTask.AsynTaskListner;
import com.trible.scontact.networks.params.AccountParams;
import com.trible.scontact.networks.params.ContactParams;
import com.trible.scontact.networks.params.GroupParams;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.pojo.ErrorInfo;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.pojo.GsonHelper;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.ListUtil;
import com.trible.scontact.utils.StringUtil;
import com.trible.scontact.value.PrefKeys;
import com.trible.scontact.value.RequestCode;

/**
 * @author Trible Chen
 *the main class is to show list of friend and groups
 */
public final class SContactMainActivity extends CustomSherlockFragmentActivity 
									implements OnItemClickListener
									,OnClickListener
									,OnItemLongClickListener
									,OnGroupActionListener{

	DrawerLayout mDrawerLayout;
	ListView mGroupListView;
	GroupsListAdapter mGroupListAdapter;
	ActionBarDrawerToggle mDrawerToggle;
	FriendsListFragment mFriendsListFragment;
	TabLayoutHelper mContactTabHelper;
	LayoutInflater mInflater;
	LoadingDialog mLoadingDialog;
	
	IGroupControl mGroupController,mLocalGroupController,mReomoteGroupController;
	List<GroupInfo> mRemoteGroupsInfo,mLocalGroupsInfo,mCurGroupsInfo;
	public static List<ContactInfo> myAllContacts;
	View mCreateGroup;
	ChooseGroupActionDialog mGroupAcionDialog;
	
	AccountInfo mUserInfo;
	int mSelectedGroupPos = -1;
	
	// override life circle methods===============
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		setContentView(R.layout.activity_scontact);
		initDefaultData();
		initView();
		loadMyAllContacts();
	}

	void initDefaultData(){
		mUserInfo = AccountInfo.getInstance();
		mGroupListAdapter = new GroupsListAdapter(this);
		mLocalGroupController = new LocalGroupControlller(this);
		mReomoteGroupController = new RemoteGroupControlller(this);
		
	}
	void initView(){
		mLoadingDialog = new LoadingDialog(this);
		mLoadingDialog.setTipText(R.string.waiting);
		initLeftDrawer();
		initContentView();
	}
	void initContentView(){

		FragmentManager manager = getSupportFragmentManager();
		mFriendsListFragment = (FriendsListFragment) manager
				.findFragmentByTag("FriendsFragment");
		if ( mFriendsListFragment == null ){
			FragmentTransaction tst = manager.beginTransaction();
			mFriendsListFragment = FriendsListFragment.getInstance();
			tst.replace(R.id.frame_users_list,mFriendsListFragment, "FriendsFragment");
			tst.commit();
		}
	}
	void initLeftDrawer(){
		mCreateGroup = findViewById(R.id.drawer_group_list_create_group_root);
		mCreateGroup.setOnClickListener(this);
		mContactTabHelper = new TabLayoutHelper((ViewGroup) findViewById(R.id.tabs_layout),
				new int[]{R.string.remote_contacts,R.string.local_contacts});
		mContactTabHelper.setOnItemClickListner(this);
		mContactTabHelper.setDefaultSelection(0);
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
		mGroupListView = (ListView) findViewById(R.id.group_list_view);
		mGroupListView.setAdapter(mGroupListAdapter);
		mGroupListView.setOnItemClickListener(this);
		mGroupListView.setOnItemLongClickListener(this);
		mDrawerToggle = new ActionBarDrawerToggle(
				this, getSupportActionBar(), mDrawerLayout, 
				R.drawable.ic_drawer, R.string.open_drawer, R.string.close_drawer);
		getSupportActionBar().setDisplayOptions(
		        ActionBar.DISPLAY_SHOW_TITLE |
		        ActionBar.DISPLAY_USE_LOGO |
		        ActionBar.DISPLAY_SHOW_HOME |
		        ActionBar.DISPLAY_HOME_AS_UP);
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_scontact_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if ( arg1 == RESULT_OK ){
			switch (arg0) {
				case RequestCode.CREATE_GROUP:
					GroupInfo newGroup = (GroupInfo) arg2.getSerializableExtra(CreateOrUpdateGroupActivity.RESULT_GROUP);
					mRemoteGroupsInfo.add(newGroup);
					mGroupListAdapter.setData(mRemoteGroupsInfo);
					break;
				case RequestCode.CHANGE_SETTING:
					boolean signout = arg2.getBooleanExtra(SettingsActivity.SIGN_OUT_TAG, false);
					if ( signout ){
						finish();
					}
					break;
				default:
					break;
			}
		}
		super.onActivityResult(arg0, arg1, arg2);
	}
	//end override life circle methods===============
	
	//override listener methods===============
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_search:
				simpleDisplayActivity(SearchGroupOrFriendActivity.getIntentMyselfBundle(""));
				break;
			case android.R.id.home:
				mDrawerToggle.onOptionsItemSelected(item);
				return true;
			case R.id.action_settings:
				simpleGetResultFormActivity(SettingsActivity.class, RequestCode.CHANGE_SETTING);
				break;
			case R.id.action_refresh:
				loadGroups();
				loadMyAllContacts();
				break;
			case R.id.action_my_contacts:
				simpleDisplayActivity(MyContactsActivity.class);
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.drawer_group_list_create_group_root:
				simpleGetResultFormActivity(CreateOrUpdateGroupActivity.class
						,RequestCode.CREATE_GROUP);
				break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if ( parent == null ){//drawer tabs item click
			switch (position) {
				case 1:
					mGroupController = mLocalGroupController;
					mCreateGroup.setVisibility(View.GONE);
					break;
				case 0 :
					mCreateGroup.setVisibility(View.VISIBLE);
					mGroupController = mReomoteGroupController;
					break;
				default:
					break;
			}
			loadGroups();
		} else {//drawer group item click
			if ( mContactTabHelper.getSelection() == 0 ){
				loadRomoteFriendsByGroup(mRemoteGroupsInfo.get(position).getId());
			} else if ( mContactTabHelper.getSelection() == 1 ){
				loadLocalFriendsByGroup(mLocalGroupsInfo.get(position).getId());
			}
			mSelectedGroupPos = position;
			mDrawerLayout.closeDrawers();
		}
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		if ( mContactTabHelper.getSelection() == 1 )return true;//only handle remote group
		GroupInfo info = mRemoteGroupsInfo.get(position);
		if ( mRemoteGroupsInfo != null && mRemoteGroupsInfo.get(position) != null ){
			mGroupAcionDialog = new ChooseGroupActionDialog(this, mRemoteGroupsInfo.get(position));
			AccountInfo uInfo = AccountInfo.getInstance();
			if ( uInfo.getId().equals(info.getOwnerId())){
				mGroupAcionDialog.setMutilVisible(true, true, true, false, false);
			} else {
				mGroupAcionDialog.setMutilVisible(true, false, true, true, false);
			}
			mGroupAcionDialog.show();
		} else {
			Bog.toast(R.string.invalid);
		}

		return false;
	}
	
	//end override listener methods===============

	//private methods===============
	private void updateUILoadingGroup(){
		mLoadingDialog.getDialog().dismissDialogger();
		mLoadingDialog.show();
	}
	private void updateUILoadGroupDone(){
		mLoadingDialog.getDialog().dismissDialogger();
		mGroupListAdapter.setData(mCurGroupsInfo);
	}
	private void loadGroups(){
		if ( mContactTabHelper.getSelection() == 0 ){
			loadRemoteGroups();
		} else if ( mContactTabHelper.getSelection() == 1 ){
			loadLocalGroups();
		}
	}
	private void loadLocalGroups(){
		if ( mLocalGroupsInfo != null ){
			mCurGroupsInfo = mLocalGroupsInfo;
			updateUILoadGroupDone();
			return;
		}
		updateUILoadingGroup();
		SimpleAsynTask.doTask2(new AsynTaskListner() {
			@Override
			public void onTaskDone(NetWorkEvent event) {
				if ( mContactTabHelper.getSelection() == 1 ){
					mCurGroupsInfo = mLocalGroupsInfo;
				}
				updateUILoadGroupDone();
			}
			
			@Override
			public void doInBackground() {
				mLocalGroupsInfo = mGroupController.getGroupInfoList(0);
			}
		});
	}
	private void loadRemoteGroups(){
		if ( mRemoteGroupsInfo != null ){
			mCurGroupsInfo = mRemoteGroupsInfo;
			updateUILoadGroupDone();
			return;
		}
		mUserInfo = AccountInfo.getInstance();
		updateUILoadingGroup();
		SContactAsyncHttpClient.post(
				GroupParams.getUserGroupParams(mUserInfo.getId()),
				null, new AsyncHttpResponseHandler(){
					@Override
					public void onFinish() {
						super.onFinish();
						updateUILoadGroupDone();
					}
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						super.onSuccess(arg0, arg1, arg2);
						ErrorInfo err;
						mRemoteGroupsInfo	= GsonHelper.getInfosFromJson(arg2, new TypeToken<List<GroupInfo>>(){}.getType());
						if ( mContactTabHelper.getSelection() == 0 ){
							mCurGroupsInfo = mRemoteGroupsInfo;
						}
						if ( mRemoteGroupsInfo == null ){
							mRemoteGroupsInfo = new ArrayList<GroupInfo>();
							err = GsonHelper.getInfoFromJson(arg2, ErrorInfo.class);
							Bog.toast(err.toString());
						} else {
							updateUILoadGroupDone();
						}
					}
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						super.onFailure(arg0, arg1, arg2, arg3);
						Bog.toast(getString(R.string.connect_server_err));
					}
				});
	}
	
	private void loadLocalFriendsByGroup(long gid){
		mFriendsListFragment.loadLocalFriendsByGroup(gid);
	}
	
	private void loadRomoteFriendsByGroup(Long gid){
		mFriendsListFragment.loadRomoteFriendsByGroup(gid);
	}

	
	void loadMyAllContacts(){
		//load cache first
		myAllContacts = ContactInfo.getFromPref();
		if ( ListUtil.isEmpty(myAllContacts) ){
			SContactAsyncHttpClient.post(ContactParams.getUserContactsParams(mUserInfo.getId()),
					null, new AsyncHttpResponseHandler(){
				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					super.onSuccess(arg0, arg1, arg2);
					myAllContacts = GsonHelper.getInfosFromJson(arg2, new ContactInfo().listType());
					if ( myAllContacts != null ){
						ContactInfo.saveToPref(myAllContacts);
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
		
	}

	//end private methods===========
	
	@Override
	public void onAparted(GroupInfo g) {
		onExited(g);
	}

	@Override
	public void onExited(GroupInfo g) {
		if ( mRemoteGroupsInfo != null && g != null ){
			for ( GroupInfo info : mRemoteGroupsInfo ){
				if ( g.getId().equals(info.getId() )){
					mRemoteGroupsInfo.remove(info);
					break;
				}
			}
		}
		mGroupListAdapter.setData(mRemoteGroupsInfo);
	}
}
